package ir.data;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import ir.util.Coerce;
import ir.util.IndyMath;
import ir.util.ItemTimer;
import ir.util.JDate;
import ir.util.JDateTime;
import ir.util.Reflector;
import ir.util.SetKit;
import ir.util.StringKit;
import ir.util.TypeKit;
/**
 * Encapsulates a connection to a database, provides persistence services to
 * IRow and IRecord, provides an implementation of ITranslator. Consuming
 * applications should derive from Database for each database to be used, and
 * instantiate per request.
 */
public class Database implements ITranslator,AutoCloseable
{
  private static Map<String, RecInfo> _recInfo = new ConcurrentHashMap<String, RecInfo>(64);
  public static int DefaultFetchSize=500;
  private static Map<String, ItemTimer> StatementTimers = new ConcurrentHashMap<String, ItemTimer>(512);
  private static Map<String, TableDef> TableDefs = new ConcurrentHashMap<String, TableDef>(250);
  public static boolean TrackOpeners = true;
  private PooledConnection _con = null;
  private String _dbName = null;
  private DatabaseMetaData _dmd = null;
  private DbDriver _driver = DbDriver.Unknown;
  private int _fetchSize = 100;
  protected Language language = Language.English;
  private String _opener = "[not tracked]";
  private final Map<String, IRecord> _recordCache = new HashMap<String, IRecord>();
  protected boolean _recordCacheEnabled = true;
  private DatabaseUpdateListener _updateListener = null;
  private String currentSql = null;
  private long currentSqlStart = 0;
  protected final Map<String, String> namedSql = new ConcurrentHashMap<String, String>();
  private boolean statsOn = true;
  /**
   * Closes all connections for the database's pool
   */
  protected static void closePool(String sDriver, String sUrl, String sUsr, String sPwd)
  {
    ConnPool.close(sDriver, sUrl, dbName(sUrl), sUsr, sPwd);
  }
  protected static String dbName(String sUrl)
  {
    int lastSlash = sUrl.lastIndexOf('/');
    return sUrl.substring(lastSlash + 1);
  }
  public static String getCountSQL(String sql)
  {
    String slc = sql.toLowerCase().replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
    int from = slc.indexOf(" from ");
    String r = "";
    int groupAt = slc.indexOf(" group by ");
    if (groupAt > 0)
    {
      r = sql.substring(from, groupAt);
    }
    else
    {
      int orderAt = slc.indexOf(" order by ");
      if (orderAt > 0)
      {
        r = sql.substring(from, orderAt);
      }
      else
      {
        r = sql.substring(from);
      }
    }
    return "select count(*) " + r;
  }
  public static ItemTimer[] getTimers()
  {
    return StatementTimers.values().toArray(new ItemTimer[0]);
  }
  public static String resolveSql(String sql, List<Object> parms) throws Exception
  {
    if (parms != null)
    {
      for (Object o : parms)
      {
        int q = sql.indexOf('?');
        if (-1 < q)
        {
          if (o == null)
          {
            sql = sql.substring(0, q) + "NULL" + sql.substring(q + 1);
          }
          else
          {
            sql = sql.substring(0, q) + "'" + StringKit.replace(TypeKit.convert(o, ""), "'", "\\'") + "'" + sql.substring(q + 1);
          }
        }
      }
    }
    return sql;
  }
  public static String resolveSql(String sql, Object[] parms) throws Exception
  {
    return resolveSql(sql,parms == null ? new ArrayList<Object>() : Arrays.asList(parms));
  }
  /**
   * Loads the values from IRow implementer to another
   */
  public static void setValues(IRow rF, IRow rT) throws Exception
  {
    if (!rF.getClass().equals(rT.getClass()))
    {// not the same object, use more generic method
      Reflector.setPublicInstanceValues(rF, rT);
      return;
    }
    int c = rF.getFields().length;
    for (int i = 0; i < c; i++)
    {
      rT.setValue(i, rF.getValue(i));
    }
  }
  public Database()
  {
    _fetchSize = DefaultFetchSize;
  }
  public boolean addColumn(String table,String column,String specifications) throws Exception
  {
    if (!columnExists(table,column))
    {
      execute("alter table " + table + " add column " + column + " " + specifications);
      StringKit.println("Column {0}.{1} added",table,column);
      return true;
    }
    return false;
  }
  void afterDelete(IRecord rec) throws Exception
  {
    if (null != _updateListener)
    {
      _updateListener.afterDelete(this, rec);
    }
    rec.afterDelete(this);
    _recordCache.remove(rec.getCacheKey(this));
  }
  protected void afterInsert(DbCommand c,IRecord rec,int autoIdx) throws Exception
  {
    afterInsert(c,rec,autoIdx,null);
  }
  void afterInsert(DbCommand c,IRecord rec,int autoIdx,Integer autoKey) throws Exception
  {
    if (autoIdx > -1)
    {
      int autoVal;
      if (autoKey == null)
      {
        autoVal = c.getGeneratedKey();
      }
      else
      {
        autoVal = autoKey;
      }
      if (autoVal > -1)
      {
        rec.setValue(autoIdx, autoVal);
      }
    }
    if (null != _updateListener)
    {
      _updateListener.afterInsert(this, rec);
    }
    rec.afterInsert(this);
    rec.makeClean();
    if (_recordCacheEnabled)
    {
      _recordCache.put(rec.getCacheKey(this),rec);
    }
  }
  void afterUpdate(IRecord rec) throws Exception
  {
    IRecord old = rec.getOldValue();
    if (null != _updateListener)
    {
      _updateListener.afterUpdate(this, rec,old);
    }
    rec.afterUpdate(this,old);
    rec.makeClean();
    if (_recordCacheEnabled)
    {
      _recordCache.put(rec.getCacheKey(this), rec);
    }
  }
  /**
   * Ensures the values of the enum class passed are allowed on the column passed.  Will not
   * execute alter statement at all if column and enum are already in accord.
   * <p>WARNING: any non-compliant data in table will cause this statement to fail,you will
   * need to do a migration instead.
   */
  public void align(Class<? extends Enum<?>>cls,String table,String column) throws Exception
  {
    if (isAligned(cls,table,column))
    {
      StringKit.println(cls.getName() + " already aligned for " + table + "." + column + ".");
      return;
    }
    Enum<?>[] values = cls.getEnumConstants();
    StringBuilder sql = new StringBuilder("alter table " + table + " modify column " + column + " enum(");
    String comma = "";
    for (Enum<?> t : values)
    {
      sql.append(comma).append("'").append(t.name()).append("'");
      comma = ",";
    }
    sql.append(")");
    try
    {
      execute(sql.toString());
      StringKit.println(cls.getName() + ".align for " + table + "." + column);
    }
    catch (Exception e)
    {
      StringKit.println(cls.getName() + ".align for " + table + "." + column + ":" + e.getMessage());
    }
  }
  /**
   * If record uses a sequence, sets the sequence field and returns -1.
   * <br>If record uses autoincrement, returns the index of the auto field,
   * <br>Otherwise returns -1;
   */
  protected int beforeInsertAuto(IRecord rec) throws Exception
  {
    int autoIdx = -1;
    String seqFld = rec.getSequenceField(this);
    if (seqFld != null)
    {
      int seqIdx = rec.getFieldIndex(seqFld);
      rec.setValue(seqIdx,getSequenceNextVal(rec));
    }
    else
    {
      autoIdx = getAutoIndex(rec);
    }
    return autoIdx;
  }
  //
  void beginExecution()
  {
    currentSqlStart = System.currentTimeMillis();
  }

  void beginExecution(String statementSql)
  {
    currentSql = statementSql;
    currentSqlStart = System.currentTimeMillis();
  }
  /**
   * Turns autocommit off on database
   */
  public void beginTransaction() throws Exception
  {
    _con.setAutoCommit(false);
  }
  /**
   * Performs a cascading delete.
   */
  public int cascadeDelete(IRecord parent, String fromCols, String toTbl, String toCols) throws Exception
  {
    if (parent.getTable().equalsIgnoreCase(toTbl))
    {
      throw new Exception("Cannot cascade delete to source table.");
    }
    if (fromCols == null || toCols == null)
    {
      throw new Exception("From-to columns cannot be null.");
    }
    String[] fc = StringKit.split(fromCols, ",");
    String[] tc = StringKit.split(toCols, ",");
    if (fc.length == 0 || fc.length != tc.length)
    {
      throw new Exception("From-to columns mismatch.");
    }
    StringBuilder s = new StringBuilder("delete from ");
    s.append(toTbl).append(" where 'a'='a'");
    List<Object> lst = new ArrayList<Object>();
    for (int i = 0; i < fc.length; i++)
    {
      s.append(" and ").append(tc[i]).append("=?");
      lst.add(parent.getValue(parent.getColumnIndex(fc[i])));
    }
    return execute(s.toString(), lst.toArray());
  }
  protected String checkSql(String sql) throws Exception
  {
    currentSql = sql;
    String replacementSql = namedSql.get(sql);
    if (replacementSql == null)
    {
      return sql;
    }
    return replacementSql;
  }
  /**
   * Closes the database connection.
   */
  @Override
  public void close()
  {
    namedSql.clear();
    _recordCache.clear();
    if (_con != null)
    {
      try
      {
        if (!_con.getAutoCommit())
        {
          _con.rollback();
          _con.setAutoCommit(false);
        }
        _con.close();
      }
      catch (Exception e)
      {
        System.out.println(getClass().getName() + ".close(): " + e.getMessage());
      }
    }
    _con = null;
  }
  public boolean columnExists(String table, String column) throws Exception
  {
    if (isMySQL())
    {
      return mysqlColumnExists(table,column);
    }
    ResultSet rs = null;
    try
    {
      rs = getConnection().getMetaData().getColumns(null,_dbName, table, column);
      return rs.next();
    }
    catch (Exception e)
    {
      return false;
    }
    finally
    {
      if (rs != null)
      {
        try
        {
          rs.close();
        }
        catch (Exception ee)
        {
        }
      }
    }
  }
  public void commit() throws Exception
  {
    _con.commit();
    _con.setAutoCommit(true);
  }
  void completeExecution() throws Exception
  {
    if (!statsOn)
    {
      return;
    }
    if (currentSql != null)
    {
      String k = _dmd.getURL();
      ItemTimer t = StatementTimers.get(k);
      if (t == null)
      {
        t = new ItemTimer(k);
        StatementTimers.put(k, t);
      }
      t.addHit(StringKit.left(StringKit.squish(currentSql), 150), currentSqlStart);
    }
    currentSql = null;
    currentSqlStart = 0;
  }
  /**
   * Deletes the record based on it's embodied key, returns whether the
   * deletion worked
   */
  public boolean delete(IRecord rec) throws Exception
  {
    currentSql = rec.getClass().getSimpleName() + ".delete";
    String sql = getInfo(rec).getDelete();
    rec.beforeDelete(this);
    int r = prepareNoCheck(sql, 1).execute(getParameters(rec, SQLCommand.Delete));
    if (0 < r)
    {
      afterDelete(rec);
    }
    return 0 < r;
  }
  public void dropColumn(String table,String column) throws Exception
  {
    if (columnExists(table,column))
    {
      execute("alter table " + table + " drop column " + column);
    }
  }
  /**
   * Executes the passed statements,joined by semicolons with the passed parameters, returns count
   * of rows affected.
   */
  protected int execute(List<String> sqlCommands, Object... parameters) throws Exception
  {
    String semiColon="";
    StringBuilder sql = new StringBuilder();
    for (String cmd : sqlCommands)
    {
      sql.append(semiColon).append(checkSql(cmd));
      semiColon=";";
    }
    return prepareCmd(sql.toString(),1).execute(parameters);
  }
  /**
   * Executes the passed statement with the passed parameters, returns count
   * of rows affected.
   */
  public int execute(String sql, Object... parameters) throws Exception
  {
    return prepareCmd(sql,1).execute(parameters);
  }
  /**
   * Executes the passed statement with the passed parameters, returns count
   * of rows affected, ignores but logs exception
   */
  public int executeIgnore(String sql, Object... parameters)
  {
    try
    {
      return prepareCmd(sql,1).execute(parameters);
    }
    catch (Exception exc)
    {
      //StringKit.println("ignored exception for " + sql + ":: " + exc.getMessage());
      return 0;
    }
  }
  public void executeScript(String script, String lineDelimiter, String... linePrefixFilters)
      throws Exception
  {
    script = script.replace("ï»¿","");//remove UTF-8 indicator
    String[] scriptLines = StringKit.split(script, lineDelimiter);
    for (String line : scriptLines)
    {
      line = line.trim();
      if (line.length()==0)
      {
        continue;
      }
      boolean skip = false;
      if (linePrefixFilters != null)
      {
        for (String prefix : linePrefixFilters)
        {
          if (line.toLowerCase().startsWith(prefix.toLowerCase()))
          {
            skip=true;
            break;
          }
        }
      }
      if (! skip)
      {
        getConnection().createStatement().execute(line);
      }
    }
  }
  /**
   * Resolves the passed statement with the passed parameters, executes it,
   * outputs result <- resolved sql to standard output returns count of rows
   * affected.
   */
  public int executeTrace(String sql, Object... parameters) throws Exception
  {
    String res = resolveSql(sql, parameters);
    int result = execute(sql, parameters);
    StringKit.println("{0} rows <- {1}", result, res);
    return result;
  }
  /**
   * Uses the object's embodied key to tell whether a row with that key
   * exists.
   */
  public boolean exists(IRecord rec, Object... keys) throws Exception
  {
    setKey(rec, keys);
    if (_recordCacheEnabled && _recordCache.containsKey(rec.getCacheKey(this)))
    {
      return true;
    }
    String sql = getInfo(rec).getExists();
    return 0 < selectScalar(sql, 0, getParameters(rec, SQLCommand.Exists));
  }
  @Override
  protected void finalize()
  {
    if (isOpen())
    {
      StringKit.println(getClass().getSimpleName() + " " + hashCode() + " leak<-" + _opener);
    }
    close();
  }
  /**
   * fix transforms the passed value so that it can be bound to the passed
   * column
   */
  private Object fix(Object o, ColDef cd) throws Exception
  {
    if (o == null)
    {
      return null;
    }
    if (o instanceof Boolean)
    {
      if (!cd.isBoolean())
      {
        return Boolean.TRUE.equals(o) ? 1 : 0;
      }
    }
    if (cd.isNumeric())
    {
      double min = cd.getMin();
      double max = cd.getMax();
      if (o instanceof BigDecimal)
      {
        BigDecimal d = (BigDecimal) o;
        if (d.doubleValue() < min || d.doubleValue() > max)
        {
          //System.out.println("Value " + o + " for " + cd.getTable() + "." + cd.getName() + " Database.fix 0");
          return BigDecimal.ZERO;
        }
        return d.setScale(cd.getScale(), IndyMath.RoundMode);
      }
      else if (o instanceof Double || o instanceof Float)
      {
        double val = IndyMath.round(Coerce.toDouble(o), cd.getScale());
        if (val < min || val > max)
        {
          //System.out.println("Value " + o + " for " + cd.getTable() + "." + cd.getName() + " Database.fix 0");
          return 0;
        }
        return o instanceof Float ? (float) val : val;
      }
      else
      {
        long val = Coerce.toLong(o);
        if (val < min || val > max)
        {
          //System.out.println("Value " + o + " for " + cd.getTable() + "." + cd.getName() + " Database.fix 0");
          return 0;
        }
        return val;
      }
    }
    else if (cd.isChar() && cd.getSize() > 0)
    {
      if (o.toString().length() > cd.getSize())
      {
        //System.out.println("Value for " + cd.getTable() + "." + cd.getName() + " Database.fix truncated: " + o);
      }
      return StringKit.left(o.toString(), cd.getSize());
    }
    return o;
  }
  /**
   * Returns the index of the autoincrement field on the passed table, or -1
   * if not applicable or retrieval failed
   */
  int getAutoIndex(IRecord rec) throws Exception
  {
    TableDef tbl = getTableDef(rec.getTable());
    String a = tbl.getAutoColumn();
    if (a != null)
    {
      return rec.getColumnIndex(a);
    }
    return -1;
  }
  public String getClassDef(String tblName) throws Exception
  {
    String className = tblName + "Rec";
    if (tblName.length() > 1)
    {
      if (tblName.toLowerCase().charAt(0) == 't' && tblName.charAt(1) == tblName.toUpperCase().charAt(1))
      {
        className = tblName.substring(1) + "Rec";
      }
    }
    TableDef td = getTableDef(tblName);
    StringBuilder sb = new StringBuilder("");
    sb.append("public class " + className + " extends BaseRecord<br>{<br>");
    sb.append("public static final String TABLE=\"" + td.getName() + "\";<br>");
    List<ColDef> cda = new ArrayList<ColDef>(td.getColumns());
    Collections.sort(cda, new Comparator<ColDef>()
    {
      @Override
      public int compare(ColDef o1, ColDef o2)
      {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    for (ColDef cd : cda)
    {// field name constants
      String cn = StringKit.initCap(cd.getName());
      String n = cd.getName().toUpperCase();
      if (n.equals("TABLE"))
        n += "_FIELD";
      sb.append("public static final String " + n + "=\"" + cn + "\";<br>");
    }
    sb.append("//<br>");
    for (ColDef cd : cda)
    {// field declarations
      String cn = StringKit.initCap(cd.getName());
      sb.append("public " + cd.getJavaTypeName() + " " + cn + "=" + cd.getInitialValue() + ";<br>");
    }
    sb.append("//<br>public String getTable()<br>" + "{<br>&nbsp;&nbsp;&nbsp;&nbsp;return TABLE;<br>}<br>}<br>//<br>");
    for (ColDef cd : cda)
    {// control declarations
      String cn = "ctl" + StringKit.initCap(cd.getName());
      if (cd.isDate())
        sb.append("public JDateTimeControl " + cn + ";<br>");
      else
        sb.append("public JControl " + cn + ";<br>");
    }
    sb.append("//<br>");
    for (ColDef cd : cda)
    {// control mappings
      String cn = "ctl" + StringKit.initCap(cd.getName());
      String fn = className + "." + cd.getName().toUpperCase();
      sb.append("mapControl(" + cn + ",_rec," + fn + ");<br>");
    }
    return sb.toString();
  }
  /**
   * Gets a ColDef Object for the named table and column
   */
  public ColDef getColDef(String table, String column) throws Exception
  {
    // return column names in lower case
    TableDef td = getTableDef(table);
    return td.getColumn(column);
  }
  /**
   * Gets a Column's size -> for displaying text boxes and trimming input
   */
  public int getColSize(String table, String column) throws Exception
  {
    return getColDef(table, column).getSize();
  }
  public String getColumnNames(IRecord rec) throws Exception
  {
    return getInfo(rec).getColumns();
  }
  public int getColumnScale(String table, String column, int defaultIfNegative) throws Exception
  {
    int scale = getColDef(table, column).getScale();
    return scale >= 0 ? scale : defaultIfNegative;
  }
  public Connection getConnection()
  {
    return _con;
  }
  /**
   * returns the command used to test a connection when taking from the pool.
   * This is necessary because pooled connections sometimes go stale.
   */
  protected String getConnectionTest()
  {
    return "select 1";
  }
  public String getCurrentSql()
  {
    return currentSql;
  }
  /**
   * Return enum value mapped to name or ordinal
   */
  private Object getEnumValue(Object o, ColDef cd)
  {
    if (cd.isBoolean() || cd.isNumeric())
    {
      return ((Enum<?>) o).ordinal();
    }
    else
    {
      return ((Enum<?>) o).name();
    }
  }
  private RecInfo getInfo(IRecord rec) throws Exception
  {
    String rk = _con.getPoolKey() + rec.getClass();
    RecInfo ri = _recInfo.get(rk);
    if (ri == null)
    {
      ri = new RecInfo(this, rec);
      _recInfo.put(rk, ri);
    }
    return ri;
  }
  List<Object>getInsertParameters(IRecord rec,String...updateColumns) throws Exception
  {
    List<Object>parms = new ArrayList<Object>();
    parms.addAll(Arrays.asList(getParameters(rec, SQLCommand.Insert)));
    if (updateColumns != null && updateColumns.length> 0)
    {
      for (String c : updateColumns)
      {
        int columnIndex = rec.getColumnIndex( c );
        if (columnIndex < 0)
        {
          throw new Exception(rec.getClass().getSimpleName() + ".getInsertParameters updateColumn not found:" + c );
        }
        parms.add(getParameter(rec,columnIndex));
      }
    }
    return parms;
  }
  /**
   * Returns the names of the columns on the Record's Primary Key or an empty
   * String[] if no keys are discovered.
   */
  protected String[] getKey(IRecord rec) throws Exception
  {
    String[] pkn = rec.getKey();
    if (pkn == null || pkn.length == 0)
      pkn = getTableDef(rec.getTable()).getPrimaryKey();
    return pkn;
  }
  public Language getLanguage()
  {
    return language;
  }
  public DatabaseMetaData getMetaData()
  {
    return _dmd;
  }
  public String getName()
  {
    return _dbName == null ? "" : _dbName;
  }
  /**
   * May perform fix-ups on value from record to be bound to sql statement.
   */
  private Object getParameter(IRecord rec, int colIdx) throws Exception
  {
    String columnName = rec.getColumns()[colIdx];
    Object o = rec.getValue(colIdx);
    if (o == null)
    {
      return null;
    }
    if (rec.isOptionalForeignKey(this,columnName))
    {// if it's an optional foreign key,blank or zero should be null
      if (Coerce.toString(o).equals("") || (o instanceof Number && 0 == ((Number) o).doubleValue()))
      {
        return null;
      }
    }
    ColDef cd = getColDef(rec.getTable(),columnName);
    if (cd == null)
    {
      throw new Exception("ColDef for " + rec.getTable() + "." + columnName + " not found.");
    }
    Field fi = Reflector.getUpdatablePublicInstanceField(rec,rec.getFields()[colIdx]);
    if (fi.getType().isEnum())
    {
      return getEnumValue(o, cd);
    }
    return fix(o, cd);
  }
  /**
   * Provides values from record to be bound to sql statement.
   */
  Object[] getParameters(IRecord rec, SQLCommand cmdType) throws Exception
  {
    setKey(rec);
    String[] flds = rec.getFields();
    String[] cols = rec.getColumns();
    int autoFieldIndex = getAutoIndex(rec);
    int sequenceFieldIndex = autoFieldIndex > -1 ? -1 : rec.getFieldIndex(rec.getSequenceField(this));
    List<Object> lst = new ArrayList<Object>();
    if (cmdType.bindFields())
    {// create parms all fields
      for (int i = 0; i < cols.length; i++)
      {
        if (i == autoFieldIndex && !cmdType.bindAuto())
        {
          continue;
        }
        if (i == sequenceFieldIndex && cmdType==SQLCommand.InsertNextVal)
        {
          continue;
        }
        lst.add(getParameter(rec, i));
      }
    }
    if (cmdType.bindKeys())
    {// create parms for keys
      getParametersForKeys(rec, flds, lst);
    }
    return lst.toArray();
  }
  private void getParametersForKeys(IRecord rec, String[] fieldNames, List<Object> lst) throws Exception
  {
    String[] pk = getKey(rec);
    for (int i = 0; i < pk.length; i++)
    {
      String cn = pk[i];
      int iFld = rec.getColumnIndex(cn);
      if (iFld < 0)
      {
        throw new Exception("primary key column not bound:" + cn);
      }
      String fn = fieldNames[iFld];
      Field fi = Reflector.getUpdatablePublicInstanceField(rec, fn);
      if (fi == null)
      {
        throw new Exception("field name wrong:" + fn);
      }
      Object o = rec.getValue(iFld);
      if (o instanceof Boolean)
      {
        ColDef cd = getColDef(rec.getTable(), cn);
        if (cd.isNumeric())
        {
          o = Boolean.TRUE.equals(o) ? 1 : 0;
        }
      }
      lst.add(rec.getValue(iFld));
    }
  }
  public int getScale(String table,String column) throws Exception
  {
    ColDef cd = getColDef(table, column);
    if (cd == null)
    {
      throw new Exception("Column " + column + " not found on table " + table);
    }
    return cd.getScale();
  }
  /**
   * If record uses a sequence, returns the sequence field value to
   * assign to a new insertion.
   */
  protected int getSequenceNextVal(IRecord rec) throws Exception
  {
    String seqName = rec.getSequenceName(this);
    String sql = "select " + seqName + ".nextval from dual";
    return selectScalar(sql, 1);
  }
  /**
   * Returns the TableDef Object for the named table
   */
  public TableDef getTableDef(String tblName) throws Exception
  {
    if (_con == null)
    {
      throw new Exception("not connected!");
    }
    if (tblName == null)
    {
      throw new Exception("need table name");
    }
    String key = prependSchema(tblName.toLowerCase());
    TableDef td = TableDefs.get(key);
    if (td == null)
    {
      td = new TableDef(_con, tblName);
      TableDefs.put(key, td);
    }
    return td;
  }
  public String getUserMail()
  {
    return "";
  }
  /**
   * Inserts the record with its current values, sets the autoincrement field,
   * if any, returns whether insert was successful.
   */
  public boolean insert(IRecord rec) throws Exception
  {
    currentSql = rec.getClass().getSimpleName() + ".insert";
    DbCommand ps = prepareInsert(rec,false);
    rec.beforeInsert(this);
    int autoIdx = beforeInsertAuto(rec);
    int returnValue = 0;
    try
    {
      returnValue = ps.execute(getParameters(rec, SQLCommand.Insert));
      if (returnValue > 0)
      {
        afterInsert(ps,rec,autoIdx);
      }
      return 0 < returnValue;
    }
    catch (Exception e)
    {
      Exception enhanced = new Exception(e.getMessage() + "\n\t" + rec.getClass().getSimpleName() + " " + rec.toString());
      enhanced.setStackTrace(e.getStackTrace());
      throw enhanced;
    }
  }
  /**
   * Inserts the record with its current values, sets the autoincrement field, if any,
   * returns whether insert was successful.  Does nothing if record exists.
   */
  public boolean insertIgnore(IRecord rec) throws Exception
  {
    currentSql = rec.getClass().getSimpleName() + ".insertIgnore";
    DbCommand ps = prepareInsert(rec,true);
    int autoIdx = beforeInsertAuto(rec);
    rec.beforeInsert(this);
    int returnValue = 0;
    try
    {
      returnValue = ps.execute(getParameters(rec, SQLCommand.Insert));
      if (returnValue > 0)
      {
        afterInsert(ps,rec,autoIdx);
      }
      return 0 < returnValue;
    }
    catch (Exception e)
    {
      Exception enhanced = new Exception(e.getMessage() + "\n\t" + rec.getClass().getSimpleName() + " " + rec.toString());
      enhanced.setStackTrace(e.getStackTrace());
      throw enhanced;
    }
  }
  /**
   * Inserts the record with its current values, and nextVal('tablename') as value for the sequence column.
   * Returns whether insert was successful.
   * <p>Note this method DOES NOT INVOKE IRecord.afterInsert because we are not getting
   * the value assigned back.  The purpose of this method is to insert multiple
   * records quickly.
   */
  public boolean insertNextVal(IRecord rec) throws Exception
  {
    currentSql = rec.getClass().getSimpleName() + ".insertNextVal";
    String sql = getInfo(rec).getInsertNext();
    DbCommand ps = prepareNoCheck(sql, 1);
    return 0 < ps.execute(getParameters(rec, SQLCommand.InsertNextVal));
  }
  /**
   * Inserts the record with its current values, without setting the autoincrement field if any.
   * Returns whether insert was successful.
   */
  public boolean insertNoGen(IRecord rec) throws Exception
  {
    currentSql = rec.getClass().getSimpleName() + ".insertNoGen";
    String sql = getInfo(rec).getInsertNoGen();
    DbCommand ps = prepareNoCheck(sql, 1);
    int result = ps.execute(getParameters(rec, SQLCommand.InsertNoGen));
    if (result > 0)
    {
      afterInsert(ps,rec,-1);
    }
    return result>0;
  }
  /**
   * Uses on duplicate key clause to update passed column names.
   * @return true on insert, false on update
   * @throws Exception on problem
   */
  public boolean insertUpdate(IRecord rec,String updateColumn1,String...updateColumns) throws Exception
  {
    int autoIdx = beforeInsertAuto(rec);
    if (autoIdx > -1)
    {
      if (Coerce.toLong(rec.getValue(autoIdx)) > 0)
      {
        throw new Exception("Cannot insertUpdate " + rec.getClass().getSimpleName() + " with value for " + rec.getColumns()[autoIdx] + " > 0.");
      }
    }
    currentSql = rec.getClass().getSimpleName() + ".insertOnDup";
    List<String>updCols = SetKit.asList(updateColumn1, updateColumns);
    DbCommand ps = prepareInsert(rec,false,updCols.toArray(new String[0]));
    rec.beforeInsert(this);
    int returnValue;
    try
    {
      List<Object>parms = getInsertParameters(rec,updCols.toArray(new String[0]));
      returnValue = ps.execute(parms.toArray());
      if (returnValue == 2)
      {
        afterUpdate(rec);
        return false;
      }
      else
      {
        afterInsert(ps,rec,autoIdx);
        return true;
      }
    }
    catch (Exception e)
    {
      Exception enhanced = new Exception(e.getMessage() + "\n\t" + rec.getClass().getSimpleName() + " " + rec.toString());
      enhanced.setStackTrace(e.getStackTrace());
      throw enhanced;
    }
  }
  /**
   * Indicates whether the column definition is aligned with the java enum
   */
  public boolean isAligned(Class<? extends Enum<?>>cls,String table,String column) throws Exception
  {
    String[] dbValues = selectMysqlEnumColumnValues(table,column);
    Enum<?>[] enumValues = cls.getEnumConstants();
    if (dbValues.length != enumValues.length)
    {
      return false;
    }
    Set<String>enumValueNames=new HashSet<String>();
    for (Enum<?> en : enumValues)
    {
      enumValueNames.add(en.name());
    }
    for (String dbValue : dbValues)
    {
      if (! enumValueNames.contains(dbValue))
      {
        return false;
      }
    }
    return true;
  }
  public boolean isMySQL()
  {
    return _driver == DbDriver.MySQL;
  }
  /**
   * Indicates whether database is open.
   */
  public boolean isOpen()
  {
    if (_con == null)
      return false;
    try
    {
      return !_con.isClosed();
    }
    catch (Exception e)
    {
      return false;
    }
  }
  public boolean isOracle()
  {
    return _driver == DbDriver.Oracle;
  }
  protected void loadNamedStatements(Map<String, String> m)
  {
    namedSql.putAll(m);
  }
  @Override
  public String md(JDate dt) throws Exception
  {
    if (dt == null || dt.isZero())
      return "";
    return xlate("mmm" + dt.month()) + " " + dt.day();
  }
  @Override
  public String md(JDateTime dt) throws Exception
  {
    if (dt == null || dt.isZero())
      return "";
    return xlate("mmm" + dt.month()) + " " + dt.day();
  }
  @Override
  public String mdy(JDate dt) throws Exception
  {
    if (dt == null || dt.isZero())
    {
      return "";
    }
    return xlate("mmm" + dt.month()) + " " + dt.day() + ", " + dt.year();
  }
  @Override
  public String mdy(JDateTime dt) throws Exception
  {
    if (dt == null || dt.isZero())
    {
      return "";
    }
    return xlate("mmm" + dt.month()) + " " + dt.day() + ", " + dt.year();
  }
  public String mdyhm(JDateTime dt) throws Exception
  {
    if (dt == null || dt.isZero())
    {
      return "";
    }
    return mdy(dt.getJDate()) + " " + StringKit.right("0" + dt.hour(), 2) + ":" + StringKit.right("0" + dt.minute(), 2);
  }
  public boolean mysqlColumnExists(String table,String column ) throws Exception
  {
    return 0 < selectScalar("select 1 from information_schema.columns "
        + " where upper(table_schema)=upper(schema()) and upper(table_name)=? and upper(column_name)=upper(?)",
        0,table, column);
  }
  public boolean mysqlIndexExists(String name) throws Exception
  {
    return 0 < selectScalar("SELECT 1 FROM information_schema.KEY_COLUMN_USAGE"
        + " where upper(constraint_schema)=upper(schema()) and upper(constraint_name)=upper(?) limit 1",0,name);
  }
  public String mysqlKeyType(String table,String key) throws Exception
  {
    return selectScalar("SELECT constraint_type FROM information_schema.TABLE_CONSTRAINTS T"
        + " where table_schema=schema() and table_name=? and constraint_name=?","",table,key);
  }
  public boolean mysqlRoutineExists(String name ) throws Exception
  {
    return 0 < selectScalar("select 1 from information_schema.routines where routine_schema=schema() and routine_name=?",
        0,name);
  }
  /**
   * Opens the database to the passed connection Parameters
   */
  public void open(String sDriver, String sUrl, String sUsr, String sPwd, int nPoolSize) throws Exception
  {
    int questionAt = sUrl.indexOf('?');
    int lastSlash = sUrl.lastIndexOf('/',questionAt == -1 ? sUrl.length() - 1 : questionAt);
    String url = sUrl.substring(0, lastSlash + 1);
    _dbName = sUrl.substring(lastSlash + 1);
    open(sDriver, url, _dbName, sUsr, sPwd, nPoolSize);
  }
  /**
   * Opens the database to the passed connection Parameters
   */
  public void open(String sDriver, String sUrl, String sDb, String sUsr, String sPwd, int nPoolSize) throws Exception
  {
    _dbName = sDb.toLowerCase();
    _con = ConnPool.get(sDriver, sUrl, sDb, sUsr, sPwd, getConnectionTest(), nPoolSize,40);
    _dmd = _con.getMetaData();
    // what driver are we talking to?
    String drv = _dmd.getDriverName().toLowerCase();
    if (0 <= drv.indexOf("mysql"))
    {
      _driver = DbDriver.MySQL;
    }
    else if (0 <= drv.indexOf("postgre"))
    {
      _driver = DbDriver.PostGres;
    }
    else if (0 <= drv.indexOf("oracle"))
    {
      _driver = DbDriver.Oracle;
    }
    else if (0 <= drv.indexOf("mssql"))
    {
      _driver = DbDriver.MsSQL;
    }
    else if (0 <= drv.indexOf("odbc"))
    {
      _driver = DbDriver.ODBC;
    }
    else
    {
      _driver = DbDriver.Unknown;
    }
    if (TrackOpeners)
    {
      final int maxLevels=5;
      int level=1;
      Exception e = new Exception();
      e.fillInStackTrace();
      StackTraceElement[] tee = e.getStackTrace();
      _opener = "";
      String openerDelim="";
      for (StackTraceElement te : tee)
      {
        String cn = te.getClassName();
        if (-1==cn.indexOf("BasePage") && -1==cn.indexOf("BaseReport") && ! cn.endsWith("Database") && ! cn.endsWith("Db"))
        {
          _opener += openerDelim + te.getClassName() + "." + te.getMethodName() + ":" + te.getLineNumber();
          openerDelim="<-";
          if (level++>maxLevels)
          {
            break;
          }
        }
      }
    }
  }
  /**
   * Prepares an sql statement and returns an ICommand implementer
   */
  public ICommand prepare(String sqlOrKey) throws Exception
  {
    return prepare(sqlOrKey,-1);
  }
  /**
   * Prepares an sql statement and returns an ICommand implementer
   */
  public ICommand prepare(String sqlOrKey,int batchSize) throws Exception
  {
    String sql = checkSql(sqlOrKey);
    DbCommand cmd = prepareCmd(sql,batchSize);
    cmd.setTimingName(sqlOrKey + "-" + batchSize);
    return cmd;
  }
  protected DbCommand prepareCmd(String sql) throws Exception
  {
    sql = checkSql(sql);
    return prepareNoCheck(sql, -1);
  }
  protected DbCommand prepareCmd(String sql,int batchSize) throws Exception
  {
    sql = checkSql(sql);
    return prepareNoCheck(sql, batchSize);
  }
  public IRecCommand prepareDelete(IRecord rec, int batchSize) throws Exception
  {
    DbCommand cmd = prepareNoCheck(getInfo(rec).getDelete(), batchSize);
    return cmd;
  }
  private DbCommand prepareInsert(IRecord rec,boolean withIgnore,String...updateColumns) throws Exception
  {
    String sql = getInfo(rec).getInsert();
    if (withIgnore)
    {
      sql = sql.replaceFirst("insert ","insert ignore ");
    }
    else if (updateColumns != null && updateColumns.length> 0)
    {
      sql += " on duplicate key update ";
      String comma="";
      for (String c : updateColumns)
      {
        sql += comma + c + "=?";
        comma = ",";
      }
    }
    DbCommand cmd = prepareNoCheck(sql, 1);
    if (updateColumns != null && updateColumns.length>0)
    {
      cmd.setUpdateColumns(updateColumns);
    }
    return cmd;
  }
  public IRecCommand prepareInsert(IRecord rec, int batchSize) throws Exception
  {
    return prepareInsert(rec,false,(String[])null);
  }
  public IRecCommand prepareInsertIgnore(IRecord rec, int batchSize) throws Exception
  {
    return prepareInsert(rec,true,(String[])null);
  }
  public IRecCommand prepareInsertUpdate(IRecord rec,int batchSize,String updateColumn1, String...updateColumns) throws Exception
  {
    return prepareInsert(rec,false,SetKit.join(updateColumn1, updateColumns));
  }
  private DbCommand prepareNoCheck(String sql, int batchSize) throws Exception
  {
    PreparedStatement ps = null;
    if (sql.trim().toLowerCase().startsWith("select"))
    {
      ps = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ps.setFetchSize(_fetchSize);
    }
    else
    {
      ps = _con.prepareStatement(sql);
    }
    return new DbCommand(this, ps, sql,batchSize);
  }
  public IRecCommand prepareUpdate(IRecord rec, int batchSize) throws Exception
  {
    DbCommand cmd = prepareNoCheck(getInfo(rec).getUpdate(), batchSize);
    return cmd;
  }
  private String prependSchema(String tbl)
  {
    return _dbName == null ? tbl : tbl.indexOf(".") > -1 ? tbl : _dbName + "." + tbl;
  }
  /**
   * Implemented to satisfy ITranslator
   */
  @Override
  public void release()
  {
  }
  public void rollback() throws Exception
  {
    _con.rollback();
    _con.setAutoCommit(true);
  }
  /**
   * Selects the row based on using the passed parameters as a key, and fills
   * the fields, returns whether the record was found. If no values are
   * passed, uses the values already on the record. eg. <br>
   * CustRec theRec = new CustRec(); <br>
   * boolean res = db.select(theRec,passedCustRow); <br>
   * // or <br>
   * theRec.CustRow = passedCustRow; <br>
   * boolean res = db.select(theRec);
   */
  public boolean select(IRecord rec, Object... keys) throws Exception
  {
    setKey(rec, keys);
    String ck = null;
    if (_recordCacheEnabled)
    {
      ck = rec.getCacheKey(this);
      IRecord r = _recordCache.get(ck);
      if (r != null)
      {
        setValues(r, rec);
        rec.afterRead(this);
        return true;
      }
    }
    if (selectEmbodied(rec))
    {
      if (_recordCacheEnabled)
      {//do NOT put object in cache, copy it because caller owns it
        _recordCache.put(ck,(IRecord)rec.copy());
      }
      return true;
    }
    return false;
  }
  public byte[] selectBytes(String sql, Object... parameters) throws Exception
  {
    return prepareCmd(sql).selectBytes(parameters);
  }
  /**
   * Fills a csv file from the data selected.
   * @param IRow can be null
   */
  public void selectCsv(String sql,IRow row,File toFile,Set<String>excludedColumns, Object... parameters) throws Exception
  {
    sql = checkSql(sql);
    PreparedStatement ps = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    ps.setFetchSize(_fetchSize);
    IReader reader = selectReader(sql,parameters);
    try
    {
      reader.toCsv(row, toFile, excludedColumns);
    }
    finally
    {
      reader.close();
    }
  }
  protected boolean selectEmbodied(IRecord theRec) throws Exception
  {
    String sql = getInfo(theRec).getSelect();
    currentSql = theRec.getClass().getSimpleName() + ".select";
    DbCommand cmd = prepareNoCheck(sql, 1);
    return cmd.selectFirst(theRec, getParameters(theRec, SQLCommand.Select));
  }
  public void selectExcel(String sql,IRow row, File toFile,Set<String>excludedColumns, Object... parameters) throws Exception
  {
    sql = checkSql(sql);
    PreparedStatement ps = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    ps.setFetchSize(_fetchSize);
    IReader reader = selectReader(sql,parameters);
    try
    {
      reader.toExcel(row, toFile, excludedColumns);
    }
    finally
    {
      reader.close();
    }
  }
  /**
   * Loads the values from the first row found to the passed IRow implementer,
   * returns whether a row was indeed found. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?" ; <br>
   * boolean res = db.selectFirst(sql,theRow,74,"fred");
   */
  public boolean selectFirst(String sql, IRow row, Object... parameters) throws Exception
  {
    return prepareCmd(sql).selectFirst(row, parameters);
  }
  /** issues a select while bypassing record cache */
  public boolean selectFresh(IRecord rec, Object... keys) throws Exception
  {
    setKey(rec, keys);
    _recordCache.remove(rec.getCacheKey(this));
    return selectEmbodied(rec);
  }
  public String selectJson(String sql, IRow r, Object... parameters) throws Exception
  {
    return selectReader(sql, parameters).toJson(r);
  }
  public String selectJson(String sql, Object... parameters) throws Exception
  {
    return selectJson(sql, null, parameters);
  }
  /**
   * Returns a List of instances of the class passed for the statement passed
   * with the parameters passed. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?"; <br>
   * CustRec[] res = db.selectRows(sql,CustRec.class,74,"fred");
   */
  public <E extends IRow> List<E> selectList(String sql, Class<E> c, Object... parameters) throws Exception
  {
    return prepareCmd(sql).selectList(c, parameters);
  }
  /**
   * Returns a List of Lists of instances of the classes passed for the statements passed
   * with the parameters passed. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?"; <br>
   * CustRec[] res = db.selectRows(sql,CustRec.class,74,"fred");
   */
  protected List<List<IRow>> selectLists(List<String>sqlCmds,List<Class<? extends IRow>> templateClasses, Object... parameters) throws Exception
  {
    String semiColon="";
    StringBuilder sql = new StringBuilder();
    for (String cmd : sqlCmds)
    {
      sql.append(semiColon).append(checkSql(cmd));
      semiColon=";";
    }
    return selectLists(sql.toString(),templateClasses, parameters);
  }
  /**
   * Returns a List of Lists of instances of the classes passed for the statement passed
   * with the parameters passed. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?"; <br>
   * CustRec[] res = db.selectRows(sql,CustRec.class,74,"fred");
   */
  public List<List<IRow>> selectLists(String sql, List<Class<? extends IRow>> templateClasses, Object... parameters) throws Exception
  {
    return prepareCmd(sql).selectLists(templateClasses, parameters);
  }
  public double selectMax(String table, String field, String where, Object... parms) throws Exception
  {
    String sql = "select max(" + field + ") from " + table + " where " + where;
    return selectScalar(sql, 0.0, parms);
  }
  /**
   * Returns an array of strings representing the possible values for a MySQL
   * enum column.
   */
  public String[] selectMysqlEnumColumnValues(String table,String column) throws Exception
  {
    String expression = selectScalar("SELECT SUBSTRING(COLUMN_TYPE,5) "
        + " FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=schema()"
        + " AND TABLE_NAME=? and COLUMN_NAME=?","",table,column);
    if (expression.length()<5)
    {//expected form is ('Value1','Value2',...)
      return new String[0];
    }
    return StringKit.split(StringKit.replace(expression,new String[]{"(",")","'"},new String[]{"","",""}));
  }
  /**
   * Returns an array of NameRow instances for the statement passed with the
   * parameters passed. This is handiest for select boxes. <br>
   * eg. <br>
   * String sql =
   * "select f2 as RowNumber,f3 as RowName from tTbl where parm1=? and parm2=?"
   * ; <br>
   * NameRow[] res = db.selectNameRows(sql,74,"fred");
   */
  public NameRow[] selectNameRows(String sql, Object... parameters) throws Exception
  {
    return selectRows(sql, NameRow.class, parameters);
  }
  /**
   * Returns a DataPage of instances of the class passed for the statement
   * passed with the parameters passed. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?"; <br>
   * CustRec[] res = db.selectRows(sql,CustRec.class,74,"fred");
   */
  public <E extends IRow> DataPage<E> selectPage(String sql, int offset, int pageSize, Class<E> c, Object... parameters) throws Exception
  {
    return selectPage(sql, offset, pageSize, -1, c, parameters);
  }
  /**
   * Returns a DataPage of instances of the class passed for the statement
   * passed with the parameters passed. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?"; <br>
   * CustRec[] res = db.selectRows(sql,CustRec.class,74,"fred");
   */
  public <E extends IRow> DataPage<E> selectPage(String sqlIn, int offset, int pageSize, int preCount, Class<E> c, Object... parameters)
      throws Exception
  {
    int pageOffset = preCount < 0 ? offset : Math.max(0, Math.min(offset, preCount));
    DataPage<E> pg = new DataPage<E>(pageOffset,pageSize,preCount);
    pg.select(this,checkSql(sqlIn).trim(),c,parameters);
    return pg;
  }
  /**
   * Returns an array of PairRow instances for the statement passed with the
   * parameters passed. This is handiest for select boxes. <br>
   * eg. <br>
   * String sql =
   * "select f2 as o1,f3 as o2 from tTbl where parm1=? and parm2=?"; <br>
   * PairRow[] res = db.selectPairRows(sql,74,"fred");
   */
  public List<PairRow> selectPairRows(String sql, Object... parameters) throws Exception
  {
    return selectList(sql, PairRow.class, parameters);
  }
  /**
   * Returns an IReader for the statement passed with the parameters passed. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?"; <br>
   * IReader res = db.selectReader(sql,74,"fred"); <br>
   * MyRec theRec = new MyRec(); <br>
   * while (res.next(theRec)) <br>
   * doSomethingWith(theRec); <br>
   * res.close();
   */
  public IReader selectReader(String sqlIn, Object... parameters) throws Exception
  {// for readers, we need to support bidirectional navigation
    String sql = checkSql(sqlIn);
    PreparedStatement ps = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    ps.setFetchSize(_fetchSize);
    return new DbCommand(this, ps, sql,1).selectReader(parameters);
  }
  /**
   * Returns an array of instances of the class passed for the statement
   * passed with the parameters passed. <br>
   * eg. <br>
   * String sql = "select f1,f2,f3 from tTbl where parm1=? and parm2=?"; <br>
   * CustRec[] res = db.selectRows(sql,CustRec.class,74,"fred");
   */
  public <E extends IRow> E[] selectRows(String sql, Class<E> c, Object... parameters) throws Exception
  {
    return prepareCmd(sql).selectRows(c, parameters);
  }
  public ResultSet selectRs(String sql, Object... parameters) throws Exception
  {
    PreparedStatement ps = _con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    ps.setFetchSize(_fetchSize);
    DbCommand c = new DbCommand(this, ps, sql,1);
    c.setParameters(parameters);
    return c.getResultSet();
  }
  /**
   * Returns the first column value of the first row selected, coerced to the
   * type of the default value passed. If no rows are found, the default value
   * is returned. <br>
   * eg. <br>
   * String sql = "select sum(f1) from tTbl where parm1=? and parm2=?"; <br>
   * int res = db.selectScalar(sql,0,74,"fred");
   */
  public <T> T selectScalar(String sql, T dft, Object... parameters) throws Exception
  {
    Object o = prepareCmd(sql).selectScalar(parameters);
    if (o == null)
      return dft;
    return TypeKit.convert(o, dft);
  }
  /**
   * Returns a ScalarPage of selected values from selectScalarArray
   */
  @SuppressWarnings("unchecked")
  public <A> A[] selectScalarArray(String sql, Class<A> c, Object... parameters) throws Exception
  {
    List<A> a = selectScalarList(sql, c, parameters);
    A[] b = (A[]) java.lang.reflect.Array.newInstance(c, a.size());
    a.toArray(b);
    return b;
  }
  @SuppressWarnings("unchecked")
  public <A> List<A> selectScalarList(String sql, Class<A> c, Object... parameters) throws Exception
  {
    List<Object> a = new ArrayList<Object>();
    DbReader rdr = prepareCmd(sql).getReader(parameters);
    try
    {
      while (rdr.next())
      {
        a.add(TypeKit.convert(rdr.getObject(1), c));
      }
      return (List<A>) a;
    }
    finally
    {
      rdr.close();
    }
  }
  @SuppressWarnings("unchecked")
  public <A> ScalarPage selectScalarPage(String sql, Class<A> c, int startRow, int rowCount, Object... parameters) throws Exception
  {
    List<Object> a = new ArrayList<Object>();
    DbReader rdr = prepareCmd(sql).getReader(parameters);
    int totalRows = rdr.getCount();
    try
    {
      if (startRow <= 1 || rdr.absolute(startRow - 1))
      {
        while (a.size() < rowCount && rdr.next())
        {
          a.add(TypeKit.convert(rdr.getObject(1), c));
        }
      }
      A[] b = (A[]) java.lang.reflect.Array.newInstance(c, a.size());
      a.toArray(b);
      return new ScalarPage(b, startRow, totalRows);
    }
    finally
    {
      rdr.close();

    }
  }
  @SuppressWarnings("unchecked")
  public <A> Set<A> selectSet(String sql, Class<A> c, Object... parameters) throws Exception
  {
    Set<Object> a = new TreeSet<Object>();
    DbReader rdr = prepareCmd(sql).getReader(parameters);
    try
    {
      while (rdr.next())
      {
        a.add(TypeKit.convert(rdr.getObject(1), c));
      }
      return (Set<A>) a;
    }
    finally
    {
      rdr.close();
    }
  }
  public void setColumnScale(String table, String col, int decs) throws Exception
  {
    getColDef(table, col).setScale(decs);
  }

  public void setFetchSize(int fetchSize)
  {
    _fetchSize = fetchSize;
  }
  /**
   * Places the passed key values on the passed IRecord implementer.
   */
  public void setKey(IRecord theRec, Object... keys) throws Exception
  {
    if (keys != null && keys.length > 0)
    {
      String[] kca = getKey(theRec);
      if (kca.length != keys.length)
      {
        throw new Exception("key values passed not suitable");
      }
      for (int i = 0; i < keys.length; i++)
      {
        int iFld = theRec.getColumnIndex(kca[i]);
        if (iFld < 0)
        {
          throw new Exception("key column " + kca[i] + " not on " + theRec.getClass().getName());
        }
        theRec.setValue(iFld, keys[i]);
      }
    }
  }
  /**
   * Sets the language for this instance.
   */
  @Override
  public void setLanguage(Language lang)
  {
    language = lang;
  }
  public void setOpener(String opener)
  {
    _opener = opener;
  }
  public void setStatementTiming(boolean on)
  {
    this.statsOn = on;
  }
  public void setUpdateListener(DatabaseUpdateListener ul)
  {
    _updateListener = ul;
  }
  public boolean tableExists(String tblName)
  {
    try
    {
      return getTableDef(tblName) != null;
    }
    catch (Exception e)
    {
      return false;
    }
  }
  @Override
  public String toString()
  {
    return _con==null ? "[closed]" : _con.toString();
  }
  /**
   * Updates the record based on it's embodied key, returns whether the
   * update worked.
   */
  public boolean update(IRecord rec) throws Exception
  {
    if (!rec.isDirty())
    {
      return true;
    }
    rec.beforeUpdate(this);
    currentSql = rec.getClass().getSimpleName() + ".update";
    try
    {
      Object[] parms = getParameters(rec, SQLCommand.Update);
      DbCommand cmd = prepareNoCheck(getInfo(rec).getUpdate(), 1);
      if (0 < cmd.execute(parms))
      {
        afterUpdate(rec);
        return true;
      }
    }
    catch (Exception e)
    {
      Exception enhanced = new Exception(e.getMessage() + "\n\t" + rec.getClass().getSimpleName() + " " + rec.toString());
      enhanced.setStackTrace(e.getStackTrace());
      throw enhanced;
    }
    return false;
  }
  public void updateResequence(String sql, Class<? extends IRecord> recClass, String seqFldName, Object... parms) throws Exception
  {
    IReader rdr = null;
    try
    {
      int seq = 10;
      rdr = selectReader(sql, parms);
      IRecord rec = recClass.newInstance();
      Field f = Reflector.getUpdatablePublicInstanceField(rec, seqFldName);
      while (rdr.next(rec))
      {
        Reflector.setPublicInstanceValue(rec, f, seq);
        seq += 100;
        update(rec);
      }
    }
    finally
    {
      if (rdr != null)
        rdr.close();
    }
  }
  /**
   * Updates the record if it exists, else inserts it.
   */
  public boolean write(IRecord rec) throws Exception
  {
    if (exists(rec))
    {
      return update(rec);
    }
    return insert(rec);
  }
  /**
   * xlate converts a string to local form
   */
  public String xlate(String key) throws Exception
  {
    return key;
  }
  @Override
  public String xlate(String string, Object... pa) throws Exception
  {
    return StringKit.format(string, pa);
  }
  /**
   * Returns key translated UNLESS value is same as key, then returns ""
   */
  @Override
  public String xlateIfChanged(String key) throws Exception
  {
    String value = xlate(key);
    return key.equalsIgnoreCase(value) ? "" : value;
  }
  /**
   * xlate converts a string to local form
   */
  @Override
  public String xlateInit(String key,String initVal) throws Exception
  {
    return key;
  }
  public enum DbDriver {
    MsSQL, MySQL, ODBC, Oracle, PostGres, Unknown
  }
}
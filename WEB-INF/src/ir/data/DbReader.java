package ir.data;

import ir.util.Coerce;
import ir.util.DateKit;
import ir.util.FileKit;
import ir.util.IndyMath;
import ir.util.JDate;
import ir.util.JDateTime;
import ir.util.Reflector;
import ir.util.StringKit;
import ir.util.TypeKit;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
class DbReader implements IReader
{
  private static byte[] _ba = new byte[] {};
  private DbCommand _cmd = null;
  private List<DbReaderExchanger> _exchange = null;
  private ResultSetMetaData _md = null;
  private Class<? extends IRow> _rowClass = null;
  private int _rowCount = -1;
  ResultSet _rs = null;
  private final List<Integer> _unboundColumns = new ArrayList<Integer>();
  public DbReader(DbCommand cmd) throws Exception
  {
    _cmd = cmd;
    _rs = cmd.getResultSet();
    try
    {
      _md = _rs.getMetaData();
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && -1 < e.getMessage().toLowerCase().indexOf("resultset closed"))
      {// hack, sometimes rs gets inexplicably closed..
        return;
      }
      throw (e);
    }
  }
  public DbReader(DbCommand cmd, ResultSet rs) throws Exception
  {
    _cmd = cmd;
    _rs = rs;
    try
    {
      _md = _rs.getMetaData();
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && -1 < e.getMessage().toLowerCase().indexOf("resultset closed"))
      {// hack, sometimes rs gets inexplicably closed..
        return;
      }
      throw (e);
    }
  }
  /*
   * @see com.ir.data.IReader#absolute(int)
   */
  public boolean absolute(int nRow) throws Exception
  {
    try
    {
      return _rs.absolute(nRow);
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && -1 < e.getMessage().toLowerCase().indexOf("resultset closed"))
      {// hack, sometimes rs gets inexplicably closed..
        return false;
      }
      throw (e);
    }
  }
  /*
   * @see com.ir.data.IReader#absolute(int)
   */
  public boolean absolute(int nRow, IRow rowToFill) throws Exception
  {
    try
    {
      if (_rs.absolute(nRow))
      {
        setValues(rowToFill);
        return true;
      }
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && -1 < e.getMessage().toLowerCase().indexOf("resultset closed"))
      {// hack, sometimes rs gets inexplicably closed..
        return false;
      }
      throw (e);
    }
    return false;
  }
  /*
   * @see com.ir.data.IReader#beforeFirst()
   */
  public void beforeFirst() throws Exception
  {
    _rs.beforeFirst();
  }
  /*
   * @see com.ir.data.IReader#close()
   */
  @Override
  public void close()
  {
    try
    {
      _rowClass = null;
      _exchange = null;
      _rs.close();
    }
    catch (Exception e)
    {
    }
  }
  @Override
  public void finalize()
  {
    close();
  }
  /*
   * @see com.ir.data.IReader#findColumn(java.lang.String)
   */
  public int findColumn(String colName) throws Exception
  {
    try
    {
      return _rs.findColumn(colName);
    }
    catch (Exception e)
    {
      return -1;
    }
  }
  /*
   * @see com.ir.data.IReader#first()
   */
  public boolean first(IRow rowToFill) throws Exception
  {
    try
    {
      if (_rs.first())
      {
        setValues(rowToFill);
        return true;
      }
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && -1 < e.getMessage().toLowerCase().indexOf("resultset closed"))
      {// hack, sometimes rs gets inexplicably closed..
        return false;
      }
      throw (e);
    }
    return false;
  }
  @Override
  public boolean getBoolean(int iCol) throws Exception
  {
    return StringKit.True(getString(iCol));
  }
  @Override
  public byte[] getBytes(int iCol) throws Exception
  {
    /*
    int type = _md.getColumnType(iCol);
    if (type == Types.LONGVARBINARY || type==Types.VARBINARY)
    {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      InputStream is = _rs.getBinaryStream(iCol);
      if (is == null)
      {
        return new byte[0];
      }
      try
      {
        final int blockSize=4000;
        byte[] block=new byte[blockSize];
        int bytesRead = is.read(block);
        while (bytesRead>0)
        {
          os.write(block,0,bytesRead);
          bytesRead = is.read(block);
        }
        return os.toByteArray();
      }
      finally
      {
        is.close();
        os.close();
      }
    }
    else if (type == Types.BLOB)
    {
      Blob blob = _rs.getBlob(iCol);
      if (blob == null)
      {
        return new byte[0];
      }
      return blob.getBytes(1L,(int) blob.length());
    }
     */
    return _rs.getBytes(iCol);
  }

  /*
   * @see com.ir.data.IReader#getCount()
   */
  @Override
  public int getCount() throws Exception
  {
    _rowCount = getCountInt();
    if (_rowCount == -1)
    {
      int before = _rs.getRow();
      if (0 == before)
      {
        if (_rs.isBeforeFirst())
          before = -1;
        else if (_rs.isAfterLast())
          before = -2;
      }
      _rs.last();
      _rowCount = _rs.getRow();
      if (before == -2)
        _rs.afterLast();
      else if (before == -1)
        _rs.beforeFirst();
      else
        _rs.absolute(before);
    }
    return _rowCount;
  }
  int getCountInt()
  {
    String slc = _cmd.getSQL().trim().toLowerCase();
    int total = 0;
    String sql = slc;
    Database db = _cmd.getDatabase();
    try
    {
      if (!slc.startsWith("select"))
        throw new Exception("No count available for non-select.");
      if (slc.indexOf("union") == -1)
      {// we have no unions
        if (slc.indexOf(" select ", 8) > -1 || slc.indexOf("(select ", 8) > -1)
        {// we have a subquery
          return -1;
        }
      }
      sql = Database.getCountSQL(getResolvedSQL());
      // check for and split at unions
      int unionAt = slc.indexOf(" union ");
      if (0 > unionAt)
      {
        total = db.selectScalar(sql, 0);
        return total;
      }
      int start = 0;
      while (unionAt > 0)
      {
        String q = Database.getCountSQL(sql.substring(start, unionAt - start));
        total += db.selectScalar(q, 0);
        start = unionAt + 7;
        unionAt = slc.indexOf(" union ", start);
        if (unionAt == -1)
        {
          q = Database.getCountSQL(sql.substring(start));
          total += db.selectScalar(q, 0);
        }
      }
    }
    catch (Exception e)
    {
      System.out.println(getClass().getName() + ".rowCount failed:\n" + sql + "\n: " + e.getMessage());
      return -1;
    }
    return total;
  }
  @Override
  public double getDouble(int iCol) throws Exception
  {
    return Coerce.toDouble(getString(iCol));
  }
  protected List<DbReaderExchanger> getExchanger(IRow theRow) throws Exception
  {
    Class<? extends IRow> c = theRow.getClass();
    if (_exchange == null || !c.equals(_rowClass))
    {
      _rowClass = c;
      String[] fa = theRow.getFields();
      _exchange = new ArrayList<DbReaderExchanger>(fa.length);
      for (int i = 1; i <= _md.getColumnCount(); i++)
      {
        String colName = _md.getColumnName(i);
        int fi = theRow.getColumnIndex(colName);
        if (fi == -1)
        {
          _unboundColumns.add(i);
        }
        else
        {
          _exchange.add(new DbReaderExchanger(Reflector.getUpdatablePublicInstanceField(theRow, fa[fi]), i, _md.getScale(i)));
        }
      }
    }
    return _exchange;
  }
  @Override
  public float getFloat(int iCol) throws Exception
  {
    return StringKit.floatDft(getString(iCol), 0);
  }
  /*
   * @see com.ir.data.IReader#getInt()
   */
  @Override
  public int getInt(int iCol) throws Exception
  {
    return StringKit.intDft(getString(iCol), 0);
  }
  /*
   * @see com.ir.data.IReader#getJDate()
   */
  @Override
  public JDate getJDate(int iCol) throws Exception
  {
    return new JDate(_rs.getDate(iCol));
  }
  /*
   * @see com.ir.data.IReader#getJDateTime()
   */
  @Override
  public JDateTime getJDateTime(int iCol) throws Exception
  {
    return new JDateTime(_rs.getTimestamp(iCol));
  }
  /*
   * @see com.ir.data.IReader#getLong()
   */
  @Override
  public long getLong(int iCol) throws Exception
  {
    return StringKit.longDft(getString(iCol), 0);
  }
  @Override
  public ResultSetMetaData getMetaData() throws Exception
  {
    if (_md == null)
    {
      _md = _rs.getMetaData();
    }
    return _md;
  }
  /*
   * @see com.ir.data.IReader#getObject()
   */
  @Override
  public Object getObject(int iCol) throws Exception
  {
    Object o = null;
    try
    {
      return _rs.getObject(iCol);
    }
    catch (Exception e)
    {
      if (e instanceof SQLException)
      {
        SQLException se = (SQLException) e;
        if (se.getSQLState() != null && se.getSQLState().equals("S1009"))
          return null;
      }
      try
      {
        Blob b = _rs.getBlob(iCol);
        ObjectInputStream os = new ObjectInputStream(b.getBinaryStream());
        o = os.readObject();
        os.close();
      }
      catch (Exception ee)
      {// fall on through
        throw e;
      }
    }
    return o;
  }
  @Override
  public String getResolvedSQL() throws Exception
  {
    return _cmd == null ? "?" : _cmd.getResolvedSQL();
  }
  /*
   * @see com.ir.data.IReader#getRow()
   */
  @Override
  public int getRow() throws Exception
  {
    return _rs.getRow();
  }
  @Override
  public String getString(int iCol) throws Exception
  {
    return Coerce.toString(_rs.getObject(iCol));
  }
  /*
   * @see com.ir.data.IReader#next()
   */
  public boolean next() throws Exception
  {
    try
    {
      return _rs.next();
    }
    catch (Exception e)
    {
      if (e.getMessage() != null && -1 < e.getMessage().toLowerCase().indexOf("resultset closed"))
      {// hack, sometimes rs gets inexplicably closed..
        return false;
      }
      throw (e);
    }
  }
  /*
   * @see com.ir.data.IReader#next(com.ir.data.IRow)
   */
  @Override
  public boolean next(IRow rowToFill) throws Exception
  {
    boolean b = next();
    if (b && rowToFill != null)
    {
      setValues(rowToFill);
    }
    return b;
  }
  @Override
  public void setCount(int c)
  {
    _rowCount = c;
  }
  /*
   * @see com.ir.data.IReader#setValues(com.ir.data.IRow)
   */
  @Override
  public void setValues(IRow rowToFill) throws Exception
  {
    DbReaderExchanger exe = null;
    List<DbReaderExchanger> exa = getExchanger(rowToFill);
    Object bareValue = null;
    Object coercedValue = null;
    try
    {
      for (DbReaderExchanger ex : exa)
      {
        exe = ex;
        if (ex.field.getType().equals(_ba.getClass()))
        {// hack for raw byte[] reading
          ex.field.set(rowToFill, getBytes(ex.columnOrdinal));
          bareValue = "byte[]";
        }
        else
        {
          bareValue = getObject(ex.columnOrdinal);
          coercedValue = TypeKit.convert(bareValue, ex.field.getType());
          if (coercedValue instanceof BigDecimal)
          {
            if (ex.columnScale > 0 && ex.columnScale <= 9)
            {
              coercedValue = ((BigDecimal) coercedValue).setScale(ex.columnScale, IndyMath.RoundMode);
            }
          }
          ex.field.set(rowToFill, coercedValue);
        }
      }
    }
    catch (Exception e)
    {
      String cn = bareValue == null ? ": " : " (" + bareValue.getClass().getName() + "): ";
      Exception et = new Exception("DbReader.setValues " + rowToFill.getClass().getSimpleName() + "." + exe.field.getName() + "="
          + bareValue + cn + e.getMessage());
      et.setStackTrace(e.getStackTrace());
      throw et;
    }
    for (int rsColIndex : _unboundColumns)
    {
      int ct = _md.getColumnType(rsColIndex);
      if (ct == java.sql.Types.BLOB || ct == java.sql.Types.LONGVARBINARY || ct == java.sql.Types.VARBINARY)
      {// hack for raw byte[] reading
        rowToFill.setTemp(_md.getColumnName(rsColIndex), getBytes(rsColIndex));
      }
      else
      {
        rowToFill.setTemp(_md.getColumnName(rsColIndex), getObject(rsColIndex));
      }
    }
    rowToFill.afterRead(_cmd.getDatabase());
    if (rowToFill instanceof IRecord)
    {
      ((IRecord) rowToFill).makeClean();
    }
  }
  private void toCsv(File f, Set<String> excludes) throws Exception
  {
    Writer wtr = null;
    try
    {
      getMetaData();
      wtr = new FileWriter(f);
      //wtr.write(FileKit.Utf8Marker);
      String comma = "";
      for (int i = 1; i <= _md.getColumnCount(); i++)
      {
        String cn = _md.getColumnName(i);
        if (excludes != null && excludes.contains(cn))
        {
          continue;
        }
        wtr.write(comma);
        wtr.write('"' + cn + '"');
        comma = ",";
      }
      wtr.write("\r\n");
      while (_rs.next())
      {
        comma = "";
        for (int i = 1; i <= _md.getColumnCount(); i++)
        {
          String cn = _md.getColumnName(i);
          if (excludes != null && excludes.contains(cn))
          {
            continue;
          }
          wtr.write(comma);
          String ccn = _md.getColumnClassName(i);
          if (ccn.endsWith("String"))
          {
            wtr.write('"');
          }
          wtr.write(Coerce.toString(getObject(i)).replace('"', '\''));
          if (ccn.endsWith("String"))
          {
            wtr.write('"');
          }
          comma = ",";
        }
        wtr.write("\r\n");
      }
    }
    finally
    {
      if (wtr != null)
      {
        wtr.close();
      }
      this.close();
    }
  }
  /**
   * Fills a file with all rows in the result set like
   * 
   * <pre>
   * "y",1,2
   * </pre>
   * 
   * Note that this operation will close the reader.
   */
  @Override
  public void toCsv(IRow row, File f, Set<String> excludes) throws Exception
  {
    if (row == null)
    {
      toCsv(f, excludes);
      return;
    }
    Writer wtr = null;
    try
    {
      wtr = new FileWriter(f);
      List<Field> fa = Reflector.getUpdatablePublicInstanceFields(row);
      boolean first = true;
      for (Field fld : fa)
      {
        if (excludes != null && excludes.contains(fld.getName()))
        {
          continue;
        }
        if (first)
        {
          first = false;
        }
        else
        {
          wtr.write(",");
        }
        wtr.write('"' + fld.getName() + '"');
      }
      wtr.write("\r\n");
      while (next(row))
      {
        first = true;
        for (Field fld : fa)
        {
          if (excludes != null && excludes.contains(fld.getName()))
          {
            continue;
          }
          if (first)
          {
            first = false;
          }
          else
          {
            wtr.write(",");
          }
          if (fld.getType().equals(String.class))
          {
            wtr.write('"');
          }
          wtr.write(Coerce.toString(fld.get(row)).replace('"', '\''));
          if (fld.getType().equals(String.class))
          {
            wtr.write('"');
          }
        }
        wtr.write("\r\n");
      }
    }
    finally
    {
      if (wtr != null)
      {
        wtr.close();
      }
      this.close();
    }
  }
  @Override
  public void toExcel(IRow row, File f, Set<String> excludes) throws Exception
  {

  }
  @Override
  public String toJson(IRow row) throws Exception
  {
    try
    {
      String comma = "";
      StringBuilder b = new StringBuilder("[");
      while (next(row))
      {
        b.append(comma);
        comma = ",";
        if (row != null)
        {
          b.append(row.toString());
        }
        else
        {
          b.append("{");
          String cc = "";
          for (int i = 1; i <= _md.getColumnCount(); i++)
          {
            String v = getString(i);
            int t = _md.getColumnType(i);
            if (ColDef.isNumeric(t))
            {
            }
            else if (t == Types.BIT || t == Types.BOOLEAN)
            {
              v = "" + (Coerce.toBool(v) ? 1 : 0);
            }
            else if (t == Types.DATE || t == Types.TIMESTAMP)
            {
              if (v.equals(""))
                v = "'0000-00-00 00:00:00'";
              else
                v = "'" + DateKit.ymdhms(DateKit.parse(v)) + "'";
            }
            else
            {
              v = StringKit.jsq(v);
            }
            b.append(cc).append(_md.getColumnName(i)).append(":").append(v);
            cc = ",";
          }
          b.append("}");
        }
      }
      return b.append("]").toString();
    }
    finally
    {
      close();
    }
  }
  @Override
  @SuppressWarnings("unchecked")
  public <A extends IRow> List<A> toList(Class<A> c) throws Exception
  {
    List<A> a = new ArrayList<A>();
    IRow newRow = c.newInstance();
    try
    {
      while (next(newRow))
      {
        a.add((A) newRow);
        newRow = c.newInstance();
      }
      return a;
    }
    finally
    {
      close();
    }
  }
  @Override
  public List<List<Object>> toMatrix() throws Exception
  {
    int cc = _md.getColumnCount();
    List<List<Object>> a = new ArrayList<List<Object>>();
    while (next())
    {
      List<Object> rd = new ArrayList<Object>(cc);
      for (int i = 1; i <= cc; i++)
      {
        Object o = _rs.getObject(i);
        rd.add(o);
      }
      a.add(rd);
    }
    close();
    return a;
  }
}

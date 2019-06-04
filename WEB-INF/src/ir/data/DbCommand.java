package ir.data;

import ir.util.JDate;
import ir.util.JDateTime;
import ir.util.StringKit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * See ICommand
 */
public class DbCommand implements ICommand,IRecCommand
{
  private Database db = null;
  private final List<Object> parameters = new ArrayList<Object>();
  private PreparedStatement preparedStatement = null;
  private String sql = null;
  private String commandTimingName=null;
  private final List<IRecord>records=new ArrayList<IRecord>();
  private final Map<String,Integer>recordIndicesByKey=new HashMap<String,Integer>();
  private String[]updateColumns=null;
  private int batchCount=0;
  private int batchSize=-1;
  private char recordOp=' ';
  private final static char recordOpDelete='d';
  private final static char recordOpUpdate='u';
  private final static char recordOpInsert='i';
  private String openerStackTrace = "[not tracked]";
  //
  public DbCommand(Database db, PreparedStatement ps, String sql,int batchSize) throws Exception
  {
    this.db = db;
    this.preparedStatement = ps;
    this.sql = sql;
    this.commandTimingName = batchSize + "-" + sql;
    this.batchSize = batchSize;
    Exception e = new Exception();
    e.fillInStackTrace();
    StackTraceElement[] tee = e.getStackTrace();
    for (StackTraceElement te : tee)
    {
      String cn = te.getClassName();
      if (! cn.endsWith("DbCommand") && ! cn.endsWith("Command"))
      {
        this.openerStackTrace = te.getClassName() + "." + te.getMethodName() + ":" + te.getLineNumber();
        break;
      }
    }
  }

  @Override
  public void addBatch(IRecord rec) throws Exception
  {
    if (batchCount>0)
    {
      throw new Exception("Cannot mix addBatch(IRecord) and addBatch(...)");
    }
    String slc = sql.toLowerCase().trim();
    recordOp = slc.charAt(0);
    if (slc.startsWith("insert "))
    {
      if (records.size()==0)
      {
        commandTimingName = rec.getClass().getSimpleName() + ".insert";
        if (slc.indexOf(" ignore ")>-1)
        {
          commandTimingName += "Ignore";
        }
        else if (slc.indexOf(" on duplicate")>-1)
        {
          commandTimingName += "Update";
        }
        commandTimingName += "Batch-" + batchSize;
      }
      records.add(rec);
      addBatchInternal(db.getInsertParameters(rec,updateColumns).toArray());
    }
    else if (slc.startsWith("update "))
    {
      if (records.size()==0)
      {
        commandTimingName = rec.getClass().getSimpleName() + ".updateBatch-" + batchSize;
      }
      if (! rec.isDirty())
      {
        return;
      }
      addOrReplaceRecord(rec);
      addBatchInternal(db.getParameters(rec, SQLCommand.Update));
    }
    else if (slc.startsWith("delete "))
    {
      if (records.size()==0)
      {
        commandTimingName = rec.getClass().getSimpleName() + ".deleteBatch-" + batchSize;
      }
      addOrReplaceRecord(rec);
      addBatchInternal(db.getParameters(rec, SQLCommand.Delete));
    }
  }
  @Override
  public void addBatch(Object... parameters) throws Exception
  {
    if (records.size()>0)
    {
      throw new Exception("Cannot mix addBatch(IRecord) and addBatch(...)");
    }
    addBatchInternal(parameters);
  }
  private void addBatchInternal(Object... parameters) throws Exception
  {
    setParameters(parameters);
    if (records.size() == 0)
    {
      batchCount++;
    }
    if (batchCount + records.size() == batchSize)
    {
      if (batchCount + records.size() == 1)
      {
        executeInternal();
      }
      else
      {
        executeBatch();
      }
    }
    else
    {
      preparedStatement.addBatch();
    }
  }
  /** if we add the same record twice, just keep the latest one in the batch */
  private void addOrReplaceRecord(IRecord rec) throws Exception
  {
    String recordKey = rec.getCacheKey(db);
    Integer index = recordIndicesByKey.get(recordKey);
    if (index == null)
    {
      records.add(rec);
      recordIndicesByKey.put(recordKey,records.size() - 1);
    }
    else
    {
      records.set(index.intValue(),rec);
    }
  }
  private void addParm(int idx, Object val)
  {
    while (parameters.size() < idx)
    {
      parameters.add(null);
    }
    parameters.set(idx - 1, val);
  }
  private void afterExecuteRecordBatch(int[] results) throws Exception
  {
    switch (recordOp)
    {
      case recordOpInsert:
      {
        int autoIdx = db.getAutoIndex(records.get(0));
        if (autoIdx > -1)
        {
          List<Integer>keys = getGeneratedKeys();
          for (int i=0;i < keys.size() && i < records.size();i++)
          {
            int result = results.length > i ? 1 : results[i];
            IRecord rec = records.get(i);
            if (result>0)
            {
              db.afterInsert(this,rec,autoIdx, keys.get(i));
            }
            else
            {
              StringKit.println(commandTimingName + " result " + result + " for " + rec);
            }
          }
        }
      }
      break;
      case recordOpUpdate:
        for (IRecord rec : records)
        {
          db.afterUpdate(rec);
        }
        break;
      case recordOpDelete:
        for (IRecord rec : records)
        {
          db.afterDelete(rec);
        }
        break;
    }
    records.clear();
  }
  private void beforeExecuteRecordBatch() throws Exception
  {
    for (IRecord rec : records)
    {
      switch (recordOp)
      {
        case recordOpInsert:
          rec.beforeInsert(db);
          break;
        case recordOpUpdate:
          rec.beforeUpdate(db);
          break;
        case recordOpDelete:
          rec.beforeDelete(db);
          break;
      }
    }
  }
  /*
   * @see com.ir.data.ICommand#executeUpdate()
   */
  public int execute(Object... parameters) throws Exception
  {
    setParameters(parameters);
    return executeInternal();
  }
  /*
   * @see com.ir.data.ICommand#executeBatch()
   */
  @Override
  public int[] executeBatch() throws Exception
  {
    if (batchCount==0 && records.size()==0)
    {
      return new int[]{};
    }
    if (records.size()>0)
    {
      beforeExecuteRecordBatch();
    }
    db.beginExecution(commandTimingName);
    int[] results = preparedStatement.executeBatch();
    db.completeExecution();
    preparedStatement.clearParameters();
    preparedStatement.clearWarnings();
    batchCount=0;
    if (records.size()>0)
    {
      afterExecuteRecordBatch(results);
    }
    return results;
  }
  /*
   * @see com.ir.data.ICommand#executeUpdate()
   */
  private int executeInternal() throws Exception
  {
    db.beginExecution();
    try
    {
      return preparedStatement.executeUpdate();
    }
    catch (Exception e)
    {
      if (batchSize == 1 && e.getMessage() != null)
      {
        StringBuilder b = new StringBuilder(e.getMessage());
        if (e.getCause() != null)
        {
          b.append("\n cause: ").append(e.getCause().getMessage());
        }
        b.append("\n\t").append(Database.resolveSql(sql, parameters.toArray()));
        Exception ee = new Exception(b.toString());
        ee.setStackTrace(e.getStackTrace());
        throw ee;
      }
      throw e;
    }
    finally
    {
      batchCount=0;
      records.clear();
      db.completeExecution();
    }
  }
  @Override
  protected void finalize() throws Throwable
  {
    if (records.size() + batchCount>0)
    {
      StringKit.println("DbCommand finalize with " + batchCount + " active batches,"
          + records.size() + " active records, opened from  " + openerStackTrace
          + "\n\t->" + sql);
    }
  }
  Database getDatabase()
  {
    return db;
  }
  public int getGeneratedKey() throws Exception
  {
    List<Integer> keys= getGeneratedKeys();
    if (keys.size() > 0)
    {
      return keys.get(0).intValue();
    }
    return 0;
  }
  @Override
  public List<Integer> getGeneratedKeys() throws Exception
  {
    List<Integer> lst = new ArrayList<Integer>();
    ResultSet rs = preparedStatement.getGeneratedKeys();
    try
    {
      while (rs.next())
      {
        lst.add(rs.getInt(1));
      }
      return lst;
    }
    finally
    {
      rs.close();
    }
  }

  DbReader getReader(Object... parameters) throws Exception
  {
    setParameters(parameters);
    db.beginExecution();
    DbReader r = new DbReader(this);
    db.completeExecution();
    return r;
  }

  public String getResolvedSQL() throws Exception
  {
    return Database.resolveSql(sql, parameters.toArray());
  }

  ResultSet getResultSet() throws Exception
  {
    if (this.sql.toLowerCase().trim().startsWith("call "))
    {
      preparedStatement.execute();
      return preparedStatement.getResultSet();
    }
    return preparedStatement.executeQuery();
  }

  @Override
  public String getSource()
  {
    return sql;
  }

  String getSQL()
  {
    return sql;
  }
  public PreparedStatement getStatement()
  {
    return preparedStatement;
  }

  /**
   * Returns the first column value of the first row selected, or null if no
   * rows are found.
   */
  public byte[] selectBytes(Object... parameters) throws Exception
  {
    DbReader rdr = getReader(parameters);
    try
    {
      if (rdr.next())
        return rdr.getBytes(1);
      return new byte[0];
    }
    finally
    {
      rdr.close();
    }
  }

  /*
   * @see com.ir.data.ICommand#selectFirst(com.ir.data.IRow,Object...)
   */
  public boolean selectFirst(IRow row, Object... parameters) throws Exception
  {
    DbReader rdr = getReader(parameters);
    try
    {
      return rdr.next(row);
    }
    finally
    {
      rdr.close();
    }
  }

  public <A extends IRow> List<A> selectList(Class<A> c, Object... parameters) throws Exception
  {
    return getReader(parameters).toList(c);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public List<List<IRow>> selectLists(List<Class<? extends IRow>> templateClasses, Object... parameters) throws Exception
  {
    List<List<IRow>> result = new ArrayList<List<IRow>>();
    setParameters(parameters);
    db.beginExecution(sql);
    DbReader reader = null;
    if (preparedStatement.execute())
    {
      for (int i = 0; i < templateClasses.size(); i++)
      {
        Class template = templateClasses.get(i);
        if (i > 0)
        {
          preparedStatement.getMoreResults();
        }
        try
        {
          reader = new DbReader(this, preparedStatement.getResultSet());
          result.add(reader.toList(template));
        }
        catch (Exception exc)
        {
          db.completeExecution();
          String text = (exc.getMessage() == null ? exc.getClass().getSimpleName() : exc.getMessage())
              + "\ntemplate class: " + template.getName()
              + "\nsql: " + this.sql;
          Exception enh = new Exception(text);
          enh.setStackTrace(exc.getStackTrace());
          throw enh;
        }
        finally {
        	if(reader!=null)
        	{
        		reader.close();
        	}
        	
        }
      }
    }
    db.completeExecution();
    return result;
  }
  /*
   * @see com.ir.data.ICommand#executeQuery()
   */
  public IReader selectReader(Object... parameters) throws Exception
  {
    return getReader(parameters);
  }
  @SuppressWarnings("unchecked")
  public <A extends IRow> A[] selectRows(Class<A> c, Object... parameters) throws Exception
  {
    List<A> a = selectList(c, parameters);
    A[] b = (A[]) java.lang.reflect.Array.newInstance(c, a.size());
    a.toArray(b);
    return b;
  }
  /**
   * Returns the first column value of the first row selected, or null if no
   * rows are found.
   */
  public Object selectScalar(Object... parameters) throws Exception
  {
    Object o = null;
    DbReader rdr = getReader(parameters);
    try
    {
      if (rdr.next())
        o = rdr.getObject(1);
      return o;
    }
    finally
    {
      rdr.close();
    }
  }
  public void setByte(int iParm, byte val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setByte(iParm, val);
  }

  public void setDate(int iParm, JDate val) throws Exception
  {
    addParm(iParm, val);
    if (val.isZero())
    {
      preparedStatement.setNull(iParm, java.sql.Types.DATE);
    }
    else
    {
      preparedStatement.setDate(iParm, val.getSQLDate());
    }
  }

  public void setDateTime(int iParm, JDateTime val) throws Exception
  {
    addParm(iParm, val);
    if (val.isZero())
    {
      preparedStatement.setNull(iParm, java.sql.Types.TIMESTAMP);
    }
    else
    {
      java.sql.Timestamp ts = new java.sql.Timestamp(val.getTime());
      preparedStatement.setTimestamp(iParm, ts);
    }
  }

  public void setDouble(int iParm, double val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setDouble(iParm, val);
  }
  public void setFloat(int iParm, float val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setFloat(iParm, val);
  }

  public void setInt(int iParm, int val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setInt(iParm, val);
  }

  public void setLong(int iParm, long val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setLong(iParm, val);
  }

  public void setMaxRows(int max) throws Exception
  {
    preparedStatement.setMaxRows(max);
  }

  public void setNull(int iParm, int sqlType) throws Exception
  {
    addParm(iParm, null);
    preparedStatement.setNull(iParm, sqlType);
  }

  public void setObject(int iParm, Object val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setObject(iParm, val);
  }

  protected void setParameters(Object... parameters) throws Exception
  {
    Object o = null;
    int i = 0;
    if (parameters == null || parameters.length == 0)
    {
      return;
    }
    try
    {
      for (i = 0; i < parameters.length; i++)
      {
        o = parameters[i];
        int pn = i + 1;
        if (o == null)
        {
          setObject(pn, o);
        }
        else if (o instanceof Character)
        {
          setString(pn, "" + o);
        }
        else if (o instanceof String)
        {
          setString(pn, (String) o);
        }
        else if (o instanceof Integer)
        {
          setInt(pn, (Integer) o);
        }
        else if (o instanceof Byte)
        {
          setByte(pn, (Byte) o);
        }
        else if (o instanceof Short)
        {
          setShort(pn, (Short) o);
        }
        else if (o instanceof Long)
        {
          setLong(pn, (Long) o);
        }
        else if (o instanceof Float)
        {
          setFloat(pn, (Float) o);
        }
        else if (o instanceof Double)
        {
          setDouble(pn, (Double) o);
        }
        else if (o instanceof JDate)
        {
          setDate(pn, (JDate) o);
        }
        else if (o instanceof JDateTime)
        {
          setDateTime(pn, (JDateTime) o);
        }
        else if (o instanceof byte[])
        {
          preparedStatement.setBytes(pn, (byte[]) o);
        }
        else
        {
          setObject(pn, o);
        }
      }
    }
    catch (Exception e)
    {
      throw (e);// only here as breakpoint parking spot
    }
  }

  public void setShort(int iParm, short val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setShort(iParm, val);
  }

  public void setString(int iParm, String val) throws Exception
  {
    addParm(iParm, val);
    preparedStatement.setString(iParm, val);
  }

  public void setTimingName(String n)
  {
    commandTimingName = n;
  }
  public void setUpdateColumns(String[] updateColumns)
  {
    this.updateColumns = updateColumns;
  }
}

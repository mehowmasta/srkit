package ir.data;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
/**
 * This is a wrapper of <code>Connection</code> to be used with
 * <code>IConnectionPool</code>: its feature is that it returns itself to its
 * pool rather than closing. It also maintains a list of prepared statements.
 */
class PooledConnection implements Connection
{
  private Connection _conn;
  private final java.util.Date _created = new java.util.Date();
  private String _db = null;
  private String _driver = null;
  private int _maxStm = -1;
  private String _pwd = null;
  protected Map<String, PreparedStatement> _stm = new HashMap<String, PreparedStatement>(64);
  private String _url = null;
  private String _user = null;
  public static String getPoolKey(String sDriver, String sUrl, String Db, String sUsr, String sPwd)
  {
    return sDriver + ":" + sUrl + Db + ":" + sUsr + ":" + sPwd;
  }
  /**
   * creates a connection and places it within the pool passed
   * 
   * @param ConnPool
   *            the owning pool
   * @param Connection
   *            the related IConnection implementer
   */
  public PooledConnection(String driver, String url, String db, String user, String pwd) throws Exception
  {
    _driver = driver;
    _url = url;
    _db = db;
    _user = user;
    _pwd = pwd;
    Properties p = new Properties();
    p.setProperty("autoDeserialize", "true");
    p.setProperty("user", _user);
    p.setProperty("password", _pwd);
    Class.forName(_driver);
    _conn = DriverManager.getConnection(url + db, p);
    DatabaseMetaData dmd = _conn.getMetaData();
    _maxStm = dmd.getMaxStatements();
  }
  public void abort(Executor executor) throws SQLException
  {
  }
  /**
   * Clears all warnings reported for this <code>Connection</code> object.
   * After a call to this method, the method <code>getWarnings</code> returns
   * null until a new warning is reported for this Connection.
   * 
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public void clearWarnings() throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.clearWarnings();
  }
  /**
   * Returns connection to pool
   * 
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public void close() throws java.sql.SQLException
  {
    try
    {
      ConnPool.put(this);
    }
    catch (Exception e)
    {
      throw new java.sql.SQLException(e.getMessage());
    }
  }
  /**
   * Makes all changes made since the previous commit/rollback permanent and
   * releases any database locks currently held by the Connection. This method
   * should be used only when auto-commit mode has been disabled.
   * 
   * @exception SQLException
   *                if a database access error occurs
   * @see #setAutoCommit
   */
  @Override
  public void commit() throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.commit();
  }

  @Override
  public Array createArrayOf(String arg0, Object[] arg1) throws SQLException
  {
    throw new SQLException("Not implemented.");
  }

  @Override
  public Blob createBlob() throws SQLException
  {
    throw new SQLException("Not implemented.");
  }

  @Override
  public Clob createClob() throws SQLException
  {
    throw new SQLException("Not implemented.");
  }
  @Override
  public NClob createNClob() throws SQLException
  {
    return null;
  }
  @Override
  public SQLXML createSQLXML() throws SQLException
  {
    return null;
  }
  /**
   * Creates a <code>Statement</code> object for sending SQL statements to the
   * database. SQL statements without parameters are normally executed using
   * Statement objects. If the same SQL statement is executed many times, it
   * is more efficient to use a PreparedStatement
   * 
   * JDBC 2.0
   * 
   * Result sets created using the returned Statement will have forward-only
   * type, and read-only concurrency, by default.
   * 
   * @return a new Statement object
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.Statement createStatement() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.createStatement();
    return null;
  }

  /**
   * JDBC 2.0
   * 
   * Creates a <code>Statement</code> object that will generate
   * <code>ResultSet</code> objects with the given type and concurrency. This
   * method is the same as the <code>createStatement</code> method above, but
   * it allows the default result set type and result set concurrency type to
   * be overridden.
   * 
   * @param resultSetType
   *            a result set type; see ResultSet.TYPE_XXX
   * @param resultSetConcurrency
   *            a concurrency type; see ResultSet.CONCUR_XXX
   * @return a new Statement object
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.createStatement(resultSetType, resultSetConcurrency);
    return null;
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#createStatement(int, int, int)
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
  {
    return _conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
  }
  @Override
  public Struct createStruct(String arg0, Object[] arg1) throws SQLException
  {
    throw new SQLException("Not implemented.");
  }
  /**
   * override of finalize to return to pool
   */
  @Override
  public void finalize() throws Throwable
  {
    close();
  }
  /**
   * Closes underlying connection
   */
  public void fullClose()
  {
    Collection<PreparedStatement> iter = _stm.values();
    for (PreparedStatement stm : iter)
    {
      try
      {
        stm.close();
      }
      catch (Exception e)
      {
      }
    }
    try
    {
      _stm = null;
      _conn.close();
      _conn = null;
    }
    catch (Exception e)
    {
    }
  }

  /**
   * Gets the current auto-commit state.
   * 
   * @return the current state of auto-commit mode
   * @exception SQLException
   *                if a database access error occurs
   * @see #setAutoCommit
   */
  @Override
  public boolean getAutoCommit() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.getAutoCommit();
    return false;
  }

  /**
   * Returns the Connection's current catalog name.
   * 
   * @return the current catalog name or null
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public String getCatalog() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.getCatalog();
    return null;
  }
  @Override
  public Properties getClientInfo() throws SQLException
  {
    throw new SQLException("Not implemented.");
  }
  @Override
  public String getClientInfo(String arg0) throws SQLException
  {
    throw new SQLException("Not implemented.");
  }
  public Date getCreation()
  {
    return _created;
  }
  public String getDbName()
  {
    return _driver + _url + _db;
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#getHoldability()
   */
  @Override
  public int getHoldability() throws SQLException
  {
    return _conn.getHoldability();
  }
  /**
   * Gets the metadata regarding this connection's database. A Connection's
   * database is able to provide information describing its tables, its
   * supported SQL grammar, its stored procedures, the capabilities of this
   * connection, and so on. This information is made available through a
   * DatabaseMetaData object.
   * 
   * @return a DatabaseMetaData object for this Connection
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.DatabaseMetaData getMetaData() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.getMetaData();
    return null;
  }
  public int getNetworkTimeout() throws SQLException
  {
    return 0;
  }
  public String getPoolKey()
  {
    return getPoolKey(_driver, _url, _db, _user, _pwd);
  }
  public String getSchema() throws SQLException
  {
    return null;
  }
  public int getStatementCount()
  {
    return _stm.size();
  }
  public StatementInfo[] getStatements()
  {
    throw new RuntimeException("Not implemented.");
  }

  /**
   * Gets this Connection's current transaction isolation level.
   * 
   * @return the current TRANSACTION_* mode value
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public int getTransactionIsolation() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.getTransactionIsolation();
    return 0;
  }

  /**
   * JDBC 2.0
   * 
   * Gets the type map object associated with this connection. Unless the
   * application has added an entry to the type map, the map returned will be
   * empty.
   * 
   * @return the <code>java.util.Map</code> object associated with this
   *         <code>Connection</code> object
   */
  @Override
  public Map<String, Class<?>> getTypeMap() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.getTypeMap();
    return null;
  }

  /**
   * Returns the first warning reported by calls on this Connection.
   * 
   * <P>
   * <B>Note:</B> Subsequent warnings will be chained to this SQLWarning.
   * 
   * @return the first SQLWarning or null
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.SQLWarning getWarnings() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.getWarnings();
    return null;
  }
  /**
   * Tests to see if a Connection is closed.
   * 
   * @return true if the connection is closed; false if it's still open
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public boolean isClosed() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.isClosed();
    return false;
  }
  /**
   * Tests to see if the connection is in read-only mode.
   * 
   * @return true if connection is read-only and false otherwise
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public boolean isReadOnly() throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.isReadOnly();
    return false;
  }
  @Override
  public boolean isValid(int arg0) throws SQLException
  {
    throw new SQLException("Not implemented.");
  }
  @Override
  public boolean isWrapperFor(Class<?> arg0) throws SQLException
  {
    throw new SQLException("Not implemented.");
  }

  /**
   * Converts the given SQL statement into the system's native SQL grammar. A
   * driver may convert the JDBC sql grammar into its system's native SQL
   * grammar prior to sending it; this method returns the native form of the
   * statement that the driver would have sent.
   * 
   * @param sql
   *            a SQL statement that may contain one or more '?' parameter
   *            placeholders
   * @return the native form of this statement
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public String nativeSQL(String sql) throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.nativeSQL(sql);
    return null;
  }
  /**
   * Creates, prepares and returns a PreparedStatement, or its equivalent from
   * cache
   * 
   * @param String
   *            sql
   * @return PreparedStatement
   * @throws Exception
   *             with explanation in message on failure to retrieve or build
   */
  protected PreparedStatement prepare(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
  {
    String k = sql.trim().toLowerCase();
    PreparedStatement ps = null;
    try
    {
      if (_stm.containsKey(k))
      {
        ps = _stm.get(k);
        // we clear parameters here so that if the statement has a
        // problem,
        // it will kick up an exception and consumer shouldn't use it.
        ps.clearParameters();
        // System.out.println("get stmt " + ps.hashCode() + " " + k);
        return ps;
      }
    }
    catch (Exception e)
    {
      // err(e);
    }
    if (k.startsWith("select"))
    {
      ps = _conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }
    else
    {
      if (_conn.getMetaData().supportsGetGeneratedKeys())
      {
        ps = _conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      }
      else
      {// not all drivers support Statement.RETURN_GENERATED_KEYS
        ps = _conn.prepareStatement(sql);
      }
    }
    // System.out.println("new stmt " + ps.hashCode()+ " " + k);
    _stm.put(k, ps);
    if (_maxStm > 0 && _stm.size() > _maxStm)
    {
      System.out.println("Connection statement max of " + _maxStm + " exceeded.");
      _maxStm = -1;// just to suppress message
    }
    return ps;
  }
  /**
   * Creates a <code>CallableStatement</code> object for calling database
   * stored procedures. The CallableStatement provides methods for setting up
   * its IN and OUT parameters, and methods for executing the call to a stored
   * procedure.
   * 
   * <P>
   * <B>Note:</B> This method is optimized for handling stored procedure call
   * statements. Some drivers may send the call statement to the database when
   * the method <code>prepareCall</code> is done; others may wait until the
   * CallableStatement is executed. This has no direct effect on users;
   * however, it does affect which method throws certain SQLExceptions.
   * 
   * JDBC 2.0
   * 
   * Result sets created using the returned CallableStatement will have
   * forward-only type and read-only concurrency, by default.
   * 
   * @param sql
   *            a SQL statement that may contain one or more '?' parameter
   *            placeholders. Typically this statement is a JDBC function call
   *            escape string.
   * @return a new CallableStatement object containing the pre-compiled SQL
   *         statement
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.CallableStatement prepareCall(String sql) throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.prepareCall(sql);
    return null;
  }
  /**
   * JDBC 2.0
   * 
   * Creates a <code>CallableStatement</code> object that will generate
   * <code>ResultSet</code> objects with the given type and concurrency. This
   * method is the same as the <code>prepareCall</code> method above, but it
   * allows the default result set type and result set concurrency type to be
   * overridden.
   * 
   * @param resultSetType
   *            a result set type; see ResultSet.TYPE_XXX
   * @param resultSetConcurrency
   *            a concurrency type; see ResultSet.CONCUR_XXX
   * @return a new CallableStatement object containing the pre-compiled SQL
   *         statement
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws java.sql.SQLException
  {
    if (_conn != null)
      return _conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    return null;
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
   */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws SQLException
      {
    return _conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
      }
  /**
   * Creates a <code>PreparedStatement</code> object for sending parameterized
   * SQL statements to the database.
   * 
   * A SQL statement with or without IN parameters can be pre-compiled and
   * stored in a PreparedStatement object. This object can then be used to
   * efficiently execute this statement multiple times.
   * 
   * <P>
   * <B>Note:</B> This method is optimized for handling parametric SQL
   * statements that benefit from precompilation. If the driver supports
   * precompilation, the method <code>prepareStatement</code> will send the
   * statement to the database for precompilation. Some drivers may not
   * support precompilation. In this case, the statement may not be sent to
   * the database until the <code>PreparedStatement</code> is executed. This
   * has no direct effect on users; however, it does affect which method
   * throws certain SQLExceptions.
   * 
   * JDBC 2.0
   * 
   * Result sets created using the returned PreparedStatement will have
   * forward-only type and read-only concurrency, by default.
   * 
   * @param sql
   *            a SQL statement that may contain one or more '?' IN parameter
   *            placeholders
   * @return a new PreparedStatement object containing the pre-compiled
   *         statement
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.PreparedStatement prepareStatement(String sql) throws java.sql.SQLException
  {
    return prepare(sql, 0, 0);
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int)
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
  {
    return _conn.prepareStatement(sql, autoGeneratedKeys);
  }
  /**
   * JDBC 2.0
   * 
   * Creates a <code>PreparedStatement</code> object that will generate
   * <code>ResultSet</code> objects with the given type and concurrency. This
   * method is the same as the <code>prepareStatement</code> method above, but
   * it allows the default result set type and result set concurrency type to
   * be overridden.
   * 
   * @param resultSetType
   *            a result set type; see ResultSet.TYPE_XXX
   * @param resultSetConcurrency
   *            a concurrency type; see ResultSet.CONCUR_XXX
   * @return a new PreparedStatement object containing the pre-compiled SQL
   *         statement
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
      throws java.sql.SQLException
      {
    return prepare(sql, resultSetType, resultSetConcurrency);
      }
  @Override
  public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws java.sql.SQLException
      {// todo: decide whether holdability matters to us
    return prepare(sql, resultSetType, resultSetConcurrency);
      }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
  {
    return _conn.prepareStatement(sql, columnIndexes);
  }
  @Override
  public java.sql.PreparedStatement prepareStatement(String sql, String[] p) throws java.sql.SQLException
  {
    PreparedStatement ps = prepare(sql, 0, 0);
    if (p != null)
    {
      for (int i = 0; i < p.length; i++)
        ps.setString(i + 1, p[i]);
    }
    return ps;
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
   */
  @Override
  public void releaseSavepoint(Savepoint savepoint) throws SQLException
  {
    _conn.releaseSavepoint(savepoint);
  }

  /**
   * Drops all changes made since the previous commit/rollback and releases
   * any database locks currently held by this Connection. This method should
   * be used only when auto- commit has been disabled.
   * 
   * @exception SQLException
   *                if a database access error occurs
   * @see #setAutoCommit
   */
  @Override
  public void rollback() throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.rollback();
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#rollback(java.sql.Savepoint)
   */
  @Override
  public void rollback(Savepoint savepoint) throws SQLException
  {
    _conn.rollback(savepoint);
  }
  /**
   * Sets this connection's auto-commit mode. If a connection is in
   * auto-commit mode, then all its SQL statements will be executed and
   * committed as individual transactions. Otherwise, its SQL statements are
   * grouped into transactions that are terminated by a call to either the
   * method <code>commit</code> or the method <code>rollback</code>. By
   * default, new connections are in auto-commit mode.
   * 
   * The commit occurs when the statement completes or the next execute
   * occurs, whichever comes first. In the case of statements returning a
   * ResultSet, the statement completes when the last row of the ResultSet has
   * been retrieved or the ResultSet has been closed. In advanced cases, a
   * single statement may return multiple results as well as output parameter
   * values. In these cases the commit occurs when all results and output
   * parameter values have been retrieved.
   * 
   * @param autoCommit
   *            true enables auto-commit; false disables auto-commit.
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public void setAutoCommit(boolean autoCommit) throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.setAutoCommit(autoCommit);
  }
  /**
   * Sets a catalog name in order to select a subspace of this Connection's
   * database in which to work. If the driver does not support catalogs, it
   * will silently ignore this request.
   * 
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public void setCatalog(String catalog) throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.setCatalog(catalog);
  }
  @Override
  public void setClientInfo(Properties arg0) throws SQLClientInfoException
  {
  }
  @Override
  public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setHoldability(int)
   */
  @Override
  public void setHoldability(int holdability) throws SQLException
  {
    _conn.setHoldability(holdability);
  }
  public void setNetworkTimeout(Executor executor,int milliseconds) throws SQLException
  {
  }
  /**
   * Puts this connection in read-only mode as a hint to enable database
   * optimizations.
   * 
   * <P>
   * <B>Note:</B> This method cannot be called while in the middle of a
   * transaction.
   * 
   * @param readOnly
   *            true enables read-only mode; false disables read-only mode.
   * @exception SQLException
   *                if a database access error occurs
   */
  @Override
  public void setReadOnly(boolean readOnly) throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.setReadOnly(readOnly);
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setSavepoint()
   */
  @Override
  public Savepoint setSavepoint() throws SQLException
  {
    return _conn.setSavepoint();
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.sql.Connection#setSavepoint()
   */
  @Override
  public Savepoint setSavepoint(String name) throws SQLException
  {
    return _conn.setSavepoint(name);
  }
  public void setSchema(String schema) throws SQLException
  {
  }
  /**
   * Attempts to change the transaction isolation level to the one given. The
   * constants defined in the interface <code>Connection</code> are the
   * possible transaction isolation levels.
   * 
   * <P>
   * <B>Note:</B> This method cannot be called while in the middle of a
   * transaction.
   * 
   * @param level
   *            one of the TRANSACTION_* isolation values with the exception
   *            of TRANSACTION_NONE; some databases may not support other
   *            values
   * @exception SQLException
   *                if a database access error occurs
   * @see DatabaseMetaData#supportsTransactionIsolationLevel
   */
  @Override
  public void setTransactionIsolation(int level) throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.setTransactionIsolation(level);
  }
  /**
   * JDBC 2.0
   * 
   * Installs the given type map as the type map for this connection. The type
   * map will be used for the custom mapping of SQL structured types and
   * distinct types.
   * 
   * @param the
   *            <code>java.util.Map</code> object to install as the
   *            replacement for this <code>Connection</code> object's default
   *            type map
   */
  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws java.sql.SQLException
  {
    if (_conn != null)
      _conn.setTypeMap(map);
  }
  @Override
  public String toString()
  {
    return _user + "@" + _url;
  }
  @Override
  public <T> T unwrap(Class<T> arg0) throws SQLException
  {
    throw new SQLException("Not implemented.");
  }
}

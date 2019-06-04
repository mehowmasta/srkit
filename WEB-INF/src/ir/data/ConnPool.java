package ir.data;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * JDBC Connection pool. Connections returned are PooledConnections so that
 * Connection.close() is overridden to return to the pool.
 */
abstract class ConnPool
{
  private static Map<String, ConnPuddle> _data = new ConcurrentHashMap<String, ConnPuddle>();
  private static boolean _trace = false;
  private static void close(PooledConnection c)
  {
    ConnPuddle pud = _data.get(c.getPoolKey());
    try
    {
      if (pud != null)
      {
        pud.fullClose(c);
      }
      c.fullClose();
    }
    catch (Exception e)
    {
    }
  }
  private static void close(Statement c)
  {
    try
    {
      c.close();
    }
    catch (Exception e)
    {
    }
  }
  protected static synchronized void close(String key)
  {
    try
    {
      ConnPuddle pud = _data.get(key);
      if (pud != null)
      {
        pud.close();
      }
    }
    catch (Exception e)
    {
      trace("ConnPool.close " + key + ": " + e.getMessage());
    }
  }
  /**
   * Closes all connections relating to the passed values.
   * 
   * @param String
   *            JDBC Driver
   * @param String
   *            JDBC URL
   * @param String
   *            DbName
   * @param String
   *            User ID or null if not applicable
   * @param String
   *            Password or null if not applicable
   */
  public static synchronized void close(String sDriver, String sUrl, String sDb, String sUsr, String sPwd)
  {
    close(PooledConnection.getPoolKey(sDriver, sUrl, sDb, sUsr, sPwd));
  }
  /**
   * Returns a new connection for the parameters specified, or a cached
   * equivalent.
   * 
   * @param String
   *            JDBC Driver
   * @param String
   *            JDBC URL
   * @param String
   *            DbName as suffix of url
   * @param String
   *            User ID or null if not applicable
   * @param String
   *            Password or null if not applicable
   */
  public static synchronized PooledConnection get(String sDriver, String sUrl, String sDb, String sUsr, String sPwd, String testCommand,
      int maxPoolSize, int refuseConnectLevel) throws Exception
      {
    String key = PooledConnection.getPoolKey(sDriver, sUrl, sDb, sUsr, sPwd);
    ConnPuddle pud = _data.get(key);
    if (pud == null)
    {
      pud = new ConnPuddle(sDriver, sUrl, sDb, sUsr, sPwd, maxPoolSize);
      _data.put(key, pud);
    }
    if (pud.inCount() + pud.outCount() >= refuseConnectLevel)
    {
      throw new Exception("Connection refused due to refuseConnectLevel " + refuseConnectLevel);
    }
    while (pud.inCount() > 0)
    {
      PooledConnection c = pud.get();// check out
      _data.put(key, pud);// update cache with diminished list
      Statement st = null;
      try
      {
        st = c.createStatement();// verify the conn is still good
        if (st == null)
        {
          throw new Exception("Couldn't create statement.");
        }
        if (testCommand.trim().toLowerCase().startsWith("select"))
        {
          ResultSet rs = st.executeQuery(testCommand);
          rs.close();
        }
        else
        {
          st.execute(testCommand);
        }
        close(st);
        return c;
      }
      catch (Exception e)
      {
        trace("bad " + pud.Db + "." + c.hashCode() + ": " + e.getMessage());
        close(st);
        close(c);
      }
    }
    PooledConnection c = pud.create();
    return c;
      }
  /**
   * Returns a connection to the cache
   * 
   * @param String
   *            JDBC Driver
   * @param String
   *            JDBC URL
   * @param String
   *            User ID or null if not applicable
   * @param String
   *            Password or null if not applicable
   */
  public static void put(PooledConnection c) throws Exception
  {
    String key = c.getPoolKey();
    ConnPuddle pud = _data.get(key);
    if (pud != null)
    {// get list for parameters specified
      pud.put(c);
      _data.put(key, pud);// update cache with list
    }
    else
    {
      c.fullClose();
    }
  }
  /**
   * Clears connection cache
   */
  public static synchronized void reset()
  {
    for (String k : _data.keySet())
      close(k);
    _data.clear();
  }
  private static void trace(String m)
  {
    if (_trace)
      System.out.println(m);
  }
}

package ir.data;

import java.util.ArrayList;
import java.util.List;

class ConnPuddle
{
  private final List<PooledConnection> Connections = new ArrayList<PooledConnection>();
  String Db;
  String Driver;
  int MaxSize = 5;
  String Pwd;
  String Url;
  String User;
  private int connectionsOut=0;
  ConnPuddle(String sDriver, String sUrl, String sDb, String sUsr, String sPwd, int maxPoolSize)
  {
    Driver = sDriver;
    Url = sUrl;
    Db = sDb;
    User = sUsr;
    Pwd = sPwd;
    MaxSize = maxPoolSize;
  }
  public void close()
  {
    while (Connections.size() > 0)
    {
      Connections.remove(0).fullClose();
    }

  }
  public PooledConnection create() throws Exception
  {
    connectionsOut++;
    return new PooledConnection(Driver,Url,Db,User,Pwd);
  }
  public void fullClose(PooledConnection c)
  {
    c.fullClose();
    if (connectionsOut>0)
    {
      connectionsOut--;
    }
  }
  public PooledConnection get()
  {
    connectionsOut++;
    return Connections.remove(0);
  }
  public int inCount()
  {
    return Connections.size();
  }
  public int outCount()
  {
    return connectionsOut;
  }
  public synchronized void put(PooledConnection c)
  {
    if (connectionsOut>0)
    {
      connectionsOut--;
    }
    if (Connections.size() < MaxSize)
    {
      Connections.add(0, c);// reuse busiest
    }
    else
    {
      c.fullClose();
    }
  }
}
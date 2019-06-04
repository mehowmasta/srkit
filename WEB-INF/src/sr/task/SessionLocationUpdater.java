package sr.task;

import ir.data.PairRow;
import ir.util.JsonObj;
import ir.util.StreamKit;
import ir.util.StringKit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sr.data.LoginDb;
/**
 * In case the call to the service we use to set session source locations is down or slow
 * we defer setting the location text on session records to this indexed queue.
 */
public class SessionLocationUpdater extends Thread
{
  private final Queue<String> _queue = new LinkedBlockingQueue<String>();
  private final Set<String> _set = new HashSet<String>();
  private boolean _run = true;
  private static Map<String,String>ipAddresses = new ConcurrentHashMap<String,String>();
  /**
   * @return a location string for the passed ip address, if any is cached, otherwise null
   */
  public static String getLocation(String addr)
  {
    return ipAddresses.get(addr);
  }
  public static String ipLocation(String addr)
  {//for plan b if necessary see ipinfo.io
    String cityStateCountry = ipAddresses.get(addr);
    if (cityStateCountry == null)
    {
      int CONNECTION_TIMEOUT = 2000; // timeout in millis
      RequestConfig requestConfig = RequestConfig.custom()
          .setConnectionRequestTimeout(CONNECTION_TIMEOUT)
          .setConnectTimeout(CONNECTION_TIMEOUT)
          .setSocketTimeout(CONNECTION_TIMEOUT)
          .build();
      CloseableHttpClient client = HttpClients.createDefault();
      HttpGet get = new HttpGet("http://ip-api.com/json/" + addr);
      get.setConfig(requestConfig);
      CloseableHttpResponse response = null;
      try
      {
        response = client.execute(get);
        JsonObj js = new JsonObj(StreamKit.toString(response.getEntity().getContent()));
        cityStateCountry = js.getString("city") + ", " + js.getString("regionName")
            + ", " + js.getString("countryCode")
            + ", " + js.getString("lat") + "," + js.getString("lon");
        ipAddresses.put(addr, cityStateCountry);
      }
      catch(Exception e)
      {
        StringKit.println("SessionLocationUpdater for " + addr + ": " + e.getMessage());
        return "";
      }
      finally
      {
        if (response != null)
        {
          try
          {
            response.close();
          }
          catch (Exception e)
          {
            StringKit.println("SessionLocationUpdater.close: " + e.getMessage());
          }
        }
      }
    }
    return cityStateCountry;
  }
  public static void prime(LoginDb db) throws Exception
  {
    List<PairRow>pairs = db.selectIpAddressPairs();
    for (PairRow p : pairs)
    {
      ipAddresses.put(p.o1.toString(),p.o2.toString());
    }
  }
  public SessionLocationUpdater()
  {
    setName(getClass().getSimpleName() + ":" + getId());
    setPriority(Thread.MIN_PRIORITY);
    setDaemon(true);
  }
  private void prime()
  {
    LoginDb db = new LoginDb();
    try
    {
      db.open();
      prime(db);
    }
    catch (Exception nevermind)
    {
    }
    db.close();
  }
  public void push(String fromAddr)
  {
    if (_set.add(fromAddr))
    {
      _queue.add(fromAddr);
    }
  }
  @Override
  public void run()
  {
    prime();
    while (_run)
    {
      try
      {
        sleep(1000);
      }
      catch (Exception e)
      {
      }
      LoginDb db = null;
      String addr;
      while (_run && (addr = _queue.poll()) != null)
      {
        try
        {
          String location = ipLocation(addr);
          if (location != null && location.length()>0)
          {
            if (db == null)
            {
              db = new LoginDb();
              db.open();
            }
            db.execute("update tSession set Location=? where ifnull(Location,'')='' and FromAddr=?",location,addr);
          }
        }
        catch (Exception awNevermind)
        {
          StringKit.println("SessionLocationUpdater for " + addr + ": " + awNevermind.getMessage());
        }
      }
      if (db != null)
      {
        db.close();
        db = null;
      }
    }
  }
  public void stopRunning()
  {
    _run = false;
    interrupt();
  }
}

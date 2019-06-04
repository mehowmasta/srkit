package sr.web;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 */
public class SesListener implements HttpSessionListener
{
  public static int activeSessionCount = 0;
  /*
   * @see
   * javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http
   * .HttpSessionEvent)
   */
  @Override
  public void sessionCreated(HttpSessionEvent evn)
  {
    activeSessionCount++;
  }
  /*
   * @see
   * javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet
   * .http.HttpSessionEvent)
   */
  @Override
  public void sessionDestroyed(HttpSessionEvent evn)
  {
    if (App.isEnding())
    {
      return;
    }
  }
}

package sr.web;

import ir.util.DateKit;
import ir.util.StringKit;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/**
 *
 */
public class CtxListener implements ServletContextListener
{
  public static String started="??:??";
  /*
   * @seejavax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
   * ServletContextEvent)
   */
  @Override
  public void contextDestroyed(ServletContextEvent evn)
  {
    ServletContext ctx = evn.getServletContext();
    System.out.println("*");
    System.out.println("* " + ctx.getServletContextName() + ": contextDestroyed beginning.");
    System.out.println("*");
    for(WebSocketClient c : App.webSocketClients.values())
    {
        try
        {
            c.session.close();
        }
        catch (IOException e)
        {
            // w/e
        }
    }
    App.destroy(evn);
    System.out.println(ctx.getServletContextName() + ": contextDestroyed finished.");
  }
  /*
   * @see
   * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
   * .ServletContextEvent)
   */
  @Override
  public void contextInitialized(ServletContextEvent evn)
  {
    started=DateKit.hm(new Date());
    if (evn==null)
    {
      StringKit.println("CtxListener.contextInitialized: event is null?");
    }
    else if (evn.getServletContext()==null)
    {
      StringKit.println("CtxListener.contextInitialized: context is null?");
    }
    else
    {
      App.init(evn);
    }
  }
}

package sr.web;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import ir.util.JsonObj;
import ir.util.StringKit;

class WebSocketHandler implements MessageHandler.Whole<String>
{
final Session session;
final WebSocketEndpoint webSock;
WebSocketClient client;
WebSocketHandler(WebSocketEndpoint webSock, Session session)
{
    this.webSock = webSock;
    this.session = session;
}
@Override
public void onMessage(String msg)
{
    StringBuilder response = new StringBuilder("{\"ok\":1");
    try
    {
        JsonObj json = new JsonObj(msg);
        String function = json.getString("fn");
        if (function.equalsIgnoreCase("register"))
        {
            String machineKey = json.getString("mk");
            int userId = json.getInt("id");
            this.client = new WebSocketClient(machineKey, session, userId);
            WebSocketEndpoint.register(client);
        }
        else
        {
            if (client == null)
            {//should never happen right?
              client = WebSocketEndpoint.findClient(session);
            }
        }
    }
    catch (Exception exc)
    {
        StringKit.println("WebSocketHandler.onMessage processing request: " + exc.getMessage());
        return;
    }
    response.append("}");
    try
    {
      if (client != null)
      {
        client.send(response.toString());
      }
      else
      {
        synchronized(this.session)
        {
          session.getBasicRemote().sendText(response.toString());
        }
      }
    }
    catch (Exception exc)
    {
      StringKit.println("WebSocketHandler.onMessage sending reply: " + exc.getMessage());
    }
}
}
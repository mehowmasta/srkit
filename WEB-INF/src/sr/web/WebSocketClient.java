package sr.web;

import javax.websocket.Session;

public class WebSocketClient
{
  public final Session session;
  public final String machineKey;
  public final int userId;
  public WebSocketClient(String machineKey,Session session,int userId)
  {
    this.machineKey = machineKey;
    this.session = session;
    this.userId = userId;
  }
  public synchronized void send(String json) throws Exception
  {
      synchronized(this.session)
      {
        session.getBasicRemote().sendText(json);
      }
  }
  @Override
  public String toString()
  {
    return "{machineKey:" + machineKey + ",userId:" + userId + ",sessionId:" + session.getId() + "}";
  }
}
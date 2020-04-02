package sr.web;

import java.util.ArrayList;
import java.util.List;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import ir.util.StringKit;
import sr.data.UserRec;

@ServerEndpoint("/webSock")
public class WebSocketEndpoint extends Endpoint
{
public static WebSocketClient findClient(Session session)
{
    for (WebSocketClient client : App.webSocketClients.values())
    {
        if (client.session.getId() == session.getId())
        {
            return client;
        }
    }
    return null;
}
public static List<WebSocketClient> findClientsByUser(UserRec user)
{
    List<WebSocketClient> lst = new ArrayList<WebSocketClient>();
    for (WebSocketClient client : App.webSocketClients.values())
    {
        if (client.userId == user.Row)
        {
            lst.add(client);
        }
    }
    return lst;
}
public static List<WebSocketClient> findClientsByUserRow(int userRow)
{
    List<WebSocketClient> lst = new ArrayList<WebSocketClient>();
    for (WebSocketClient client : App.webSocketClients.values())
    {
        if (client.userId == userRow)
        {
            lst.add(client);
        }
    }
    return lst;
}
public static WebSocketClient getClientKey(String machineKey)
{
    WebSocketClient client = App.webSocketClients.get(machineKey);
    return client;
}
public static synchronized void print(String machineKey, String json)
{
    WebSocketClient that = getClientKey(machineKey);
    if (that != null)
    {
        if (that.session.isOpen())
        {
            try
            {
                that.send(json);
            }
            catch (Exception e)
            {
                println("failed to send payload to " + machineKey + ": " + e.getMessage());
            }
        }
    }
}
static void println(String m, Object... parms)
{
    StringKit.println("WebSocket " + StringKit.format(m, parms));
}
static void register(WebSocketClient client)
{
    App.webSocketClients.put(client.machineKey, client);
}
private static void removeClient(Session session) throws Exception
{
    try
    {
        WebSocketClient client = findClient(session);
        if (client != null)
        {
            App.webSocketClients.remove(client.machineKey);
        }
    }
    catch (Exception exc)
    {
        StringKit.debug("WebsocketEndpoint failed to remove client for session " + session.getId() + ": "
                + exc.getMessage());
    }
}
public static synchronized void send(String machineKey, String json)
{
    print(machineKey, json);
}
@Override
public void onClose(Session session, CloseReason closeReason)
{
    try
    {
        removeClient(session);
        super.onClose(session, closeReason);
    }
    catch (Exception ohNevermind)
    {
    }
}
@Override
public void onError(Session session, Throwable throwable)
{
    try
    {
        if (session.isOpen())
        {
            session.close(new CloseReason(CloseCodes.PROTOCOL_ERROR,"error sent from client machine "));
        }
    }
    catch (Exception ohNevermind)
    {
    }
}
@Override
public void onOpen(Session session, EndpointConfig endpointConfig)
{
    session.addMessageHandler(new WebSocketHandler(this, session));
}
}

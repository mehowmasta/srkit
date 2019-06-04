package sr.web.page;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import ir.util.Coerce;
import ir.util.StringKit;
import sr.data.AppDb;
import sr.data.UserRec;
import sr.data.UserRole;
import sr.web.App;

/**
 * Controls similarly named jsp file
 */
public class PatreonWebhookPage extends AppBasePage {
	private static Map<String, Method> _methods = new HashMap<String, Method>();
	private String _body = "";
	private JSONObject _json = null;
	private final static String okOne = "{\"ok\":1}";
	//
	public boolean confirmSignature(String body,String signature)  {
	    String result = "";
		try 
		{
	      SecretKeySpec signingKey = new SecretKeySpec(App.getPatreonWebhook().getBytes("UTF-8"), "HMACMD5");
	      Mac mac = Mac.getInstance("HMACMD5");
	      mac.init(signingKey);
	      byte[] rawHmac = mac.doFinal(body.getBytes());
	      result = Hex.encodeHexString(rawHmac);
		} 
		catch (Exception e)
		{
	      return false;
		}
		return signature.equals(result);
	}	
	private void checkHeaders() {
		StringBuilder b = new StringBuilder();
		Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerValue = headers.nextElement();
            b.append(headerValue)
             .append(":")
             .append(request.getHeader(headerValue));
            if (headers.hasMoreElements()) {
                b.append(",");
            }
        }	
	}
	public String createPatreon() throws Exception
	{
		String name = _json.getJSONObject("data").getJSONObject("attributes").getString("full_name");
		String email = _json.getJSONObject("data").getJSONObject("attributes").getString("email");		
		if(email!=null && !email.isEmpty())
		{
			UserRec u = new UserRec();
			u.Name = name;
			u.EMail = email;
			u.Login = email;
			u.Role = UserRole.Runner;
			if(!u.isDupLogin(db))
			{
				db.insert(u);
				u.updatePwd(db, UserRec.DEFAULT_PASS);
				StringKit.println("Patreon Created: {0} - {1}",name,email);
			}
		}
		return okOne;
	}
	public String deletePatreon() throws Exception 
	{
		String name = _json.getJSONObject("data").getJSONObject("attributes").getString("full_name");
		String email = _json.getJSONObject("data").getJSONObject("attributes").getString("email");		
		StringKit.println("Patreon Deleted: {0} - {1}",name,email);
		return okOne;
	}
	private synchronized Method getMethod(String fn) throws Exception {
		Method m = _methods.get(fn);
		if (m == null) {
			m = getClass().getMethod(fn);
			if (m != null) {
				m.setAccessible(true);
				_methods.put(fn, m);
			}
		}
		return m;
	}
	protected boolean initWebhook() throws Exception
	{
		checkHeaders();
		read();
		if(confirmSignature(_body,request.getHeader("x-patreon-signature")))
		{
			UserRec u = new UserRec(1);
			db = AppDb.open(u);		
			return true;
		}
		return false;
	}
	@Override
	protected boolean isMultiPart() {
		return true;
	}
	@Override
	protected boolean process() throws Exception {
		this.session = request.getSession(false);
		if (!initWebhook())
	    {
		    set("JSON", "{\"security\":1}");
		    return false;
	    }
		try {
			write();
		} catch (Exception e) {
			set("JSON", "{\"exc\":1}");
			Throwable cause = e.getCause();
			if (cause == null) {
				String m = e.getMessage() == null ? e.getClass().getName() : e.getMessage().replace('\'', '`');
				StringKit.println("PatreonWebhook Exception: " + m);
				StringKit.println("PatreonWebhook JSON String: "+ _body);
			} else {
				StringKit.println("PatreonWebhook Exception");
				StringKit.println("PatreonWebhook JSON String: "+ _body);
				cause.printStackTrace();
			}
		}
		if(db!=null)
		{
			db.close();
		}
		return true;
	}
	public void read() throws IOException {
		request.getReader();			
		StringBuilder b = new StringBuilder();
		BufferedReader reader = null;
		try {
		    reader = request.getReader();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        b.append(line);
		    }
		    _body = b.toString();
		    _json = new JSONObject(_body);
		}
		catch(Exception e)
		{
			StringKit.println(e);
		}
		finally {
			if(reader!=null)
			{
				reader.close();
			}
		}
	}
	private String run(String fn) throws Exception {
		Method m = getMethod(fn);
		if (m == null) {
			return "{exc:'unrecognized function [" + fn + "]'}";
		}
		String result = Coerce.toString(m.invoke(this));
		return result;
	}
	public String updatePatreon() throws Exception {
		String name = _json.getJSONObject("data").getJSONObject("attributes").getString("full_name");
		String email = _json.getJSONObject("data").getJSONObject("attributes").getString("email");	
		if(email!=null && !email.isEmpty())
		{
			UserRec u = new UserRec();
			u.Name = name;
			u.EMail = email;
			u.Login = email;
			u.Role = UserRole.Runner;
			if(!u.isDupLogin(db))
			{
				db.insert(u);
				u.updatePwd(db, UserRec.DEFAULT_PASS);
				StringKit.println("Patreon Updated: {0} - {1}",name,email);
			}
		}
		return okOne;
		
	}
	@Override
	public void write() throws Exception {
		String trigger = request.getHeader("x-patreon-event");
		String function = trigger.split(":")[1] + "Patreon";
		set("JSON",run(function));		
	}

}
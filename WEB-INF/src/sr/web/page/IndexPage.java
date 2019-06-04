package sr.web.page;

import java.util.HashMap;
import java.util.Map;

import ir.util.JDateTime;
import ir.util.StringKit;
import sr.data.LoginDb;
import sr.data.UserRec;

/**
 * Controls similarly named jsp file
 */
public class IndexPage extends AppBasePage {

	private String errorText = "";
	private int failureCounter = 0;
	public static Map<String, Integer> Failures = new HashMap<String, Integer>();
	private String remoteAddress;

	@Override
	protected void init() throws Exception {
		remoteAddress = getRequest().getRemoteAddr();
		Integer t = Failures.get(remoteAddress);
		failureCounter = t == null ? 0 : t;
		if (failureCounter > 20) {
			StringKit.println("sr5 remote address " + remoteAddress + " redirected to lockout.");
			setRedirect("error.jsp");
			return;
		}
		if (!posted()) {
			sesClose();
		}
	    invalidateSession();
	    invalidateUser();
	    if(hasParm("errorText"))
	    {
	    	errorText = readString("errorText",200);
	    }
	}

	@Override
	protected void read() throws Exception {
		String userId = readString("_u", 60);
		String password = readString("_p", 60);
		if (userId.equals("")) {
			errorText = "User id is required.";
			return;
		}
		if (password.equals("") && !userId.equals("Guest")) {
			errorText = "Password is required.";
			return;
		}
		String failMsg = "Login failed for '" + userId + "' at " + JDateTime.now().hms();
		UserRec theUsr = LoginDb.login(userId, password, remoteAddress);
		if (theUsr == null) {
			Failures.put(remoteAddress, failureCounter + 1);
			Thread.sleep(2000);
			errorText = failMsg;
			return;
		}
		if (sesStart(theUsr)) {
			setRedirect(Page.Home, "t", System.currentTimeMillis());
		} else {
			errorText = failMsg;
			return;
		}
		return;
	}

	@Override
	protected void write() throws Exception {
		set("ErrorText",errorText);
	}
}

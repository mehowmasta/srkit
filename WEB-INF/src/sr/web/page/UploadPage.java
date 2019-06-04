package sr.web.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
/**
 * Controls upload.jsp
 */

import ir.util.StringKit;
import ir.web.JRequest;

public class UploadPage extends AppBasePage {
	public String code = "";
	public UploadPage() {
		currentPage = Page.lookupUrl(StringKit.chop(getClass().getSimpleName(), 4).toLowerCase() + ".jsp");
	}

	protected void initPrivate(PageContext pageContext) throws Exception {
		response = (HttpServletResponse) pageContext.getResponse();
		request = new JRequest((HttpServletRequest) pageContext.getRequest());
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("X-Frame-Options", "DENY");
		session = request.getSession(true);
	}

	@Override
	protected boolean isMultiPart() {
		return true;
	}

	@Override
	public boolean process(PageContext pageContext) throws Exception {
		initPrivate(pageContext);
		readMultipart();
		if (readOk()) {
			write();
		}
		return true;
	}

	public boolean readOk() throws Exception {
		code = readString("iCode", 40);
		if (!hasFiles()) {
			set("JSON", "{ok:0,error:'No files'}");
			return false;
		}
		String[] acceptedPaths = new String[] { "uploads", "mike" };
		boolean accepted = false;
		for (String s : acceptedPaths) {
			if (code.indexOf(s) > -1) {
				accepted = true;
			}
		}
		if (accepted) {
			return true;

		} else {
			set("JSON", "{ok:0,error:'Invalid code'}");
			return false;
		}
	}

	@Override
	public void write() throws Exception {		
	    	return;
	}
}

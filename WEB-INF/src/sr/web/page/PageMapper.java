package sr.web.page;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import ir.util.StringKit;
import ir.web.IWebPage;
import sr.web.App;

//
public enum PageMapper {
	_inst;
	private final Map<Class<IWebPage>, String> _byClass = new HashMap<Class<IWebPage>, String>();
	private final Map<String, Class<IWebPage>> _byName = new HashMap<String, Class<IWebPage>>();

	/**
	 * Returns singleton instance, having initialized if necessary.
	 */
	public static PageMapper getInstance() {
		return _inst;
	}

	public static void handleException(PageContext pctx, Throwable exc) {
		Throwable t = exc.getCause() != null ? exc.getCause() : exc;
		String em = t.getMessage();
		if (em == null) {
			em = t.getClass().getName();
		}
		StringKit.println(em);
		StringKit.println(StringKit.format(exc.getStackTrace(), "\n"));
		HttpServletRequest req = (HttpServletRequest) pctx.getRequest();
		HttpServletResponse res = (HttpServletResponse) pctx.getResponse();
		try {
			StringBuilder b = new StringBuilder("<!DOCTYPE html> <html> <body>");
			if (App.isDev()) {
				b.append("<b>").append(em).append("</b><br>");
				b.append(StringKit.format(exc.getStackTrace(), "<br>"));
			} else {
				b.append("<p><img src='favicon.ico'>").append("<p><b>Sorry/D&eacute;sol&eacute;</b><br>")
						.append("<p>You've found an error in ").append(AppBasePage.PRODUCT_NAME).append(".")
						.append("<p><b>It's not your fault.</b>").append("  We thank you for helping us make ")
						.append(AppBasePage.PRODUCT_NAME).append(" better.");
			}
			b.append("<p>Click <a href='home.jsp'>here</a> to continue working.").append("</body></html>");
			res.getWriter().println(b.toString());
		} catch (Exception e3) {
			System.out.println("Failed to report the error following because of " + e3.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 * Initializes maps from public constants here, which list jsp page names, to
	 * xxxxPage classes that process requests for them.
	 */
	private PageMapper() {
		try {
			initialize();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//
	private void addEntry(String jsp, Class<IWebPage> bean) {
		_byName.put(jsp, bean);
		_byClass.put(bean, jsp);
	}

	//
	public Class<IWebPage> getClassForName(String name) {
		return _byName.get(name.toLowerCase());
	}

	public String getName(Class<IWebPage> pg) {
		return _byClass.get(pg);
	}

	/**
	 * <ul>
	 * <li>maps HttpRequest to xxxPage class</li>
	 * <li>create new instance of xxxPage class and invokes its process() method
	 * </li>
	 * <li>if processing is to continue, returns page instance</li>
	 * <li>if processing should not continue, invokes release() method and returns
	 * null</li>
	 * </ul>
	 */
	public IWebPage getPage(PageContext pctx) {
		IWebPage thePage = null;
		try {
			HttpServletRequest req = (HttpServletRequest) pctx.getRequest();
			String pc = req.getRequestURI();
			String pageName = StringKit.getFileName(pc);
			if (pageName.equals("")) {
				pageName = "index.jsp";
			}
			//
			Class<IWebPage> cl = getClassForName(pageName);
			if (cl == null) {
				throw new Exception("Page class for " + pageName + " not found.");
			}
			thePage = cl.newInstance();
			boolean bContinue = thePage.process(pctx);
			return bContinue ? thePage : null;
		} catch (Throwable t) {
			handleException(pctx, t);
			return null;
		}
	}

	public Set<String> getTargets() throws Exception {
		return _byName.keySet();
	}

	private void initialize() throws Exception {
		File myFile = new File(AppBasePage.class.getResource("AppBasePage.class").getFile());
		if (myFile == null || !myFile.exists()) {
			throw new RuntimeException("Could not find my file AppBasePage.class!");
		}
		File myParent = myFile.getParentFile();
		if (myParent == null || !myParent.exists()) {
			throw new RuntimeException("Could not find folder for AppBasePage.class!");
		}
		File[] fa = myParent.listFiles();
		String pkgName = AppBasePage.class.getPackage().getName();
		for (int i = 0; fa != null && i < fa.length; i++) {
			File beanFile = fa[i];
			if (beanFile.getName().toLowerCase().endsWith("page.class")) {
				String beanName = beanFile.getName().substring(0, beanFile.getName().length() - 6);
				String fullBeanName = pkgName + "." + beanName;
				String jspName = beanName.toLowerCase().substring(0, beanName.length() - 4) + ".jsp";
				try {
					Class<IWebPage> beanClass = (Class<IWebPage>) Class.forName(fullBeanName);
					addEntry(jspName, beanClass);
				} catch (Exception e) {
					StringKit.println("Page map failed to link {0} to {1}.", jspName, fullBeanName);
				}
			}
		}
	}
}

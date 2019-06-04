package sr.web.page;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import ir.data.ColDef;
import ir.data.Database;
import ir.data.FieldError;
import ir.data.IRecord;
import ir.data.IValidator;
import ir.util.Coerce;
import ir.util.JDateTime;
import ir.util.JMap;
import ir.util.Reflector;
import ir.util.StringKit;
import ir.web.IControl;
import ir.web.IWebPage;
import ir.web.JControl;
import ir.web.JRequest;
import ir.web.LocationBar;
import ir.web.MappedControl;
import ir.web.UploadFile;
import ir.web.UploadReader;
import ir.web.WebKit;
import sr.data.AppDb;
import sr.data.CharacterRec;
import sr.data.CharacterRow;
import sr.data.MessageThreadUserRec;
import sr.data.RaceRec;
import sr.data.SessionRec;
import sr.data.StatusType;
import sr.data.ThemeRec;
import sr.data.UserRec;
import sr.web.App;
import sr.web.Images;
import sr.web.SessionKeys;

/**
 * 
 */
public abstract class AppBasePage implements IWebPage, IValidator {
	//
	public static final String DELETE_BUTTON = "btnDelete2075";
	public static final String DELIMITER = "♦";
	public static final String PRODUCT_NAME = "SRKit";
	private static final Map<String, UserRec> SESSION_BACKUP = new ConcurrentHashMap<String, UserRec>();
	private static final Map<String, String> SESSION_STATUS = new ConcurrentHashMap<String, String>();
	public static final String SPLITTER = "♥";
	public static final String SUBMIT_BUTTON = "btnSubmit2075";	
	//
	protected String agentAndCpu = "[unknown]";
	public Page currentPage;
	public UserRec currentUser = null;
	public CharacterRow currentPlayer = new CharacterRow();
	public ThemeRec currentTheme = new ThemeRec();
	public AppDb db = null;
	private final List<FieldError> fieldErrors = new ArrayList<FieldError>();
	protected final String hFromPage = "hhFromPage";
	private boolean isMobile = false;
	protected LocationBar locationBar = null;
	private final Map<String, MappedControl> mappedControls = new HashMap<String, MappedControl>();
	protected int MaxUploadBytes = 100 * 1024 * 1024;
	private final List<String> pageErrors = new ArrayList<String>();
	protected JMap pageSettings = new JMap("");
	private String redirectTo = null;
	protected JRequest request = null;
	private final Map<String, String> requestProperties = new HashMap<String, String>();
	protected HttpServletResponse response;
	protected HttpSession session = null;
	protected static Map<Integer, Long> sessionInfo = new HashMap<Integer, Long>();
	protected List<UploadFile> uploadFiles = new ArrayList<UploadFile>();
	protected String uploadFolder = null;
	public static Set<String> UserAgents = new HashSet<String>();

	//
	protected AppBasePage() {
		this.currentPage = Page.lookupUrl(StringKit.chop(getClass().getSimpleName(), 4).toLowerCase() + ".jsp");
	}

	@Override
	public void addErrors(IValidator iv) {
		List<String> errorList = iv.getErrors();
		for (String e : errorList) {
			pageErrors.add(e);
		}
	}

	protected void addErrors(String e, Object... parms) {
		pageErrors.add(format(e, parms));
	}

	/**
	 * Associates the translation of a passed key with a passed field name.
	 */
	@Override
	public void addFieldError(String field, String msgKey, Object... parms) throws Exception {
		fieldErrors.add(new FieldError(field, format(msgKey, parms)));
	}

	protected void addHit() throws Exception {
	}

	protected void addLocation() throws Exception {
		
		addLocation(getPageName(), getURL());
	}

	protected void addLocation(String pageName, String urlBase, Object... parms) throws Exception {
		if (isDialog()) {
			return;
		}
		if (getDatabase() != null) {
			String url = url(urlBase, parms);
			if (isShownOnLocationBar(url)) {
				locationBar.add(pageName, url);
			}
		}
	}

	public void addPageError(String e, Object... parms) throws Exception {
		addErrors(e, parms);
	}

	protected String backButton() throws Exception {
		String back = "";
		String last = locationBar.previous(getPageName());
		if(last.contains("error"))
		{
			locationBar.removeLast();
			locationBar.removeLast();
			last = locationBar.previous(getPageName());
		}
		if (last == null) {
			return back;
		} else if (locationBar.size() > 1) {
			back += "<button type='button' class='navButton back hover' data-hover='Go back a page' tabindex='1' onclick='sr5.go(\""+url(last)+"\")'>⇦</button>\n";
		}
		return back;
	}

	@Override
	public void clearErrors() {
		pageErrors.clear();
		fieldErrors.clear();
	}

	protected String eeJson() {
		if (isOK()) {
			return "{\"ok\":1}";
		}
		return "{\"ee\":"+getErrorListJson()+"}";
	}

	String eeJson(String msgLbl, Object... parms) throws Exception {
		addPageError(msgLbl, parms);
		return eeJson();
	}

	public String endForm() throws Exception {
		return "</form>";
	}

	public String eventButton(String name, Object label, String onclick, String attrs) {
		return WebKit.eventButton(name, label, onclick, attrs);
	}

	public String startForm() throws Exception {
		return startForm("");
	}

	public String startForm(String attrs) throws Exception {
		if (attrs == null) {
			attrs = "";
		}
		if (attrs.toLowerCase().indexOf("autocomplete") == -1) {
			attrs += " autocomplete='off' ";
		}
		//
		StringBuilder sb = new StringBuilder("");
		sb.append("<form name='f1' class='loading' id='f1' method='POST'");
		sb.append(" action=").append(WebKit.quote(WebKit.escapeHtml(url(getPageName()))));
		sb.append(" ").append(attrs).append(" ");
		if (isMultiPart()) {
			sb.append(" enctype=\"multipart/form-data\" ");
		}
		sb.append(">");
		sb.append(getPageHeader());
		return sb.toString();
	}

	public String format(String m, Object... p) {
		return StringKit.format(m, p);
	}

	public String get(String propName) {
		if (requestProperties.containsKey(propName)) {
			return requestProperties.get(propName);
		}
		return "";
	}

	public boolean getBool(String propName) {
		return StringKit.True(get(propName));
	}

	@Override
	public Database getDatabase() {
		return db;
	}

	public String getError() throws Exception {// getPageStatus
		String s = sesGetAndClear(SessionKeys.Error, "");
		if (s != null) {
			return s;
		}
		return "";
	}

	public String getErrorListJson() {
		String json = StringKit.jsqa(getErrors());
		clearErrors();
		return json;
	}

	protected String getErrorPage() {
		return "home.jsp";
	}

	@Override
	public List<String> getErrors() {
		List<String> temp = new ArrayList<String>(pageErrors);
		for (FieldError e : fieldErrors) {// getErrorList
			temp.add(e.toString());
		}
		return temp;
	}

	@Override
	public List<FieldError> getFieldErrors() {
		return fieldErrors;
	}

	public List<UploadFile> getFiles() {
		return uploadFiles;
	}

	public String getImage(String imageName)
	{
		return Images.get(imageName);
	}
	public String getMessageCount() throws Exception {
	    StringBuilder b = new StringBuilder();
	    int unseenCount = MessageThreadUserRec.selectUnseenCount(db, currentUser.Row);
	    set("MessageCount",unseenCount);
	    String className = "messageCounter ";
	    if(unseenCount>0)
	    {
	        className += "show";
	    }
	    b.append("<div id='messageCountDiv' class='")
	     .append(className)
	     .append("'>")
	     .append(unseenCount)
	     .append("</div>");    
	    return b.toString();    
	}
	public String getNavMenu() throws Exception {
		MenuTitleComparator comp = new MenuTitleComparator();
		StringBuilder t = new StringBuilder("");
		t.append("<div id='nav' style='white-space:nowrap;'>")
				// .append("<div class='navButtonDummy'></div>")
				.append("<button type='button' id='navButton' class='navButton hover' data-hover='Navigation menu' onclick='nav.toggleMenu()'>")
				.append("&#9704;</button>").append(backButton()).append("<div id='navMenu' class='navMenu'>")
				.append("<ul>").append("<li onclick='sr5.go(\""+Page.Home.jsp()+"\")'><a href='#'>")
				.append("<img src='" + Images.get(Images.Home) + "' class='medIcon'>").append("<span>").append("Home")
				.append("</span></a></li>");
		List<Page> kids = new ArrayList<Page>();
		kids.addAll(Page.Home.getNonNullChildren());
		for (Page kid : kids) {
			if (kid.sysAdminOnly() && !currentUser.isSysAdmin()) {
				continue;
			}
			else if(!kid.allowGuest() && currentUser.isGuest())
			{
				continue;
			}
			List<Page> grandKids = kid.getNonNullChildren();
			if (false /*grandKids.size() > 0   turning off grandkids for now*/) {
				Collections.sort(grandKids, comp);
				boolean first = true;
				for (Page grandKid : grandKids) {
					if (grandKid.sysAdminOnly() && !currentUser.isSysAdmin()) {
						continue;
					}
					if (first) {
						t.append("<li> <a href='#' onclick='nav.toggleSubMenu(this)' class='expandable'>")
								.append("<img src='").append(Images.get(kid.image)).append("' class='medIcon'>").append("<span>")
								.append(kid.title()).append("</span>").append("</a><div class='navMenu subMenu'>\n")
								.append("<ul>").append("<li> <a href='#' onclick='nav.back(this)'>")
								.append("<span style='padding-right:0;'>&larr;</span>")
								// .append("<img src='").append(Images.Back).append("' class='medIcon'>")
								.append("<span>").append("Back").append("</span>").append("</a></li>\n");
						first = false;
					}
					t.append("<li id='navMenu" + grandKid.name() + "'>").append(WebKit.anchor(
							"<img src='" + Images.get(grandKid.image) + "' class='medIcon'><span>" + grandKid.title() + "</span>",
							grandKid.jsp())).append("</li>\n");
				}
				if (!first) {
					t.append("</ul></div>\n");
				}
			} else {
				t.append("<li id='navMenu" + kid.name() + "' onclick='sr5.go(\""+kid.jsp()+"\")'>")
						.append(WebKit.anchor(
								"<img src='" + Images.get(kid.image) + "' class='medIcon'><span>" + kid.title() + "</span>",
								"#"))
						.append("</li>\n");
			}
		}
		t.append("<li>")
				.append(WebKit.anchorExt("<img src='" + Images.get(Images.Lift) + "' class='medIcon'><span>Logout</span>",
						"onclick='sr5.go(\"index.jsp\")'", "#"))
				.append("</li>").append("</ul>").append("</div>").append("</div>");
		return t.toString();
	}

	public String getNavPageTitle() throws Exception {
		return "<div class='navPageTitle " + (locationBar.size() == 1 ? "home" : "") + "'>" + currentPage.titleKey
				+ "</div>";
	}

	@Override
	public List<String> getPageErrors() {
		return pageErrors;
	}

	public String getPageHeader() throws Exception {
		StringBuilder b = new StringBuilder();
		b.append("<nav>").append(getNavMenu()).append(getQuickBtns()).append(getNavPageTitle()).append("</nav>\n");
		return b.toString();
	}

	public String getPageName() throws Exception {
		return currentPage.jsp();
	}

	public <A> A getPageSetting(String fieldName, A dftVal) throws Exception {
		return pageSettings.get(getPageName() + "-" + fieldName, dftVal);
	}

	public String getPreviousPage() throws Exception {
		return locationBar.previous(getURL());
	}

	private List<Step> getProcessSteps() {
		List<Step> steps = new ArrayList<Step>();
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				initCommon();
			}

			@Override
			public String toString() {
				return "initCommon";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				initControls();
			}

			@Override
			public String toString() {
				return "initControls";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				if (posted()) {
					readMultipart();
				}
			}

			@Override
			public String toString() {
				return "readMultipart";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				init();
			}

			@Override
			public String toString() {
				return "init";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				if (posted() && isOK()) {
					readMappedControls();
				}
			}

			@Override
			public String toString() {
				return "readMappedControls";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				if (posted() && isOK()) {
					read();
				}
			}

			@Override
			public String toString() {
				return "read";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				if (posted() && isOK()) {
					update();
				}
			}

			@Override
			public String toString() {
				return "update";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				if (inSession()) {
					addLocation();
					sesSet(SessionKeys.LocBar, locationBar);
				}
			}

			@Override
			public String toString() {
				return "setCommon";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				write();
			}

			@Override
			public String toString() {
				return "write";
			}
		});
		steps.add(new Step() {
			@Override
			public void take() throws Exception {
				writeControls();
			}

			@Override
			public String toString() {
				return "writeControls";
			}
		});
		return steps;
	}

	public String getProductName() throws Exception {
		return PRODUCT_NAME;
	}

	protected String getQuickBtns() throws Exception {
		StringBuilder b = new StringBuilder();
		b.append("<div id='navQuickBtns' class='quickBtns'>");
		b.append("<div id='navTopBtn' class='imgBtn hover'  data-hover='Top' onclick='sr5.top();'><img class='medIcon' src='"
				+ Images.get(Images.Top) + "'><label>Top</label></div>");
		
		/*
		if (currentUser.isSysAdmin()) {
			b.append(
					"<div id='navSearchBtn' class='imgBtn hover' data-hover='Bugs!!!'  onclick='bugPop.show();'><img class='medIcon' src='"
							+ Images.get(Images.Bug) + "'><label>Bugs</label></div>");
		}
		*/
		if (hasSearch()) {
			b.append(
					"<div id='navSearchBtn' class='imgBtn hover' data-hover='Search page for...'  onclick='searchPop.show();'><img class='medIcon' src='"
							+ Images.get(Images.Search) + "'><label>Search</label></div>");
		}
		if (hasInfo()) {
			b.append(
					"<div id='navInfoBtn' class='imgBtn hover' data-hover='Extra info' onclick='sr5.showInfo();'><img class='medIcon' src='"
							+ Images.get(Images.Info) + "'><label>Info</label></div>");
		}
		if (hasSort()) {
			b.append(
					"<div id='navSortBtn' class='imgBtn hover' data-hover='Sort layout' onclick='sr5.sortList();'><img class='medIcon' src='"
							+ Images.get(Images.Sort) + "'><label>Sort</label></div>");
		}
		b.append(
				"<div id='navDiceRollBtn' class='imgBtn hover' data-hover='Roll some dice!' onclick='diceRollPop.show();'><img class='medIcon' src='"
						+ Images.get(Images.Dice) + "' ><label>Roll!</label></div>");
		// b.append("<div class='imgBtn' onclick='sr5.go(\"index.jsp\");'><img
		// class='medIcon' src='icons/lift.svg'><label>Logout</label></div>")
		if (!currentUser.isGuest()|| App.isDev()) {
			b.append(
					"<div id='navSearchBtn' class='imgBtn hover' data-hover='Comm Link'  onclick='messengerPop.show();'><img class='medIcon' src='"
							+ Images.get(Images.Comm) + "'><label>Comm Link</label>").append(getMessageCount()).append("</div>");
		}
		/*
		b.append("<div id='navJournalBtn' class='imgBtn hover' data-hover='Digital Journal' onclick='sr5.showJournal();'><img class='medIcon' src='"
							+ Images.get(Images.Notebook) + "'><label>Journal</label></div>");
		*/
		if (currentPlayer.Character != null && currentPlayer.Character.Row > 0) {
			b.append(
					"<div id='navPcBtn' class='imgBtn hover' data-hover='Character Sheet' onclick='sr5.showPlayerCharacter();'><img class='medIcon' src='"
							+ Images.get(Images.Password) + "'><label>" + StringKit.leftEllipsis(currentPlayer.Character.Name,12) + "</label></div>");
		}
		b.append("</div>");
		return b.toString();
	}

	protected String getRedirectPage() {
		return "redir.jsp";
	}

	protected String getRedirectParm() {
		return "RedirUrl";
	}

	public JRequest getRequest() {
		return request;
	}

	public String getStatus() throws Exception {// getPageStatus
		String s = sesGetAndClear(SessionKeys.Status, "");
		if (s != null) {
			return s;
		}
		return "";
	}

  public String getThemeParameters() throws Exception
  {
    return currentTheme.getUrlParameters();
  }
	/**
	 * Returns url of this page, including passed query parameters
	 *
	 * @return String
	 */
	protected String getURL() throws Exception {
		String s = getPageName();
		String q = request.getQueryString();
		if (null != q && q.length() > 0) {
			s += "?" + q;
		}
		return s;
	}

	public boolean hasFiles() {
		return uploadFiles.size() > 0;
	}

	public boolean hasInfo() {
		return false;
	}

	public boolean hasPageSetting(String f) throws Exception {
		return pageSettings.containsKey(getPageName() + "-" + f);
	}

	public boolean hasParm(String key) {
		return request.hasParm(key);
	}

	public boolean hasProperty(String propName) {
		return requestProperties.containsKey(propName);
	}

	public boolean hasSearch() {
		return false;
	}

	public boolean hasSort() {
		return false;
	}

	public String imageButton(String label, String img, String onclick) {
		return "<button type='button' class='mainBtn hover' data-hover="+label+" onclick='" + onclick + "'><img src='" + img
				+ "' class='medIcon'><br>" + label + "</button>";
	}

	protected void init() throws Exception {
	}

	protected void initCommon() throws Exception {
		App.setContextPath(getRequest());
		initDeviceType();
		set("MobileDevice",isMobile);
		set("Delimiter", DELIMITER);
		set("Splitter", SPLITTER);
		set("MessageCount",0);
		set("DeleteButton", DELETE_BUTTON);
		set("ServerTime",JDateTime.now());
		set("User",false);
		this.session = request.getSession(false);
		if (this instanceof IndexPage) {// after session so we can send status to index page
			return;
		}
		if (!initUser()) {
			if (pageRequiresSession()) {
				setRedirect(Page.Index,"errorText","Session has expired.");
			}
			return;
		}
		set("User","{Row:"+currentUser.Row+",Name:"+StringKit.jsq(currentUser.Name)+",PlayerCharacter:"+currentUser.PlayerCharacter+"}");
		initPlayer();
		if(db!=null)
		{
			set("Metatypes",RaceRec.selectAll(db).toString());
		}
		set("Sex",CharacterRec.CharacterSex.selectJson());
		set("IconPath",Images.getIconPath());
	}

	/**
	 * constructControls is called from process() before initCommon to instantiate
	 * and name uninitialized public IControl implementer members on the page.
	 */
	protected void initControls() throws Exception {
		Field[] fa = getClass().getFields();
		for (Field f : fa) {
			Class<?> c = f.getType();
			Object memberObject = f.get(this);
			if (Reflector.classImplements(c, IControl.class)) {
				if (memberObject == null) {
					IControl controlInstance = null;
					try {
						Constructor<?> nameConstructor = c.getConstructor(String.class);
						if (nameConstructor != null) {
							controlInstance = (IControl) nameConstructor.newInstance(f.getName());
						}
					} catch (Exception nevermindFallThrough) {

					}
					if (controlInstance == null) {
						controlInstance = (IControl) c.newInstance();
						controlInstance.setName(f.getName());
					}
					memberObject = controlInstance;
					f.set(this, controlInstance);
				} else {
					IControl co = (IControl) memberObject;
					if (co.getName() == null || co.getName().equals("")) {
						co.setName(f.getName());
					}
				}
			}
		}
	}

	private void initDeviceType() throws Exception {
		String agent = request.getHeader("User-Agent");
		String cpu = request.getHeader("ua-cpu");
		agentAndCpu = (agent + " cpu:" + StringKit.coalesce(cpu, "[unknown]")).toLowerCase();
		if (inSession()) {
			Object o = sesGet(SessionKeys.MobileDevice);
			if (o != null) {
				isMobile = Coerce.toBool(o);
				return;
			}
		}
		UserAgents.add(agentAndCpu);
		if (cpu != null) {
			cpu = cpu.toLowerCase();
			if (cpu.startsWith("arm")) {
				isMobile = true;
			}
		}
		if (!isMobile && agent != null) {
			agent = agent.toLowerCase();
			if (agent.contains("mobi") || agent.contains("android")) {
				isMobile = true;
			}
		}
		if (inSession()) {
			sesSet(SessionKeys.MobileDevice, isMobile);
		}
	}

	protected void initPlayer() throws Exception {
		if (currentUser.PlayerCharacter > 0) {
			currentPlayer = CharacterRow.selectCharacter(db, currentUser.PlayerCharacter);
		}
		set("CurrentCharacter",currentPlayer.Character.Row == 0 ? false : currentPlayer.toJson());
	}

	protected boolean initUser() throws Exception {
		UserRec testUser = new UserRec();
		if (inSession()) {
			testUser.Row = sesGet(SessionKeys.UserRow, 0);
		}
		if (testUser == null || testUser.Row == 0) {
			return false;
		}
		currentUser = testUser;
		db = AppDb.open(currentUser);
		if (db == null) {
			return false;
		}
		db.setOpener(getClass().getName());
		if (!db.select(currentUser)) {
			return false;
		}
		locationBar = sesGet(SessionKeys.LocBar, new LocationBar(Page.Home.jsp(), Page.Home.title()));
		setTheme(db);
		this.pageSettings = currentUser.getPageSettingsMap();
		return true;
	}

	protected void invalidateSession() {
		session = request.getSession(false);
		if (session != null) {
			try {
				SessionRec.end(db, sesGet(SessionKeys.SesRow, 0));
			} catch (Exception ignore) {
			}
			session.invalidate();
			session = null;
		}
	}

	protected void invalidateUser() {
		currentUser = null;
	}

	public boolean isDialog() {
		return false;
	}

	protected boolean isLocalhost() {
		String hlc = request.getRemoteHost().toLowerCase();
		return hlc.contains("localhost") || hlc.contains("0:0:0:0");
	}
	public boolean isMobile() {
		return isMobile;
	}
	protected boolean inSession() {
		if (session == null) {
			return false;
		}
		try {
			return 0 < Coerce.toInt(session.getAttribute(SessionKeys.UserRow));
		} catch (Exception wtf) {
			return false;
		}
	}
	public boolean isGuest()
	{
		return currentUser.isGuest();
	}
	protected boolean isMultiPart() {
		return false;
	}

	public boolean isOK() {
		return pageErrors.size() == 0 && fieldErrors.size() == 0;
	}

	public boolean isShownOnLocationBar(String url) {
		return !url.toLowerCase().startsWith(Page.Index.jsp());
	}
	public boolean isSysAdmin()
	{
		return currentUser.isSysAdmin();
	}
	protected String jsq(Object v) {
		return StringKit.jsq(v);
	}

	/**
	 * Maps the passed control to a field on the object passed. Mapped controls take
	 * the field's value and attributes at init time, then set the field value from
	 * the request at read time.
	 */
	protected void mapControl(IControl c, Object container, String field) throws Exception {
		if (mappedControls.containsKey(c.getName())) {
			mappedControls.remove(c.getName());
		}
		if (container instanceof IRecord) {
			IRecord r = (IRecord) container;
			ColDef cd = db.getColDef(r.getTable(), field);
			if (cd == null) {
				throw new Exception("Cannot find column definition for " + r.getTable() + "." + field);
			}
			MappedControl mc = new MappedControl(c, db, r, field);
			mappedControls.put(c.getName(), mc);
			if (!posted() && hasParm(c.getName())) {
				try {
					mc.read(request, this);
				} catch (Exception e) {
					System.out.println("mapControl defaultOnGet for " + getClass().getName() + "." + c.getName() + ": "
							+ e.getMessage());
				}
			}
			if (c instanceof JControl && cd.isChar() && cd.getSize() > JControl.DefaultTextAreaWidth) {
				((JControl) c).setTextArea(0, JControl.DefaultTextAreaWidth);
			}
		} else {
			mappedControls.put(c.getName(), new MappedControl(c, container, field));
		}
		c.setTitle(field);
	}

	protected boolean pageRequiresSession() {
		return true;
	}

	public boolean posted() {
		try {
			if (readString(hFromPage, 128).equals(getPageName())) {
				return getRequest().posted();
			}
		} catch (Exception e) {
			System.out.println(getClass().getName() + ".posted: " + e.getMessage());
		}
		return getRequest().selfPosted();
	}

	protected boolean process() throws Exception {
		List<Step> steps = getProcessSteps();
		for (int i = 0; i < steps.size() && !redirected(); i++) {
			Step step = steps.get(i);
			Thread.currentThread().setName(currentPage.name() + "-" + step.toString());
			step.take();
		}
		if (redirected()) {
			addHit();
			redirect(redirectTo);
			return false;
		}
		return true;
	}

	@Override
	public boolean process(PageContext pctx) throws Exception {// getPageContext
		response = (HttpServletResponse) pctx.getResponse();
		request = new JRequest((HttpServletRequest) pctx.getRequest());
		response.setCharacterEncoding("UTF-8");
		if (!isMultiPart()) {
			request.setCharacterEncoding("UTF-8");
		}
		return process();
	}

	public void putPageSetting(String key, Object val) throws Exception {
		putPageSetting(getPageName(), key, val);
	}

	public void putPageSetting(String pageName, String key, Object val) throws Exception {
		String aggregateKey = pageName + "-" + key;
		// StringKit.debug("putPageSetting " + aggregateKey + "=[" + val + "]");
		pageSettings.put(aggregateKey, val);
	}

	protected void read() throws Exception {
	}

	//
	protected <T> T read(IControl c, T oDft) throws Exception {
		return c.read(request, oDft, this);
	}

	protected <T> T read(String parm, T oDft) throws Exception {
		return read(parm, oDft, 60);
	}

	private <T> T read(String parm, T oDft, int maxLength) throws Exception {
		return request.read(parm, oDft, maxLength);
	}

	public boolean readBoolean(String ctlName) throws Exception
	{
	    return request.readBoolean(ctlName);
	}
	  protected float readFloat(String parm)
	  {
	    return request.read(parm, 0f);
	  }
	public int readInt(IControl c) throws Exception {
		int v = readInt(c.getName());
		c.setValue(v);
		return v;
	}

	public int readInt(String k) throws Exception {
		return read(k, 0);
	}

	public int[] readIntArray(String name) {
		return request.readIntArray(name);
	}

	protected void readMappedControls() throws Exception {
		MappedControl mc = null;
		String n = "[unknown]";
		try {
			for (Map.Entry<String, MappedControl> me : mappedControls.entrySet()) {
				mc = me.getValue();
				n = mc.getName();
				mc.read(request, this);
			}
		} catch (Exception e) {
			System.out.println("Exception coming for " + n);
			throw e;
		}
	}

	protected void readMultipart() throws Exception {
		// upload attachments so that the user's choices
	    // are not lost in case of a downstream error
	    try
	    {
	        String ct = request.getHeader("content-type");
	        if (uploadFolder == null)
	        {
	            uploadFolder = App.getRealUploadDir();
	        }
	        if (uploadFolder == null || uploadFolder.equals(""))
	        {
	            throw new Exception("Upload folder not provided.");
	        }
	        if (ct != null && ct.startsWith("multipart/form-data"))
	        {
	            uploadFiles = new UploadReader().read(this, getRequest(), uploadFolder, MaxUploadBytes);
	        }
	    }
	    catch (Exception e)
	    {
	        setError("&rarr;" + e.getMessage());
	        return;
	    }
	}
	/** readString filters out newlines and carriage returns */
	public String readString(String parm, int len) throws Exception {
		return request.read(parm, "", len);
	}

	/**
	 * Removes the last entry on the location bar
	 */
	public void removeLastLocation() {
		locationBar.removeLast();
	}

	protected void savePageSettings() throws Exception {
		currentUser.updatePageSettings(db, pageSettings);
	}

	public void sesClear(String key) {
		if (session != null) {
			session.removeAttribute(key);
		}
	}

	private void redirect(String url) throws Exception {
		if (currentUser != null && db != null) {
			savePageSettings();
		}
		release();
		if (url.toLowerCase().endsWith("pdf")) {
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			url += "?t=" + System.currentTimeMillis();
			response.sendRedirect(url);
		} else {
			String rdp = getRedirectPage();
			sesSet(getRedirectParm(), url);
			response.sendRedirect(rdp);
		}
	}

	//
	protected boolean redirected() {
		return redirectTo != null;
	}

	public void release() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	protected void sesClose() throws Exception {
		HttpSession ses = sesGet();
		if (ses != null) {
			try {
				SESSION_BACKUP.remove(ses.getId());
				SESSION_STATUS.remove(ses.getId());
			} catch (Exception nevermind) {
			}
			try {
				ses.invalidate();
			} catch (Exception nevermind) {
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <A> A sesGet(String key, A oDft) throws Exception {
		if (session == null) {
			return null;
		}
		Object v = session.getAttribute(key);
		if (oDft == null) {
			return (A) v;
		}
		if (v == null) {
			session.setAttribute(key, oDft);
			return oDft;
		}
		return (A) Coerce.to(v, oDft.getClass());
	}

	public Object sesGet(String key) {
		return session == null ? null : session.getAttribute(key);
	}

	protected HttpSession sesGet() {
		return request.getSession(false);
	}

	public <A> A sesGetAndClear(String key, A oDft) throws Exception {
		A v = sesGet(key, oDft);
		sesClear(key);
		return v;
	}

	public void sesSet(String key, Object oVal) throws Exception {
		if (session != null) {
			try {
				session.setAttribute(key, oVal);
			} catch (Exception e) {
				StringKit.println("sesSet(" + key + "," + oVal + ") failed: " + e.getMessage());
			}
		}
	}

	protected boolean sesStart(UserRec theUsr) throws Exception {
		this.currentUser = theUsr;
		this.db = AppDb.open(currentUser);
		this.session = request.getSession(true);
		session.setMaxInactiveInterval(2 * 60 * 60);
		if (locationBar == null) {
			locationBar = new LocationBar(Page.Home.jsp(), Page.Home.titleKey);
		} else {
			locationBar.add("Home", Page.Home.titleKey);
		}
		int sessionRow = SessionRec.start(db, request);
		sesSet(SessionKeys.LocBar, locationBar);
		sesSet(SessionKeys.UserRow, currentUser.Row);
	    sesSet(SessionKeys.ThemeRow, currentUser.ThemeRow);
		sesSet(SessionKeys.SesRow, sessionRow);
		sessionInfo.put(sessionRow, System.currentTimeMillis());
		return true;
	}

	public void set(String propName, Object value) {
		requestProperties.put(propName, value == null ? "" : value.toString());
	}

	public void set(String propName, String fmt, Object... parms) {
		requestProperties.put(propName, format(fmt, parms));
	}

	protected void setError(String m, Object... parms) throws Exception {
		setErrorRaw(m, parms);
	}

	protected void setErrorRaw(String message) throws Exception {
		if (session != null) {
			String sts = sesGet(SessionKeys.Error, "");
			if (sts.indexOf(message) > -1) {
				return;
			}
			if (sts.length() > 0) {
				sts += "<br>";
			}
			sts += message;
			session.setAttribute(SessionKeys.Error, sts);
			addPageError(message);
		} else {
			//if session is null you will be returned to the index page
			set("errorText",message);
			StringKit.println(message);
		}
	}

	protected void setErrorRaw(String m, Object... parms) throws Exception {
		setErrorRaw(format(m, parms));
	}

	protected void setRedirect(String urlBase, Object... parms) {
		redirectTo = url(urlBase, parms);
	}

	protected void setRedirect(Page page, Object... parms) {
		redirectTo = url(page.jsp(), parms);
	}

	/**
	 * sets the controller to go back a page
	 */
	protected void setRedirectBack() throws Exception {
		setRedirect(getPreviousPage());
	}

	protected void setStatus(String m, Object... parms) throws Exception {
		setStatusRaw(m, parms);
	}

	protected void setStatusRaw(String message) throws Exception {
		if (session != null) {
			String sts = sesGet(SessionKeys.Status, "");
			if (sts.indexOf(message) > -1) {
				return;
			}
			if (sts.length() > 0) {
				sts += "<br>";
			}
			sts += message;
			session.setAttribute(SessionKeys.Status, sts);
		} else {
			StringKit.println(message);
		}
	}

	protected void setStatusRaw(String m, Object... parms) throws Exception {
		setStatusRaw(format(m, parms));
	}
	public void setTheme(Database db) throws Exception
	{
	    db.select(currentTheme,currentUser.ThemeRow);
	    Images.setIconPath(currentTheme.IconFolder);
	}
	protected String submit(String name, String label) {
		return WebKit.submit(name, label);
	}

	protected String submit(String name, String label, String otherAttributes) {
		return WebKit.submit(name, label, otherAttributes);
	}

	protected void update() throws Exception {
	}

	public boolean userIsGuest() {
		if (currentUser != null) {
			return currentUser.isGuest();
		}
		return true;
	}

	/**
	 * builds and returns a URL
	 */
	public String url(String urlBase, Object... parmsAndValues) {
		return WebKit.url(getRequest(), urlBase, parmsAndValues);
	}

	/**
	 * Prepares the page to be displayed.
	 */
	protected void write() throws Exception {
	}

	private void writeControls() throws Exception {
		Field[] fa = getClass().getFields();
		for (Field f : fa) {
			Object o = f.get(this);
			if (o != null) {
				if (!hasProperty(f.getName())) {
					set(f.getName(), o.toString());
				}
			}
		}
	}

	class MenuTitleComparator implements Comparator<Page> {
		@Override
		public int compare(Page a, Page b) {
			if (a == null) {
				return -1;
			}
			if (b == null) {
				return 1;
			}
			try {
				return a.title().compareTo(b.title());
			} catch (Exception e) {
				return a.name().compareTo(b.name());
			}
		}
	}

	interface Step {
		/** perform work */
		void take() throws Exception;

		@Override
		String toString();
	}
}
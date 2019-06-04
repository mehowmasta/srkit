package sr.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sr.data.AppDb;
import sr.data.AppRec;
import sr.data.LoginDb;
import ir.util.Coerce;
import ir.util.FileKit;
import ir.util.StringKit;
import ir.util.XmlDoc;
import ir.web.WebKit;

public abstract class App
{//failed to test
  public final static Set<String>pageTitleCheck=new HashSet<String>();
  private static ServletContext context = null;
  public static List<String>clientLog = new ArrayList<String>();
  /** in lowercase */
  private static String contextPath = null;
  private static Boolean isDev = null;
  private static boolean isEnding = false;
  private static String patreonWebhook = null;
  private static String realImageDir = null;
  private static String realImportDir = null;
  private static String realUploadDir = null;
  private static final File testModeAppHome;
  private static Map<String, String> testModeParameters = null;
  static final Map<String, WebSocketClient> webSocketClients = new ConcurrentHashMap<String, WebSocketClient>();
  static{
    File cb=new File("/mn/workspaces/sr/sr");
    try
    {
      File f = new File(AppRec.class.getResource("AppRec.class").getFile());
      File folder = f.getParentFile();
      f = new File(folder,"index.jsp");
      while (true)
      {
        folder = folder.getParentFile();
        if (folder == null || ! folder.exists())
        {
          break;
        }
        File fi = new File(folder,"index.jsp");
        if (fi.exists())
        {
          cb = folder;
          break;
        }
      }
    }
    catch (Exception e)
    {
      StringKit.println("Failed to initialize app home:" + e.getMessage());
    }
    testModeAppHome=cb;
  }
  public static final int MaxMemPct = 80;


  private static void clearUploadFolder(ServletContext ctx)
  {
      try
      {
          String realDir = App.getRealUploadDir();
          String[] files = new File(realDir).list();
          for (int iF = 0; files != null && iF < files.length; iF++)
          {
              if (!files[iF].equalsIgnoreCase("index.htm"))
              {
                  File f = new File(realDir, files[iF]);
                  if(!f.isDirectory())
                  {
                      f.delete();
                  }
              }
          }
      }
      catch (Exception e)
      {// not fatal
          StringKit.println("failed to clear report folder:" + e.getMessage());
      }
  }
  private static void createMissingDirectories()
  {
      String testDir[] = {realImageDir,realUploadDir,realImportDir};
      for(String s : testDir)
      {
          File dir = new File(s);        
          // if the directory does not exist, create it
          if (!dir.exists()) {
              System.out.println("creating directory: " + dir.getName());
              try{
                  dir.mkdir();
              } 
              catch(SecurityException se){
                  //handle it
              }  
          }
      }
  }
  public static void destroy(ServletContextEvent evn)
  {
    isEnding = true;

    clearUploadFolder(evn.getServletContext());
    System.out.println("\n*\n");
    for (String str : clientLog)
    {
      System.out.println(str);
    }
    AppDb.shutdown();
  }
  //
  public static String getDbDriver() throws Exception
  {
    return getParamString("dbdriver", true);
  }
  public static int getDbPoolSize() throws Exception
  {
    return getParamInt("dbpool", true);
  }
  public static String getDbPwd() throws Exception
  {
    return getParamString("dbpwd", true);
  }
  public static String getDbUrl() throws Exception
  {
    return getParamString("dburl", true);
  }
  public static String getDbUser() throws Exception
  {
    return getParamString("dbuser", true);
  }
  public static String getEnv() throws Exception
  {
    return getParamString("env",true);
  }
  public static String getFullUrl(String url)
  {
    if (url == null)
    {
      url = "";
    }
    String contextPrefix = (contextPath.startsWith("/") ? "" : "/") + contextPath;
    String prefix = "";
    if (! url.startsWith(contextPrefix))
    {
      prefix = contextPrefix;
    }
    if (!url.startsWith("/"))
    {
      prefix += "/";
    }
    return prefix + url;
  }

  public static String getIconDir()
  {
    try
    {
      return getParamString("icons", true);
    }
    catch (Exception e)
    {
      return "icons";
    }
  }
  public static String getImageDir()
  {
    try
    {
      return getParamString("imagedir", true);
    }
    catch (Exception e)
    {
      return "images";
    }
  }
  public static String getImportDir()
  {
    try
    {
      return getParamString("importDir", true);
    }
    catch (Exception e)
    {
      return "_imports";
    }
  }
  public static String getUploadDir()
  {
    try
    {
      return getParamString("uploadDir", true);
    }
    catch (Exception e)
    {
      return "_uploads";
    }
  }
  protected static boolean getParamBoolean(String key, boolean required) throws Exception
  {
    return StringKit.True(getParamString(key, required));
  }
  protected static int getParamInt(String key, boolean required) throws Exception
  {
    String v = getParamString(key, required);
    try
    {
      return Integer.parseInt(v);
    }
    catch (Exception e)
    {
      return 0;
    }
  }
  protected static String getParamString(String key, boolean required) throws Exception
  {
    String s=null;
    if (context == null)
    {
      initTestModeParameters();
      s = testModeParameters.get(key);
    }
    else
    {
      s = context.getInitParameter(key);
    }
    if (s == null)
    {
      if (required)
      {
        throw new Exception("Required context value " + key + " not found.");
      }
    }
    return s;
  }
  public static String getPatreonWebhook() throws Exception
  {
	  try
	    {
	      return getParamString("patreonWebhook", true);
	    }
	    catch (Exception e)
	    {
	      return "";
	    }
  }
  public static String getRealImageDir() throws Exception
  {
    return realImageDir;
  }
  public static String getRealImportDir() throws Exception
  {
    return realImportDir;
  }
  public static String getRealPath(String p) throws Exception
  {
    if (context == null)
    {
      return testModeAppHome.getAbsolutePath() + File.separatorChar + p;
    }
    if (p==null || p.equals(""))
    {
      p = "" + File.separatorChar;
    }
    else if (p.charAt(0) != File.separatorChar)
    {
      p = File.separatorChar + p;
    }
    return context.getRealPath(p);
  }
  public static String getRealUploadDir() throws Exception
  {
    return realUploadDir;
  }
  public static void init(ServletContextEvent evn)
  {
    StringKit.println("Initializing on jdk " + System.getProperty("java.version") + "...");
    setContext(evn.getServletContext());
    if (context != null)
    {
    	
    }
    System.setProperty("mail.mime.charset","utf-8");
    LoginDb loginDb = new LoginDb();
    try
    {
      loginDb.open();
      loginDb.startup();
      StringKit.println("initialized " + context.getRealPath("/"));
    }
    catch (Exception e)
    {
      StringKit.println(" failed to initialize context:" + e.getMessage());
      e.printStackTrace();
    }
    finally
    {
      loginDb.close();
    }

  }
  //
  private static void initTestModeParameters()
  {
    if (testModeParameters==null && testModeAppHome.exists())
    {
      File f = new File(testModeAppHome.getAbsolutePath() + "/WEB-INF/web.xml");
      if (f.exists())
      {
        try
        {
          Map<String,String>m = new HashMap<String,String>();
          String data = FileKit.readToString(f);
          Document doc = XmlDoc.create(data);
          NodeList contextParams = doc.getElementsByTagName("context-param");
          for (int i=0;i<contextParams.getLength();i++)
          {
            String k = null;
            String v = null;
            Node contextParam = contextParams.item(i);
            NodeList contextParamKids = contextParam.getChildNodes();
            for (int j=0;j<contextParamKids.getLength();j++)
            {
              Node kid = contextParamKids.item(j);
              if (kid.getNodeName().equals("param-name"))
              {
                k = kid.getFirstChild().getNodeValue();
              }
              else if (kid.getNodeName().equals("param-value"))
              {
                v = kid.getFirstChild().getNodeValue();
              }
            }
            if (k != null)
            {
              m.put(k,Coerce.toString(v));
            }
          }
          testModeParameters = m;
        }
        catch (Exception e)
        {
          StringKit.println("Failed to initialize testModeMap from " + f.getName() + ":" + e.getMessage());
        }
      }
      else
      {
        StringKit.println("Failed to initialize testModeMap from " + f.getName() + " not found.");
      }
    }
  }
  public static boolean isDev() throws Exception
  {
    if (isDev == null)
    {
      isDev = getEnv().equalsIgnoreCase("dev");
    }
    return isDev;
  }
  public static boolean isEmailSecure() throws Exception
  {
    return getParamBoolean("emailssl",false );
  }
  public static boolean isEnding()
  {
    return isEnding;
  }
  public static boolean isProd() throws Exception
  {
    return getEnv().equalsIgnoreCase("prod");
  }
  public static boolean isTest()
  {
    if (context == null)
    {
      return testModeAppHome.getAbsolutePath().indexOf("test") > -1;
    }
    if (contextPath.indexOf("test") > -1)
    {
      return true;
    }
    try
    {
      return ! isProd();
    }
    catch (Exception e)
    {
      return false;
    }
  }
  private static void setContext(ServletContext ctx)
  {
    context = ctx;
    contextPath = ctx.getContextPath().substring(1).toLowerCase();
    realImageDir = context.getRealPath(File.separatorChar + getImageDir());
    realImportDir = context.getRealPath(File.separatorChar + getImportDir());
    realUploadDir = context.getRealPath(File.separatorChar + getUploadDir());
    createMissingDirectories();
  }
  public static void setContextPath(HttpServletRequest req)
  {
    if (contextPath == null)
    {
      contextPath = WebKit.getFullContextPath(req).toLowerCase();
    }
  }
}

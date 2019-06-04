package sr.data;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import sr.web.App;
import ir.data.Database;
import ir.data.DbCommand;
import ir.data.IMultilingualRow;
import ir.data.IRecord;
import ir.data.IRow;
import ir.data.Language;
import ir.data.MultilingualRowComparator;
import ir.util.Coerce;
import ir.util.StringKit;
import ir.web.WebKit;

/**
 *
 */
public class AppDb extends Database
{
  private static Map<Integer, AppDb> activePerThread = new ConcurrentHashMap<Integer, AppDb>();
  private static Map<String, Object> connectionVariables = new ConcurrentHashMap<String, Object>();
  public static String[][] connectParameters = new String[][]{
    {"useOldAliasMetadataBehavior","true"}
  };
  private static final Map<String,String>sqlStatements=new HashMap<String,String>();
  private final UserRec user;
  public static AppRec cast(IRecord rec)
  {
    return (AppRec)rec;
  }
  static String enhanceUrl(String uBase)
  {
    String url = uBase;
    for (String[] p : connectParameters)
    {
      String urlTest = url.replace('?','&').toLowerCase();
      String param = p[0];
      String value = p[1];
      if (! urlTest.contains("&" + param.toLowerCase() + "&"))
      {
        url = WebKit.url(url,param,value);
      }
    }
    return url;
  }
  public static AppDb getThreadInstance()
  {
    return activePerThread.get(Thread.currentThread().hashCode());
  }
  public static boolean isDev() throws Exception
  {
    return App.isDev();
  }
  public static AppDb open(UserRec theUsr) throws Exception
  {
    AppDb db = new AppDb(theUsr);
    db.open();
    return db;
  }
  public static void shutdown()
  {
    try
    {
      closePool(App.getDbDriver(), App.getDbUrl(), App.getDbUser(), App.getDbPwd());
    }
    catch (Exception e)
    {
      StringKit.println("AppDb.shutdown:" + e.getMessage());
    }
  }
  /**
   * construction
   */
  protected AppDb(UserRec user)
  {
    super();
    this.user = user;
  }
  private void afterOpen() throws Exception
  {
    File imageFolder = new File(App.getRealImageDir() + "/");
    if (!imageFolder.exists())
    {
      imageFolder.mkdirs();
    }
    if (sqlStatements.size()==0)
    {
      sqlStatements.putAll(Sql.getMap());
    }
    loadNamedStatements(sqlStatements);
    putConnectionVar("us", user.Row);
    execute("set @us=?",user.Row);//@cn necessary for cache loading
    activePerThread.put(Thread.currentThread().hashCode(), this);
  }
  /**
   * If record uses a sequence, sets the sequence field and returns -1.
   * <br>If record uses autoincrement, returns the index of the auto field,
   * <br>Otherwise returns -1;
   */
  @Override
  protected int beforeInsertAuto(IRecord rec) throws Exception
  {
    return super.beforeInsertAuto(rec);
  }
  @Override
  public void close()
  {
    activePerThread.remove(Thread.currentThread().hashCode());
    super.close();
  }
  @Override
  public boolean delete(IRecord rec) throws Exception
  {
    return super.delete(rec);
  }
  @Override
  public boolean exists(IRecord rec, Object... keys) throws Exception
  {
    setKey(rec, keys);
    return super.exists(rec, keys);
  }
  public Comparator<?> getComparator(Class<? extends IMultilingualRow> cls,Language lang) throws Exception
  {
    return new MultilingualRowComparator(lang);
  }
  @SuppressWarnings("unchecked")
  private <T extends Object> T getConnectionVar(String k, T dft)
  {
    Object v = connectionVariables.get(getConnection().hashCode() + "." + k);
    return v == null ? dft : (T) v;
  }
  protected String getMysqlTimezoneOffset(int timezoneHoursOffset)
  {
    return (timezoneHoursOffset >= 0 ? "+" : "") + timezoneHoursOffset + ":00";
  }
  public String getName(IMultilingualRow rec) throws Exception
  {
    switch (this.language)
    {
      case French:
        return StringKit.coalesce(rec.getNameFr(),rec.getNameEn());
      case Spanish:
        return StringKit.coalesce(rec.getNameSp(),rec.getNameEn());
      default:
        return rec.getNameEn();
    }
  }
  public List<List<Object>> getStats() throws Exception
  {
    List<List<Object>> r = new ArrayList<List<Object>>();
    String[] keys = new String[] { "key_read%" };
    for (String key : keys)
    {
      r.addAll(selectReader("show status like ?", key).toMatrix());
    }
    return r;
  }
  public UserRec getUser() throws Exception
  {
    return (UserRec) user.copy();
  }
  @Override
  public String getUserMail()
  {
    return user.EMail;
  }
  @Override
  public boolean insert(IRecord rec) throws Exception
  {
    return super.insert(rec);
  }
  int key(IRecord rec) throws Exception
  {
    String[] ka = rec.getCacheKey(this).split(Pattern.quote("."));
    return Coerce.toInt(ka[ka.length - 1]);
  }
  protected void open() throws Exception
  {
    String url = enhanceUrl(App.getDbUrl());
    open(App.getDbDriver(),
        url,
        App.getDbUser(),
        App.getDbPwd(),
        App.getDbPoolSize());
    afterOpen();
  }
  private void putConnectionVar(String k, Object v)
  {
    connectionVariables.put(getConnection().hashCode() + "." + k, v);
  }
  public String[] selectLastLoginInfo() throws Exception
  {
    return selectScalarArray("AppDb.selectLastLoginInfo", String.class);
  }
  /**
   * override the defualt select list to filter for SourceBooks
   * check which source books the user has enabled
   * and filter the list for those books.
   * Only if the IRow contains column "Source"
   */
  @Override
  public <E extends IRow> List<E> selectList(String sql, Class<E> c, Object... parameters) throws Exception
  {
	  List<E> list = prepareCmd(sql).selectList(c, parameters);
	  List<E> filteredList = new ArrayList<E>();
	  UserRec user = getUser();
	  try {
		  c.getField("Source");
		  for(E item : list)
		  {
			  String source = (String) item.getValue(item.getFieldIndex("Source"));
			  if(user.isGuest())
			  {
				  item.setValue(item.getFieldIndex("Description"), "");
			  }
			  if(user.SourceBooks.indexOf(source)>-1)
			  {
				  filteredList.add(item);
			  }
		  }
		  return filteredList;
	  }
	  catch (Exception e){
		  //iRow does not have source column, return list
		  return list;
	  }
  }
  public List<Integer>selectSites() throws Exception
  {
    return selectScalarList("AppDb.selectSites",Integer.class);
  }
  @Override
  public void setKey(IRecord theRec, Object... keys) throws Exception
  {
    super.setKey(theRec,keys);
  }
  public void testAll() throws Exception
  {
    StringKit.println(getClass().getSimpleName() + ".testAll...");
    Map<String,String>m = new TreeMap<String,String>(namedSql);
    for (Map.Entry<String,String> e : m.entrySet())
    {
      try
      {
        DbCommand c = prepareCmd(e.getValue());
        PreparedStatement ps = c.getStatement();
        ParameterMetaData pmd = ps.getParameterMetaData();
        for (int i=0;i<pmd.getParameterCount();i++)
        {
          ps.setObject(i + 1,null);
        }
        ps.execute();
      }
      catch (Exception ex)
      {
        StringKit.println(e.getKey() + "\n\t" + ex.getMessage() + "\n\t" + e.getValue());
      }
    }
  }
  @Override
  public boolean update(IRecord rec) throws Exception
  {
    return super.update(rec);
  }
  /**
   * xlate will look up the label for the passed key in the company-specific
   * label cache (stored in tString).  If xlate fails to find the tString record,
   * xlate will look in the global label cache AppDb.labelMap loaded from
   * gLabel.  If xlate fails to find an entry in AppDb.labelMap, it will add
   * one for the new label.
   */
  @Override
  public String xlate(String key) throws Exception
  {
    return xlateInit(key,null);
  }
  /**
   * This override of xlate will interpolate the optional extra parameters into
   * the label like java.text.MessageFormat.format where replacement parameters are
   * {0}, {1}...
   */
  @Override
  public String xlate(String string, Object... pa) throws Exception
  {
    return StringKit.format(xlate(string), pa);
  }
}
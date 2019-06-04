package sr.data;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import sr.web.App;
import ir.data.BaseRecord;
import ir.data.ColDef;
import ir.data.Database;
import ir.data.ForeignKeyOptional;
import ir.data.IMultilingualRow;
import ir.data.IRecord;
import ir.data.IValidator;
import ir.data.MultilingualRowComparator;
import ir.data.NamedRow;
import ir.util.Coerce;
import ir.util.Reflector;
import ir.util.StringKit;

public abstract class AppRec extends BaseRecord
{
  private static Map<String, Class<? extends AppRec>> subclassesByTable = new ConcurrentHashMap<String, Class<? extends AppRec>>();
  public static AppDb cast(Database db)
  {
    return (AppDb)db;
  }
  public static Class<? extends AppRec> getClassForTable(String table)
  {
    return getTableMap().get(table.toLowerCase());
  }
  @SuppressWarnings("unchecked")
  public static List<Class<? extends AppRec>>getExtenders() throws Exception
  {
    List<Class<? extends AppRec>> list = new ArrayList<Class<? extends AppRec>>();
    File folder = new File(AppRec.class.getResource("AppRec.class").getFile()).getParentFile();
    File[] files = folder.listFiles();
    for (File f : files)
    {
      if (! f.getName().endsWith("Rec.class") || f.getName().equals("AppRec.class"))
      {
        continue;
      }
      Class<?> c = Class.forName("croute.data." + StringKit.removeExtension(f.getName()));
      if (! AppRec.class.isAssignableFrom(c))
      {
        continue;
      }
      list.add((Class<? extends AppRec>)c);
    }
    return list;
  }
  public static String getImageFolder()
  {
    return "images";
  }
  @SuppressWarnings("unchecked")
  public static <T extends NamedRow> java.util.Comparator<T> getNameComparator(AppDb db, Class<T>cls)
  {
    if (IMultilingualRow.class.isAssignableFrom(cls))
    {
      return (Comparator<T>) new MultilingualRowComparator(db.getLanguage());
    }
    return new Comparator<T>()
    {
      @Override
      public int compare(NamedRow a,NamedRow b)
      {
        return a.getNameEn().compareToIgnoreCase(b.getNameEn());
      }
    };
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static synchronized Map<String, Class<? extends AppRec>> getTableMap()
  {
    if (subclassesByTable.size() == 0)
    {
      URL u = AppRec.class.getResource("AppRec.class");
      File[] fa = new File(u.getFile()).getParentFile().listFiles();
      String pkgName = AppRec.class.getPackage().getName();
      for (int i = 0; i < fa.length; i++)
      {
        File classFile = fa[i];
        String simpleName = StringKit.getFileName(classFile.getName()).toLowerCase();
        if (simpleName.endsWith("rec.class") && !simpleName.equals("apprec.class") && ! simpleName.equals("sharedrec.class"))
        {
          String className = classFile.getName().substring(0, classFile.getName().length() - 6);
          String fullClassName = pkgName + "." + className;
          try
          {
            Class objectClass = Class.forName(fullClassName);
            if (objectClass.isInterface())
            {
              continue;
            }
            Object instance = objectClass.newInstance();
            if (instance instanceof IRecord)
            {
              subclassesByTable.put(((IRecord) instance).getTable().toLowerCase(), objectClass);
            }
          }
          catch (Exception e)
          {
            StringKit.println("AppRec failed to link {0}.", fullClassName);
          }
        }
      }
    }
    return subclassesByTable;
  }
  public boolean checkNumber(IValidator iv, String columnName, String labelKey) throws Exception
  {
    return checkNumber(iv, columnName, labelKey, null, null);
  }
  public boolean checkNumber(IValidator iv, String columnName, String labelKey, Number min, Number max) throws Exception
  {
    Database db = iv.getDatabase();
    ColDef cd = db.getColDef(getTable(), columnName);
    if (!cd.isNumeric())
    {
      StringKit.println("validateNumber makes no sense for {0}.{1}", this.getTable(), columnName);
      return false;
    }
    Object vRaw = Reflector.getUpdatablePublicInstanceField(this, columnName).get(this);
    double v = Coerce.toDouble(vRaw);
    int decimals = cd.getScale();
    if (min == null)
    {
      min = cd.getMin();
    }
    if (max == null)
    {
      max = cd.getMax();
    }
    if (v < min.doubleValue() || v > max.doubleValue())
    {
      iv.addPageError("Value {0} for {1} not acceptable - must be between {2} and {3}", vRaw, db.xlate(labelKey),
          StringKit.num(min.doubleValue(), decimals), StringKit.num(max.doubleValue(), decimals));
      return false;
    }
    return true;
  }
  @Override
  protected Object clone() throws CloneNotSupportedException
  {
    // TODO Auto-generated method stub
    return super.clone();
  }
  /**
   * Allows extenders to exclude columns from toJson routine
   */
  @Override
  protected boolean excludeFromJson(String fieldName)
  {
    return fieldName.equalsIgnoreCase("pwd");
  }
  public String getImageDir()
  {
    return App.getImageDir() + "/";
  }
  @Override
  public final String[] getOptionalReferences(Database db) throws Exception
  {
    List<Field> fields = Reflector.getUpdatablePublicInstanceFieldsWith(this, ForeignKeyOptional.class);
    if (fields.size() > 0)
    {
      int i=0;
      String[] names=new String[fields.size()];
      for (Field f : fields)
      {
        names[i++] = f.getName();
      }
      return names;
    }
    return null;
  }
  public String getRealImageDir() throws Exception
  {
    return App.getRealImageDir() + "/";
  }

  public int getRow()
  {
    try
    {
      Field f = Reflector.getUpdatablePublicInstanceField(this, "Row");
      if (f == null)
      {
        StringKit.println("Cannot use AppRow.getRow for " + getClass().getSimpleName());
        return hashCode();
      }
      return Coerce.toInt(f.get(this));
    }
    catch (Exception e)
    {
      return hashCode();
    }
  }
}

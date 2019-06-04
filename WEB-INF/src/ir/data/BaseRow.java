package ir.data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import ir.util.Coerce;
import ir.util.DateKit;
import ir.util.IndyMath;
import ir.util.JDate;
import ir.util.JDateTime;
import ir.util.Reflector;
import ir.util.StringKit;
import ir.util.TypeKit;
/**
 * BaseRow is an IRow adapter.
 */
public abstract class BaseRow implements IRow
{
  protected static double doubleEqualTolerance = 0.000001;
  public static final int maxJsonScale=10;
  private Map<String, Object> _tempValues = null;
  protected IRecord previousValue = null;
  public static BigDecimal bigDecimal(double d)
  {
    return new BigDecimal(d);
  }
  public static boolean eq(BigDecimal a, BigDecimal b)
  {
    return IndyMath.eq(a, b);
  }
  public static boolean eq(boolean a, boolean b)
  {
    return a == b;
  }
  public static boolean eq(double a, double b)
  {
    return Math.abs(a - b) < BaseRow.doubleEqualTolerance;
  }
  public static boolean eq(long a, long b)
  {
    return a == b;
  }
  public static String getMethodName()
  {
    StackTraceElement upOne = new Exception().getStackTrace()[1];
    return StringKit.getExtension(upOne.getClassName()) + "." + upOne.getMethodName();
  }
  public static String makeContains(String like)
  {
    if (like == null || like.trim().equals(""))
    {
      return "%";
    }
    if (like.equals("_"))
    {//match any non-blank/non-null value
      return "_";
    }
    if (like.indexOf('%') == -1)
    {//user entered percent sign, assume they know what they're doing
      if (like.charAt(0) != '%')
      {
        like = "%" + like;
      }
      if (like.charAt(like.length() - 1) != '%')
      {
        like += "%";
      }
    }
    return like;
  }
  public static String makeStartsWith(String like)
  {
    if (like == null || like.trim().equals(""))
    {
      return "%";
    }
    if (!like.endsWith("%"))
    {
      like += "%";
    }
    return like;
  }
  public static boolean ne(BigDecimal a, BigDecimal b)
  {
    return IndyMath.ne(a, b);
  }
  public static boolean ne(BigDecimal a, BigDecimal b,BigDecimal tolerance)
  {
    return IndyMath.ne(a, b,tolerance);
  }
  public static boolean ne(boolean a, boolean b)
  {
    return a != b;
  }
  public static boolean ne(double a, double b)
  {
    return ne(a,b,BaseRow.doubleEqualTolerance);
  }
  public static boolean ne(double a, double b,double tolerance)
  {
    return Math.abs(a - b) >= Math.abs(tolerance);
  }
  public static boolean ne(long a, long b)
  {
    return a != b;
  }
  public static boolean nz(BigDecimal d)
  {
    return IndyMath.nz(d);
  }
  public static boolean nz(double d)
  {
    return IndyMath.nz(d);
  }
  public static boolean nz(double d,int scale)
  {
    return IndyMath.nz(d,scale);
  }
  public static int oneMax(int v)
  {
    return v == 1 ? Integer.MAX_VALUE : v;
  }
  public static BigDecimal round(BigDecimal d, int digits)
  {
    return IndyMath.round(d, digits);
  }
  public static double round(double d, int digits)
  {
    return IndyMath.round(d, digits);
  }
  public static int roundInt(double d)
  {
    return IndyMath.roundInt(d);
  }
  /**
   * returns {rec},{rec2},{...} WITHOUT wrapping [] or new Array()
   *
   * @return
   */
  public static String toJson(List<? extends IRow> lst)
  {
    if (lst == null || lst.size()==0)
    {
      return "[]";
    }
    StringBuilder b = new StringBuilder("[");
    String comma = "";
    for (IRow r : lst)
    {
      b.append(comma).append(r.toString());
      comma = ",\n";
    }
    return b.append("]").toString();
  }
  public static boolean z(BigDecimal d)
  {
    return IndyMath.isZero(d);
  }
  public static int zeroMax(int v)
  {
    return v == 0 ? Integer.MAX_VALUE : v;
  }
  public static JDate zeroMax(JDate d)
  {
    return d.isZero() ? JDate.max() : d;
  }
  public static JDateTime zeroMax(JDateTime d)
  {
    return d.isZero() ? JDateTime.max() : d;
  }
  /**
   * Plug point to add processing after row data is read from result set.
   */
  @Override
  public void afterRead(Database db) throws Exception
  {
  }
  /**
   * Casts an IRecord to this type
   */
  @SuppressWarnings("unchecked")
  public <E extends IRow> E cast(IRecord irec)
  {
    return (E) irec;
  }
  /**
   * Returns a copy of this IRow implementer
   */
  @SuppressWarnings("unchecked")
  @Override
  public <E extends IRow> E copy() throws Exception
  {
    return (E) super.clone();
  }
  /**
   * Allows extenders to exclude columns from toJson routine
   */
  protected boolean excludeFromJson(String fieldName)
  {
    return false;
  }
  /**
   * Gets the index of the passed column
   */
  @Override
  public int getColumnIndex(String colName)
  {
    return StringKit.indexOfNoCase(getColumns(), colName);
  }
  /**
   * Returns an array of column names to exchange data with the fields
   * returned by getFields, and therefore must be parallel to getFields.
   * Default implementation, just return getFields()
   */
  @Override
  public String[] getColumns()
  {
    return getFields();
  }
  /**
   * Gets the index of the passed field
   */
  @Override
  public int getFieldIndex(String fldName)
  {
    return StringKit.indexOfNoCase(getFields(), fldName);
  }
  protected List<Field> getFieldInfo()
  {
    return Reflector.getUpdatablePublicInstanceFields(this);
  }
  /**
   * Returns array of fields to be mapped to columns returned by getColumns,
   * and therefore must be parallel to getColumns. Default implementation,
   * just get all public,updatable fields by using Reflection.
   */
  @Override
  public String[] getFields()
  {
    List<String> lst = Reflector.getUpdatablePublicInstanceFieldNames(this);
    String[] ret = new String[lst.size()];
    lst.toArray(ret);
    return ret;
  }
  @Override
  public <T> T getTemp(String k, Class<T> cReturnType) throws Exception
  {
    if (_tempValues == null)
    {
      return null;
    }
    Object o = _tempValues.get(k);
    if (o == null)
    {
      return null;
    }
    return TypeKit.convert(o, cReturnType);
  }
  @Override
  public <T> T getTemp(String k, Class<T> cReturnType,T defaultValue) throws Exception
  {
    if (_tempValues == null)
    {
      return defaultValue;
    }
    Object o = _tempValues.get(k);
    if (o == null)
    {
      return defaultValue;
    }
    return TypeKit.convert(o, cReturnType);
  }
  /**
   * Gets a value from a field by its ordinal position within the list
   * returned by GetColumns
   */
  @Override
  public Object getValue(int Ordinal) throws Exception
  {
    List<Field> fia = getFieldInfo();
    if (Ordinal < 0 || Ordinal > fia.size())
    {
      throw new Exception(getClass().getName() + ".getValue field index " + Ordinal + " is invalid.");
    }
    return fia.get(Ordinal).get(this);
  }
  public boolean hasTempValue(String k)
  {
    return _tempValues != null && _tempValues.get(k) != null;
  }
  private String json(Object o)
  {
    if (o instanceof Boolean || o instanceof Number || o instanceof Integer || o instanceof Byte || o instanceof Short
        || o instanceof Float || o instanceof Long || o instanceof Double)
    {
      return o.toString();
    }
    return jsq(o);
  }
  protected String jsq(Object v)
  {
    return StringKit.jsq(v);
  }
  @Override
  public void setTemp(String k, Object o)
  {
    if (_tempValues == null)
    {
      _tempValues = new HashMap<String, Object>();
    }
    _tempValues.put(k, o);
  }

  /**
   * Assigns a value to a field by its ordinal position within the list
   * returned by GetColumns
   */
  @Override
  public void setValue(int Ordinal, Object oVal) throws Exception
  {
    List<Field> fia = getFieldInfo();
    if (Ordinal < 0 || Ordinal > fia.size())
    {
      throw new Exception(getClass().getName() + ".setValue field index " + Ordinal + " is invalid.");
    }
    Field f = fia.get(Ordinal);
    Reflector.setPublicInstanceValue(this, f, oVal);
  }
  /**
   * Assigns values to fields by matching on attributes on JSONObject.
   * JSONObject attributes not matching fields get loaded as temporary
   * values on the row.
   */
  @Override
  public void setValuesFrom(JSONObject jso) throws Exception
  {
    String[] fa = getFields();
    for (int fi = 0; fi < fa.length; fi++)
    {
      String fn = fa[fi];
      if (jso.has(fn))
      {
        setValue(fi, jso.get(fn));
      }
    }
    @SuppressWarnings("unchecked")
    Iterator<String> iter = jso.keys();
    while (iter.hasNext())
    {
      String key = iter.next();
      if (-1 == getFieldIndex(key))
      {
        setTemp(key,jso.get(key));
      }
    }
  }
  /**
   * Sets values on passed JSONObject from this row's fields
   * and temporary values.
   */
  @Override
  public void setValuesOn(JSONObject jso) throws Exception
  {
    String[] fa = getFields();
    for (int fi = 0; fi < fa.length; fi++)
    {
      String fn = fa[fi];
      jso.put(fn,this.getValue(fi));
    }
    if (_tempValues != null)
    {
      for (String tempValueKey : _tempValues.keySet())
      {
        jso.put(tempValueKey,_tempValues.get(tempValueKey));
      }
    }
  }
  protected String toJsonString(Object o) throws Exception
  {
    String v = Coerce.toString(o);
    if (o instanceof Boolean)
    {
      return v;
    }
    if (o instanceof BigDecimal)
    {
      return StringKit.num((BigDecimal) o,maxJsonScale);
    }
    if (TypeKit.isNumber(o))
    {
      return StringKit.num(Coerce.toDouble(o),maxJsonScale);
    }
    if (o instanceof JDateTime)
    {
      if (v.equals(""))
      {
        return "\"0000-00-00 00:00:00\"";
      }
      else
      {
        return "\"" + JDateTime.parse(v).ymdhms() + "\"";
      }
    }
    if (o instanceof Date)
    {
      if (v.equals(""))
      {
        return "\"0000-00-00 00:00:00\"";
      }
      else
      {
        return "\"" + DateKit.ymdhms(DateKit.parse(v)) + "\"";
      }
    }
    return jsq(v);
  }
  /**
   * Overridden to return JSON
   * {t:getClass().getSimpleName(),col:val,col:val,...}
   */
  @Override
  public String toString()
  {
    StringBuilder b = new StringBuilder();
    String c = "";
    b.append("{");
    try
    {
      String[] fa = getFields();
      for (String fn : fa)
      {
        if (excludeFromJson(fn.toLowerCase()))
        {
          continue;
        }
        Field f = Reflector.getUpdatablePublicInstanceField(this, fn);
        String v = toJsonString(f.get(this));
        b.append(c).append(jsq(fn)).append(":").append(v);
        c = ",";
      }
      if (_tempValues != null)
      {
        for (String k : _tempValues.keySet())
        {
          if (excludeFromJson(k.toLowerCase()))
          {
            continue;
          }
          Object o = _tempValues.get(k);
          String n = Coerce.toString(k);
          for (char ch : n.toCharArray())
          {
            if (!Character.isJavaIdentifierPart(ch))
            {
              k = jsq(n);
              break;
            }
          }
          b.append(c).append(jsq(n)).append(":").append(json(o));
          c = ",";
        }
      }
      String _t;
      if (this instanceof IRecord)
      {
        _t = ((IRecord)this).getTable();
      }
      else
      {
        _t = getClass().getSimpleName();
      }
      b.append(c).append(jsq("_t")).append(":").append(jsq(_t));
    }
    catch (Exception e)
    {
      b.append(c).append("\"exc\":\"").append(jsq(e.getMessage())).append("\"");
    }
    b.append("}");
    return b.toString();
  }
}

package ir.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coerce
{

  private static SimpleDateFormat _dfDft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static Map<Class<?>, Method> _myCoercers = new HashMap<Class<?>, Method>();
  static
  {
    try
    {
      Method[] ma = Coerce.class.getMethods();
      for (Method m : ma)
      {
        int mods = m.getModifiers();
        if (m.getName().startsWith("to") && Modifier.isStatic(mods) && Modifier.isPublic(mods))
        {
          _myCoercers.put(m.getReturnType(), m);
        }
      }
    }
    catch (Exception nevermind)
    {
    }
  }
  //
  public static boolean equals(Object v1, Object v2)
  {
    if (v1 == null)
    {
      if (v2 == null)
      {
        return true;
      }
      return false;
    }
    else if (v2 == null)
    {
      return false;
    }
    else
    {
      return v2.equals(like(v1, v2));
    }
  }
  @SuppressWarnings("unchecked")
  public static <A> A like(Object val, Object template)
  {
    if (template == null)
    {
      return null;
    }
    try
    {
      return (A) TypeKit.convert(val, template);
    }
    catch (Exception e)
    {
      return null;
    }
  }
  private static long time(Object o)
  {
    if (o == null)
      return 0;
    try
    {
      return _dfDft.parse(o.toString()).getTime();
    }
    catch (Exception e)
    {
      try
      {
        return DateKit.parse(o.toString()).getTime();
      }
      catch (Exception ee)
      {
        System.out.println("Failed to coerce " + o.toString() + " to Date.");
        return 0;
      }
    }
  }
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <A> A to(Object val, Class<A> targetType) throws Exception
  {
    if (targetType.isEnum())
    {
      Enum[] ca = (Enum[]) targetType.getEnumConstants();
      if (val == null)
      {
        val = ca[0];
      }
      else if (val instanceof Integer)
      {
        int i = (Integer) val;
        val = ca[0];
        if (i >= 0 && i < targetType.getEnumConstants().length)
        {
          val = ca[i];
        }
      }
      else
      {
        for (int i = 0; i < ca.length; i++)
        {
          if (val.toString().equals(ca[i].name()))
          {
            val = ca[i];
            break;
          }
        }
        if (val instanceof String)
        {
          val = ca[0];
        }
      }
      return (A) val;
    }
    else if (val instanceof Clob)
    {
      return (A) Coerce.toStr((Clob) val);
    }
    try
    {
      Method m = _myCoercers.get(targetType);
      if (m != null)
      {
        return (A) m.invoke(null, val);
      }
    }
    catch (Exception giveUp)
    {
    }
    if (val == null)
    {
      try
      {
        return targetType.newInstance();
      }
      catch (Exception nevermind)
      {
      }
    }
    return targetType.cast(val);
  }
  public static BigDecimal toBigDecimal(Object o)
  {
    if (o == null || o.toString().length() == 0)
    {
      return BigDecimal.ZERO;
    }
    String v = o.toString().replace(",", "");
    try
    {
      return new BigDecimal(v);
    }
    catch (Exception useCoerceThen)
    {
      return new BigDecimal(toDouble(v));
    }
  }
  public static boolean toBool(Object o)
  {
    if (o == null)
    {
      return false;
    }
    if (o instanceof Boolean)
    {
      return ((Boolean)o).booleanValue();
    }
    String v = toString(o).toLowerCase().trim();
    if (v.equals(""))
    {
      return false;
    }
    if (v.charAt(0) == 'y' || v.charAt(0) == 't' || v.startsWith("checked") || v.startsWith("on"))
    {
      return true;
    }
    if (v.charAt(0) == 'n' || v.charAt(0) == 'f' || v.startsWith("off"))
    {
      return false;
    }
    try
    {// any non-zero numeric is true
      if (toDouble(o) != 0)
      {
        return true;
      }
    }
    catch (Exception e)
    {
    }
    return false;
  }
  public static byte toByte(Object o)
  {
    return o == null ? 0 : Byte.valueOf(o + "");
  }
  public static byte[] toByteArray(Object o)
  {
    if (o == null)
    {
      return null;
    }
    if (o instanceof java.sql.Blob)
    {
      java.sql.Blob b = (java.sql.Blob) o;
      try
      {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = b.getBinaryStream();
        int byt;
        while (-1 != (byt = is.read()))
        {
          bos.write(byt);
        }
        is.close();
        return bos.toByteArray();
      }
      catch (Exception e)
      {
        throw new RuntimeException(e.getMessage());
      }
    }
    return (byte[]) o;
  }
  public static Byte toByteObj(Object o)
  {
    return o == null ? 0 : Byte.valueOf(0 + "");
  }
  public static char toChar(Object o)
  {
    String v = toString(o);
    return v.length() > 0 ? v.charAt(0) : ' ';
  }
  public static double toDouble(Object o)
  {
    if (o == null)
    {
      return 0;
    }
    String v = toString(o).trim().replace(",", "");
    if (v.length()==0)
    {
      return 0;
    }
    try
    {
      double dv;
      int eAt = v.toLowerCase().indexOf("e");
      if (eAt > 0)
      {
        int e = toInt(v.substring(eAt + 1));
        dv = Double.parseDouble(v.substring(0, eAt)) * Math.pow(10, e);
      }
      else
      {
        dv = Double.parseDouble(v);
      }
      if (Double.isNaN(dv))
      {
        return 0;
      }
      return dv;
    }
    catch (Exception e)
    {
    }
    return 0;
  }
  public static float toFloat(Object o)
  {
    return (float) toDouble(o);
  }
  public static int[] toInt(List<?> lst)
  {
    int[] a = new int[lst.size()];
    for (int i = 0; i < lst.size(); i++)
    {
      a[i] = Coerce.toInt(lst.get(i));
    }
    return a;
  }
  public static int toInt(Object o)
  {
    return (int) toDouble(o);
  }
  public static Integer toInteger(Object o)
  {
    return (int) toDouble(o);
  }
  public static long[] toLong(List<?> lst)
  {
    long[] a = new long[lst.size()];
    for (int i = 0; i < lst.size(); i++)
      a[i] = Coerce.toLong(lst.get(i));
    return a;
  }
  //
  public static long toLong(Object o)
  {
    return (long) toDouble(o);
  }
  public static short toShort(Object o)
  {
    return (short) toDouble(o);
  }
  public static Short toShortObj(Object o)
  {
    return (short) toDouble(o);
  }
  //
  public static java.sql.Date toSqlDate(Object o)
  {
    if (o != null && o instanceof java.sql.Date)
      return (java.sql.Date) o;
    java.sql.Date v = new java.sql.Date(0);
    if (o != null)
    {
      if (o instanceof java.sql.Timestamp)
        v.setTime(((java.sql.Timestamp) o).getTime());
      else if (o instanceof java.sql.Time)
        v.setTime(((java.sql.Time) o).getTime());
      else if (o instanceof java.util.Date)
        v.setTime(((java.util.Date) o).getTime());
      else
        v.setTime(time(o));
    }
    return v;
  }
  public static java.sql.Time toSqlTime(Object o)
  {
    if (o != null && o instanceof java.sql.Time)
      return (java.sql.Time) o;
    java.sql.Time v = new java.sql.Time(0);
    if (o != null)
    {
      if (o instanceof java.sql.Timestamp)
        v.setTime(((java.sql.Timestamp) o).getTime());
      else if (o instanceof java.sql.Date)
        v.setTime(((java.sql.Date) o).getTime());
      else if (o instanceof java.util.Date)
        v.setTime(((java.util.Date) o).getTime());
      else
        v.setTime(time(o));
    }
    return v;
  }
  public static java.sql.Timestamp toSqlTimestamp(Object o)
  {
    if (o != null && o instanceof java.sql.Timestamp)
      return (java.sql.Timestamp) o;
    java.sql.Timestamp v = new java.sql.Timestamp(0);
    if (o != null)
    {
      if (o instanceof java.sql.Time)
        v.setTime(((java.sql.Time) o).getTime());
      else if (o instanceof java.sql.Date)
        v.setTime(((java.sql.Date) o).getTime());
      else if (o instanceof java.util.Date)
        v.setTime(((java.util.Date) o).getTime());
      else
        v.setTime(time(o));
    }
    return v;
  }
  //
  private static String toStr(Clob clb) throws Exception
  {
    Reader rdr = null;
    try
    {
      char[] ca = new char[16384];
      int charsRead = 0;
      StringBuffer b = new StringBuffer((int) clb.length());
      rdr = clb.getCharacterStream();
      while (0 < (charsRead = rdr.read(ca)))
        b.append(ca, 0, charsRead);
      return b.toString();
    }
    finally
    {
      if (rdr != null)
        rdr.close();
    }
  }
  public static String toString(Object o)
  {
    if (o == null)
    {
      return "";
    }
    if (o instanceof byte[])
    {
      return new String((byte[]) o);
    }
    if (o instanceof BigDecimal)
    {
      BigDecimal bd = (BigDecimal)o;
      return StringKit.num(bd,bd.scale());
    }
    return o.toString();
  }
  public static java.util.Date toUtilDate(Object o)
  {
    if (o != null && o instanceof java.util.Date)
      return (java.util.Date) o;
    java.util.Date v = new java.util.Date(0);
    if (o != null)
    {
      if (o instanceof java.sql.Date)
        v.setTime(((java.sql.Date) o).getTime());
      if (o instanceof java.sql.Time)
        v.setTime(((java.sql.Time) o).getTime());
      else if (o instanceof java.sql.Timestamp)
        v.setTime(((java.sql.Timestamp) o).getTime());
      else
        v.setTime(time(o));
    }
    return v;
  }
  public static int toZeroOne(Object v)
  {
    return Coerce.toBool(v) ? 1 : 0;
  }
}

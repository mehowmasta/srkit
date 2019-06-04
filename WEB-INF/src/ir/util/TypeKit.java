package ir.util;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeKit
{
  private static TypeKit _inst = null;
  private final Map<Class<?>, ITypeUtil> _map = new ConcurrentHashMap<Class<?>, ITypeUtil>();
  /**
   * Coerce the passed value to the class of the passed template.
   */
  @SuppressWarnings("unchecked")
  public static <A> A convert(Object val, A oTemplate) throws Exception
  {
    if (oTemplate == null)
    {
      throw new Exception("Template cannot be null.");
    }
    if (oTemplate instanceof Enum)
    {
      return (A) getInstance().getUtil(Enum.class).convert(val, oTemplate.getClass());
    }
    return (A) convert(val, oTemplate.getClass());
  }
  /**
   * Coerce the passed value to the passed class. Will even try to handle null
   * values.
   */
  @SuppressWarnings("unchecked")
  public static <A> A convert(Object val, Class<A> c) throws Exception
  {
    try
    {
      if (val != null && val.getClass().equals(c))
      {
        return (A) val;
      }
      if (val != null)
      {
        return c.cast(val);
      }
    }
    catch (Exception e)
    {
      System.out.print("");// just a breakpoint landing pad
    }
    return getInstance().cvt(c, val);
  }
  protected static synchronized TypeKit getInstance() throws Exception
  {
    if (_inst != null)
    {
      return _inst;
    }
    _inst = new TypeKit();
    return _inst;
  }
  public static boolean isNumber(Object o)
  {
    if (o == null)
    {
      return false;
    }
    return (o instanceof Double || o instanceof Float || o instanceof Integer || o instanceof Long || o instanceof Short
        || o instanceof Number || o instanceof Byte);
  }
  /**
   * Returns the non-null default value for the class, if any is configured,
   * or null;
   */
  public static <A> A nullDefault(Class<A> c) throws Exception
  {
    return getInstance().nullDft(c);
  }
  protected TypeKit() throws Exception
  {
    registerConverters();
  }
  private void addUtil(Class<?> clz, ITypeUtil itu)
  {
    _map.put(clz, itu);
  }
  @SuppressWarnings("unchecked")
  protected <A> A cvt(Class<A> c, Object val) throws Exception
  {
    try
    {
      if (val == null)
        return nullDefault(c);
      ITypeUtil tu = getUtil(c);
      if (tu != null)
      {
        return (A) tu.convert(val, c);
      }
      return c.cast(val);
    }
    catch (Exception e)
    {
      if (val == null)
        return null;// maybe no converter is registered
      throw new Exception("Unable to convert [" + val.getClass().getName() + " '" + val + "'] to " + c.getName());
    }
  }
  protected ITypeUtil getUtil(Class<?> c)
  {
    Class<?> searchClass = c;
    if (c.isEnum())
    {
      searchClass = Enum.class;
    }
    return _map.get(searchClass);
  }
  @SuppressWarnings("unchecked")
  protected <A> A nullDft(Class<A> c) throws Exception
  {
    try
    {
      ITypeUtil tu = getUtil(c);
      if (tu != null)
      {
        return (A) tu.nullDefault(c);
      }
      return c.newInstance();
    }
    catch (Exception e)
    {
      return null;
    }
  }
  private void registerConverters()
  {
    addUtil(BigDecimal.class, new BigDecimalUtil());
    addUtil(Boolean.class, new BooleanUtil());
    addUtil(Boolean.TYPE, new BooleanUtil());
    addUtil(Byte.class, new ByteUtil());
    addUtil(Byte.TYPE, new ByteUtil());
    addUtil(Character.class, new CharacterUtil());
    addUtil(Character.TYPE, new CharacterUtil());
    addUtil(Double.class, new DoubleUtil());
    addUtil(Double.TYPE, new DoubleUtil());
    addUtil(Enum.class, new EnumUtil());
    addUtil(Float.class, new FloatUtil());
    addUtil(Float.TYPE, new FloatUtil());
    addUtil(Integer.class, new IntegerUtil());
    addUtil(Integer.TYPE, new IntegerUtil());
    addUtil(JDate.class, new JDateUtil());
    addUtil(JDateTime.class, new JDateTimeUtil());
    addUtil(Long.class, new LongUtil());
    addUtil(Long.TYPE, new LongUtil());
    addUtil(Short.class, new ShortUtil());
    addUtil(Short.TYPE, new ShortUtil());
    addUtil(String.class, new StringUtil());
  }
  protected class BigDecimalUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null)
      {
        return BigDecimal.ZERO;
      }
      try
      {
        return new BigDecimal(o.toString().replace(",", ""));
      }
      catch (Exception nevermindCoerce)
      {
        return new BigDecimal(Coerce.toDouble(o) + "");
      }
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return BigDecimal.ZERO;
    }
  }
  protected class BooleanUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null || o.toString().equals(""))
        return false;
      return StringKit.True(o.toString());
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return false;
    }
  }
  protected class ByteUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o instanceof Byte)
      {
        return o;
      }
      if (o == null || o.toString().equals(""))
      {
        return 0;
      }
      if (o instanceof Integer)
      {
        return ((Integer) o).byteValue();
      }
      if (o instanceof Short)
      {
        return ((Short) o).byteValue();
      }
      try
      {
        return Byte.valueOf(o.toString());
      }
      catch (Exception nevermind)
      {
      }
      return o.toString().getBytes()[0];
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return (byte) 0;
    }
  }
  protected class CharacterUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null || o.toString().equals(""))
        return (char) ' ';
      return o.toString().charAt(0);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return ' ';
    }
  }

  protected class DoubleUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      return Coerce.toDouble(o);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return 0.0;
    }
  }
  protected class EnumUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      Class<?> ec = c.isEnum() ? c : c.getSuperclass();
      Object[] ea = ec.getEnumConstants();
      if (ea == null || ea.length == 0)
      {
        throw new Exception("Cannot convert " + o + (o == null ? "" : " (" + o.getClass().getName() + ")") + " to " + c.getName());
      }
      if (o != null && o.toString().length() > 0)
      {
        String s = o.toString();
        if (StringKit.isNumber(s))
        {
          int n = StringKit.intDft(s, 0);
          if (n >= 0 && n < ea.length)
          {
            return ea[n];
          }
        }
        for (Object e : ea)
        {
          if (e.toString().equalsIgnoreCase(s))
          {
            return e;
          }
        }
      }
      return ea[0];
    }
    @Override
    public Object nullDefault(Class<?> c)
    {
      return c.getEnumConstants()[0];
    }
  }
  protected class FloatUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      return Coerce.toFloat(o);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return 0f;
    }
  }

  protected class IntegerUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null || o.toString().trim().equals(""))
        return 0;
      if (o.toString().toLowerCase().equals("true"))
        return 1;
      if (o.toString().toLowerCase().equals("false"))
        return 0;
      if (o.getClass().isEnum())
      {
        Object[] ec = o.getClass().getEnumConstants();
        for (int ie = 0; ie < ec.length; ie++)
        {
          if (ec[ie] == o)
            return ie;
        }
        return 0;
      }
      if (o instanceof Boolean)
      {
        return Boolean.TRUE.equals(o) ? 1 : 0;
      }
      return Coerce.toInt(o);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return 0;
    }
  }

  public interface ITypeUtil
  {
    public Object convert(Object fromObject, Class<?> toClass) throws Exception;
    public Object nullDefault(Class<?> cl);
  }
  protected abstract class ITypeUtilBase implements ITypeUtil
  {
    @Override
    public Object nullDefault(Class<?> cl)
    {
      try
      {
        return cl.newInstance();
      }
      catch (Exception e)
      {
        return null;
      }
    }
  }
  protected class JDateTimeUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null)
        return JDateTime.zero();
      if (o.getClass().equals(java.util.Date.class))
        return new JDateTime((java.util.Date) o);
      else if (o.getClass().equals(java.sql.Date.class))
        return new JDateTime((java.sql.Date) o);
      else if (o.getClass().equals(java.sql.Timestamp.class))
        return new JDateTime((java.sql.Timestamp) o);
      else if (o.getClass().equals(JDate.class))
        return new JDateTime((JDate) o);
      return JDateTime.parse(o);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return JDateTime.zero();
    }
  }
  protected class JDateUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null)
      {
        return JDate.zero();
      }
      if (o.getClass().equals(java.util.Date.class))
      {
        return new JDate((java.util.Date) o);
      }
      if (o.getClass().equals(java.sql.Date.class))
      {
        return new JDate((java.sql.Date) o);
      }
      else if (o.getClass().equals(java.sql.Timestamp.class))
      {
        return new JDate((java.sql.Timestamp) o);
      }
      if (o.getClass().equals(JDateTime.class))
      {
        return ((JDateTime) o).getJDate();
      }
      return JDate.parse(o);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return JDate.zero();
    }
  }
  protected class LongUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      return Coerce.toLong(o);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return 0L;
    }
  }
  protected class ShortUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null || o.toString().trim().equals(""))
      {
        return 0;
      }
      if (o.toString().toLowerCase().equals("true"))
      {
        return 1;
      }
      if (o.toString().toLowerCase().equals("false"))
      {
        return 0;
      }
      if (o instanceof Integer)
      {
        return ((Integer) o).shortValue();
      }
      if (o.getClass().isEnum())
      {
        Object[] ec = o.getClass().getEnumConstants();
        for (int ie = 0; ie < ec.length; ie++)
        {
          if (ec[ie] == o)
          {
            return ie;
          }
        }
        return 0;
      }
      if (o instanceof Boolean)
      {
        return Boolean.TRUE.equals(o) ? 1 : 0;
      }
      return Coerce.toShort(o);
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return (short) 0;
    }
  }
  protected class StringUtil extends ITypeUtilBase
  {
    @Override
    public Object convert(Object o, Class<?> c) throws Exception
    {
      if (o == null)
        return "";
      if (!o.getClass().equals(String.class))
        return o.toString();
      return o;
    }
    @Override
    public Object nullDefault(Class<?> cl)
    {
      return "";
    }
  }
}

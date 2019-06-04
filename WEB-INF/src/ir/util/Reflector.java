package ir.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Reflector provides data exchange between objects through reflection - caching
 * of field objects is enabled to help performance - all methods are static, and
 * do not throw exceptions Annotation
 */
public abstract class Reflector
{
  private static Map<Class<?>,ReflectorElement> map = new ConcurrentHashMap<Class<?>,ReflectorElement>();
  /**
   * Indicates whether class c or any of its ancestors implements interface i.
   */
  public static boolean classImplements(Class<?> c,Class<?>i)
  {
    Set<Class<?>> set = new HashSet<Class<?>>();
    set.addAll(Arrays.asList(c.getInterfaces()));
    if (set.contains(i))
    {
      return true;
    }
    Class<?> sup = c.getSuperclass();
    while (sup != null && ! sup.equals(Object.class))
    {
      set.clear();
      set.addAll(Arrays.asList(sup.getInterfaces()));
      if (set.contains(i))
      {
        return true;
      }
      sup = sup.getSuperclass();
    }
    return false;
  }
  //
  static protected Object deepCopy(Object oldObj) throws Exception
  {
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    try
    {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(bos);
      // serialize and pass the object
      oos.writeObject(oldObj);
      oos.flush();
      ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
      ois = new ObjectInputStream(bin);
      // return the new object
      return ois.readObject();
    }
    catch (Exception e)
    {
      throw (e);
    }
    finally
    {
      oos.close();
      ois.close();
    }
  }
  private static ReflectorElement get(Class<?> c)
  {
    ReflectorElement ele = map.get(c);
    if (ele == null)
    {
      ele = new ReflectorElement(c);
      map.put(c,ele);
    }
    return ele;
  }
  /**
   * Gets a Map of public non-Final Field objects that on the passed object
   * 
   * @param Object
   * @return Map
   */
  protected static Map<String, Field> getFieldMap(Class<?> clz)
  {
    return Collections.unmodifiableMap(get(clz).fieldMap);
  }
  /**
   * Gets named field from object, if implemented
   * 
   * @param Object
   * @param String
   *            field name
   * @return Field or null if not implemented
   */
  public static Field getUpdatablePublicInstanceField(Object o, String fn)
  {
    return get(o.getClass()).getField(fn);
  }
  /**
   * Gets an array of the names of public non-Final Fields on the passed
   * object
   * 
   * @param Object
   * @return List of field name Strings
   */
  public static List<String> getUpdatablePublicInstanceFieldNames(Object o)
  {
    return Collections.unmodifiableList(get(o.getClass()).fieldNames);
  }
  /**
   * Gets an array of public non-Final Field objects on the passed object
   * 
   * @param Object
   * @return Field[]
   */
  public static List<Field> getUpdatablePublicInstanceFields(Object o)
  {
    return Collections.unmodifiableList(get(o.getClass()).fields);
  }
  /**
   * Gets value for named field as a string. Will return "" on null, and "1"
   * or "0" for bools.
   */
  public static String getUpdatablePublicInstanceFieldString(Object o, String fn) throws Exception
  {
    Field f = getUpdatablePublicInstanceField(o, fn);
    if (f == null)
    {
      throw new Exception("Field " + fn + " not found on " + o.getClass().getName());
    }
    Object v = f.get(o);
    if (v == null)
    {
      return "";
    }
    Class<?> c = f.getType();
    if (c == Boolean.TYPE || c == Boolean.class)
    {
      return f.getBoolean(o) ? "1" : "0";
    }
    return v.toString();
  }
  /**
   * Gets never-null list of Fields marked with passed annotation.
   */
  public static List<Field> getUpdatablePublicInstanceFieldsWith(Object o,Class<? extends Annotation> annoClass)
  {
    return get(o.getClass()).getFieldsWith(annoClass);
  }
  /**
   * Sets the value of a passed field on an object. If the value is null, the
   * field will be set from the default of a new instance.
   * 
   * @param Object
   * @param Field
   * @param String
   * @throws Exception
   */
  public static void setPublicInstanceValue(Object o, Field f, Object oVal) throws Exception
  {
    f.set(o, TypeKit.convert(oVal, f.getType()));
  }

  /**
   * Loads the values from one instance of an object to another
   * 
   * @param Object
   *            from
   * @param Object
   *            to
   */
  public static void setPublicInstanceValues(Object oFrom, Object oTo) throws Exception
  {
    List<Field> fa = Reflector.getUpdatablePublicInstanceFields(oFrom);
    Class<?> cTo = oTo.getClass();
    for (Field fFrom : fa)
    {
      int mod = fFrom.getModifiers();
      if (Modifier.isFinal(mod) || Modifier.isStatic(mod))
      {
        continue;
      }
      Field fTo = null;
      try
      {
        fTo = cTo.getField(fFrom.getName());
      }
      catch (Exception e)
      {
        continue;// must not exist
      }
      mod = fTo.getModifiers();
      if (Modifier.isFinal(mod) || Modifier.isStatic(mod))
      {
        continue;
      }
      Class<?> c = fFrom.getType();
      Object v = fFrom.get(oFrom);
      if (v == null)
      {
        fTo.set(oTo, TypeKit.nullDefault(fTo.getType()));
      }
      else
      {
        if (!c.isPrimitive() && !c.equals(String.class) && v instanceof Serializable)
        {
          fTo.set(oTo, deepCopy(v));
        }
        else
        {
          fTo.set(oTo, v);
        }
      }
    }
  }
}

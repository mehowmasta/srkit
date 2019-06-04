package ir.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This class provides utility functions for Strings
 */
public class SetKit
{
  public static <E extends Object> List<E> asList(E first,E[] latter)
  {
    List<E> lst = new ArrayList<E>();
    lst.add(first);
    if (latter != null && latter.length>0)
    {
      lst.addAll(Arrays.asList(latter));
    }
    return lst;
  }
  public static List<Integer> asList(int[]a)
  {
    List<Integer> lst = new ArrayList<Integer>();
    for (int v : a)
    {
      lst.add(v);
    }
    return lst;
  }
  /*
  public static String[] join(String first,String[] latter)
  {
    List<String> lst = new ArrayList<String>();
    lst.add(first);
    if (latter != null && latter.length>0)
    {
      lst.addAll(Arrays.asList(latter));
    }
    return lst.toArray(new String[0]);
  }
   */
  public static <T extends Object> T[] join(T first,T[] latter)
  {
    List<T> lst = new ArrayList<T>();
    lst.add(first);
    if (latter != null && latter.length>0)
    {
      lst.addAll(Arrays.asList(latter));
    }
    @SuppressWarnings("unchecked")
    T[] a = (T[]) java.lang.reflect.Array.newInstance(first.getClass(), lst.size());
    return lst.toArray(a);
  }
  public static int sum(int[] a)
  {
    int i = 0;
    for (int j : a)
    {
      i += j;
    }
    return i;
  }
  public static double[] toDouble(String[] sa)
  {
    double[] r = new double[sa.length];
    for (int i = 0; i < sa.length; i++)
    {
      r[i] = Coerce.toDouble(sa[i]);
    }
    return r;
  }
  public static int[] toInt(Integer[] lst)
  {
    int[] r = new int[lst.length];
    for (int i = 0; i < lst.length; i++)
    {
      r[i] = lst[i].intValue();
    }
    return r;
  }
  public static int[] toInt(List<Integer> lst)
  {
    int[] r = new int[lst.size()];
    for (int i = 0; i < lst.size(); i++)
    {
      r[i] = lst.get(i).intValue();
    }
    return r;
  }
  public static int[] toInt(String[] sa)
  {
    int[] r = new int[sa.length];
    for (int i = 0; i < sa.length; i++)
    {
      r[i] = StringKit.intDft(sa[i], 0);
    }
    return r;
  }
  public static Map<Integer, Integer> toIntInt(Map<String, String> m)
  {
    Map<Integer, Integer> r = new HashMap<Integer, Integer>(m.size());
    for (String k : m.keySet())
    {
      r.put(Coerce.toInt(k), Coerce.toInt(m.get(k)));
    }
    return r;
  }
  public static List<Integer> toList(int[] intArray)
  {
    List<Integer> list= new ArrayList<Integer>();
    for (int i : intArray)
    {
      list.add(i);
    }
    return list;
  }
  public static short[] toShort(String[] sa)
  {
    short[] r = new short[sa.length];
    for (int i = 0; i < sa.length; i++)
      r[i] = Coerce.toShort(sa[i]);
    return r;
  }
  public static Map<Short, Short> toShortShort(Map<String, String> m)
  {
    Map<Short, Short> r = new HashMap<Short, Short>(m.size());
    for (String k : m.keySet())
    {
      r.put(Coerce.toShort(k), Coerce.toShort(m.get(k)));
    }
    return r;
  }
}

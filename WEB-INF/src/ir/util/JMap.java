package ir.util;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
/**
 * JMap is a case-insensitive, Serializable Map that will cast values returned
 * from get to the class of a passed template.
 */
public class JMap implements Serializable
{
  private final static String eqDelim = "[=]";
  private final static String pairDelim = "[&]";
  private static final long serialVersionUID = 1L;
  private final Map<String, String> map=new TreeMap<String,String>();
  public JMap(Map<String, String> m)
  {
    load(m);
  }
  //
  public JMap(String v)
  {
    load(StringKit.toMap(v, eqDelim, pairDelim));
  }
  public boolean containsKey(String k)
  {
    return map.containsKey(k.toLowerCase());
  }
  public <A> A get(String k, A dft) throws Exception
  {
    Object o = map.get(k.toLowerCase());
    if (o == null)
    {
      put(k, dft);
      return dft;
    }
    return Coerce.like(o, dft);
  }
  private void load(Map<String, String> m)
  {
    for (String k : m.keySet())
    {
      map.put(k.toLowerCase(),m.get(k));
    }
  }
  public void put(String k, Object v)
  {
    if (v == null)
    {
      map.remove(k.toLowerCase());
    }
    else
    {
      map.put(k.toLowerCase(), Coerce.toString(v));
    }
  }
  public void remove(String k)
  {
    map.remove(k.toLowerCase());
  }
  public int size()
  {
    return map.size();
  }
  //
  @Override
  public String toString()
  {
    String d = "";
    StringBuilder b = new StringBuilder();
    for (String k : map.keySet())
    {
      b.append(d).append(k).append(eqDelim).append(map.get(k));
      d = pairDelim;
    }
    return b.toString();
  }
}

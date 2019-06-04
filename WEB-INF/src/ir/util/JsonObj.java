package ir.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonObj
{
  private JSONObject _js = null;
  private String _src = null;

  public JsonObj(JSONObject o)
  {
    _js = o;
    _src = o.toString();
  }
  public JsonObj(String s)
  {
    try
    {
      if (s == null || s.equals(""))
      {
        s = "{}";
      }
      if (s.startsWith("[") && s.endsWith("]"))
      {
        s = "new Array(" + s.substring(1, s.length() - 1) + ")";
      }
      s = s.replaceFirst("ï»¿", "");
      int index = s.indexOf('{');
      if(index!=0)
      {
      	s = s.substring(index);
      }
      _js = new JSONObject(s);
      _src = s;
    }
    catch (Exception e)
    {
      StringKit.println(e);
      System.out.println("JsonObj failed to construct from " + s);
      try
      {
        _js = new JSONObject("{}");
      }
      catch (Exception ee)
      {
      }
    }
  }
  public JsonObj get(String key) throws Exception
  {
    if (_js.has(key))
    {
      String v = _js.getString(key);
      if (v == null)
      {
        return null;
      }
      // return new JsonObj(_js.getJSONObject(key));
      return new JsonObj(v);
    }
    return null;
  }
  public JSONArray getArray(String key) throws Exception
  {
    return _js.getJSONArray(key);
  }
  public BigDecimal getBigDecimal(String key) throws Exception
  {
    return Coerce.toBigDecimal(getString(key));
  }
  public boolean getBoolean(String key) throws Exception
  {
    return Coerce.toBool(getString(key));
  }
  public JDate getDate(String key) throws Exception
  {
    return JDate.parse(getString(key));
  }
  public JDateTime getDateTime(String key) throws Exception
  {
    return JDateTime.parse(getString(key));
  }
  public double getDouble(String key) throws Exception
  {
    return Coerce.toDouble(getString(key));
  }
  public int getInt(String key) throws Exception
  {
    return Coerce.toInt(getString(key));
  }
  public JSONObject getJsonObject()
  {
	  return _js;
  }
  public Map<String, String> getProperties()
  {
    Map<String, String> m = new HashMap<String, String>();
    String[] pna = getPropertyNames();
    for (String pn : pna)
      m.put(pn, getString(pn));
    return m;
  }
  public String[] getPropertyNames()
  {
    return JSONObject.getNames(_js);
  }
  public short getShort(String key) throws Exception
  {
    return Coerce.toShort(getString(key));
  }
  public String getString(String key)
  {
    try
    {
      return _js.getString(key);
    }
    catch (Exception e)
    {
      System.out.println("JsonObj.getString failed to extract [" + key + "] from [" + _src + "]: " + e.getMessage());
    }
    return "";
  }
  public boolean has(String key)
  {
    return _js.has(key);
  }
  public void put(String key, Object v) throws Exception
  {
    _js.put(key, v);
  }
  @Override
  public String toString()
  {
    return _src;
  }
}

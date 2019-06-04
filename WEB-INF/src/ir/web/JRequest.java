package ir.web;

import ir.util.Coerce;
import ir.util.SetKit;
import ir.util.StringKit;
import ir.util.TypeKit;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
/**
 * wrapper for HttpServletRequest
 */
public class JRequest extends HttpServletRequestWrapper
{
  public static final String MultiValueDelimiter = ";";
  private Map<String, String> values = null;
  /**
   * Constructs
   */
  public JRequest(HttpServletRequest req)
  {
    super(req);
  }
  public String dump(String equalStr, String pairDelim)
  {
    return WebKit.dump(this, equalStr, pairDelim);
  }
  /**
   * Override of getParameter to look in getAttribute as well. This somewhat
   * ugly hack makes combining multi-part data with regular data on a form
   * easy.
   */
  @Override
  public String getParameter(String name)
  {
    return StringKit.rtrim(Coerce.toString(getParameterNullable(name)));
  }
  private String getParameterNullable(String name)
  {
    String v = null;
    if (values != null)
    {
      v = values.get(name);
    }
    if (v == null)
    {
      String[] pv = super.getParameterValues(name);
      if (pv != null)
      {
        if (pv.length == 1)
        {
          v = pv[0];
        }
        else
        {
          v = StringKit.join(MultiValueDelimiter, pv);
        }
      }
    }
    if (v == null)
    {
      Object o = super.getAttribute(name);
      if (o != null)
      {
        v = o.toString();
      }
    }
    return v;
  }
  @Override
  public String getQueryString()
  {
    String qry = super.getQueryString();
    if (values != null)
    {
      Map<String,String> map = StringKit.toMap(qry,"=","&");
      for (String k : map.keySet())
      {
        if (values.containsKey(k))
        {
          map.put(k,values.get(k));
        }
      }
      try
      {
        qry = StringKit.joinMap(map,"=","&");
      }
      catch (Exception nevermind)
      {
      }
    }
    return qry;
  }
  public String getReferrer()
  {
    return Coerce.toString(getHeader("Referer"));
  }
  /**
   * Indicates whether Request has the named parameter
   * 
   * @param String
   *            name of control
   * @return boolean
   */
  public boolean hasParm(String name)
  {
    String v = getParameterNullable(name);
    if (v == null)
    {
      v = getParameterNullable(name + ".x");
    }
    if (v == null)
    {
      v = getParameterNullable(name + ".y");
    }
    return v != null;
  }
  public boolean posted()
  {
    return getMethod().equalsIgnoreCase("POST");
  }
  /**
   * Returns the named value from the request up to 60 characters, coerced to
   * the type of the default value passed. If no value is found, the default
   * value is returned.
   */
  public <T> T read(String ctlName, T dft)
  {
    return read(ctlName, dft, 60);
  }
  /**
   * Returns the named value from the request, trimmed to length and coerced
   * to the type of the default value passed. If no value is found, the
   * default value is returned.
   */
  public <T extends Object> T read(String ctlName, T dft, int maxDataLen)
  {
    if (!hasParm(ctlName))
    {
      return dft;
    }
    String v = readString(ctlName, maxDataLen);
    try
    {
      if (dft instanceof Double || dft instanceof Float || dft instanceof BigDecimal)
      {
        return TypeKit.convert(readDouble(ctlName),dft);
      }
      return TypeKit.convert(v, dft);
    }
    catch (Exception e)
    {
      return dft;
    }
  }
  public String[] readArray(String name)
  {
    String[] a = super.getParameterValues(name);
    //If the string containing our array is wrapped in square brackets, remove them to properly parse the array
    if(a != null && a.length == 1 && a[0].length() >= 3 && a[0].charAt(0)=='[' && a[0].charAt(a[0].length()-1)==']' ){
    	return a[0].substring(1, a[0].length()-1).split(",");
    }
    return a == null ? new String[0] : a.length == 1 ? a[0].split(",") : a;
  }
  public BigDecimal readBigDecimal(String name)
  {
    return new BigDecimal(readDouble(name));
  }
  /**
   * get named parameter as a Boolean
   * 
   * @param String
   *            name of control
   * @return boolean interpretation of value, else false if value is not in
   *         request
   */
  public boolean readBoolean(String name)
  {
    String v = getParameter(name);
    if (v == null)
    {
      return false;
    }
    return StringKit.True(v);
  }

  /**
   * get named parameter as a double
   * 
   * @param String
   *            name of control
   * @return double default 0 if parameter is not in request
   */
  public double readDouble(String name)
  {
    String v = getParameter(name);
    try
    {
      if (StringKit.isNumber(v))
      {
        return Double.parseDouble(v);
      }
    }
    catch (Exception e)
    {
    }
    return 0;
  }
  public double[] readDoubleArray(String name)
  {
    return SetKit.toDouble(readArray(name));
  }
  /**
   * readInt takes an integer from the request data for this named control
   * 
   * @param String
   *            name of control
   * @return int default 0 if parameter is not in request
   */
  public int readInt(String name)
  {
    return readInt(name, 0);
  }
  /**
   * readInt takes an integer from the request data for this named control
   * 
   * @param String
   *            name of control
   * @param int default in case parameter is not in request
   * @return int
   */
  public int readInt(String name, int dft)
  {
    String v = getParameter(name);
    if (v == null)
    {
      return dft;
    }
    return StringKit.intDft(v, dft);
  }
  public int[] readIntArray(String name)
  {
    return SetKit.toInt(readArray(name));
  }
  /**
   * Gets a string from the request parameters and returns the leftmost maxLen
   * chars
   * 
   * @param String
   *            name of control
   * @param int maximum length to return
   * @return String
   */
  public String readString(String name, int maxLen)
  {
    return StringKit.left(getParameter(name), maxLen > 0 ? maxLen : 255);
  }
  public boolean selfPosted()
  {
    boolean posted = posted();
    if (posted)
    {
      String ref = getReferrer();
      if (!ref.equals(""))
      {
        ref = ref.toLowerCase();
        String uri = getRequestURI().toLowerCase();
        int q = uri.indexOf('?');
        if (q > 0)
        {
          uri = uri.substring(0, q);
        }
        posted = ref.indexOf(uri) > -1;
      }
    }
    return posted;
  }
  public void setValue(String k, Object v)
  {
    if (values == null)
    {
      values = new HashMap<String, String>();
    }
    String valNow = values.get(k);
    if (valNow != null)
    {
      values.put(k, valNow + MultiValueDelimiter + v);
    }
    else
    {
      values.put(k, Coerce.toString(v));
    }
  }
}

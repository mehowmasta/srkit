package ir.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.json.JSONObject;
import sr.web.App;
/**
 * This class provides utility functions for Strings
 */
public abstract class StringKit
{
  public static final String ALPHA_UPPER_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static Map<Integer,String> threadPrefixMap=new ConcurrentHashMap<Integer,String>();
  private static Boolean devbox=null;
  private static List<String>output = new ArrayList<String>();
  private static final String[]stackPrefixesToKeep = new String[]{"ir.","sr.","org.apache.jsp."};
  private static String alpha(int x, String abc)
  {
    if (x < abc.length())
    {
      return abc.substring(x, x + 1);
    }
    return alpha(x / abc.length() - 1, abc) + (x % abc.length() + 1);
  }
  public static String alphaMixed(int x)
  {
    final String abc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    return alpha(x, abc);
  }
  public static String alphaUpper(int x)
  {
    final String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    return alpha(x, abc);
  }

  public static String alphaUpperNumeric(int x)
  {
    return alpha(x, ALPHA_UPPER_NUMERIC_STRING);
  }
  public static String camelCaseJavaName(String fieldName)
  {
    String result = fieldName.replace('-',' ').replace('_',' ');
    result = StringKit.proper(result);
    result = StringKit.replace(StringKit.squish(result)," ","");
    return result;
  }
  public static String chop(String s, int nCharsToChop)
  {
    if (s.length() <= nCharsToChop)
    {
      return "";
    }
    return s.substring(0, s.length() - nCharsToChop);
  }
  public static void chop(StringBuilder b, int nCharsToChop)
  {
    if (b.length() >= nCharsToChop)
      b.setLength(b.length() - nCharsToChop);
  }
  public static String chopAdd(String s, int nCharsToChop,Object...stuffToAdd)
  {
    StringBuilder b = new StringBuilder(chop(s,nCharsToChop));
    for (Object o : stuffToAdd)
    {
      b.append(Coerce.toString(o));
    }
    return b.toString();
  }
  public static String cleanFileName(String in)
  {
    String out = replace(in, new String[] { "/", "\\", "'", "\"", " ", ";", ":"}, new String[] { "", "", "", "", "", "", ""});
    return out;
  }
  public static void clearLogPrefix()
  {
    threadPrefixMap.remove(Thread.currentThread().hashCode());
  }
  public static void clearOutput()
  {
    output.clear();
  }
  public static String coalesce(Object... oa)
  {
    for (Object o : oa)
    {
      String v = Coerce.toString(o).trim();
      if (v.length() > 0)
      {
        return v;
      }
    }
    return "";
  }
  /**
   * Returns the number of time search occurs in data.
   *
   * @param data
   * @param search
   * @return
   */
  public static int count(String data, String search)
  {
    if (data == null || search == null)
    {
      return 0;
    }
    int c = 0;
    int iStr = data.indexOf(search);
    while (-1 < iStr)
    {
      c++;
      iStr = data.indexOf(search, iStr + search.length());
    }
    return c;
  }
  public static String deAccent(String v)
  {
    if (v==null || v.equals(""))
    {
      return "";
    }
    String nfdNormalizedString = Normalizer.normalize(v, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(nfdNormalizedString).replaceAll("");
  }
  public static void debug(Object fmt, Object... parms) throws Exception
  {
    if (devbox==null)
    {
      devbox = App.isDev();      
    }
    if (devbox)
    {
      println(fmt,parms);
    }
    else
    {

    }
  }
  public static void deduplicate(boolean caseInsensitive, String[][] arrays)
  {
    Set<String> used = new java.util.HashSet<String>();
    for (int i = 0; i < arrays.length; i++)
    {
      List<String> a = new ArrayList<String>();
      for (int j = 0; j < arrays[i].length; j++)
      {
        String v = arrays[i][j];
        if (caseInsensitive)
        {
          v = v.toLowerCase();
        }
        if (used.add(v))
        {
          a.add(v);
        }
      }
      arrays[i] = a.toArray(new String[0]);
    }
  }
  public static String deduplicate(String delim, String... sa)
  {
    List<Object> lst = new ArrayList<Object>();
    Set<String> set = new HashSet<String>();
    for (String s : sa)
    {
      String[] a = s.split(" ");
      for (String w : a)
      {
        if (set.add(w.toLowerCase()))
        {
          lst.add(w);
        }
      }
    }
    return join(delim, lst);
  }

  public static List<String> deduplicatePhones(List<String> list)
  {
    Set<String> check = new java.util.HashSet<String>();
    List<String>result=new ArrayList<String>();
    for (String phoneString : list)
    {
      String phoneDigits = digits(phoneString);
      if (phoneDigits.trim().length()==0)
      {
        continue;
      }
      if (check.add(phoneDigits))
      {
        result.add(phoneString);
      }
    }
    return result;
  }
  public static String delimitedList(String delim, boolean excludeEmpty, String... oa)
  {
    StringBuilder b = new StringBuilder("");
    for (int i = 0; i < oa.length; i++)
    {
      if (oa[i] == null)
      {
        if (!excludeEmpty && b.length() > 0)
          b.append(delim);
        continue;
      }
      String v = oa[i];
      if (b.length() > 0 && v.length() > 0)
        b.append(delim);
      b.append(v);
    }
    return b.toString();
  }
  public static String delimitedList(String delim, String... oa)
  {
    return delimitedList(delim, true, oa);
  }
  public static String digits(String v)
  {
    if (v == null || v.equals(""))
    {
      return "";
    }
    StringBuilder b = new StringBuilder();
    for (char a : v.toCharArray())
    {
      if (Character.isDigit(a))
      {
        b.append(a);
      }
    }
    return b.toString();
  }
  /**
   * return whether two strings are equal
   */
  public static boolean eq(String s1, String s2)
  {// let's treat null and "" as equal
    if (s1 == null || s1.equals(""))
    {
      if (s2 == null || s2.equals(""))
        return true;
      return false;
    }
    if (s2 == null)
      return false;
    // compare as number
    try
    {
      Double n1 = new Double(s1);
      Double n2 = new Double(s2);
      return (n1.compareTo(n2) == 0);
    }
    catch (Exception e)
    {
    }
    // compare as date
    /*
     * try { Date d1 = new Date(s1); Date d2 = new Date(s2); return
     * (d1.compareTo(d2) == 0); } catch (Exception e) { }
     */
    // must be string, so compare no case
    return (s1.equalsIgnoreCase(s2));
  }
  static public int[] findAll(String s, String find)
  {
    List<Integer> a = new ArrayList<Integer>();
    int found = s.indexOf(find);
    while (found > -1)
    {
      a.add(found);
      found = s.indexOf(find, found + find.length());
    }
    int[] ret = new int[a.size()];
    for (int i = 0; i < a.size(); i++)
      ret[i] = a.get(i);
    return ret;
  }
  /**
   * convert a string to a integer, with a default value if it's not numeric
   */
  static public float floatDft(Object o, int dft)
  {
    return isNumber(Coerce.toString(o)) ? Coerce.toFloat(o) : dft;
  }
  public static String format(BigDecimal d)
  {
    return format(d, true);
  }
  public static String format(BigDecimal d, boolean bCommas)
  {
    return format(d, d.scale(), bCommas);
  }
  public static String format(BigDecimal d, int decimals)
  {
    return format(d, decimals, true);
  }
  public static String format(BigDecimal d, int decimals, boolean bCommas)
  {
    String returnValue = d + "";
    DecimalFormat nf = new DecimalFormat();
    nf.setGroupingUsed(bCommas);
    nf.setMinimumFractionDigits(decimals);
    nf.setMaximumFractionDigits(decimals);
    try
    {
      returnValue = nf.format(d);
    }
    catch (Exception e)
    {
    }
    return returnValue;
  }
  public static String format(double d, int nDecimals)
  {
    return format(d, nDecimals, true);
  }
  public static String format(double d, int nDecimals, boolean bCommas)
  {
    String returnValue = d + "";
    DecimalFormat nf = new DecimalFormat();
    nf.setGroupingUsed(bCommas);
    nf.setMinimumFractionDigits(nDecimals);
    nf.setMaximumFractionDigits(nDecimals);
    nf.setRoundingMode(RoundingMode.HALF_UP);
    try
    {
      returnValue = nf.format(d);
    }
    catch (Exception e)
    {
    }
    return returnValue;
  }

  public static String format(long l)
  {
    return format(l, 0);
  }
  public static String format(StackTraceElement[] stea, String delim)
  {
    int ourFirstIndex = stea.length;
    int ourLastIndex = -1;
    StringBuilder b = new StringBuilder("");
    for (int i = 0; i <stea.length; i++)
    {
      StackTraceElement ele = stea[i];
      String cn = ele.getClassName();
      boolean keeper = false;
      for (String prefix : stackPrefixesToKeep)
      {
        if (cn.startsWith(prefix))
        {
          keeper = true;
          ourFirstIndex = Math.min(i, ourFirstIndex);
          ourLastIndex = Math.max(i, ourLastIndex);
          break;
        }
      }
      if (keeper)
      {
        b.append(cn).append(".").append(ele.getMethodName()).append(":").append(ele.getLineNumber()).append(delim);
      }
    }
    if (ourFirstIndex>0 && ourFirstIndex!=stea.length)
    {
      b.insert(0,"..." + ourFirstIndex + " levels down" + delim);
    }
    if (ourLastIndex < stea.length-1)
    {
      b.append("...").append(stea.length - ourLastIndex-1).append(" levels up").append(delim);
    }
    return b.toString();
  }
  public static String format(String f,Object...parms)
  {
    if (f == null || f.length()==0)
    {
      return "";
    }
    Object[] nonNullParameters = null;
    if (parms==null || parms.length==0)
    {
      return f;
    }
    String fn = f.replace("'","''");
    nonNullParameters = new Object[parms.length];
    for (int i=0;i<parms.length;i++)
    {
      nonNullParameters[i]=parms[i]==null?"":parms[i].toString();
    }
    try
    {
      return MessageFormat.format(fn,nonNullParameters);
    }
    catch (Exception x)
    {
      for (int i=nonNullParameters.length-1;i>=0;i--)
      {
        fn = replace(fn,"{"+i+"}",(String)nonNullParameters[i]);
      }
      return fn;
    }
  }
  public static void format(StringBuilder b, Object... parms)
  {
    if (parms != null && parms.length > 0)
    {
      String fn = b.toString().replace("'", "\"");
      Object[] sa = new Object[parms.length];
      for (int i = 0; i < parms.length; i++)
      {
        sa[i] = parms[i] == null ? "" : parms[i].toString();
      }
      b.setLength(0);
      b.append(MessageFormat.format(fn, sa));
    }
  }
  /**
   * Returns all characters after the last dot in the string,
   */
  static public String getExtension(String fileName)
  {
    if (fileName == null)
    {
      return "";
    }
    int iDot = fileName.lastIndexOf('.');
    if (iDot < 0)
    {
      return "";
    }
    int iTo = fileName.length();
    int qAt = fileName.indexOf('?', iDot);
    if (qAt > 0)
    {
      iTo = qAt;
    }
    return fileName.substring(iDot + 1, iTo);
  }
  /**
   * Returns all characters after the last slash or backslash in the string
   */
  static public String getFileName(String fullPath)
  {
    int searchBackFrom = fullPath.lastIndexOf('#');
    if (searchBackFrom == -1)
    {
      searchBackFrom = fullPath.length() - 1;
    }
    int lastSlash = fullPath.lastIndexOf('/', searchBackFrom);
    int iBackSlash = fullPath.lastIndexOf('\\', searchBackFrom);
    if (iBackSlash > lastSlash)
    {
      lastSlash = iBackSlash;
    }
    int iTo = fullPath.length();
    int qAt = fullPath.indexOf('?', lastSlash);
    if (qAt > 0)
    {
      iTo = qAt;
    }
    return fullPath.substring(lastSlash + 1, iTo);
  }
  private static String getLogPrefix()
  {
    String prefix = threadPrefixMap.get(Thread.currentThread().hashCode());
    return prefix == null ? "sr5" : prefix;
  }
  public static List<String>getOutput()
  {
    return new ArrayList<String>(output);
  }
  public static String humanizeJson(String jsonObjectString, String pairDelimiter)
  {
    return humanizeJson(jsonObjectString, pairDelimiter, null);
  }
  public static String humanizeJson(String jsonObjectString, String pairDelimiter, Map<String, String> renamedProperties,
      String... suppressedProperties)
  {
    Set<String> supp = null;
    if (suppressedProperties != null && suppressedProperties.length > 0)
    {
      supp = new HashSet<String>();
      supp.addAll(Arrays.asList(suppressedProperties));
    }
    String nextPairDelimiter = "";
    StringBuilder b = new StringBuilder();
    try
    {
      JsonObj o = new JsonObj(jsonObjectString);
      Map<String, String> properties = new TreeMap<String, String>(o.getProperties());
      for (String property : properties.keySet())
      {
        String name = property;
        if (supp != null && supp.contains(property))
        {
          continue;
        }
        if (renamedProperties != null && renamedProperties.containsKey(property))
        {
          name = renamedProperties.get(property);
        }
        b.append(nextPairDelimiter).append(name).append("=").append(o.getString(property));
        nextPairDelimiter = pairDelimiter;
      }
      return b.toString();
    }
    catch (Exception e)
    {
      StringKit.println("humanizeJson(" + jsonObjectString + ")\n->" + e.getMessage());
    }
    return b.toString();
  }
  /**
   * return name/value pairs string from Map
   */
  public static int indexOfNoCase(String[] a, String s)
  {
    if (s == null)
    {
      return -1;
    }
    for (int i = 0; i < a.length; i++)
    {
      if (a[i] != null && a[i].equalsIgnoreCase(s))
      {
        return i;
      }
    }
    return -1;

  }
  /** capitalizes first character */
  public static String initCap(String s)
  {
    if (s == null || s.equals(""))
    {
      return "";
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }
  /**
   * convert a string to a integer, with a default value if it's not numeric
   */
  static public int intDft(Object o, int dft)
  {
    return isNumber(Coerce.toString(o)) ? Coerce.toInt(o) : dft;
  }
  /**
   * is the passed string blank,null,or filled with space?
   */
  static public boolean isBlank(String s)
  {
    return s == null ? true : s.trim().equals("");
  }
  public static boolean isLetterOrNumber(char ch)
  {
    return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9'));
  }
  /**
   * is the passed string numeric?
   */
  static public boolean isNumber(String s)
  {
    try
    {
      new Double(s.trim());
      return true;
    }
    catch (NumberFormatException exc)
    {
      return false;
    }
  }
  /**
   * return delimited string from int[]
   */
  public static String join(String Del, int[] a)
  {
    String d = "";
    StringBuilder b = new StringBuilder("");
    for (int i : a)
    {
      b.append(d).append(i);
      d = Del;
    }
    return b.toString();
  }
  /**
   * return delimited string from List implementer
   */
  public static String join(String delimiter, List<? extends Object> a)
  {
    if (a == null)
    {
      return "";
    }
    String localDelim = "";
    StringBuilder b = new StringBuilder("");
    for (int i = 0; i < a.size(); i++)
    {
      String v = Coerce.toString(a.get(i));
      if (v.length() > 0)
      {
        b.append(localDelim).append(v);
        localDelim = delimiter;
      }
    }
    return b.toString();
  }

  public static String join(String delim, Object... va)
  {
    String localDelim = "";
    StringBuilder b = new StringBuilder("");
    if (va != null)
    {
      for (Object v : va)
      {
        String s = Coerce.toString(v).trim();
        if (s.length() > 0)
        {
          b.append(localDelim).append(Coerce.toString(v));
          localDelim = delim;
        }
      }
    }
    return b.toString();
  }
  /**
   * return delimited string from String[]
   */
  public static String join(String Del, String[] a)
  {
    String d = "";
    StringBuilder b = new StringBuilder("");
    for (int i = 0; i < a.length; i++)
    {
      b.append(d).append(a[i]);
      d = Del;
    }
    return b.toString();
  }
  public static String join(String[] a)
  {
    return join(",", a);
  }
  public static String joinMap(Map<?, ?> m) throws Exception
  {
    return joinMap(m, "=", ",");
  }
  /**
   * return name/value pairs string from Map
   */
  public static String joinMap(Map<?, ?> m, String eq, String pairDel) throws Exception
  {
    StringBuilder b = new StringBuilder("");
    String pd = "";
    for (Object k : m.keySet())
    {
      String v = Coerce.toString(m.get(k));
      if (v.indexOf(eq) > -1)
      {
        throw new Exception("Map element contains equal symbol.");
      }
      if (v.indexOf(pairDel) > -1)
      {
        throw new Exception("Map element contains pair delimiter.");
      }
      b.append(pd).append(Coerce.toString(k)).append(eq).append(v);
      pd = pairDel;
    }
    return b.toString();
  }
  public static String jsq(Object o)
  {
    return o == null ? "\"\"" : JSONObject.quote(o.toString());
  }
  public static String jsq(String[] a)
  {
    String comma = "";
    StringBuilder b = new StringBuilder("[");
    for (String s : a)
    {
      b.append(comma).append(jsq(s));
      comma = ",";
    }
    return b.append("]").toString();
  }
  public static String jsqa(List<?> a)
  {
    String comma = "";
    StringBuilder b = new StringBuilder("[");
    for (Object s : a)
    {
      b.append(comma).append(jsq(s));
      comma = ",";
    }
    return b.append("]").toString();
  }
  /**
   * return left n chars of String
   */
  public static String left(Object o, int chars)
  {
    if (o == null)
    {
      return "";
    }
    String s= Coerce.toString(o);
    if (s.length() <= chars)
    {
      return s;
    }
    return s.substring(0, chars);
  }
  /**
   * return left n-3 chars of String, with ... if more exist
   */
  public static String leftEllipsis(String s, int chars)
  {
    if (s == null)
      return "";
    if (s.length() <= chars - 3)
      return s;
    return s.substring(0, chars - 3) + "...";
  }
  /**
   * return all chars less last n
   */
  public static String leftLess(String s, int less)
  {
    if (s == null)
      return "";
    if (s.length() <= less)
      return s;
    return s.substring(0, s.length() - less);
  }
  /**
   * convert a string to a long, with a default value if it's not numeric
   */
  static public long longDft(String s, long dft)
  {
    try
    {
      return Double.valueOf(s).longValue();
    }
    catch (NumberFormatException exc)
    {
      return dft;
    }
  }
  static public String lpad(int l)
  {
    return lpad(l + "", (Integer.MAX_VALUE + "").length(), '0');
  }
  public static String lpad(long i)
  {
    return lpad(i, (Long.MAX_VALUE + "").length(), '0');
  }
  static public String lpad(Object val, int padLen, char padChar)
  {
    String v = val == null ? "" : val.toString();
    if (v.length() > padLen)
    {
      return v;
    }
    StringBuilder b = new StringBuilder(padLen + v.length());
    for (int i = 0; i < padLen - v.length(); i++)
    {
      b.append(padChar);
    }
    return b.append(v).toString();
  }
  public static String ltrim(String in)
  {
    if (in == null || in.length() == 0)
    {
      return "";
    }
    int firstNonBlank = -1;
    for (int i = 0; i < in.length(); i++)
    {
      if (!Character.isWhitespace(in.charAt(i)))
      {
        firstNonBlank = i;
        break;
      }
    }
    return firstNonBlank == -1 ? "" : in.substring(firstNonBlank);
  }
  /**
   * return length chars of String starting from startFrom
   */
  public static String mid(String s, int startFrom,int length)
  {
    if (s == null || startFrom < 0 || startFrom >= s.length())
    {
      return "";
    }
    return s.substring(startFrom, Math.min(s.length(),startFrom + length));
  }
  public static String minimumPrecision(BigDecimal d)
  {
    if (d != null && d.scale()==-1)
    {
      d= d.setScale(8,RoundingMode.HALF_UP);
    }
    if ( d== null || d.compareTo(BigDecimal.ZERO)==0)
    {
      return "0";
    }
    return d.stripTrailingZeros().toPlainString();
  }
  public static String minimumPrecision(double d)
  {
    BigDecimal bd = new BigDecimal(d);
    if (bd.scale() > 8 || bd.scale() < 0)
    {
      bd = bd.setScale(8, IndyMath.RoundMode);
    }
    return minimumPrecision(bd);
  }
  public static String nondigits(String v)
  {
    if (v == null || v.equals(""))
    {
      return "";
    }
    StringBuilder b = new StringBuilder();
    for (char a : v.toCharArray())
    {
      if (!Character.isDigit(a))
      {
        b.append(a);
      }
    }
    return b.toString();
  }

  public static String num(BigDecimal d)
  {
    return minimumPrecision(d);
  }
  public static String num(BigDecimal d, int scale)
  {
    return d==null ? "0" : minimumPrecision(d.setScale(scale, IndyMath.RoundMode));
  }
  public static String num(double d)
  {
    return minimumPrecision(d);
  }
  public static String num(double d, int maxDecimals)
  {
    return num(new BigDecimal(d).setScale(maxDecimals,IndyMath.RoundMode));
  }
  public static String numSigned(double d)
  {
    return (d > 0 ? "+" : "") + minimumPrecision(d);
  }
  public static void println(Object fmt, Object... parms)
  {
    StringBuilder out =  new StringBuilder(DateKit.hmsm(new Date()))
        .append(" ").append(getLogPrefix()).append(" ");
    if (fmt instanceof String && parms != null && parms.length>0)
    {
      out.append(format((String) fmt, parms));
    }
    else
    {
      out.append(Coerce.toString(fmt));
    }
    System.out.println(out.toString());
    output.add(out.toString());
  }
  public static void println(Throwable excCaught)
  {
    Throwable excReport = excCaught;
    StackTraceElement[] traceReport = excCaught.getStackTrace();
    if (excCaught.getCause()!=null)
    {
      excReport = excCaught.getCause();
      StackTraceElement[] causeTrace = excReport.getStackTrace();
      if (causeTrace!=null && causeTrace.length>0)
      {
        traceReport = causeTrace;
      }
    }
    StringKit.println(excReport.getMessage() + ":\n" + format(traceReport, "\n"));
  }
  public static String promptValue(String prompt, String val)
  {
    if (val == null || val.equals(""))
    {
      return "";
    }
    return prompt + " " + val;
  }
  /** capitalizes first character and then each char after non=number,non-digit*/
  public static String proper(String s)
  {
    if (s == null || s.equals(""))
    {
      return "";
    }
    StringBuilder result = new StringBuilder(s);
    boolean capNext = true;
    for (int i=0;i<s.length();i++)
    {
      if (Character.isLetterOrDigit(s.charAt(i)))
      {
        if (capNext)
        {
          result.setCharAt(i,Character.toUpperCase(s.charAt(i)));
          capNext = false;
        }
      }
      else
      {
        capNext = true;
      }
    }
    return result.toString();
  }
  static public String removeExtension(String fullPath)
  {
    String fn = getFileName(fullPath);
    int lastDot = fn.lastIndexOf('.');
    if (lastDot < 0)
      return fn;
    return fn.substring(0, lastDot);
  }
  public static String replace(Object o, String search, String replace)
  {
    if (search == null || replace == null)
    {
      return "";
    }
    return o.toString().replace(search, replace);
  }
  public static String replace(Object o, String[] search, String[] replace)
  {
    if (search == null || replace == null)
    {
      return "";
    }
    if (search.length != replace.length)
    {
      return "search-replace array mismatch.";
    }
    String s = o.toString();
    if (s.equals(""))
    {
      return "";
    }
    for (int i = 0; i < search.length; i++)
    {
      s = s.replace(search[i],replace[i]);
    }
    return s;
  }
  public static String reverseSort(Object v)
  {
    StringBuilder b = new StringBuilder(v == null ? "" : v.toString().toUpperCase());
    for (int i = 0; i < b.length(); i++)
    {
      if (Character.isDigit(b.charAt(i)))
        b.setCharAt(i, (char) ('9' - (b.charAt(i) - '0')));
      else if (Character.isLetter(b.charAt(i)))
        b.setCharAt(i, (char) ('Z' - (b.charAt(i) - 'A')));
    }
    return b.toString();
  }
  /**
   * return right n chars of String
   */
  public static String right(String s, int chars)
  {
    if (s == null)
      return "";
    int l = s.length();
    if (l <= chars)
      return s;
    return s.substring(l - chars);
  }
  static public String rpad(Object v, char pad, int len)
  {
    String s = Coerce.toString(v);
    while (s.length() < len)
    {
      s += pad;
    }
    return s;
  }
  static public String rpad(Object v, int len)
  {
    return rpad(v, ' ', len);
  }
  public static String rtrim(String in)
  {
    if (in == null || in.length() == 0)
    {
      return "";
    }
    int lastBlank = in.length();
    for (int i = in.length() - 1; i >= 0; i--)
    {
      if (!Character.isWhitespace(in.charAt(i)))
      {
        break;
      }
      lastBlank = i;
    }
    return lastBlank == 0 ? "" : in.substring(0, lastBlank);
  }
  public static boolean sameDomain(String addressOne,String addressTwo)
  {
    String[] parts1 = addressOne.replace('@', '.').toLowerCase().split("\\.");
    String[] parts2 = addressTwo.replace('@', '.').toLowerCase().split("\\.");
    return parts1.length >= 2
        && parts2.length >= 2
        && parts1[parts1.length - 2].equals(parts2[parts2.length-2])
        && parts1[parts1.length - 1].equals(parts2[parts2.length-1]);
  }
  public static void setLogPrefix(String prefix)
  {
    threadPrefixMap.put(Thread.currentThread().hashCode(),prefix);
  }
  public static String sixteenths(BigDecimal d) {
    return sixteenths(d.doubleValue());
  }
  public static String sixteenths(double d) {
    double roundedValue = IndyMath.roundFraction(d,16);
    int whole = (int) roundedValue;
    int numerator = (int) ((roundedValue - whole) * 16);
    if (numerator==0)
    {
      return whole + "";
    }
    int denominator=16;
    while (numerator % 2 == 0)
    {
      numerator /= 2;
      denominator /= 2;
    }
    return whole + " " + numerator + "/" + denominator;
  }
  public static String[] split(String s)
  {
    return split(s, ",");
  }
  /**
   * return String array using passed delimiter
   * <br>does not include empty or null elements
   */
  public static String[] split(String s, String sDel)
  {
    if (null == s || s.equals(""))
    {
      return new String[0];
    }
    StringBuilder bDel = new StringBuilder(sDel.length() * 2);
    for (char ch : sDel.toCharArray())
    {
      bDel.append("\\" + ch);
    }
    String[] r = s.split(bDel.toString());
    List<String> l = new ArrayList<String>();
    for (String ss : r)
    {
      if (ss != null && ss.trim().length() > 0)
      {
        l.add(ss.trim());
      }
    }
    return l.toArray(new String[0]);
  }
  public static String squish(String s)
  {
    s = replace(s, new String[] { "\t", "\r", "\n", ", ", "; ", ": ", " ,", " ;", " :" }, new String[] { " ", " ", " ", ",", ";", ":",
        ",", ";", ":" });
    while (s.indexOf("  ") > -1)
      s = replace(s, "  ", " ");
    return s;
  }
  public static String stackTrace(int maxLevels, String delim)
  {
    StringBuilder b = new StringBuilder("");
    Exception t = new Exception();
    t.fillInStackTrace();
    StackTraceElement[] stea = t.getStackTrace();
    for (int i = 1; i <= (maxLevels > 0 ? Math.min(maxLevels, stea.length - 1) : stea.length); i++)
    {
      b.append(i == 1 ? "" : delim).append(stea[i].getClassName()).append(".").append(stea[i].getMethodName()).append(":")
      .append(stea[i].getLineNumber());
    }
    return b.toString();
  }
  /**
   * null-save toString()
   */
  public static String str(Object o)
  {
    return o == null ? "" : o.toString();
  }
  public static String stripTrailingZeroes(String s)
  {
    if (s == null)
      return "";
    if (s.indexOf(".") > -1)
    {
      while (s.charAt(s.length() - 1) == '0')
        s = s.substring(0, s.length() - 1);
    }
    return s;
  }
  /**
   * If the passed value is a ten digit phone number like 9876543210,
   * will return 987-654-3210, else will return whatever is passed.
   */
  public static String tenDigitPhone(String v)
  {
    if (v.length()==10 && digits(v).length()==10)
    {
      return v.substring(0,3) + "-" + v.substring(3,6) + "-" + v.substring(6);
    }
    return v;
  }
  public static String thirtySeconds(BigDecimal d) {
    BigDecimal roundedValue = IndyMath.roundFraction(d,32);
    int whole = roundedValue.intValue();
    int numerator = roundedValue.subtract(new BigDecimal(whole)).multiply(new BigDecimal(32)).intValue();
    if (numerator==0)
    {
      return whole + "";
    }
    int denominator=32;
    while (numerator % 2 == 0)
    {
      numerator /= 2;
      denominator /= 2;
    }
    return whole + " " + numerator + "/" + denominator;
  }
  public static String thirtySeconds(double d) {
    return thirtySeconds(new BigDecimal(d));
  }
  /**
   * return List implementer from string using passed delimiter
   */
  public static String[] toArray(List<Object> lst)
  {
    String[] sa = new String[lst.size()];
    for (int i = 0; i < lst.size(); i++)
      sa[i] = lst.get(i).toString();
    return sa;
  }
  public static String toHex(byte[] bytes) {
		Formatter formatter = new Formatter();		
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
  public static int[] toIntArray(String v)
  {
    return SetKit.toInt(split(v));
  }

  public static String toJson(List<?>lst)
  {
    String c = "";
    StringBuilder b=new StringBuilder("[");
    for (Object o : lst)
    {
      b.append(c).append(o.toString());
      c=",";
    }
    return b.append("]").toString();
  }
  public static String toJson(Map<?,?> m)
  {
    String comma="";
    StringBuilder b = new StringBuilder("{");
    for (Map.Entry<?,?> e : m.entrySet())
    {
      b.append(comma)
      .append(jsq(e.getKey()))
      .append(":")
      .append(jsq(e.getValue()));
      comma = ",";
    }
    return b.append("}").toString();
  }
  public static String toJson(Object[]a)
  {
    return toJson(Arrays.asList(a));
  }
  /**
   * return List implementer from string using passed delimiter
   */
  public static List<String> toList(String s, String sDel)
  {
    if (null == s || s.equals(""))
      return new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(s, sDel);
    List<String> r = new ArrayList<String>(st.countTokens());
    while (st.hasMoreTokens())
    {
      r.add(st.nextToken());
    }
    return r;
  }
  public static String[] toLower(String[] sa)
  {
    if (sa == null)
    {
      return null;
    }
    String[] ra = new String[sa.length];
    for (int i = 0; i < sa.length; i++)
    {
      ra[i] = sa[i] == null ? "" : sa[i].toLowerCase();
    }
    return ra;
  }
  public static Map<String, String> toMap(String s)
  {
    return toMap(s, "=", ",");
  }
  /**
   * return map from a delimited string of name=value pairs
   */
  public static Map<String, String> toMap(String s, String eq, String and)
  {
    if (null == s || s.equals(""))
    {
      return new java.util.HashMap<String, String>();
    }
    String[] a = StringKit.split(s, and);
    Map<String, String> ht = new HashMap<String, String>(a.length);
    for (String b : a)
    {
      String[] c = StringKit.split(b, eq);
      if (c.length == 2)
      {
        ht.put(c[0], c[1]);
      }
    }
    return ht;
  }
  /**
   * helper to centralize evaluation as boolean
   */
  public static boolean True(Object o)
  {
    return Coerce.toBool(o);
  }
  public static String zeroStr(double d, String s)
  {
    return d == 0 ? (s == null ? "" : s) : d + "";
  }
  public static String zeroStr(long i, String s)
  {
    return i == 0 ? (s == null ? "" : s) : i + "";
  }
  /**
   * csvClean will filter the passed string to replace characters that would
   * mess up a csv export
   */
  public String csvClean(String v)
  {
    if (v == null || v.equals(""))
      return "";
    return v.replace('"', '`').replace(',', '^').replace('-', '_').replace('#', '*').replace('\'', '`');
  }
  public String getCaller(Object callee,String endingToIgnore)
  {
    String calleeClassName = callee.getClass().getSimpleName();
    Exception e = new Exception();
    e.fillInStackTrace();
    StackTraceElement[] tee = e.getStackTrace();
    for (StackTraceElement te : tee)
    {
      String cn = te.getClassName();
      if (! cn.endsWith(calleeClassName) && (endingToIgnore==null || ! cn.endsWith(endingToIgnore)))
      {
        return te.getClassName() + "." + te.getMethodName() + ":" + te.getLineNumber();
      }
    }
    StackTraceElement te = tee[Math.min(tee.length-1,3)];
    return te.getClassName() + "." + te.getMethodName() + ":" + te.getLineNumber();
  }
}

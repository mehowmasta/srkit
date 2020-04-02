package ir.util;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ir.data.ITranslator;
/**
 * JDateTime is an immutable datetime container that can go further back in time
 * than java.util.Date with precision to the second.
 */
public class JDateTime implements java.io.Serializable,Comparable<JDateTime>
{
  private static final long serialVersionUID = 1L;
  private static final JDateTime dateTimeMax = new JDateTime(3001, 12, 31,23,59,59);
  public final static String DefaultFormat = JDate.DefaultFormat + " HH:mm:ss";
  public final static String DefaultFormat2 = JDate.DefaultFormat + " HH:mm";
  public final static String DefaultFormat3 = JDate.DefaultFormat;
  public final static String InternalFormat = "yyyy-MM-dd HH:mm:ss";
  public final static String InternalFormat2 = "yyyy-MM-dd HH:mm";
  private static Map<String, Integer> months = null;
  private final static String[] tryFormats = { DefaultFormat, DefaultFormat2, DefaultFormat3, InternalFormat, InternalFormat2 };
  private final static JDateTime zero = new JDateTime(0, 0, 0, 0, 0, 0);
  public final static String ZeroString = "0000-00-00 00:00:00";
  private final byte _day;
  private final byte _hour;
  private final byte _minute;
  private final byte _month;
  private final byte _second;
  private final short _year;
  /**
   * determine where the first non-numeric token is
   */
  private static int findNonNumeric(String[] tokens)
  {
    for (int i = 0; i < tokens.length; i++)
    {
      if (!StringKit.isNumber(tokens[i]))
      {
        return i;
      }
    }
    return -1;
  }
  private static synchronized Map<String, Integer> getMonths()
  {
    if (months == null)
    {
      Map<String, Integer> m = new HashMap<String, Integer>();
      DateFormatSymbols symbols = new DateFormatSymbols();
      String[] shortMonths = symbols.getShortMonths();
      String[] longMonths = symbols.getMonths();
      for (int i = 0; i < shortMonths.length; i++)
      {
        m.put(shortMonths[i].toLowerCase(), i);
        m.put(longMonths[i].toLowerCase(), i);
      }
      months = m;
    }
    return months;
  }
  private static Integer lookupMonth(String monthStr)
  {
    return getMonths().get(monthStr.toLowerCase());
  }
  public static JDateTime max()
  {
    return dateTimeMax;
  }
  //
  public static JDateTime now()
  {
    return new JDateTime(new Date(System.currentTimeMillis() + (JDate.getThreadTimeZoneOffset() * 60 * 60000)));
  }
  //
  public static JDateTime parse(Object o) throws Exception
  {
    String v = Coerce.toString(o).trim();
    if (v.equals("") || ZeroString.equals(v) || JDate.ZeroString.equals(v))
    {
      return zero();
    }
    v = JDate.toEnglish(v);
    for (String format : tryFormats)
    {
      try
      {
        return new JDateTime(new SimpleDateFormat(format).parse(v));
      }
      catch (Exception e)
      {
      }
    }
    return parseHeuristic(v);
  }
  private static JDate parseDate(String dateStr) throws Exception
  {

    String[] dateTokens = dateStr.split("-");
    if (dateTokens.length < 3)
    {
      throw new Exception("Could not find three tokens in '" + dateStr + "'.");
    }
    int year = 0, month = -1, day = 0;
    int yearIndex = -1, dayIndex = -1, monthIndex = findNonNumeric(dateTokens);
    if (monthIndex > -1)
    {// non-numeric token, assume it's the month
      Integer monthObj = lookupMonth(dateTokens[monthIndex]);
      if (monthObj == null)
      {
        throw new Exception("Failed to identify month in '" + dateStr + "'.");
      }
      month = monthObj.intValue();
    }
    int notFound = monthIndex > -1 ? 2 : 3;
    int notFoundPrevious = 77;
    while (notFound > 0)
    {
      for (int i = 0; i < 3; i++)
      {
        if (i == yearIndex || i == monthIndex || i == dayIndex)
        {
          continue;
        }
        int tokenValue = Integer.parseInt(dateTokens[i]);
        if (yearIndex == -1)
        {
          if (tokenValue > 31 || tokenValue == 0)
          {// it's obviously the year
            yearIndex = i;
          }
        }
        else if (dayIndex == -1)
        {
          if (tokenValue > 12 || monthIndex > -1)
          {// year is found, so this must be day
            dayIndex = i;
          }
        }
        else
        {
          monthIndex = i;
        }
      }
      notFound = yearIndex < 0 ? 1 : 0;
      notFound += monthIndex < 0 ? 1 : 0;
      notFound += dayIndex < 0 ? 1 : 0;
      if (notFound == 3)
      {// all ambiguous, assume m-d-y
        yearIndex = 2;
        monthIndex = 0;
        dayIndex = 1;
        notFound = 0;
      }
      else if (notFound == notFoundPrevious)
      {// stuck, make some guesses
        if (monthIndex == 0 || monthIndex == 1)
        {// m-d-y or d-m-y
          if (yearIndex == -1)
          {
            yearIndex = 2;
          }
        }
        else if (yearIndex == 0)
        {// y-m-d
          monthIndex = 1;
        }
        else
        {
          monthIndex = 0;
        }
      }
      notFoundPrevious = notFound;
    }
    year = Integer.parseInt(dateTokens[yearIndex]);
    if (month == -1)
    {
      month = Integer.parseInt(dateTokens[monthIndex]);
    }
    day = Integer.parseInt(dateTokens[dayIndex]);
    return new JDate(year, month, day);
  }
  /**
   * Heuristically converts string to JDateTime.
   */
  static JDateTime parseHeuristic(String dateStr) throws Exception
  {
    if (dateStr == null || dateStr.equals("") || dateStr.equals(ZeroString) || dateStr.equals(JDate.ZeroString))
    {
      return zero();
    }
    dateStr = dateStr.replace('/', '-').replace('.', '-').replace(' ', '-').replace(',', '-').replace(':', '-');
    String[] dateTimeParts = dateStr.split("-");
    String dash = "", colon = "";
    String datePart = "";
    String timePart = "";
    for (int i = 0; i < dateTimeParts.length; i++)
    {// if a time value was also passed
      if (i < 3)
      {
        datePart += dash + dateTimeParts[i];
        dash = "-";
      }
      else
      {
        timePart += colon + dateTimeParts[i];
        colon = ":";
      }
    }
    JDate date = parseDate(datePart);
    int[] hms = parseTime(timePart);
    return new JDateTime(date, hms[0], hms[1], hms[2]);
  }
  private static int[] parseTime(String time)
  {
    int[] hms = new int[] { 0, 0, 0 };
    if (time != null && time.trim().length() > 0)
    {
      String[] timeVals = time.split(":");
      if (timeVals.length > 0)
      {
        hms[0] = Integer.parseInt(timeVals[0]);
        if (timeVals.length > 1)
        {
          hms[1] = Integer.parseInt(timeVals[1]);
          if (timeVals.length > 2)
          {
            hms[2] = Coerce.toInt(timeVals[2]);
          }
        }
      }
      if (hms[0] < 12 && time.toUpperCase().contains("PM"))
      {
        hms[0] += 12;
      }
    }
    return hms;
  }
  public static String stamp()
  {
    return JDateTime.now().toCompactString();
  }
  public static JDateTime zero()
  {
    return zero;
  }
  public JDateTime(int year, int month, int date)
  {
    this(year, month, date, 0, 0, 0);
  }
  public JDateTime(int year, int month, int date, int hr, int min, int sec)
  {
    _year = Integer.valueOf(year).shortValue();
    _month = Integer.valueOf(month).byteValue();
    _day = Integer.valueOf(date).byteValue();
    _hour = Integer.valueOf(hr).byteValue();
    _minute = Integer.valueOf(min).byteValue();
    _second = Integer.valueOf(sec).byteValue();
  }
  /**
   * Construct from sql Date
   */
  public JDateTime(java.sql.Date d)
  {
    this(DateKit.year(d), DateKit.month(d), DateKit.day(d), DateKit.hour(d), DateKit.minute(d), DateKit.second(d));
  }
  /**
   * Construct from sql Timestamp
   */
  public JDateTime(java.sql.Timestamp ts)
  {
    this(new java.util.Date(ts == null ? 0 :ts.getTime()));
  }
  /**
   * Construct from java Date
   */
  public JDateTime(java.util.Date d)
  {
    this(d == null ? 0 : DateKit.year(d), d == null ? 0 : DateKit.month(d), d == null ? 0 : DateKit.day(d), d == null ? 0 : DateKit.hour(d), d == null ? 0 : DateKit.minute(d), d == null ? 0 : DateKit.second(d));
  }
  /**
   * Construct from JDate
   */
  public JDateTime(JDate d)
  {
    this(d == null ? 0 : d.year(), d == null ? 0 : d.month(), d == null ? 0 : d.day(), 0, 0, 0);
  }
  public JDateTime(JDate d, int hour, int min, int sec)
  {
    this(d == null ? 0 : d.year(), d == null ? 0 : d.month(), d == null ? 0 : d.day(), hour, min, sec);
  }
  protected JDateTime(JDateTime dt)
  {
    _year = dt._year;
    _month = dt._month;
    _day = dt._day;
    _hour = dt._hour;
    _minute = dt._minute;
    _second = dt._second;
  }
  public JDateTime(long l)
  {
    this(new Date(l));
  }
  /**
   * Construct from string with DefaultFormat
   */
  public JDateTime(String v) throws Exception
  {
    this(parse(v));
  }
  public JDateTime addDays(int nD)
  {
    Calendar c = getCalendar();
    c.add(Calendar.DATE, nD);
    return new JDateTime(c.getTime());
  }
  public JDateTime addHours(int h)
  {
    Calendar c = getCalendar();
    c.add(Calendar.HOUR_OF_DAY, h);
    return new JDateTime(c.getTime());
  }
  public JDateTime addMinutes(int m)
  {
    Calendar c = getCalendar();
    c.add(Calendar.MINUTE, m);
    return new JDateTime(c.getTime());
  }
  @Override
  public int compareTo(JDateTime that)
  {
    return this.getTime() > that.getTime() ? 1 : -1;
  }
  /**
   * @return int day of month,starting at 1
   */
  public int day()
  {
    return _day;
  }
  /**
   * returns this date - that date in milliseconds
   */
  public long diff(JDateTime that)
  {
    long f = 0, t = 0;
    if (getDate() != null)
    {
      f = getDate().getTime();
    }
    if (that.getDate() != null)
    {
      t = that.getDate().getTime();
    }
    return f - t;
  }
  public int dow()
  {
    return isZero() ? -1 : getCalendar().get(Calendar.DAY_OF_WEEK);
  }
  @Override
  public boolean equals(Object objThat)
  {
    if (objThat == null)
    {
      return false;
    }
    if (!(objThat instanceof JDateTime))
    {
      return false;
    }
    JDateTime that = (JDateTime) objThat;
    return (this._year == that._year && this._month == that._month && this._day == that._day && this._hour == that._hour
        && this._minute == that._minute && this._second == that._second);
  }
  public String friendly(ITranslator t) throws Exception {
      JDateTime now = JDateTime.now();
      if(new JDate(this).equals(JDate.today()))
      {
          return hm12();
      }
      else if(new JDate(this).equals(JDate.yesterday()))
      {
          return t.xlate("Yesterday") + " " + hm12();
      }
      else if (new JDate(this).addDays(7).isAfter(JDate.today()))
      {
          return t.xlate(new JDate(this).dowStr()) + " " + hm12();
      }
      else if (year()==now.year())
      {
          return mdhm12(t);
      } 
      return mdyhm12(t);
  }
  public String mdyhm12(ITranslator t) throws Exception
  {
      return  isZero() ? "" : mmm(t) + " " + day() + ", " + year()
              + " " + hmnz12();
  }
  /**
   * returns a GregorianCalendar initialized to the ODate's values
   */
  public GregorianCalendar getCalendar()
  {
    return new GregorianCalendar(_year, _month - 1, _day, _hour, _minute, _second);
  }
  /**
   * returns whether values stored as a java.util.Date, or null if the values
   * do not make a valid date
   */
  public java.util.Date getDate()
  {
    if (isZero())
    {
      return new Date(0);
    }
    try
    {
      SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      String y = StringKit.right("0000" + year(), 4);
      String m = StringKit.right("00" + month(), 2);
      String d = StringKit.right("00" + day(), 2);
      String h = StringKit.right("00" + _hour, 2);
      String mi = StringKit.right("00" + _minute, 2);
      String s = StringKit.right("00" + _second, 2);
      String dt = y + "-" + m + "-" + d + "-" + h + "-" + mi + "-" + s;
      return fm.parse(dt);
    }
    catch (Exception e)
    {
      return null;
    }
  }
  /**
   * returns values stored as a JDate
   */
  public JDate getJDate()
  {
    return new JDate(year(), month(), day());
  }
  /**
   * returns a java.sql.Date if values are valid, or null
   */
  public java.sql.Date getSQLDate()
  {
    try
    {
      return new java.sql.Date(getDate().getTime());
    }
    catch (Exception e)
    {
      return null;
    }
  }
  public long getTime()
  {
    return isZero() ? 0 : getDate() == null ? -1 : getDate().getTime();
  }
  /**
   * @return a string literal
   */
  public String hm()
  {
    if (!isValid())
      return "";
    return _hour + ":" + StringKit.right("00" + _minute, 2);
  }
  public String hm12()
  {
      if (_hour > 12)
      {
          return (_hour - 12) + ":" + StringKit.right("00" + _minute, 2) + " pm";
      }
      else if(_hour == 12)
      {
          return _hour + ":" + StringKit.right("00" + _minute, 2) + " pm";
      }
      return _hour + ":" + StringKit.right("00" + _minute, 2) + " am";
  }
  public String hmnz()
  {
    if (_hour > 0 || _minute > 0)
    {
      return hm();
    }
    return "";
  }
  public String hmnz12()
  {
      if (_hour > 0 || _minute > 0)
      {
          return hm12();
      }
      return "";
  }
  public String hms()
  {
    return hms(":");
  }
  public String hms(String divider)
  {
    if (!isValid())
    {
      return "";
    }
    return StringKit.right("00" + _hour, 2) + divider + StringKit.right("00" + _minute, 2) + divider + StringKit.right("00" + _second, 2);
  }
  public String hmsnz()
  {
    if (_hour > 0 || _minute > 0 || _second > 0)
    {
      return hms();
    }
    return "";
  }
  /**
   * @return int hour of day, from 0 to 23
   */
  public int hour()
  {
    return _hour;
  }
  public boolean isAfter(JDateTime that)
  {
    return getTime() > that.getTime();
  }
  public boolean isBefore(JDateTime that)
  {
    return getTime() < that.getTime();
  }
  public boolean isMidnight()
  {
    return hour()==0 && minute()==0 && second()==0;
  }
  /**
   * returns whether values stored represent a valid date
   */
  public boolean isValid()
  {
    if (isZero())
      return true;
    if (_year < 0)
      return false;
    return !(getDate() == null);
  }
  /**
   * returns whether values stored are all zero
   */
  public boolean isZero()
  {
    return (_year == 0 && _month == 0 && _day == 0 && _hour == 0 && _minute == 0 && _second == 0);
  }
  public String longDmd()
  {
    return DateKit.longDmd(getDate());
  }
  public JDateTime max(JDateTime that)
  {
    return this.isAfter(that) ? this : that;
  }
  public JDateTime min(JDateTime that)
  {
    return this.isBefore(that) ? this : that;
  }
  /**
   * @return int minute of hour, from 0 to 59
   */
  public int minute()
  {
    return _minute;
  }
  /**
   * @return int month, from 1 to 12
   */
  public int month()
  {
    return _month;
  }
  public String mdhm12(ITranslator t) throws Exception
  {
      return isZero() ? "" : mmm(t) + " " + day() + ", " + hmnz12();
  }
  public String mmm(ITranslator t) throws Exception
  {
      return  isZero() ? "" : t.xlate("mmm" + month());
  }
  public boolean ne(JDateTime that)
  {
    return this.getTime()!=that.getTime();
  }
  public boolean notZero()
  {
    return !isZero();
  }
  /**
   * @return int second of minute, from 0 to 59
   */
  public int second()
  {
    return _second;
  }
  /**
   * @return a compact string literal
   */
  public String toCompactString()
  {
    if (!isValid())
    {
      return "[invalid]";
    }
    String y = StringKit.right("0000" + year(), 4);
    String m = StringKit.right("00" + month(), 2);
    String d = StringKit.right("00" + day(), 2);
    String h = StringKit.right("00" + _hour, 2);
    String mi = StringKit.right("00" + _minute, 2);
    String s = StringKit.right("00" + _second, 2);
    String ms = StringKit.right(System.currentTimeMillis()+"",3);
    return y + m + d + "_" + h + mi + s + "_"+ms;
  }
  public String toDashedString()
  {
    String y = StringKit.right("0000" + year(), 4);
    String m = StringKit.right("00" + month(), 2);
    String d = StringKit.right("00" + day(), 2);
    String h = StringKit.right("00" + _hour, 2);
    String mi = StringKit.right("00" + _minute, 2);
    String s = StringKit.right("00" + _second, 2);
    return y + "-" + m + "-" + d + " " + h + ":" + mi + ":" + s;
  }
  public String toICal()
  {
    String y = StringKit.right("0000" + year(), 4);
    String m = StringKit.right("00" + month(), 2);
    String d = StringKit.right("00" + day(), 2);
    String h = StringKit.right("00" + _hour, 2);
    String mi = StringKit.right("00" + _minute, 2);
    String s = StringKit.right("00" + _second, 2);
    return y + m + d + "T" + h + mi + s + "Z";
  }
  /**
   * @return a string literal
   */
  @Override
  public String toString()
  {
	  return ymdhms();
  }
  /**
   * @return int year
   */
  public int year()
  {
    return _year;
  }
  public String ymd()
  {
    return ymd("-");
  }
  public String ymd(String divider)
  {
    if (isZero() || !isValid())
    {
      return "";
    }
    String y = StringKit.right("0000" + year(), 4);
    String m = StringKit.right("00" + month(), 2);
    String d = StringKit.right("00" + day(), 2);
    return y + divider + m + divider + d;

  }
  public String ymdhms()
  {
	  if (isZero() || !isValid())
      {
          return "";
      }
      if (_hour==0 && _minute==0 && _second==0)
      {
          return ymd();
      }
      String h = StringKit.right("00" + _hour, 2);
      String mi = StringKit.right("00" + _minute, 2);
      String s = StringKit.right("00" + _second, 2);
      return ymd() + " " + h + ":" + mi + ":" + s;
  }
}

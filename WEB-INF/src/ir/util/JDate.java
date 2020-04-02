package ir.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
/**
 * JDate is an immutable date container that can go further back in time than
 * java.util.Date only works with year,month,day precision
 */
public class JDate implements java.io.Serializable,Comparable<JDate>
{
  public static final String[] frenchMonthAbbr = new String[]{"Janv", "Févr", "Mars", "Avr", "Mai", "Juin",
      "Juil", "Août", "Sept", "Oct", "Nov", "Déc"};
  public static final String[] englishMonthAbbr = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
      "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  private static final JDate dateMax = new JDate(3001, 12, 31);
  private static final JDate dateMin = new JDate(1970, 1, 1);
  public final static String DefaultFormat = "MMM d, yyyy";
  public static final String[] Dow = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
  public static final String[] DowAbbr = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
  private static ThreadLocal<Integer> HoursOffset = new ThreadLocal<Integer>();
  public final static String InternalFormat = "yyyy-MM-dd";
  private static final long serialVersionUID = 1L;
  public static int ServerLocationHoursOffset=-4;
  private static final JDate zeroDate = new JDate(0, 0, 0);
  public final static String ZeroString = "0000-00-00";
  public final static java.util.Date ZeroUtilDate = new java.util.Date(0);
  static
  {
    int utcH = Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.HOUR_OF_DAY);
    int locH = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    if (locH > utcH)
    {
      utcH += 24;
    }
    ServerLocationHoursOffset = locH - utcH;
    StringKit.println("Server location hours offset=" + ServerLocationHoursOffset + ", now=" + JDateTime.now().hms());
  }
  private final byte _day;
  private final byte _month;
  private final short _year;
  //
  public static int getServerHoursOffset()
  {
    return ServerLocationHoursOffset;
  }
  public static int getThreadTimeZoneOffset()
  {
    Integer h = HoursOffset.get();
    if (h == null)
    {
      return 0;
    }
    int res = h - ServerLocationHoursOffset;
    return res;
  }
  public static JDate max()
  {
    return dateMax;
  }
  public static JDate min()
  {
    return dateMin;
  }
  //
  public static JDate parse(Object o) throws Exception
  {
    String v = Coerce.toString(o).trim();
    if (v.equals("") || JDateTime.ZeroString.equals(v) || JDate.ZeroString.equals(v))
    {
      return zero();
    }
    v = JDate.toEnglish(v);
    try
    {
      return new JDate(new SimpleDateFormat(DefaultFormat).parse(v));
    }
    catch (Exception e)
    {
      try
      {
        return new JDate(new SimpleDateFormat(InternalFormat).parse(v));
      }
      catch (Exception e2)
      {
        return JDateTime.parse(o).getJDate();
      }
    }
  }
  public static JDate parseDefault(String v) throws Exception
  {
    if (v.trim().equals("") || ZeroString.equals(v) || JDate.ZeroString.equals(v))
    {
      return zero();
    }
    return new JDate(new SimpleDateFormat(DefaultFormat).parse(v));
  }
  public static void setThreadTimeZoneOffset(int h)
  {
    HoursOffset.set(h);
  }
  public static JDate today()
  {
    return new JDate(new Date(System.currentTimeMillis() + (getThreadTimeZoneOffset() * 60 * 60000)));
  }
  //
  public static String toEnglish(String v)
  {
    for (int i=0;i<JDate.frenchMonthAbbr.length;i++)
    {
      v = v.replace(JDate.frenchMonthAbbr[i], JDate.englishMonthAbbr[i]);
    }
    return v;
  }
  //
  public static String toFrench(String v)
  {
    for (int i=0;i<JDate.englishMonthAbbr.length;i++)
    {
      v = v.replace(JDate.englishMonthAbbr[i], JDate.frenchMonthAbbr[i]);
    }
    return v;
  }
  public static JDate yesterday()
  {
      return JDate.today().addDays(-1);
  }
  public static JDate zero()
  {
    return zeroDate;
  }
  public JDate(int year, int month, int date)
  {
    _year = Integer.valueOf(year).shortValue();
    _month = Integer.valueOf(month).byteValue();
    _day = Integer.valueOf(date).byteValue();
  }
  /**
   * Construct from sql Date
   */
  public JDate(java.sql.Date d)
  {
    this(d == null ? 0 : DateKit.year(d),d == null ? 0 :  DateKit.month(d),d == null ? 0 : DateKit.day(d));
  }
  /**
   * Construct from sql Timestamp
   */
  public JDate(java.sql.Timestamp ts)
  {
    this(new Date(ts == null ? 0 : ts.getTime()));
  }
  /**
   * Construct from java Date
   */
  public JDate(java.util.Date d)
  {
    this(d == null ? 0 : DateKit.year(d),d == null ? 0 : DateKit.month(d),d == null ? 0 : DateKit.day(d));
  }
  /**
   * Construct from JDateTime
   */
  public JDate(JDate d)
  {
    this(d == null ? 0 :d.year(),d == null ? 0 : d.month(),d == null ? 0 : d.day());
  }
  /**
   * Construct from JDateTime
   */
  public JDate(JDateTime d)
  {
    this(d.year(), d.month(), d.day());
  }
  /**
   * Construct from String with DefaultFormat
   */
  public JDate(String v) throws Exception
  {
    this(parse(v));
  }
  public JDate addDays(int nD)
  {
    Calendar c = getCalendar();
    c.add(Calendar.DATE, nD);
    return new JDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
  }
  public JDate addDays(int nD, boolean skipSaturday, boolean skipSunday)
  {
    int step = nD > 0 ? 1 : -1;
    Calendar c = getCalendar();
    while (nD != 0)
    {
      c.add(Calendar.DATE, step);
      int dow = c.get(Calendar.DAY_OF_WEEK);
      if (skipSaturday && dow == Calendar.SATURDAY)
        continue;
      if (skipSunday && dow == Calendar.SUNDAY)
        continue;
      nD -= step;
    }
    return new JDate(c.getTime());
  }
  public JDate addMonths(int n)
  {
    Calendar c = getCalendar();
    c.add(Calendar.MONTH, n);
    return new JDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
  }
  public JDate addYears(int n)
  {
    Calendar c = getCalendar();
    c.add(Calendar.YEAR, n);
    return new JDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
  }
  @Override
  public int compareTo(JDate that)
  {
    return this.getTime() > that.getTime() ? 1 : -1;
  }
  /**
   * @return int day of month
   */
  public int day()
  {
    return _day;
  }
  public int daysDifference(JDate from)
  {
    return (int) DateKit.differenceInDays(this.getDate(),from.getDate());
  }
  public int dow()
  {
    return isZero() ? -1 : getCalendar().get(Calendar.DAY_OF_WEEK);
  }
  public String dowStr() throws Exception
  {
    if (this.equals(JDate.zero()))
    {
      return "";
    }
    return DateKit.dowStr(getDate());
  }
  public JDateTime endOfDay()
  {
    return new JDateTime(_year, _month, _day, 23, 59, 59);
  }
  public JDate endOfMonth()
  {
    int y = _year + (_month == 12 ? 1 : 0);
    int m = (_month == 12 ? 1 : _month + 1);
    return new JDate(y, m, 1).addDays(-1);
  }
  @Override
  public boolean equals(Object objThat)
  {
    if (objThat == null)
      return false;
    if (!(objThat instanceof JDate))
      return false;
    JDate that = (JDate) objThat;
    return (this._year == that._year && this._month == that._month && this._day == that._day);
  }
  /**
   * returns a GregorianCalendar initialized to the ODate's values
   */
  public GregorianCalendar getCalendar()
  {
    return new GregorianCalendar(_year, _month - 1, _day, 0, 0, 0);
  }
  /**
   * returns a java.util.Date if values are valid, or null
   */
  public java.util.Date getDate()
  {
    if (isZero())
    {
      return new Date(0);
    }
    try
    {
      String y = StringKit.right("0000" + year(), 4);
      String m = StringKit.right("00" + month(), 2);
      String d = StringKit.right("00" + day(), 2);
      SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
      return fm.parse(y + "-" + m + "-" + d);
    }
    catch (Exception e)
    {
      return null;
    }
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
    return getDate() == null ? -1 : getDate().getTime();
  }
  public boolean isAfter(JDate that)
  {
    return getTime() > that.getTime();
  }
  public boolean isBefore(JDate that)
  {
    return getTime() < that.getTime();
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
    return (_year == 0 && _month == 0 && _day == 0);
  }
  public String longDmd()
  {
    return DateKit.longDmd(getDate());
  }
  /**
   * @return int month
   */
  public int month()
  {
    return _month;
  }
  public boolean notZero()
  {
    return !isZero();
  }
  public JDateTime startOfDay()
  {
    return new JDateTime(_year, _month, _day, 0, 0, 0);
  }
  public JDate startOfMonth()
  {
    return new JDate(_year, _month, 1);
  }
  /**
   * @return a string literal
   */
  @Override
  public String toString()
  {
    return ymd();
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
    if (isZero() || !isValid())
    {
      return "";
    }
    String y = StringKit.right("0000" + year(), 4);
    String m = StringKit.right("00" + month(), 2);
    String d = StringKit.right("00" + day(), 2);
    return y + "-" + m + "-" + d;
  }
}

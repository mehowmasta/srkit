package ir.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateKit
{
  private final static String DOT_SEPARATOR = ".";
  private final static String HYPHEN_SEPARATOR = "-";
  private static String[] Months_En = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
  private static String[] Months_Fr = new String[] { "Janv", "Févr", "Mars", "Avr", "Mai", "Juin", "Juil", "Août", "Sept", "Oct", "Nov",
  "Déc" };
  private static String[] Months_Fr3 = new String[] { "Jan", "Fév", "Mar", "Avr", "Mai", "Jui", "Jui", "Aoû", "Sep", "Oct", "Nov", "Déc" };
  private final static String SLASH_SEPARATOR = "/";
  public final static int UNIT_DAY = 5;
  public final static int UNIT_HOUR = 4;
  public final static int UNIT_MILLISECOND = 1;
  public final static int UNIT_MINUTE = 3;
  public final static int UNIT_MONTH = 6;
  public final static int UNIT_SECOND = 2;
  public final static int UNIT_YEAR = 7;
  /**
   * Returns a date offset by the passed number of days. Offset can be
   * negative.
   */
  public static Date addDays(Date dFrom, int days)
  {
    Calendar cl = new GregorianCalendar();
    cl.setTime(dFrom);
    cl.add(Calendar.DAY_OF_YEAR, days);
    return cl.getTime();
  }
  public static Date addMonths(Date fromDate,int nMonths)
  {
    Calendar c = cal(fromDate);
    int monthBefore = c.get(Calendar.MONTH);
    c.add(Calendar.MONTH, nMonths);
    if (c.get(Calendar.MONTH) != monthBefore + nMonths)
    {
      c.add(Calendar.DAY_OF_YEAR,nMonths > 0 ? 1 : -1);
    }
    return c.getTime();
  }
  /**
   * Returns a date offset by the passed number of week days. Offset can be
   * negative.
   */
  public static Date addWeekDays(Date dFrom, int days)
  {
    Calendar cl = new GregorianCalendar();
    cl.setTime(dFrom);
    int i = 0;
    while (i < Math.abs(days))
    {
      cl.add(Calendar.DATE, days > 0 ? 1 : -1);
      if (Calendar.SATURDAY != cl.get(Calendar.DAY_OF_WEEK) && Calendar.SUNDAY != cl.get(Calendar.DAY_OF_WEEK))
      {
        i++;
      }
    }
    return cl.getTime();
  }
  public static Date addYears(Date fromDate,int nYears)
  {
    Calendar c = cal(fromDate);
    int yearBefore = c.get(Calendar.YEAR);
    c.add(Calendar.YEAR, nYears);
    if (c.get(Calendar.YEAR) != yearBefore + nYears)
    {
      c.add(Calendar.DAY_OF_YEAR,nYears > 0 ? 1 : -1);
    }
    return c.getTime();
  }
  private static Calendar cal(Date d)
  {
    Calendar cal = getFormatSet().cal;
    cal.setTime(d);
    return cal;
  }
  /**
   * Compares the two passed <code>Calendar</code> objects and indicates their
   * relative sequence in time. <br>
   * Creation date: (10/31/00 1:15:05 PM)
   * 
   * @param date1
   *            a <code>Calendar</code> representing the first date to
   *            compare. If the value is <tt>null</tt> then it will be treated
   *            as the current date with a 0 time value (midnight).
   * @param date2
   *            a <code>Calendar</code> representing the second date to
   *            compare. If the value is <tt>null</tt> then it will be treated
   *            as the current date with a 0 time value (midnight).
   * @return the value 0 if <tt>date1</tt> is equal to <tt>date2</tt>; a value
   *         less than 0 if <tt>date1</tt> is before <tt>date2</tt>; and a
   *         value greater than 0 if <tt>date1</tt> is after <tt>date2</tt>.
   */
  public static int compare(Calendar date1, Calendar date2)
  {
    Date d1 = null;
    Date d2 = null;
    if (date1 != null)
      d1 = date1.getTime();
    if (date2 != null)
      d2 = date2.getTime();
    return (compare(d1, d2));
  }
  /**
   * Compares the two passed <code>Date</code> objects and indicates their
   * relative sequence in time. <br>
   * Creation date: (10/31/00 1:15:05 PM)
   * 
   * @param date1
   *            the first <code>Date</code> to compare. If the value is
   *            <tt>null</tt> then it will be treated as the current date with
   *            a 0 time value (midnight).
   * @param date2
   *            the second <code>Date</code> to compare. If the value is
   *            <tt>null</tt> then it will be treated as the current date with
   *            a 0 time value (midnight).
   * @return the value 0 if <tt>date1</tt> is equal to <tt>date2</tt>; a value
   *         less than 0 if <tt>date1</tt> is before <tt>date2</tt>; and a
   *         value greater than 0 if <tt>date1</tt> is after <tt>date2</tt>.
   */
  public static int compare(Date date1, Date date2)
  {
    // If date1 is null, make equal to today's date
    if (date1 == null)
    {
      Calendar now = Calendar.getInstance();
      // Set time to 00:00:00
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.HOUR_OF_DAY, 0);
      now.set(Calendar.MILLISECOND, 0);
      date1 = now.getTime();
    }
    // If date2 is null, make equal to today's date
    if (date2 == null)
    {
      Calendar now = Calendar.getInstance();
      // Set time to 00:00:00
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.HOUR_OF_DAY, 0);
      now.set(Calendar.MILLISECOND, 0);
      date2 = now.getTime();
    }
    // Compare the two dates and return the result
    return (date1.compareTo(date2));
  }
  /**
   * Compares the two passed <code>String</code> representations of dates and
   * indicates their relative sequence in time. <br>
   * Creation date: (10/31/00 1:15:05 PM)
   * 
   * @param date1
   *            a <code>String</code> representing the first date to compare.
   *            If the value is <tt>null</tt> then it will be treated as the
   *            current date with a 0 time value (midnight).
   * @param date2
   *            a <code>String</code> representing the second date to compare.
   *            If the value is <tt>null</tt> then it will be treated as the
   *            current date with a 0 time value (midnight).
   * @return the value 0 if <tt>date1</tt> is equal to <tt>date2</tt>; a value
   *         less than 0 if <tt>date1</tt> is before <tt>date2</tt>; and a
   *         value greater than 0 if <tt>date1</tt> is after <tt>date2</tt>.
   * @throws java.text.ParseException
   *             if either of the passed strings cannot be parsed to a
   *             <code>Date</code>.
   */
  public static int compare(String date1, String date2) throws java.text.ParseException
  {
    Date d1 = null;
    Date d2 = null;
    if (date1 != null && !date1.equals(""))
      d1 = parse(date1);
    if (date2 != null && !date2.equals(""))
      d2 = parse(date2);
    return (compare(d1, d2));
  }
  /**
   * Constructs a date with calendar year, month and day. Note month is 1
   * based, unlike Calendar.
   */
  public static Date create(int y, int m, int d)
  {
    return new GregorianCalendar(y, m - 1, d).getTime();
  }
  public static String dateStrToEn(String dtStr)
  {
    for (int i = 0; i < 12; i++)
    {
      dtStr = StringKit.replace(dtStr, Months_Fr[i], Months_En[i]);
      dtStr = StringKit.replace(dtStr, Months_Fr3[i], Months_En[i]);
    }
    return dtStr;
  }
  public static String dateStrToFr(String dtStr)
  {
    for (int i = 0; i < 12; i++)
    {
      dtStr = StringKit.replace(dtStr, Months_En[i], Months_Fr[i]);
    }
    return dtStr;
  }
  //
  public static int day(Date d)
  {
    return cal(d).get(Calendar.DAY_OF_MONTH);
  }
  //
  public static Date dayEnd(Date d)
  {
    Calendar c = cal(d);
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 59);
    c.set(Calendar.SECOND, 59);
    c.set(Calendar.MILLISECOND, 999);
    return c.getTime();
  }
  //
  public static Date dayStart(Date d)
  {
    Calendar c = cal(d);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }
  /**
   * Calculates the difference between the two passed dates in the specified
   * unit of time. If either of the two date parameters are null, they will be
   * defaulted to the current system date with a time value of 0 (midnight). <br>
   * Creation date: (10/31/00 3:05:34 PM)
   * 
   * @return the number of time units between the two dates.
   * @param date1
   *            the first <code>Date</code> value.
   * @param date2
   *            the second <code>Date</code> value.
   */
  public static long difference(Calendar date1, Calendar date2, int unitOfTime)
  {
    Date d1 = null;
    Date d2 = null;
    if (date1 != null)
      d1 = date1.getTime();
    if (date2 != null)
      d2 = date2.getTime();
    return (difference(d1, d2, unitOfTime));
  }
  /**
   * Calculates the difference between the two passed dates in the specified
   * unit of time. If either of the two date parameters are null, they will be
   * defaulted to the current system date with a time value of 0 (midnight). <br>
   * Creation date: (10/31/00 3:05:34 PM)
   * 
   * @return the number of time units between the two dates.
   * @param date1
   *            the first <code>Date</code> value.
   * @param date2
   *            the second <code>Date</code> value.
   */
  public static long difference(Date date1, Date date2, int unitOfTime)
  {
    long difference = 0;
    // If date1 is null, make equal to today's date
    if (date1 == null)
    {
      Calendar now = Calendar.getInstance();
      // Set time to 00:00:00
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.HOUR_OF_DAY, 0);
      now.set(Calendar.MILLISECOND, 0);
      date1 = now.getTime();
    }
    // If date2 is null, make equal to today's date
    if (date2 == null)
    {
      Calendar now = Calendar.getInstance();
      // Set time to 00:00:00
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.HOUR_OF_DAY, 0);
      now.set(Calendar.MILLISECOND, 0);
      date2 = now.getTime();
    }
    switch (unitOfTime)
    {
      case UNIT_MILLISECOND:
        difference = differenceInMilliseconds(date1, date2);
        break;
      case UNIT_SECOND:
        difference = differenceInSeconds(date1, date2);
        break;
      case UNIT_MINUTE:
        difference = differenceInMinutes(date1, date2);
        break;
      case UNIT_HOUR:
        difference = differenceInHours(date1, date2);
        break;
      case UNIT_DAY:
        difference = differenceInDays(date1, date2);
        break;
      case UNIT_MONTH:
        // difference = differenceInMonths(date1, date2);
        break;
      case UNIT_YEAR:
        // difference = differenceInYears(date1, date2);
        break;
      default:
        throw new java.lang.IllegalArgumentException("The unitOfTime argument must be one of: "
            + "UNIT_MILLISECOND, UNIT_SECOND, UNIT_MINUTE, " + "UNIT_HOUR, UNIT_DAY, UNIT_MONTH, or UNIT_YEAR.");
    }
    return (difference);
  }
  /**
   * Calculates the difference between the two passed dates in the specified
   * unit of time. If either of the two date parameters are null, they will be
   * defaulted to the current system date with a time value of 0 (midnight). <br>
   * Creation date: (10/31/00 3:05:34 PM)
   * 
   * @return the number of time units between the two dates.
   * @param date1
   *            the first <code>Date</code> value.
   * @param date2
   *            the second <code>Date</code> value.
   */
  public static long difference(String date1, String date2, int unitOfTime) throws java.text.ParseException
  {
    Date d1 = null;
    Date d2 = null;
    if (date1 != null && !date1.equals(""))
      d1 = parse(date1);
    if (date2 != null && !date2.equals(""))
      d2 = parse(date2);
    return (difference(d1, d2, unitOfTime));
  }
  /**
   * Returns the absolute value of the difference between the two supplied
   * dates in days. <br>
   * Creation date: (10/31/00 3:45:10 PM)
   * 
   * @param date1
   *            the first <code>Date</code> object.
   * @param date2
   *            the second <code>Date</code> object.
   * @return the number of days between the two passed dates.
   */
  public static long differenceInDays(Date date1, Date date2)
  {
    long diff = differenceInHours(date1, date2);
    return (diff / 24);
  }
  /**
   * Returns the absolute value of the difference between the two supplied
   * dates in hours. <br>
   * Creation date: (10/31/00 3:45:10 PM)
   * 
   * @param date1
   *            the first <code>Date</code> object.
   * @param date2
   *            the second <code>Date</code> object.
   * @return the number of hours between the two passed dates.
   */
  private static long differenceInHours(Date date1, Date date2)
  {
    long diff = differenceInMinutes(date1, date2);
    return (diff / 60);
  }
  /**
   * Returns the absolute value of the difference between the two supplied
   * dates in milliseconds. <br>
   * Creation date: (10/31/00 3:45:10 PM)
   * 
   * @param date1
   *            the first <code>Date</code> object.
   * @param date2
   *            the second <code>Date</code> object.
   * @return the number of milliseconds between the two passed dates.
   */
  private static long differenceInMilliseconds(Date date1, Date date2)
  {
    long t1 = date1.getTime();
    long t2 = date2.getTime();
    long diff = t2 - t1;
    if (diff < 0)
      diff = diff * -1;
    return (diff);
  }
  /**
   * Returns the absolute value of the difference between the two supplied
   * dates in minutes. <br>
   * Creation date: (10/31/00 3:45:10 PM)
   * 
   * @param date1
   *            the first <code>Date</code> object.
   * @param date2
   *            the second <code>Date</code> object.
   * @return the number of minutes between the two passed dates.
   */
  private static long differenceInMinutes(Date date1, Date date2)
  {
    long diff = differenceInSeconds(date1, date2);
    return (diff / 60);
  }
  /**
   * Returns the absolute value of the difference between the two supplied
   * dates in seconds. <br>
   * Creation date: (10/31/00 3:45:10 PM)
   * 
   * @param date1
   *            the first <code>Date</code> object.
   * @param date2
   *            the second <code>Date</code> object.
   * @return the number of seconds between the two passed dates.
   */
  private static long differenceInSeconds(Date date1, Date date2)
  {
    long diff = differenceInMilliseconds(date1, date2);
    return (diff / 1000);
  }
  public static String dowStr(Date d) throws Exception
  {
    if (d == null || d.getTime()==0)
    {
      return "";
    }
    return DateKit.getFormatSet().dowFull.format(d);
  }
  public static String format(Date d, String fmt)
  {
    if (null == d || 0 == d.getTime())
    {
      return "";
    }
    return new SimpleDateFormat(fmt).format(d);
  }
  /**
   * Instantiates and returns a <code>Date</code> object based on the supplied
   * <code>year</code>, <code>month</code>, and <code>day</code> values.
   * 
   * @param year
   *            the year in human-readable form (ie. 2000, not 0).
   * @param month
   *            the month in human-readable form (ie. 1 - 12).
   * @param day
   *            the day of the month (ie. 1 - 31).
   * @param time
   *            the time of day (ie. "17:15:00" or "8:30 AM")
   * @return a <code>Date</code> object.
   */
  private static Date getDate(int year, int month, int day, String time)
  {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    // Adjust the year to be appropriate for the Date costructor
    if (year < 80)
    {
      year += 2000;
    }
    else if (year < 100)
    {
      year += 1900;
    }
    // Adjust the month to be appropriate for the Date constructor
    month -= 1;
    int hour = 0;
    int minute = 0;
    int second = 0;
    int milliSecond = 0;
    // Determine hour, minute and second values if a time was supplied
    if (null != time && !time.equals(""))
    {
      time = time.toUpperCase();
      int amIdx = time.indexOf("AM");
      int pmIdx = time.indexOf("PM");
      if (amIdx > -1 || pmIdx > -1)
      {
        if (amIdx > -1)
        {
          time = time.substring(0, amIdx).trim();
        }
        else
        {
          time = time.substring(0, pmIdx).trim();
        }
      }
      String[] timeVals = time.split(":");
      if (pmIdx > -1)
      {
        timeVals[0] = String.valueOf(Integer.parseInt(timeVals[0]) + 12);
      }
      hour = Integer.parseInt(timeVals[0]);
      minute = Integer.parseInt(timeVals[1]);
      if (timeVals.length == 3)
      {
        // Check for millisecond (if '.' exists)
        int millIdx = timeVals[2].indexOf(".");
        if (millIdx > -1)
        {
          milliSecond = Integer.parseInt(timeVals[2].substring(millIdx + 1));
          second = Integer.parseInt(timeVals[2].substring(0, millIdx));
        }
        else
        {
          second = Integer.parseInt(timeVals[2]);
        }
      }
    }
    cal.set(year, month, day, hour, minute, second);
    cal.set(Calendar.MILLISECOND, milliSecond);
    return (cal.getTime());
  }
  /**
   * Returns a <tt>Date</tt> from the supplied <tt>Object</tt>. If the object
   * is <code>null</code> or cannot be converted to a <tt>Date</tt>, this
   * method will return today's date. This method is intended to be used when
   * the type of <tt>Object</tt> is unknown, but likely to be a date-related
   * class. If the passed object is an instance of <tt>java.util.Date</tt>,
   * any of the <tt>Date</tt> subclasses in the <tt>java.sql</tt> package, or
   * <tt>java.util.Calendar</tt>, it will be returned as a <tt>Date</tt>. In
   * all other cases, the string value of the object will be passed to the
   * <code>parseDateFromString()</code> method. <br>
   * Creation date: (2/1/2001 9:37:18 AM)
   * 
   * @return java.util.Date
   * @param obj
   *            java.lang.Object
   */
  public static Date getDateValue(Object obj)
  {
    if (null == obj)
    {
      return new Date();
    }
    // Handle Date and sub-classes of Date
    if (obj instanceof java.util.Date || obj instanceof java.sql.Date || obj instanceof java.sql.Time
        || obj instanceof java.sql.Timestamp)
    {
      return (Date) obj;
    }
    // Handle Calendar
    if (obj instanceof java.util.Calendar)
    {
      return ((Calendar) obj).getTime();
    }
    // Handle anything else
    try
    {
      return parse(obj.toString());
    }
    catch (ParseException e)
    {
      return new Date();
    }
  }
  private static DateFormatSet getFormatSet()
  {
    return DateFormatSet.get();
  }
  public static String hm(Date d)
  {
    return getFormatSet().hm.format(d);
  }
  public static String hms(Date d)
  {
    return getFormatSet().hms.format(d);
  }
  public static String hmsm(Date d)
  {
    return getFormatSet().hmsm.format(d);
  }
  public static String hmsz(Date d)
  {
    return getFormatSet().hms.format(d) + " " + timezoneShort();
  }
  public static int hour(Date d)
  {
    return cal(d).get(Calendar.HOUR_OF_DAY);
  }
  /** indicates whether date passed is Friday */
  public static boolean isDay(Date d,int day)
  {
    return DateKit.cal(d).get(Calendar.DAY_OF_WEEK)==day;
  }
  /** indicates whether today passed is day passed */
  public static boolean isDay(int day)
  {
    return DateKit.cal(new Date()).get(Calendar.DAY_OF_WEEK)==day;
  }
  public static boolean isMidnight(Date d)
  {
    Calendar c = new GregorianCalendar();
    c.setTime(d);
    return 0 == c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + c.get(Calendar.MILLISECOND);
  }
  public static boolean isZero(Date t)
  {
    return t == null || t.getTime() == 0;
  }
  public static boolean isZero(java.sql.Date t)
  {
    return t == null || t.getTime() == 0;
  }
  public static boolean isZero(java.sql.Time t)
  {
    return t == null || t.getTime() == 0;
  }
  public static boolean isZero(java.sql.Timestamp t)
  {
    return t == null || t.getTime() == 0;
  }
  public static String longDmd(Date d)
  {
    return getFormatSet().dmdFull.format(d);
  }
  public static String md(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    return getFormatSet().md.format(d);
  }
  //
  public static String mdhm(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    if (isMidnight(d))
    {
      return md(d);
    }
    return getFormatSet().mdhm.format(d);
  }
  //
  public static String mdhms(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    if (isMidnight(d))
    {
      return md(d);
    }
    return getFormatSet().mdhms.format(d);
  }
  public static String mdy(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    return getFormatSet().mdy.format(d);
  }
  //
  public static String mdyhm(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    if (isMidnight(d))
    {
      return mdy(d);
    }
    return getFormatSet().mdyhm.format(d);
  }
  public static int minute(Date d)
  {
    return cal(d).get(Calendar.MINUTE);
  }
  public static String minuteStamp()
  {
    return minuteStamp(new Date());
  }
  public static String minuteStamp(Date d)
  {
    return getFormatSet().minuteStamp.format(d);
  }
  public static String mmddyy(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    return getFormatSet().mmddyy.format(d);
  }
  /**
   * Retrieves calendar month. Note month is 1 based, unlike Calendar.
   */
  public static int month(Date d)
  {
    return cal(d).get(Calendar.MONTH) + 1;
  }
  public static java.sql.Date nowSqlDate()
  {
    return new java.sql.Date(new Date().getTime());
  }
  public static java.sql.Timestamp nowTimestamp()
  {
    return new java.sql.Timestamp(new Date().getTime());
  }
  /**
   * Heuristically converts string to date.
   */
  public static Date parse(String str) throws java.text.ParseException
  {
    if (str == null || str.equals(""))
    {
      return null;
    }
    Date result;
    // Assume that a [space] divides date from time
    String[] info = str.split(" ");
    String dt = info[0];
    String tm = "";
    if (info.length == 2) // if a time value was also passed
    {
      tm = info[1];
    }
    else if (info.length == 3) // if a time value with AM/PM was also passed
    {
      tm = info[1] + " " + info[2];
    }
    // Determine the date separator character
    String delim;
    if (dt.indexOf(SLASH_SEPARATOR) != -1)
    {
      delim = SLASH_SEPARATOR;
    }
    else if (dt.indexOf(HYPHEN_SEPARATOR) != -1)
    {
      delim = HYPHEN_SEPARATOR;
    }
    else if (dt.indexOf(DOT_SEPARATOR) != -1)
    {
      delim = DOT_SEPARATOR;
    }
    else
    {
      throw new java.text.ParseException("Could not determine date separator character.", 0);
    }
    // Use the date separator to split the date into individual elements
    String[] dateTokens = dt.split(delim);
    if (dateTokens.length != 3)
    {
      throw new java.text.ParseException("Failed to parse date string.", 0);
    }
    // Determine if all elements are numeric and call appropriate parser
    if (StringKit.isNumber(dateTokens[0]) && StringKit.isNumber(dateTokens[1]) && StringKit.isNumber(dateTokens[2]))
    {
      result = parseNumeric(dateTokens, tm);
    }
    else
    {
      result = parseMixed(dateTokens, tm);
    }
    return (result);
  }
  /**
   * Examines the elements of the passed array and attempts to determine which
   * element represents the year, month and day. It will return a
   * <code>Date</code> object instantiated with its findings.
   * 
   * @param dateTokens
   *            array of date elements - should only have three elements.
   * @param time
   *            any time value associated with the date.
   * @return a <code>Date</code> object.
   */
  private static Date parseMixed(String[] dateTokens, String time) throws java.text.ParseException
  {
    int year = 0;
    int month = 0;
    int day = 0;
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    DateFormatSymbols symbols = df.getDateFormatSymbols();
    String[] shortMonths = symbols.getShortMonths();
    String[] longMonths = symbols.getMonths();
    // Determine if the first element is not numeric
    if (!StringKit.isNumber(dateTokens[0]))
    {
      month = -1;
      // Determine if its value is included in one of the months array
      for (int i = 0; i < shortMonths.length; i++)
      {
        if (dateTokens[0].equalsIgnoreCase(shortMonths[i]) || dateTokens[0].equalsIgnoreCase(longMonths[i]))
        {
          month = i;
          break;
        }
      }
      // If not, throw an exception (assume no one would spell out the day
      // or year)
      if (month == -1)
      {
        throw new java.text.ParseException("Failed to parse date string.", 0);
      }
      // Determine if the second element is obviously the year (ie. Jun
      // 2000 14)
      if (Integer.parseInt(dateTokens[1]) > 31 || Integer.parseInt(dateTokens[1]) == 0)
      {
        year = Integer.parseInt(dateTokens[1]);
        day = Integer.parseInt(dateTokens[2]);
      }
      else
        // Assume the third element is the year (ie. Jun 14 2000)
      {
        year = Integer.parseInt(dateTokens[2]);
        day = Integer.parseInt(dateTokens[1]);
      }
    }
    // Determine if the second element is not numeric
    else if (!StringKit.isNumber(dateTokens[1]))
    {
      month = -1;
      // Determine if its value is included in one of the months arrays
      for (int i = 0; i < shortMonths.length; i++)
      {
        if (dateTokens[1].equalsIgnoreCase(shortMonths[i]) || dateTokens[1].equalsIgnoreCase(longMonths[i]))
        {
          month = i;
          break;
        }
      }
      // If not, throw an exception (assume no one would spell out the day
      // or year)
      if (month == -1)
      {
        throw new java.text.ParseException("Failed to parse date string.", 0);
      }
      // Determine if the first element is obviously the year (ie. 2000
      // Jun 14)
      if (Integer.parseInt(dateTokens[0]) > 31 || Integer.parseInt(dateTokens[0]) == 0)
      {
        year = Integer.parseInt(dateTokens[0]);
        day = Integer.parseInt(dateTokens[2]);
      }
      else
        // Assume the third element is the year (ie. 14 Jun 2000)
      {
        year = Integer.parseInt(dateTokens[2]);
        day = Integer.parseInt(dateTokens[0]);
      }
    }
    else
      // The third element must be not numeric
    {
      month = -1;
      // Determine if its value is included in one of the months arrays
      for (int i = 0; i < shortMonths.length; i++)
      {
        if (dateTokens[2].equalsIgnoreCase(shortMonths[i]) || dateTokens[2].equalsIgnoreCase(longMonths[i]))
        {
          month = i;
          break;
        }
      }
      // If not, throw an exception (assume no one would spell out the day
      // or year)
      if (month == -1)
      {
        throw new java.text.ParseException("Failed to parse date string.", 0);
      }
      // Determine if the second element is obviously the year (ie. 14
      // 2000 Jun)
      if (Integer.parseInt(dateTokens[1]) > 31 || Integer.parseInt(dateTokens[1]) == 0)
      {
        year = Integer.parseInt(dateTokens[1]);
        day = Integer.parseInt(dateTokens[0]);
      }
      else
        // Assume the first element is the year (ie. 2000 14 Jun)
      {
        year = Integer.parseInt(dateTokens[0]);
        day = Integer.parseInt(dateTokens[1]);
      }
    }
    return (getDate(year, month + 1, day, time));
  }
  /**
   * Examines the elements of the passed array and attempts to determine which
   * element represents the year, month and day. It will return a
   * <code>Date</code> object instantiated with its findings.
   * 
   * @param dateTokens
   *            array of date elements - should only have three elements.
   * @param time
   *            any time value associated with the date.
   * @return a <code>Date</code> object.
   */
  private static Date parseNumeric(String[] dateTokens, String time)
  {
    // Convert string tokens to int
    int[] intTokens = { Integer.parseInt(dateTokens[0]), Integer.parseInt(dateTokens[1]), Integer.parseInt(dateTokens[2]) };
    int year = 0;
    int month = 0;
    int day = 0;
    // If the first element is obviously a year...
    if (intTokens[0] > 31 || intTokens[0] == 0)
    {
      // System.out.println("First element is obviously a year.");
      // //-----------
      year = intTokens[0];
      // See if the second element is obviously a day (ie. yy/dd/mm)
      if (intTokens[1] > 12)
      {
        day = intTokens[1];
        month = intTokens[2];
      }
      else
        // Assume second element is the month (ie. yy/mm/dd)
      {
        month = intTokens[1];
        day = intTokens[2];
      }
    }
    // Else if the second element is obviously a year...
    else if (intTokens[1] > 31 || intTokens[1] == 0)
    {
      // System.out.println("Second element is obviously a year.");
      // //-----------
      year = intTokens[1];
      // See if the first element is obviously a day (ie. dd/yy/mm)
      if (intTokens[0] > 12)
      {
        day = intTokens[0];
        month = intTokens[2];
      }
      else
        // Assume the first element is the month (ie. mm/yy/dd)
      {
        month = intTokens[0];
        day = intTokens[2];
      }
    }
    // Else if the third element is obviously a year...
    else if (intTokens[2] > 31 || intTokens[2] == 0)
    {
      // System.out.println("Third element is obviously a year.");
      // //-----------
      year = intTokens[2];
      // See if the first element is obviously a day (ie. dd/mm/yy)
      if (intTokens[0] > 12)
      {
        day = intTokens[0];
        month = intTokens[1];
      }
      else
        // Assume the first element is the month (ie. mm/dd/yy)
      {
        month = intTokens[0];
        day = intTokens[1];
      }
    }
    // Else, there's no obvious year...
    else
    {
      // System.out.println("No obvious year."); //-----------
      // See if the second element could be a day
      if (intTokens[1] > 12)
      {
        // Assume format is mm/dd/yy
        month = intTokens[0];
        day = intTokens[1];
        year = intTokens[2];
      }
      // Else see if the first element could be a day...
      else if (intTokens[0] > 12)
      {
        // Assume format is dd/mm/yy
        day = intTokens[0];
        month = intTokens[1];
        year = intTokens[2];
      }
      // Else see if the third element could be a day
      else if (intTokens[2] > 12)
      {
        // Assume format is yy/mm/dd
        year = intTokens[0];
        month = intTokens[1];
        day = intTokens[2];
      }
      else
      {
        // Assume format is mm/dd/yy
        month = intTokens[0];
        day = intTokens[1];
        year = intTokens[2];
      }
    }
    return (getDate(year, month, day, time));
  }
  public static int second(Date d)
  {
    return cal(d).get(Calendar.SECOND);
  }
  public static void setNow(Date t)
  {
    t.setTime(new Date().getTime());
  }
  public static void setNow(java.sql.Date t)
  {
    t.setTime(new Date().getTime());
  }
  public static void setNow(java.sql.Time t)
  {
    t.setTime(new Date().getTime());
  }
  public static void setNow(java.sql.Timestamp t)
  {
    t.setTime(new Date().getTime());
  }
  public static void setZero(Date t)
  {
    t.setTime(0);
  }
  public static void setZero(java.sql.Date t)
  {
    t.setTime(0);
  }
  public static void setZero(java.sql.Time t)
  {
    t.setTime(0);
  }
  public static void setZero(java.sql.Timestamp t)
  {
    t.setTime(0);
  }
  public static String stamp()
  {
    return stamp(new Date());
  }
  public static String stamp(Date d)
  {
    return getFormatSet().stamp.format(d);
  }
  public static String timezoneShort()
  {
    TimeZone tz = Calendar.getInstance().getTimeZone();
    return tz.getDisplayName(tz.inDaylightTime(new Date()),TimeZone.SHORT);
  }
  public static long todayLong()
  {
    return Long.parseLong(todayYmd());
  }
  public static Long todayLongObj()
  {
    return new Long(todayYmd());
  }
  public static String todayYmd()
  {
    return getFormatSet().yyyyMMdd.format(new Date());
  }
  public static long toLong(Date d)
  {
    if (isZero(d))
    {
      return 0;
    }
    return Long.parseLong(getFormatSet().yyyyMMdd.format(d));
  }
  public static String wmd(Date d)
  {
    return getFormatSet().dFullmd.format(d);
  }
  public static int year(Date d)
  {
    return cal(d).get(Calendar.YEAR);
  }

  public static String ymd(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    return getFormatSet().ymd.format(d);
  }
  public static String ymdDash(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    return getFormatSet().ymdDash.format(d);
  }
  public static String ymdhm(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    if (isMidnight(d))
    {
      return ymd(d);
    }
    return getFormatSet().ymdhm.format(d);
  }
  public static String ymdhms(Date d)
  {
    if (isZero(d))
    {
      return "";
    }
    if (isMidnight(d))
    {
      return ymd(d);
    }
    return getFormatSet().ymdhms.format(d);
  }
}

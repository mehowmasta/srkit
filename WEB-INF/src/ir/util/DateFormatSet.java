package ir.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DateFormatSet
{
  public final DateFormat dowFull = new SimpleDateFormat("EEEE");
  public final DateFormat dFullmd = new SimpleDateFormat("EEEE, MMM d");
  public final DateFormat dmdFull = new SimpleDateFormat("EEEE, MMMM d");
  public final DateFormat hm = new SimpleDateFormat("H:mm");
  public final DateFormat hms = new SimpleDateFormat("H:mm:ss");
  public final DateFormat hmsm = new SimpleDateFormat("H:mm:ss.SSS");
  public final DateFormat md = new SimpleDateFormat("MMM d");
  public final DateFormat mdhm = new SimpleDateFormat("MMM d HH:mm");
  public final DateFormat mdhms = new SimpleDateFormat("MMM d HH:mm:ss");
  public final DateFormat mdy = new SimpleDateFormat("MMM d yyyy");
  public final DateFormat mdyhm = new SimpleDateFormat("MMM d yyyy HH:mm");
  public final DateFormat mmddyy = new SimpleDateFormat("mm/dd/yy");
  public final DateFormat minuteStamp = new SimpleDateFormat("yyyyMMdd_HHmm");
  public final DateFormat stamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
  public final DateFormat ymd = new SimpleDateFormat("yyyy/MM/dd");
  public final DateFormat ymdDash = new SimpleDateFormat("yyyy-MM-dd");
  public final DateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
  public final DateFormat ymdhm = new SimpleDateFormat("yyyy/MM/dd HH:mm");
  public final DateFormat ymdhms = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  public final Calendar cal = new GregorianCalendar();
  private static final Map<Integer,DateFormatSet> map=new ConcurrentHashMap<Integer,DateFormatSet>();
  public static void clear()
  {
    map.clear();
  }
  //
  public static DateFormatSet get()
  {
    DateFormatSet set = map.get(Thread.currentThread().hashCode());
    if (set == null)
    {
      set = new DateFormatSet();
      map.put(Thread.currentThread().hashCode(),set);
    }
    return set;
  }
}

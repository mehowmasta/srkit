package ir.web;

import ir.util.StringKit;
import java.io.Serializable;
/**
 * LocationBar is a location bar builder
 */
public final class Location implements Serializable
{
  private static final long serialVersionUID = 1L;
  public final String text;
  public final String url;
  //
  public Location(String url, String text)
  {
    this.url = url;
    this.text = text;
  }
  public String getFileName()
  {
    return StringKit.getFileName(url);
  }
  public boolean same(String thatUrl)
  {
    String thisLc = StringKit.getFileName(this.url).toLowerCase();
    String thatLc = StringKit.getFileName(thatUrl).toLowerCase();
    return thisLc.equals(thatLc);
  }
  @Override
  public String toString()
  {
    return url + "::" + text;
  }
}

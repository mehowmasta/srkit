package ir.web;

import ir.util.StringKit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * LocationBar is a location bar builder
 */
public final class LocationBar implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final String Delim = "|";
  private final String Div = "[<>]";
  private final List<Location> entries = new ArrayList<Location>();
  private final Location home;
  //
  public LocationBar(String homeUrl, String homeLabel)
  {
    this.home = new Location(homeUrl, homeLabel);
    entries.add(home);
  }
  /**
   * addEntry adds a url and title to the locbar data
   */
  public void add(String title, String url)
  {
    int foundAt = find(url);
    if (-1 < foundAt)
    {
      while (entries.size() > foundAt)
      {
        entries.remove(entries.size() - 1);
      }
    }
    entries.add(new Location(url, title));
  }
  public String currentUrl()
  {
    if (entries.size() > 0)
    {
      return entries.get(entries.size() - 1).url;
    }
    return home.url;
  }
  public int find(String url)
  {
    for (int i = 0; i < entries.size(); i++)
    {
      if (entries.get(i).same(url))
      {
        return i;
      }
    }
    return -1;
  }
  public void fromString(String s)
  {
    String[] ut = StringKit.split(s, Div);
    if (ut.length == 2)
    {
      String[] ua = StringKit.split(ut[0], Delim);
      String[] ta = StringKit.split(ut[1], Delim);
      if (ua.length == ta.length)
      {
        entries.clear();
        for (int i = 0; i < ua.length; i++)
        {
          add(ut[i], ua[i]);
        }
      }
    }
    if (find(home.url) == -1)
    {
      entries.add(0, home);
    }
  }
  /**
   * html returns the html for the bar
   */
  public String getHtml(String delim)
  {
    StringBuilder b = new StringBuilder();
    b.append("<div class='locBar'>").append("<div class='locBarInner'>");
    for (int i = 0; i < entries.size() - 1; i++)
    {
      Location loc = entries.get(i);
      b.append("<a href='").append(loc.url).append("'>").append(loc.text).append("</a>").append(delim);
    }
    Location loc = entries.get(entries.size() - 1);
    b.append("</div>");
    b.append("<div class='locBarSel'>").append(loc.text).append("</div>");
    return b.append("</div>").toString();
  }
  /**
   * text returns the text list for the bar
   */
  public String getText(int i)
  {
    return entries.get(i).text;
  }
  /**
   * urls returns the url list for the bar
   */
  public String getUrl(int i)
  {
    return entries.get(i).url;
  }
  /**
   * last returns the url of the page before that passed
   */
  public String previous(String toWhat)
  {
    int i = find(toWhat);
    if (i > 0)
    {
      return entries.get(i - 1).url;
    }
    if (entries.size() > 1)
    {
      return entries.get(entries.size() - 1).url;
    }
    return home.url;
  }
  /**
   */
  public void remove(String url)
  {
    if (entries.size() > 0)
    {
      entries.remove(entries.size() - 1);
    }
    if (entries.size() == 0)
    {
      add(home.text, home.url);
    }
  }
  /**
   * removeLast is used by pages that submit themselves, so their name won't
   * show up twice
   */
  public void removeLast()
  {
    int lastIndex = entries.size() - 1;
    try
    {
      if (lastIndex > 0)
      {
        entries.remove(lastIndex);
      }
    }
    catch (Exception e)
    {
      StringKit.println("LocationBar.removeLast index=" + lastIndex + " size=" + entries.size());
    }
  }
  public int size()
  {
    return entries.size();
  }
  public String toShortString()
  {
    StringBuilder b = new StringBuilder();
    String d = "";
    for (Location loc : entries)
    {
      int lastSlashAt = loc.url.lastIndexOf('/');
      b.append(d).append(loc.url.substring(lastSlashAt + 1));
      d = " -> ";
    }
    return b.toString();
  }
  @Override
  public String toString()
  {
    StringBuilder b = new StringBuilder();
    String d = "";
    for (Location loc : entries)
    {
      b.append(d).append(loc.url);
      d = " -> ";
    }
    return b.toString();
  }
}

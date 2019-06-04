package ir.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class ItemTimer extends TimerTask
{
  private final Map<String, ItemTime> _data = new HashMap<String, ItemTime>();
  private final String _id;
  //
  public ItemTimer(String id)
  {
    _id = id;
  }
  //
  public void addHit(String itemKey, long startMillis)
  {
    if (itemKey != null && itemKey.length() > 0)
    {
      getItem(itemKey).addHit(System.currentTimeMillis() - startMillis);
    }
  }
  public List<String> getCsv()
  {
    List<ItemTime> lst = sortAndSum();
    List<String> out = new ArrayList<String>();
    out.add("Item Statistics for " + _id);
    if (lst.size() > 0)
    {
      ItemTime tot = lst.remove(0);
      out.add("t " + (int) tot.getTime()
          + ",Avg " + StringKit.format(tot.getAvgSeconds(), ItemTime.scale)
          + ",Hits "+ tot.getHits()
          + ",Max " + StringKit.format(tot.getMaxMillis() / 1000.0, ItemTime.scale)
          + ",%t" + ",ItemKey");
      for (ItemTime t : lst)
      {
        double pctOfWhole = t.getTime() * 100 / tot.getTime();
        if (pctOfWhole < 0.1)
        {
          break;
        }
        out.add(t.getText(tot.getTime()));
      }
    }
    return out;
  }
  public String getId()
  {
    return _id;
  }
  //
  protected ItemTime getItem(String itemKey)
  {
    ItemTime pt = _data.get(itemKey);
    if (pt == null)
    {
      pt = new ItemTime(itemKey);
      _data.put(itemKey, pt);
    }
    return pt;
  }
  @Override
  public void run()
  {
    List<String> lst = getCsv();
    for (String v : lst)
    {
      System.out.println(v);
    }
    _data.clear();
  }
  private List<ItemTime> sortAndSum()
  {
    ItemTime sum = new ItemTime("[sum]");
    List<ItemTime> lst = new ArrayList<ItemTime>();
    lst.addAll(_data.values());
    for (ItemTime p : lst)
    {
      sum.add(p);
    }
    lst.add(sum);
    Collections.sort(lst);
    return lst;
  }
}

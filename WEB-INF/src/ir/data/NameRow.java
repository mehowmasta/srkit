package ir.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ir.util.StringKit;

/**
 * A data structure used to fill select boxes.
 *
 * @see Database#selectNameRows
 */
public class NameRow extends BaseRow
{
  public static Comparator<NameRow> DefaultComparator = new Comparator<NameRow>()
  {
    @Override
    public int compare(NameRow r1, NameRow r2)
    {
      return r1.Name.compareToIgnoreCase(r2.Name);
    }
  };
  public String Name = "";
  public int Row = 0;
  public static void sort(List<NameRow> lst)
  {
    Collections.sort(lst, DefaultComparator);
  }
  public NameRow()
  {
  }
  public NameRow(int v, String name)
  {
    Row = v;
    Name = name;
  }
  public String toJson()
  {
    return "{\"r\"\":" + Row + ",\"n:\"" + StringKit.jsq(Name) + "}";
  }
  @Override
  public String toString()
  {
    return "{\"r\":" + Row + ",\"n\":" + StringKit.jsq(Name) + "}";
  }
}

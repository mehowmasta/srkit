package ir.data;

import java.util.Comparator;
import ir.util.StringKit;

public class MultilingualRowComparator implements Comparator<IMultilingualRow>
{
  public final Language lang;
  public MultilingualRowComparator(Language lang)
  {
    this.lang = lang;
  }
  @Override
  public int compare(IMultilingualRow o1,IMultilingualRow o2)
  {
    try
    {
      return StringKit.coalesce(o1.getName(lang),o1.getNameEn())
          .compareToIgnoreCase(StringKit.coalesce(o2.getName(lang),o2.getNameEn()));
    }
    catch (Exception exc)
    {
      return o1.getNameEn().compareToIgnoreCase(o2.getNameEn());
    }
  }

}

package sr.data;

import ir.data.BaseRow;
import ir.data.Database;
import ir.data.IMultilingualRow;
import ir.data.Language;

public class AppRow extends BaseRow
{
  public int cn;// note the type of this value must agree with FeneRec.cn
  public static AppDb cast(Database db)
  {
    return (AppDb) db;
  }
  @Override
  protected boolean excludeFromJson(String fieldName)
  {
    return fieldName.equalsIgnoreCase("cn") ||  fieldName.equalsIgnoreCase("pwd");
  }
  public String getName(Language lang) throws Exception
  {
    if (! (this instanceof IMultilingualRow))
    {
      throw new Exception("getName(lang) not supported for " + this.getClass().getSimpleName());
    }
    return lang.getName((IMultilingualRow) this);
  }
  public int getSite()
  {
    return cn;
  }
}

package ir.data;


/**
 * An IRow implementer with one defined integer field called Value
 */
public class IntRow extends BaseRow
{
  public int Value = 0;
  public IntRow()
  {
  }
  public String toJson()
  {
    return "{v:" + Value + "}";
  }
}

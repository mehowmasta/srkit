package ir.data;

public interface IMultilingualRow extends NamedRow
{
  public String getName(Language lang) throws Exception;
  @Override
  public String getNameEn();
  public String getNameFr();
  public String getNameSp();
}

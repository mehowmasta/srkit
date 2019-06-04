package ir.data;

public enum Language
{
  English("en")
  {
    @Override
    public String getName(IMultilingualRow r)
    {
      return r.getNameEn();
    }
  },
  French("fr")
  {
    @Override
    public String getName(IMultilingualRow r)
    {
      return r.getNameFr();
    }
  },
  NoTranslation("42")
  {
    @Override
    public String getName(IMultilingualRow r)
    {
      return r.getNameEn();
    }
  },Spanish("sp"){
    @Override
    public String getName(IMultilingualRow r)
    {
      return r.getNameSp();
    }
  };
  public final String isoCode;
  private Language(String isoCode)
  {
    this.isoCode=isoCode;
  }
  public abstract String getName(IMultilingualRow r);
}

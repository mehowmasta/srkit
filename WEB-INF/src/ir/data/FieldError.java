package ir.data;

public class FieldError
{
  public String field;
  public String text;
  public FieldError(String field, String text)
  {
    this.field = field;
    this.text = text;
  }
  @Override
  public String toString()
  {
    return this.field + ": " + this.text;
  }
}

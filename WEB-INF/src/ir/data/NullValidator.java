package ir.data;

import ir.util.StringKit;
import java.util.ArrayList;
import java.util.List;

public class NullValidator implements IValidator
{
  private Database _db=null;
  private final List<String> pageErrors = new ArrayList<String>();
  private final List<FieldError> fieldErrors = new ArrayList<FieldError>();
  private final boolean includeIdentification=false;
  public NullValidator()
  {
  }
  public NullValidator(Database db)
  {
    _db = db;
  }
  public void addErrorNoTranslate(String msg,Object... parms) throws Exception
  {
    pageErrors.add(StringKit.format(msg,parms));
  }
  @Override
  public void addErrors(IValidator iv)
  {
    List<String>errorList = iv.getErrors();
    for (String e : errorList)
    {
      pageErrors.add(e);
    }
  }
  /**
   * Associates the translation of a passed key with a passed field name.
   */
  @Override
  public void addFieldError(String field, String msgKey, Object... parms) throws Exception
  {
    fieldErrors.add(new FieldError(field, StringKit.format(msgKey, parms)));
  }
  @Override
  public void addPageError(String msgKey, Object... parms) throws Exception
  {
    if (_db != null)
    {
      pageErrors.add(_db.xlate(msgKey, parms));
    }
    else
    {
      pageErrors.add(StringKit.format(msgKey, parms));
    }
  }
  @Override
  public void clearErrors()
  {
    pageErrors.clear();
    if (fieldErrors != null)
    {
      fieldErrors.clear();
    }
  }
  @Override
  public Database getDatabase() throws Exception
  {
    return _db;
  }
  @Override
  public List<String> getErrors()
  {
    List<String> temp = new ArrayList<String>(pageErrors);
    for (FieldError e : fieldErrors)
    {
      temp.add(e.toString());
    }
    return temp;
  }

  @Override
  public List<FieldError> getFieldErrors()
  {
    return fieldErrors;
  }
  @Override
  public List<String> getPageErrors()
  {
    return pageErrors;
  }

  @Override
  public boolean isOK()
  {
    return pageErrors.size() == 0 && fieldErrors.size() == 0;
  }
  public void setDatabase(Database db)
  {
    _db = db;
  }

  @Override
  public String toString()
  {
    StringBuilder b = new StringBuilder();
    String delim="";
    List<String>errors = getErrors();
    for (String e : errors)
    {
      b.append(delim).append(e);
      delim = "|";
    }
    return b.toString();
  }

}

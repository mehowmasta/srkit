package ir.data;

import java.util.List;

public interface IValidator
{
  /**
   * Adds an error without translating it.
   */
  //public void addErrorNoTranslate(String message, Object... parms) throws Exception;
  /**
   * Adds errors from other validator.
   */
  public void addErrors(IValidator that) throws Exception;
  /**
   * Associates the translation of a passed key with a passed field name.
   */
  public void addFieldError(String field, String msgKey, Object... parms) throws Exception;
  /**
   * Adds the translation of a passed key to the error list.
   */
  public void addPageError(String msgKey, Object... parms) throws Exception;
  /**
   * Clears errors from validator.
   */
  public void clearErrors();
  /**
   * Returns the database contained by or accessible to the implementer.
   */
  public Database getDatabase() throws Exception;
  /**
   * Returns page errors and field errors in the list - never null.  Field errors
   * are shown as Field: Message
   */
  public List<String> getErrors();
  /**
   * Returns the errors in the list - never null.
   */
  public List<FieldError> getFieldErrors();
  /**
   * Returns the errors in the list - never null.
   */
  public List<String> getPageErrors();
  /**
   * Indicates whether addError has NOT been called.
   */
  public boolean isOK();
  /**
   * Sets a message to appear before the first error message.
   */
  //public void setValidatorProlog(String msgKey, Object... parms) throws Exception;
}

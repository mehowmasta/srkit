package ir.data;

import ir.util.JDate;
import ir.util.JDateTime;

/**
 * Describes the behaviour of an object to provide translation service.
 */
public interface ITranslator
{
  String md(JDate dt) throws Exception;
  String md(JDateTime dt) throws Exception;
  String mdy(JDate dt) throws Exception;
  String mdy(JDateTime dt) throws Exception;
  /**
   * releases resources used by the translator
   */
  void release();
  /**
   * sets the language
   */
  void setLanguage(Language lang);
  /**
   * Translates the passed string key to the consumers language.
   */
  String xlate(String wordKey, Object... parms) throws Exception;
  /**
   * Returns key translated UNLESS value is same as key, then returns ""
   */
  public String xlateIfChanged(String key) throws Exception;
  /**
   * Translates the passed string key to the consumers language. If
   * the key has not been used before, initializes it to the passed
   * value.
   */
  String xlateInit(String wordKey, String initialValue) throws Exception;
}

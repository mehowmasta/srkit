package sr.web;

import sr.web.page.AppBasePage;

public abstract class SessionKeys
{
  private static int _seed = 274;
  public final static String Alert = AppBasePage.PRODUCT_NAME + _seed++;
  public final static String Error = AppBasePage.PRODUCT_NAME + _seed++;
  public final static String LocBar = AppBasePage.PRODUCT_NAME + _seed++;
  public static final String MobileDevice =  AppBasePage.PRODUCT_NAME + _seed++;
  public static final String PunchAdminDate =  AppBasePage.PRODUCT_NAME + _seed++;
  public final static String SesRow = AppBasePage.PRODUCT_NAME + _seed++;
  public final static String Status = AppBasePage.PRODUCT_NAME + _seed++;
  public final static String ThemeRow =  AppBasePage.PRODUCT_NAME + _seed++;
  public final static String UserCom = AppBasePage.PRODUCT_NAME + _seed++;
  public final static String UserRow =  AppBasePage.PRODUCT_NAME + _seed++;
}

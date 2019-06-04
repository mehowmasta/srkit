package sr.data;

import java.util.List;
import ir.data.Database;
import ir.data.NameRow;
/**
 * a data structure of translation information
 */
public class ThemeRec extends AppRec
{
  //
  public static final String BACKGROUND1 = "B1";
  public static final String BACKGROUND2 = "B2";
  public static final String BACKGROUND3 = "B3";
  public static final String BACKGROUND4 = "B4";
  public static final String FOREGROUND1 = "F1";
  public static final String FOREGROUND2 = "F2";
  public static final String FOREGROUND3 = "F3";
  public static final String FOREGROUND4 = "F4";
  public static final String ICONFOLDER = "IconFolder";
  public static final String NAME = "Name";
  public static final String ROW = "Row";
  public final static String TABLE = "tTheme";
  public final static String TABLE1 = "T1";
  public final static String TABLE2 = "T2";
  public final static String TABLE3 = "T3";
  public final static String TABLE4 = "T4";
  public final static String TABLE5 = "T5";
  public final static String SHADOW1 = "S1";
  public final static String SHADOW2 = "S2";
  public final static String SHADOW3 = "S3";
  //
  public String B1 = "2F2F31"; //Main background color
  public String B2 = "444"; //secondary background color
  public String B3 = "000"; //table background
  public String B4 = "000"; //table background
  public String F1 = "EBB530"; //Main button color / icon color
  public String F2 = "680015"; //highlight color
  public String F3 = "FFF"; //main text
  public String F4 = "FFF"; //main text
  public String IconFolder = "white";
  public String Name = "Default";
  public int Row = 0;
  public String S1 = "111"; //primary shadow color
  public String S2 = "444"; //secondary shadow
  public String S3 = "111"; //secondary shadow
  public String T1 = "000"; //Table background
  public String T2 = "EBB530"; //table title color
  public String T3 = "FFF"; //table text color
  public String T4 = "2F2F31"; //table odd row color
  public String T5 = "444"; //table even row color
  //
  public static List<ThemeRec> selectAll(Database db) throws Exception
  {
    return db.selectList("ThemeRec.selectAll",ThemeRec.class);
  }
  public static int selectCount(AppDb db) throws Exception
  {
    return selectAll(db).size();
  }
  public static List<NameRow> selectNameRows(Database db) throws Exception
  {
	  return db.selectList("ThemeRec.selectNameRows", NameRow.class);
  }
  public static ThemeRec selectNext(AppDb db,int theme) throws Exception
  {
    List<ThemeRec>themes = selectAll(db);
    ThemeRec input = new ThemeRec();
    input.Row = theme;
    ThemeRec result = themes.get(0);
    for (int i=0;i<themes.size();i++)
    {
      ThemeRec r = themes.get(i);
      if (r.Row == theme)
      {
        input = r;
        if (i < themes.size()-1)
        {
          result = themes.get(i+1);
          break;
        }
      }
    }
    return result;
  }
  public static ThemeRec selectRandom(AppDb db) throws Exception
  {
    ThemeRec result = new ThemeRec();
    result.Row = (int)Math.floor(Math.random() * ThemeRec.selectCount(db) + 1);
    db.select(result);
    return result;
  }
  /**
   * @return String home table
   */
  @Override
  public String getTable()
  {
    return TABLE;
  }
  public String getUrlParameters() throws Exception
  {
    StringBuilder b = new StringBuilder();
    b.append(BACKGROUND1).append("=").append(B1).append("&")
    .append(BACKGROUND2).append("=").append(B2).append("&")
    .append(BACKGROUND3).append("=").append(B3).append("&")
    .append(BACKGROUND4).append("=").append(B4).append("&")
    .append(FOREGROUND1).append("=").append(F1).append("&")
    .append(FOREGROUND2).append("=").append(F2).append("&")
    .append(FOREGROUND3).append("=").append(F3).append("&")
    .append(FOREGROUND4).append("=").append(F4).append("&")
    .append(TABLE1).append("=").append(T1).append("&")
    .append(TABLE2).append("=").append(T2).append("&")
    .append(TABLE3).append("=").append(T3).append("&")
    .append(TABLE4).append("=").append(T4).append("&")
    .append(TABLE5).append("=").append(T5).append("&")
    .append(SHADOW1).append("=").append(S1).append("&")
    .append(SHADOW2).append("=").append(S2).append("&")
    .append(SHADOW3).append("=").append(S3);
    return b.toString();
  }
}

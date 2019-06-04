package sr.data;

import ir.data.Database;
import ir.data.ForeignKeyMandatory;
import ir.util.JDateTime;
import ir.util.StringKit;
import javax.servlet.http.HttpServletRequest;
import sr.task.SessionLocationUpdater;
/**
 * a data structure of translation information
 */
public class SessionRec extends AppRec
{
  public static SessionLocationUpdater locationUpdater=null;
  public final static String TABLE = "tSession";
  public String Agent = "[unknown]";
  public String FromAddr = "";
  public String Location="";
  public int Row = 0;
  public boolean Secure = false;
  public JDateTime SesEnd = JDateTime.zero();
  public JDateTime SesStart;
  @ForeignKeyMandatory(toTable=UserRec.TABLE,toColumn="Row")
  public int SesUser = 0;
  //
  public static void end(Database db, int sesRow) throws Exception
  {
    db.execute("SessionRec.end", JDateTime.now(), sesRow);
  }
  public static void endAll(LoginDb db) throws Exception
  {
    db.execute("SessionRec.endAll");
  }
  public static int start(AppDb db, HttpServletRequest request) throws Exception
  {
    SessionRec r = new SessionRec();
    r.SesUser = db.getUser().Row;
    r.Location = r.FromAddr = request.getRemoteAddr();
    r.Secure = request.isSecure();
    r.SesStart = new JDateTime(System.currentTimeMillis());
    r.Agent = request.getHeader("User-Agent");
    db.insert(r);
    return r.Row;
  }
  public SessionRec()
  {
    SesStart = new JDateTime(System.currentTimeMillis());
  }
  @Override
  public void beforeInsert(Database db) throws Exception
  {
    Agent = StringKit.coalesce(Agent,"[unknown]");
  }
  @Override
  public String getTable()
  {
    return TABLE;
  }
}

package sr.data;

import ir.data.Database;
import ir.util.StringKit;

public class MessageThreadUserRec extends AppRec
{
	public enum MessageThreadUserRole{
		Owner,Mod,Guest;
		public static void align(Database db)
        {
            StringBuilder b = new StringBuilder();
            String comma="";
            b.append("alter table tMessageThreadUser modify column Role enum(");
            for (MessageThreadUserRole t : values())
            {
                b.append(comma).append("'").append(t.name()).append("'");
                comma=",";
            }
            b.append(") not null");
            try
            {
                db.execute(b.toString());
                StringKit.println("MessageThreadUserRole.align ok.");
            }
            catch (Exception e)
            {
                StringKit.println("MessageThreadUserRole.align: " + e.getMessage());
            }
        }
	}
    public static final String LASTROWSEEN = "LastRowSeen";
    public static final String ROLE = "Role";
    public static final String SHAREROLL = "ShareRoll";
    public static final String TABLE = "tMessageThreadUser";
    public static final String THREAD = "Thread";
    public static final String USER = "User";
    //
    /** keep track of the last announcement the user has seen */
    public int LastRowSeen = 0;
    public boolean ShareRoll = false;
    public MessageThreadUserRole Role = MessageThreadUserRole.Guest;
    public int Thread = 0;
    public int User = 0;
    //
    public static int selectUnseenCount(Database db, int userRow) throws Exception
    {
        return db.selectScalar("MessageThreadUserRec.selectUnseenCount",0,userRow,userRow);
    }
    public static int updateSeen(Database db, int threadId, int userRow) throws Exception
    {
        return db.execute("MessageThreadUserRec.updateSeen",threadId,threadId,userRow);
    }
    @Override
    public String getTable()
    {
        return TABLE;
    }

}
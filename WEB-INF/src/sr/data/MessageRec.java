package sr.data;

import ir.data.Database;
import ir.util.JDateTime;
import ir.util.StringKit;
import sr.data.CharacterRec.CharacterType;

public class MessageRec extends AppRec
{
    public enum MessageType {
        Message,Notification,Roll,Map;
        public static void align(Database db)
        {
            StringBuilder b = new StringBuilder();
            String comma="";
            b.append("alter table tmessage modify column Type enum(");
            for (MessageType t : values())
            {
                b.append(comma).append("'").append(t.name()).append("'");
                comma=",";
            }
            b.append(") not null");
            try
            {
                db.execute(b.toString());
                StringKit.println("MessageType.align ok.");
            }
            catch (Exception e)
            {
                StringKit.println("MessageType.align: " + e.getMessage());
            }
        }
        public static MessageType lookup(String name) {
			for (MessageType t : values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return Message;
		}
    };
    public static final String CREATEDAT = "CreatedAt";
    public static final String CREATEDBY = "CreatedBy";
    public static final String DELETED ="Deleted";
    public static final String MESSAGE = "Message";
    public static final String ROW = "Row";
    public static final String TABLE = "tMessage";
    public static final String THREAD = "Thread";
    public static final String TYPE = "Type";
    //
    
    public JDateTime CreatedAt = JDateTime.now();
    public int CreatedBy = 0;
    /** has the creator deleted this message */
    public boolean Deleted = false;
    public String Message = "";
    public int Row = 0;
    /** track which conversation this message belongs to*/
    public int Thread = 0;
    public MessageType Type = MessageType.Message;
    //
    public String getFileName(String extension)
    {
        return Thread + "_" + Row + "." + extension;
    }
    @Override
    public String getTable()
    {
        return TABLE;
    }

}
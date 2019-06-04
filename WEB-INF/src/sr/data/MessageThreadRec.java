package sr.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ir.data.Database;
import ir.data.IRow;
import ir.util.StringKit;

public class MessageThreadRec extends AppRec
{
	public enum MessageThreadType{
		Private,Community,System;	
		public static void align(Database db)
        {
            StringBuilder b = new StringBuilder();
            String comma="";
            b.append("alter table tMessageThread modify column Type enum(");
            for (MessageThreadType t : values())
            {
                b.append(comma).append("'").append(t.name()).append("'");
                comma=",";
            }
            b.append(") not null");
            try
            {
                db.execute(b.toString());
                StringKit.println("MessageThreadType.align ok.");
            }
            catch (Exception e)
            {
                StringKit.println("MessageThreadType.align: " + e.getMessage());
            }
        }
	}
    public static final String CREATEDBY = "CreatedBy";
    public static final String DELETED = "Deleted";
    public static final String DISABLERESPONSE = "DisableResponse";
    public static final String NAME = "Name";
    public static final String ROW = "Row";
    public static final String TABLE = "tMessageThread";
    public static final String TYPE = "Type";
    //
    public int CreatedBy = 0;
    public boolean Deleted = false;
    public boolean DisableResponse = false;
    public String Name = "";
    public int Row = 0;
    public MessageThreadType Type = MessageThreadType.Private;
    public static List<MessageThreadRec> selectThreadForRollShare(Database db, int userRow) throws Exception
    {
    	return db.selectList("MessageThreadRec.selectThreadForRollShare",MessageThreadRec.class,userRow);
    }
    public static int selectThreadForUsers(Database db, String userRowDelimitedString) throws Exception
    {
        return db.selectScalar("MessageThreadRec.selectThreadForUsers",-1,userRowDelimitedString);
    }
    @SuppressWarnings("unchecked")
    public static String selectThreadsForUser(Database db,int userRow) throws Exception
    {
        List<List<IRow>> result = db.selectLists("call select_message_threads(?)",Arrays.asList(MessageThreadUserRec.class,MessageRec.class,UserRec.class),userRow);
        int set=0;
        List<MessageThreadUserRec> tu = new ArrayList<MessageThreadUserRec>((Collection<? extends MessageThreadUserRec>) result.get(set++));
        List<MessageRec> a = new ArrayList<MessageRec>((Collection<? extends MessageRec>) result.get(set++));
        List<UserRec> u = new ArrayList<UserRec>((Collection<? extends UserRec>) result.get(set++));
        Map<Integer,List<UserRec>> userMap = new HashMap<Integer,List<UserRec>>(); 
        Map<Integer,List<MessageRec>> messageMap = new HashMap<Integer,List<MessageRec>>(); 
        for(MessageRec rec : a)
        {
            if(messageMap.containsKey(rec.Thread))
            {
                messageMap.get(rec.Thread).add(rec);
            }
            else
            {
                List<MessageRec> list = new ArrayList<MessageRec>();
                list.add(rec);
                messageMap.put(rec.Thread,list);
            }
        }
        for(UserRec rec : u)
        {
        	int threadId = rec.getTemp("Thread",Integer.class);
            if(userMap.containsKey(threadId))
            {
            	userMap.get(threadId).add(rec);
            }
            else
            {
                List<UserRec> list = new ArrayList<UserRec>();
                list.add(rec);
                userMap.put(threadId,list);
            }
        }
        StringBuilder b = new StringBuilder("[");
        String comma = "";
            for(MessageThreadUserRec rec : tu)
            {
            	List<MessageRec> messages = messageMap.get(rec.Thread);
            	MessageRec lastMessage = null;
            	if(messages!=null && messages.size()>0)
            	{
                	lastMessage = messageMap.get(rec.Thread).get(messageMap.get(rec.Thread).size()-1);
            	}
            	else
            	{
            		lastMessage = new MessageRec();
            	}
                b.append(comma)
                 .append("{threadId:").append(rec.Thread)
                 .append(",createdBy:").append(rec.getTemp("CreatedBy", Integer.class))
                 .append(",messages:").append(messages!=null && messages.size()>0?messages.toString():"[]")
                 .append(",lastMessage:").append(StringKit.jsq(lastMessage.Message))
                 .append(",lastDate:").append(StringKit.jsq(lastMessage.CreatedAt))
                 .append(",lastRowSeen:").append(rec.LastRowSeen)
                 .append(",timeStamp:").append(lastMessage.CreatedAt.getTime())
                 .append(",users:new KeyedArray('Row',").append(userMap.get(rec.Thread).toString()).append(")")
                 .append("}");
                comma=",";
            }
        return b.append("]").toString();
    }
    @Override
    public String getTable()
    {
        return TABLE;
    }
    public List<UserRec> selectUsers(Database db) throws Exception
    {
        return db.selectList("MessageThreadRec.selectUsers",UserRec.class,Row);
    }
}
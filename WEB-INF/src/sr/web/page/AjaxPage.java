package sr.web.page;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.util.Coerce;
import ir.util.JDate;
import ir.util.JDateTime;
import ir.util.StringKit;
import sr.data.AdeptPowerRec;
import sr.data.ArmorRec;
import sr.data.AwardType;
import sr.data.BudgetRec;
import sr.data.CharacterAdeptPowerRec;
import sr.data.CharacterArmorRec;
import sr.data.CharacterBiowareRec;
import sr.data.CharacterContactRec;
import sr.data.CharacterCyberdeckProgramRec;
import sr.data.CharacterCyberdeckRec;
import sr.data.CharacterCyberwareAttachmentRec;
import sr.data.CharacterCyberwareRec;
import sr.data.CharacterDroneRec;
import sr.data.CharacterGearRec;
import sr.data.CharacterKnowledgeRec;
import sr.data.CharacterKnowledgeRec.KnowledgeType;
import sr.data.CharacterQualityRec;
import sr.data.CharacterRatingRec;
import sr.data.CharacterRec;
import sr.data.CharacterRow;
import sr.data.CharacterSettingRec;
import sr.data.CharacterSkillRec;
import sr.data.CharacterSpellRec;
import sr.data.CharacterVehicleRec;
import sr.data.CharacterWeaponModifierRec;
import sr.data.CharacterWeaponRec;
import sr.data.CyberdeckRec;
import sr.data.CyberwareGrade;
import sr.data.CyberwareRec;
import sr.data.DroneRec;
import sr.data.EquipRec;
import sr.data.FriendRec;
import sr.data.GearRec;
import sr.data.GroupCharacterRec;
import sr.data.GroupRec;
import sr.data.ImageRec;
import sr.data.ImageRec.ImageType;
import sr.data.JournalRec;
import sr.data.MapDataRec.LayerType;
import sr.data.MapDataRec;
import sr.data.MessageRec;
import sr.data.MessageThreadRec;
import sr.data.MessageThreadUserRec;
import sr.data.ProgramRec;
import sr.data.QualityRec;
import sr.data.SkillRec;
import sr.data.SpellRec;
import sr.data.SrRec;
import sr.data.UserRec;
import sr.data.UserRole;
import sr.data.VehicleRec;
import sr.data.WeaponModifierRec;
import sr.data.WeaponModifierRec.MountType;
import sr.data.WeaponRec;
import sr.data.BudgetRec.BudgetCategory;
import sr.data.BudgetRec.BudgetType;
import sr.web.App;
import sr.web.WebSocketClient;
import sr.web.WebSocketEndpoint;

/**
 * Controls similarly named jsp file
 */
public class AjaxPage extends AppBasePage {
	private static Map<String, Method> _methods = new HashMap<String, Method>();
	private static int exceptionSendCount = 0;
	private static final int exceptionSendMax = 20;
	private final static String okOne = "{\"ok\":1}";
	private final static String okOneComma = "{\"ok\":1,";
	private final static String okZero = "{\"ok\":0}";
	private final boolean _security = false;
	private String fromPage = "";
	//
	public String acceptTransfer() throws Exception
	{
		CharacterRec rec = new CharacterRec();
		rec.Row = readInt("characterRow");
		if(!db.select(rec))
		{
			return eeJson("Failed to find character, sorry.");
		}
		if(rec.Transfer != currentUser.Row)
		{
			return eeJson("This character isn't for you! Get outta here.");
		}
		int oldUser= rec.User;
		rec.User = currentUser.Row;
		rec.Transfer = 0;
		db.update(rec);
		sendNotification(oldUser,"<i>" + currentUser.Name + "</i> has accepted your character transfer");
		return okOne;
	}
	public String addMapData() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Guests can't add map points, sorry.");
		}
		MapDataRec rec = new MapDataRec();
		int oldRow = readInt("Row");
		rec.MapRow = readInt("MapRow");
		rec.X = readInt("X");
		rec.Y = readInt("Y");
		rec.ObjectRow = readInt("ObjectRow");
		rec.Name = readString("Name",45);
		rec.Type = MapDataRec.ObjectType.lookup(readString("Type",30));
		rec.Layer = LayerType.lookup(readString("Layer",30));
		db.insert(rec);
		return okOneComma + "oldRow:"+oldRow+",newRow:"+rec.Row+"}";
	}
    public String addMessageThreadUser() throws Exception
    {
    	String userLogin = readString("userLogin",200);
    	UserRec user = UserRec.selectByLogin(db,userLogin);
    	if(user.Row==0)
    	{
    		return eeJson("User not found.");
    	}
    	else if (user.Row == currentUser.Row && !currentUser.isSysAdmin())
    	{
    		return eeJson("Adding yourself?");
    	}
    	else if(user.isGuest() && !currentUser.isSysAdmin())
    	{
    		return eeJson("Guest account? That could be anyone.");
    	}
        int threadId = readInt("threadId");
        if(threadId>0)
        {
        	//select correct thread
        	MessageThreadRec t = new MessageThreadRec();
            t.Row =threadId;
            if(!db.select(t))
            {
                return eeJson("Link not found.");
            }
            //create thread user
            MessageThreadUserRec tu = new MessageThreadUserRec();
            tu.Thread = threadId;
            tu.User = user.Row;
            db.insert(tu);  
            //create notification for thread
            MessageRec a = new MessageRec();
            a.Thread = threadId;
            a.CreatedBy = currentUser.Row;
            a.Message = user.Name + " " + db.xlate("is now connected");
            a.Type = MessageRec.MessageType.Notification;
            db.insert(a);
            //send notification to all user for that thread
            List<UserRec> userList = t.selectUsers(db);
            String responseJson = "{ok:1,addUser:"+user.toJson()+",threadId:"+t.Row+",addMessage:"+a.toString()+"}";
            for(UserRec u : userList)
            {
                List<WebSocketClient> userWebSocketClients = WebSocketEndpoint.findClientsByUser(u);
                for(WebSocketClient c : userWebSocketClients)
                {
                    c.send(responseJson);
                }
            }
        }
        else
        {//thread does not exist yet, let AjaxPage.sendMessage handle adding user to thread when thread is created
        }
        return okOneComma + "user:"+user.toJson() + "}"; 	
    }
    public String award() throws Exception
    {
		String characterRows = readString("characterRows",200);
		Map<Integer,List<CharacterRec>> userCharacters = new HashMap<Integer,List<CharacterRec>>(); 
		String[] rows = characterRows.split(SPLITTER);
		int nuyen = readInt("nuyen");
		int karma = readInt("karma");
		CharacterRec c = null;
		for(String r : rows)
		{
			try {
				c = new CharacterRec();
				c.Row = Integer.parseInt(r);
				if(db.select(c))
				{
					c.Nuyen += nuyen;
					c.Karma += karma;
					db.update(c);
					List<CharacterRec> list = userCharacters.get(c.User);
					if(list==null)
					{
						list = new ArrayList<CharacterRec>();
						list.add(c);
						userCharacters.put(c.User, list );
					}
					else
					{
						list.add(c);
					}
				}
			}
			catch (Exception e)
			{
				addPageError(e.getMessage());
				continue;
			}
		}    
		String responseJson = ""; 
		String names = "";
		String comma2= "";
        for(int u : userCharacters.keySet())
        {
        	
        	List<CharacterRec> list = userCharacters.get(u);
        	String characters = "";
        	String characterNames = "";
        	String comma = "";
        	for(CharacterRec r : list)
        	{
        		characters += comma + r.Row;
        		characterNames += comma + jsq(r.Name);
        		comma=",";
                names+=comma2 + characterNames;
                comma2=",";
        	}
        	if(u != currentUser.Row)
        	{
	        	responseJson = "{ok:1,receiveReward:1,karma:"+karma+",nuyen:"+nuyen+",items:[],characterNames:["+characterNames+"],characterRows:["+characters+"]}";
	            List<WebSocketClient> userWebSocketClients = WebSocketEndpoint.findClientsByUserRow(u);
	            for(WebSocketClient ws : userWebSocketClients)
	            {
	                ws.send(responseJson);
	            }
	    	}
        }
    	return okOneComma + "names:"+jsq(names)+",errors:"+getErrorListJson()+ ",nuyen:"+nuyen+",karma:"+karma+"}";
    }
	public String declineTransfer() throws Exception
	{
		CharacterRec rec = new CharacterRec();
		rec.Row = readInt("characterRow");
		if(!db.select(rec))
		{
			return eeJson("Failed to find character, sorry.");
		}
		if(rec.Transfer != currentUser.Row)
		{
			return eeJson("This character isn't for you! Get outta here.");
		}
		rec.Transfer = 0;
		db.update(rec);
		sendNotification(rec.User,"<i>" + currentUser.Name + "</i> has declined your character transfer.");
		return okOne;
	}
    public String deleteMessageThread() throws Exception
    {
        int threadId = readInt("threadId");
        MessageThreadRec rec = new MessageThreadRec();
        rec.Row = threadId;
        if(!db.select(rec))
        {
            return eeJson("Thread not found.");            
        }
        if(rec.CreatedBy == currentUser.Row)
        {
            rec.Deleted = true;
            db.update(rec);
        }
        return okOne;
    }
	public String deletePortrait() throws Exception{
		if(currentUser.isGuest())
		{
			return eeJson("Not for you mysterious user.");
		}
		ImageRec image = new ImageRec();
		image.Row= readInt("row");
		db.delete(image);
		return okOne;
	}
	public String deleteUser() throws Exception
	{
		if(!currentUser.isSysAdmin())
		{
            return eeJson("Only GOD can wipe users from existence."); 
		}
		UserRec rec = new UserRec();
		rec.Row = readInt("row");
		if(db.select(rec))
		{
			if(rec.Role.equals(UserRole.SysAdmin))
			{
	            return eeJson("Protected by the Dragon."); 
			}
			db.delete(rec);
		}
		else
		{
            return eeJson("User not found."); 
		}		
		return okOneComma + "user:" + rec.toString() + "}";
	}
	public String friendAccept() throws Exception
	{
		FriendRec rec = new FriendRec();
		rec.Row = readInt("row");
		db.select(rec);
		rec.Confirmed=true;
		db.update(rec);
		//create mirror record
		FriendRec mirror = new FriendRec();
		mirror.User = rec.Friend;
		mirror.Friend = rec.User;
		mirror.Confirmed = true;
		db.insert(mirror);
		UserRec newFriend = new UserRec();
		newFriend.Row = rec.User;
		db.select(newFriend);
		return okOneComma + "friend:" + rec.toString() + ",user:"+newFriend.toString()+"}";
	}
	public String friendDecline() throws Exception
	{
		FriendRec rec = new FriendRec();
		rec.Row = readInt("row");
		db.delete(rec);
		return okOne;
	}
	public String friendRemove() throws Exception
	{
		List<FriendRec> recs = FriendRec.selectFriendship(db,currentUser.Row,readInt("friendUserRow"));
		for(FriendRec r : recs)
		{
			db.delete(r);
		}
		return okOne;
	}
	public String friendRequest() throws Exception
	{
		String login = readString("login",100);
		UserRec rec = UserRec.selectByLogin(db, login);
		if(rec==null || rec.Row==0)
		{
			return eeJson("Could not find user {0}.",login);
		}
		else if(rec.Row == currentUser.Row)
		{
			return eeJson("Cannot add yourself to User List.");
		}
		FriendRec friend = new FriendRec();
		friend.User = currentUser.Row;
		friend.Friend = rec.Row;
		List<FriendRec> relation = FriendRec.selectFriendship(db, friend.User, friend.Friend);
		if(!relation.isEmpty())
		{
			for(FriendRec r : relation)
			{
				if(r.Confirmed)
				{
					return eeJson("{0} is already in the User List.",rec.Name);
				}
				else
				{
					return eeJson("Request for {0} is already pending.",rec.Name);
				}
			}
		}
		else
		{
			db.insert(friend);
			sendNotification(friend.Friend,"<i>" + currentUser.Name + "</i> has sent you a friend request, <a href='userlist.jsp'>Click Here</a>.");
		}
		return okOneComma + "user:" + rec.toString() + ",friend:"+friend.toString()+"}";
	}
	private synchronized Method getMethod(String fn) throws Exception {
		Method m = _methods.get(fn);
		if (m == null) {
			m = getClass().getMethod(fn);
			if (m != null) {
				m.setAccessible(true);
				_methods.put(fn, m);
			}
		}
		return m;
	}
	@Override
	protected boolean isMultiPart() {
		return true;
	}
	public String joinTeam() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Guest account recognized, terminating request.");
		}
		String key = readString("groupKey",GroupRec.GROUP_KEY_LENGTH);
		int characterRow = readInt("characterRow");
		if(characterRow <1)
		{
			return eeJson("Must initially save runner before joining a team.");
		}
		GroupRec group = GroupRec.selectGroupByKey(db, key);
		if(group!=null)
		{
			if(GroupCharacterRec.isDup(db, group.Row, characterRow))
			{
				return eeJson("Character is already part of team: {0}.",group.Name);
			}
			else if(group.Private)
			{
				return eeJson("Cannot join team {0}, team is private.",group.Name) ;
			}
			else if(group.Inactive)
			{
				return eeJson("Cannot join team {0}, team is no longer active.",group.Name) ;
			}
			else
			{
				GroupCharacterRec gc = new GroupCharacterRec();
				gc.GroupRow = group.Row;
				gc.CharacterRow = characterRow;
				gc.Quantity =1;
				db.insert(gc);
			}
			return "{ok:1,teamName:"+jsq(group.Name)+",groups:"+GroupRec.selectForCharacter(db, characterRow, true)+"}";
		}
		else
		{
			return eeJson("Team not found.");
		}
	}

    public String leaveMessageThread() throws Exception
    {
        int threadId = readInt("threadId");
        MessageThreadRec t = new MessageThreadRec();
        t.Row = threadId;
        if(!db.select(t))
        {
            return eeJson("Conversation not found.");
        }
        MessageThreadUserRec rec = new MessageThreadUserRec();
        rec.Thread = threadId;
        rec.User = currentUser.Row;
        db.delete(rec);
        UserRec us = new UserRec();
        us.Row = rec.User;
        db.select(us);
        MessageRec a = new MessageRec();
        a.Thread = threadId;
        a.CreatedBy = currentUser.Row;
        a.Message = us.Name + " " + db.xlate("has jacked out.");
        a.Type = MessageRec.MessageType.Notification;
        db.insert(a);
        List<UserRec> userList = t.selectUsers(db);
        String responseJson = "{ok:1,removeUser:"+rec.User+",threadId:"+t.Row+",removeMessage:"+a.toString()+"}";
        for(UserRec u : userList)
        {
            List<WebSocketClient> userWebSocketClients = WebSocketEndpoint.findClientsByUser(u);
            for(WebSocketClient c : userWebSocketClients)
            {
                c.send(responseJson);
            }
        }
        return okOne;
    }
	public String leaveTeam() throws Exception
	{
		int characterRow = readInt("characterRow");
		GroupCharacterRec.deleteGroupCharacter(db, readInt("groupRow"), characterRow);
		return "{ok:1,groups:"+GroupRec.selectForCharacter(db, characterRow, true)+"}";		
	}
	@Override
	protected boolean process() throws Exception {
		String functionName = readString("fn", 128);
		this.session = request.getSession(false);
		if (!initUser())
	    {
		    set("JSON", "{\"security\":1}");
		    return true;
	    }
		this.fromPage = readString("frPg", 128);
		try {
			write();
		} catch (Exception e) {
			set("JSON", "{\"exc\":1}");
			Throwable cause = e.getCause();
			if (cause == null) {
				String m = e.getMessage() == null ? e.getClass().getName() : e.getMessage().replace('\'', '`');
				StringKit.println("AjaxCtl." + functionName + "() from " + fromPage + ": " + m);
			} else {
				StringKit.println("AjaxCtl." + functionName + "() from " + fromPage);
				cause.printStackTrace();
			}
			if (++exceptionSendCount <= exceptionSendMax) {
				if (!App.isDev()) {

				}
			}
		}
		if(db!=null)
		{
			db.close();
		}
		return true;
	}
	public String rateCharacter() throws Exception
	{
		int rating = readInt("rating");
		int characterRow = readInt("characterRow");
		CharacterRatingRec rec = new CharacterRatingRec();
		rec.Character = readInt("characterRow");
		if(db.select(rec,characterRow,currentUser.Row))
		{
			rec.Rating = rating;
			db.update(rec);
		}
		else
		{
			rec.Rating = rating;
			db.insert(rec);
		}
		return okOneComma + "rating:" + rec.toString() + "}";
	}
	public String removeMapData() throws Exception
	{
		MapDataRec rec = new MapDataRec();
		rec.Row = readInt("Row");
		db.delete(rec);
		return okOne;
	}
    public String removeMessageThreadUser() throws Exception
    {
        int threadId = readInt("threadId");
        if(threadId<1)
        {
            return okOne;
        }
        MessageThreadRec t = new MessageThreadRec();
        t.Row =threadId;
        if(!db.select(t))
        {
            return eeJson("Conversation not found.");
        }
        MessageThreadUserRec tu = new MessageThreadUserRec();
        tu.Thread = threadId;
        tu.User = readInt("userRow");
        db.delete(tu);
        UserRec us = new UserRec();
        us.Row = tu.User;
        db.select(us);
        MessageRec a = new MessageRec();
        a.Thread = threadId;
        a.CreatedBy = currentUser.Row;
        a.Message = us.Name + " " + db.xlate("has disconnected");
        a.Type = MessageRec.MessageType.Notification;
        db.insert(a);
        List<UserRec> userList = t.selectUsers(db);
        String responseJson = "{ok:1,removeUser:"+tu.User+",threadId:"+t.Row+",removeMessage:"+a.toString()+"}";
        for(UserRec u : userList)
        {
            List<WebSocketClient> userWebSocketClients = WebSocketEndpoint.findClientsByUser(u);
            for(WebSocketClient c : userWebSocketClients)
            {
                c.send(responseJson);
            }
        }
        return okOne;
    }
    public String resetPassword() throws Exception
    {
    	if(!currentUser.isSysAdmin())
    	{
    		return eeJson("Woh slow down chummer! This feature is not for you.");
    	}
    	int userRow = readInt("userRow");
    	UserRec user = new UserRec();
    	user.Row = userRow;
    	if(!db.select(user))
    	{
    		return eeJson("User not found.");
    	}
    	user.updatePwd(db, UserRec.DEFAULT_PASS);
    	return okOne;
    }
	private String run(String fn) throws Exception {
		Method m = getMethod(fn);
		if (m == null) {
			return "{exc:'unrecognized function [" + fn + "]'}";
		}
		String result = Coerce.toString(m.invoke(this));
		return result;
	}
	public String selectAdeptPowers() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,AdeptPowerRec.class).toString() + "}";
	}
	public String selectArmor() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,ArmorRec.class).toString() + "}";
	}
	public String selectBioware() throws Exception
	{
		return okOneComma + "list:" + CyberwareRec.selectBioware(db).toString() + ",grades:"+CyberwareGrade.toJson()+"}";
	}
	public String selectBudgetList() throws Exception
	{
		return okOneComma + "list:" + BudgetRec.selectList(db,currentUser.Row,read("from",JDate.zero()),read("to",JDate.zero())).toString() + "}";
	}
	public String selectCharacterDetail() throws Exception
	{
		return okOne;
	}
	public String selectCharacters() throws Exception
	{
		return okOneComma + "list:" + CharacterRec.selectForUser(db,currentUser.Row).toString() + "}";
	}
	public String selectCharactersForPicker() throws Exception
	{
		return okOneComma + "list:" + CharacterRec.selectForPicker(db,currentUser.Row).toString() + "}";
	}
	public String selectCyberdecks() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,CyberdeckRec.class).toString() + "}";
	}
	public String selectCyberware() throws Exception
	{
		return okOneComma + "list:" + CyberwareRec.selectCyberware(db).toString() + ",grades:"+CyberwareGrade.toJson()+"}";
	}
	public String selectCyberwareAttachments() throws Exception
	{
		return okOneComma + "list:" + CyberwareRec.selectCyberwareAttachments(db).toString() + "}";
	}
	public String selectCyberwareBase() throws Exception
	{
		return okOneComma + "list:" + CyberwareRec.selectCyberwareBase(db).toString() + ",grades:"+CyberwareGrade.toJson()+"}";
	}
	public String selectDrones() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,DroneRec.class).toString() + "}";
	}
	public String selectFaces() throws Exception
	{
		return okOneComma + "list:" + ImageRec.selectForUser(db, currentUser.Row, ImageType.Face, true).toString() + "}";
	}
	public String selectGear() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,GearRec.class).toString() + "}";
	}
	public String selectGroupCharacters() throws Exception
	{
		return okOneComma + "list:" + GroupRec.selectCharacters(db, readInt("group")).toString() + "}";
	}
	public String selectImage() throws Exception
	{
		ImageRec rec = new ImageRec();
		rec.Row =readInt("row");
		db.select(rec);
		return okOneComma + "image:" + rec.toString() + "}";
	}
	public String selectJournalData() throws Exception
	{
		int userRow = readInt("row");
		if(userRow != currentUser.Row)
		{
			return eeJson("That's not your journal.");
		}		
		return okOneComma + "data:" + JournalRec.selectAll(db, userRow) + "}";
	}
	public String selectMap() throws Exception
	{
		return okOneComma + "map:" + ImageRec.selectImage(db,currentUser.Row,readInt("row")).toString() + "}";
	}
	public String selectMapData() throws Exception
	{
		return okOneComma + "mapData:" +MapDataRec.selectAll(db, readInt("mapRow")).toString()+ "}";
	}
	public String selectMaps() throws Exception
	{
		return okOneComma + "list:" + ImageRec.selectForUser(db, currentUser.Row, ImageType.Map, true).toString() + "}";
	}
    public String selectMessageThreads() throws Exception
    {
        return "{ok:1,threads:"+ MessageThreadRec.selectThreadsForUser(db, currentUser.Row) + "}";
    }
    public String selectNPCs() throws Exception
    {
    	if(currentUser.isGuest())
		{
    		return eeJson("UNREGISTERED USER. ACCESS DENIED.");
		}
    	return "{ok:1,list:"+ CharacterRec.selectForRegistry(db) + "}";
    }
	public String selectPlayer() throws Exception
	{
		int row = readInt("row");
		if(row<1)
		{
			return eeJson("Default character not set.");
		}
		return okOneComma + "player:" + CharacterRow.selectCharacter(db,row).toJson() + "}";
	}
	public String selectPrograms() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,ProgramRec.class).toString() + "}";
	}
	public String selectQualities() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,QualityRec.class).toString() + "}";
	}
	public String selectRoles() throws Exception
	{
		return okOneComma + "roles:" + UserRole.selectJson() + "}";	
	}
	public String selectSharedMap() throws Exception
	{
		return okOneComma + "map:" + ImageRec.selectImage(db,readInt("user"),readInt("row")).toString() + "}";
	}
	public String selectSkills() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,SkillRec.class).toString() + "}";
	}
	public String selectSpells() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,SpellRec.class).toString() + "}";
	}
	public String selectUserCharacters() throws Exception
	{
		int userRow = readInt("userRow");
		if(!currentUser.isSysAdmin() && !currentUser.isFriendsWith(db,userRow))
		{
			return eeJson("Don't think so chum.");
		}		
		return okOneComma + "list:" + CharacterRec.selectForUser(db,userRow).toString() + "}";
	}
	public String selectVehicles() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,VehicleRec.class).toString() + "}";
	}
	public String selectWeaponModifiers() throws Exception
	{
		return okOneComma + "list:" + WeaponModifierRec.selectWeaponModifiers(db).toString() + "}";
	}
	public String selectWeapons() throws Exception
	{
		return okOneComma + "list:" + SrRec.selectAll(db,WeaponRec.class).toString() + "}";
	}

    public String sendMessage() throws Exception
    {
        String message = readString("message",2000);
        String users = readString("users",2000);
        String type = readString("type",45);
        int thread = readInt("threadId");
        String[] usersArray = users.split(SPLITTER);
        if(usersArray.length < 2)
        {
            return eeJson("Message must have at least 1 recipient.");
        }
        List<UserRec> userList = new ArrayList<UserRec>();
        MessageThreadRec t = new MessageThreadRec();
        if(thread>0)
        {
            t.Row = thread;
            if(!db.select(t))
            {
                return eeJson("Thread not found.");
            }      
        }
        else
        {// new thread OR try finding already existing thread with same users
            int existingThread = MessageThreadRec.selectThreadForUsers(db, users.replace(SPLITTER, ","));
            if(existingThread > 0)
            {
                t.Row = existingThread;
                if(!db.select(t))
                {
                    return eeJson("Thread not found.");
                } 
            }
            else
            {
                t.CreatedBy = currentUser.Row;
                db.insert(t); 
            }
            
        }
        MessageRec a = new MessageRec();
        a.Thread = t.Row;
        a.CreatedBy = currentUser.Row;
        a.Type = MessageRec.MessageType.lookup(type);
        a.Message = message;
        db.insert(a);
        /* for file uploading - probably implement this later
        if(uploadFiles.size()>0)
        {
            for (UploadFile upload : uploadFiles)
            {
                File file = new File(upload.localName);  
                file.deleteOnExit();
                a.Row = 0;
                a.Type = MessageRec.AnnouncementType.Image;
                a.Message = FileKit.cleanFileName(FileKit.name(upload.RemoteName));  
                db.insert(a);
                File newFile = new File(App.getRealChatDir()+"\\"+a.getFileName(FileKit.extension(upload.RemoteName)));
                FileKit.move(file,newFile);
            }
        }
        else
        {
        }
        */
        if(thread < 1)
        {
            for(String s : usersArray)
            {//insert users when thread is NEW, else let ajax function remove/add-MesageThreadUser handle syncing users with thread
                MessageThreadUserRec tu = new MessageThreadUserRec();
                tu.Thread = t.Row;
                tu.User = Integer.parseInt(s);
                if(tu.User==currentUser.Row)
                {
                    tu.LastRowSeen = a.Row;
                }
                db.insertIgnore(tu);
            }
        }
        else
        {
            MessageThreadUserRec tu = new MessageThreadUserRec();
            tu.Thread = t.Row;
            tu.User = currentUser.Row;
            db.select(tu);
            tu.LastRowSeen = a.Row;
            db.update(tu);
        }
        userList = t.selectUsers(db);
        String responseJson = "{ok:1,message:"+a.toString()+",users:"+userList.toString()+",fromDate:"+jsq(a.CreatedAt)+",fromRow:"+currentUser.Row+",fromName:"+jsq(currentUser.Name)+",threadId:"+t.Row+",oldThreadId:"+thread+",createdBy:"+t.CreatedBy+"}";
        for(UserRec u : userList)
        {
            
            List<WebSocketClient> userWebSocketClients = WebSocketEndpoint.findClientsByUser(u);
            for(WebSocketClient c : userWebSocketClients)
            {
                c.send(responseJson);
            }
        }
        return responseJson;
    }
    public String setPlayerApplyModifier() throws Exception
    {
    	int playerRow = readInt("row");
    	boolean apply = readBoolean("apply");
    	CharacterRec rec = new CharacterRec();
    	rec.Row = playerRow;
    	if(db.select(rec))
    	{
    		rec.ApplyModifiers = apply;
    		db.update(rec);
    	}    	
		return okOne;
    }
    public String shareRoll() throws Exception
    {
    	int count = readInt("count");
    	int hits = readInt("hits");
    	boolean glitch = readBoolean("glitch");
    	boolean critical = readBoolean("critical");
    	List<MessageThreadRec> threads = MessageThreadRec.selectThreadForRollShare(db,currentUser.Row);
    	for(MessageThreadRec t : threads)
    	{
    		MessageRec msg = new MessageRec();
    		msg.Thread = t.Row;
    		msg.CreatedBy = currentUser.Row;
    		msg.Type = MessageRec.MessageType.Roll;
    		msg.Message = "Roll("+count+") : "+hits+" HIT"+(hits==1?"":"S") + (glitch?(critical?" CRITICAL GLITCH!!!":" GLITCH!"):"");
    		db.insert(msg);
            List<UserRec> userList = t.selectUsers(db);
            String responseJson = "{ok:1,message:"+msg.toString()+",users:"+userList.toString()+",fromDate:"+jsq(msg.CreatedAt)+",fromRow:"+currentUser.Row+",fromName:"+jsq(currentUser.Name)+",threadId:"+t.Row+",oldThreadId:"+t.Row+",createdBy:"+t.CreatedBy+"}";
            for(UserRec u : userList)
            {
                List<WebSocketClient> userWebSocketClients = WebSocketEndpoint.findClientsByUser(u);
                for(WebSocketClient c : userWebSocketClients)
                {
                    c.send(responseJson);
                }
            }
    	}
		return okOne;
    }
    public String transferCharacter() throws Exception
    {
    	CharacterRec rec = new CharacterRec();
    	rec.Row = readInt("characterRow");
    	if(!db.select(rec))
    	{
			return eeJson("Transfer failed, charcter record not found.");
    	}
    	if(rec.User != currentUser.Row)
    	{
			return eeJson("Woh buddy, you can't transfer someone elses character!?");
    	}
    	rec.Transfer = readInt("toUser");
    	if(rec.User == rec.Transfer)
    	{

			return eeJson("Sorry but, you can't transfer your character to yourself. You already own this character, think about it.");
    	}
    	db.update(rec);
    	if(rec.Transfer>0)
    	{
    		String message = "<i>" + currentUser.Name + "</i> would like to transfer a character to you. <a href='characterlist.jsp'>Click Here</a> to view request.";
    		sendNotification(rec.Transfer, message);
    	}
    	return okOne;
    }
	public String updateAmmo() throws Exception
	{
		CharacterWeaponRec rec = new CharacterWeaponRec();
		rec.Row = readInt("weaponRow");
		if(!db.select(rec))
		{
			return eeJson("Weapon not found.");
		}
		if(rec.selectCharacter(db).User != currentUser.Row)
		{
			return okZero;
		}
		rec.CurrentAmount = readInt("count");
		db.update(rec);
		return okOne;
	}

	@SuppressWarnings("unchecked")
	public String updateAmmoType() throws Exception
	{
		int itemRow = readInt("itemRow");
		if(itemRow<1)
		{
			return "{ok:0}";
		}
		try {
			Class<CharacterWeaponRec> clazz = (Class<CharacterWeaponRec>) Class.forName("sr.data.Character"+readString("type",60)+"Rec");
			CharacterWeaponRec obj = clazz.newInstance();
			obj.Row = itemRow;
			db.select(obj);
			CharacterRec character = new CharacterRec();
			character.Row = obj.CharacterRow;
			if(!db.select(character))
			{
				return eeJson("Character not found.");
			}
			if(character.User != currentUser.Row)
			{
				return okZero;
			}
			obj.CurrentAmmoRow = readInt("ammo");
			db.update(obj);
		}
		catch (Exception e){
			return okZero;
		}
		return okOne;
	}
	public String updateBudget() throws Exception
	{
		if(currentUser.isGuest())
		{

			return eeJson("Sorry Brah! Registered users only.");
		}
		BudgetRec rec = new BudgetRec();
		rec.Row = readInt("Row");
		if(rec.Row> 0)
		{
			if(!db.select(rec))
			{
				return eeJson("Budget entry not found, could not update.");
			}
		}
		if(readBoolean("Delete"))
		{
			db.delete(rec);
			rec.setTemp("Delete",true);
		}
		else
		{
			rec.Amount = readFloat("Amount");
			rec.Note = readString("Note",500);
			rec.User = currentUser.Row;
			//rec.Name = readString("Name",255);
			rec.Category = BudgetCategory.lookup(readString("Category",255));
			rec.Type = rec.Category.isExpense?BudgetType.Expense:BudgetType.Income;
	        rec.Time = read("Time",JDate.zero());
			if(rec.Row>0)
			{
				db.update(rec);
			}
			else
			{
				db.insert(rec);
			}
			rec.setTemp("ts", rec.Time.getTime()/1000);
		}
		return "{ok:1,budget:"+rec.toString()+"}";
	}
	public String updateCharacterAdeptPower() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterAdeptPowerRec cq = new CharacterAdeptPowerRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Level = Integer.parseInt(data[2]);
						cq.Note = data[3];
						db.update(cq);
					}
					else
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.AdeptPowerRow = Integer.parseInt(data[1]);
						cq.Level = Integer.parseInt(data[2]);
						cq.Note = data[3];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating adept powers.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterArmor() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==6)
				{
					CharacterArmorRec cq = new CharacterArmorRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[5]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						cq.Note = data[4];
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.ArmorRow = Integer.parseInt(data[1]);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						cq.Note = data[4];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating armor.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterBioware() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterBiowareRec cq = new CharacterBiowareRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Grade = data[3];
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.BiowareRow = Integer.parseInt(data[1]);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Grade = data[3];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating bioware.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterContact() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		CharacterContactRec rec = new CharacterContactRec();
		rec.Row = readInt("Row");
		if(rec.Row> 0)
		{
			if(!db.select(rec))
			{
				return eeJson("Contact not found, could not update.");
			}
		}
		if(readBoolean("Delete"))
		{
			db.delete(rec);
			rec.setTemp("Delete",true);
		}
		else
		{
			rec.CharacterRow = readInt("CharacterRow");
			rec.Archetype = readString("Archetype",100);
			rec.Age = readInt("Age");
			rec.Portrait = readInt("Portrait");
			rec.Sex = CharacterRec.CharacterSex.lookup(readString("Sex",2));
			rec.MetaType = readInt("MetaType");
			rec.Name = readString("Name",100);
			rec.Note = readString("Note",500);
			rec.Loyalty = readInt("Loyalty");
			rec.Connection = readInt("Connection");
			if(rec.Row>0)
			{
				db.update(rec);
			}
			else
			{
				db.insert(rec);
			}
		}
		return "{ok:1,contact:"+rec.toString()+"}";
	}
	public String updateCharacterCyberdeck() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterCyberdeckRec cq = new CharacterCyberdeckRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.CyberdeckRow = Integer.parseInt(data[1]);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating cyberdecks.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterCyberdeckProgram() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		int parentRow = readInt("parentRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==3)
				{
					CharacterCyberdeckProgramRec cq = new CharacterCyberdeckProgramRec();						
					cq.CharacterRow = characterRow;
					cq.ParentRow = parentRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[2]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.ParentRow = parentRow;
						cq.CharacterRow = characterRow;
						cq.ProgramRow = Integer.parseInt(data[1]);
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating cyberdeck programs.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterCyberware() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterCyberwareRec cq = new CharacterCyberwareRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Grade = data[3];
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.CyberwareRow = Integer.parseInt(data[1]);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Grade = data[3];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating bioware.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}

	public String updateCharacterCyberwareAttachment() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		int parentRow = readInt("parentRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterCyberwareAttachmentRec cq = new CharacterCyberwareAttachmentRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Grade = data[3];
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.ParentRow = parentRow;
						cq.CyberwareRow = Integer.parseInt(data[1]);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Grade = data[3];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating cyberware attachments.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterDrone() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterDroneRec cq = new CharacterDroneRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.DroneRow = Integer.parseInt(data[1]);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating drones.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterGear() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String gear = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(gear.length()>0)
		{
			String[] qs = gear.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==6)
				{
					CharacterGearRec cq = new CharacterGearRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[5]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Rating = Integer.parseInt(data[3]);
						cq.Note = data[4];
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.GearRow = Integer.parseInt(data[1]);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Rating = Integer.parseInt(data[3]);
						cq.Note = data[4];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating gear.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}

	public String updateCharacterKnowledge() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String knowledge = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		SkillRec skill = new SkillRec();
		if(knowledge.length()>0)
		{
			String[] qs = knowledge.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==7)
				{
					CharacterKnowledgeRec cq = new CharacterKnowledgeRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[6]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Rating = Integer.parseInt(data[1]);
						cq.Name = data[2];
						cq.Type = KnowledgeType.lookup(data[3]);
						if(!cq.Type.equals(KnowledgeType.Language))
						{
							cq.Native=false;
						}
						else
						{
							cq.Native = Boolean.parseBoolean(data[4]);
						}
						cq.Note = data[5];
						db.update(cq);
					}
					else
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.Rating = Integer.parseInt(data[1]);
						cq.Name = data[2];
						cq.Type = CharacterKnowledgeRec.KnowledgeType.lookup(data[3]);
						if(!cq.Type.equals(KnowledgeType.Language))
						{
							cq.Native=false;
						}
						else
						{
							cq.Native = Boolean.parseBoolean(data[4]);
						}
						cq.Note = data[5];
						skill = SrRec.selectByName(db, SkillRec.class, cq.Type.name());
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating knowledge.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+",skill:"+skill.toString()+"}";
	}
	public String updateCharacterQuality() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterQualityRec cq = new CharacterQualityRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Note = data[3];
						db.update(cq);
					}
					else
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.QualityRow = Integer.parseInt(data[1]);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Note = data[3];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating qualities.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterSetting() throws Exception
	{
		if(currentUser.isGuest())
		{
			return "{ok:1,setting:{}}";
		}
		String togglePanel = readString("togglePanel",200);
		CharacterSettingRec rec = new CharacterSettingRec();
		rec.CharacterRow = readInt("characterRow");
		if(db.select(rec))
		{
			rec.TogglePanel = togglePanel;
			db.update(rec);
		}
		else
		{
			rec.TogglePanel = togglePanel;
			db.insertIgnore(rec);
		}
		return "{ok:1,setting:"+rec.toString()+"}";
	}
	public String updateCharacterSkill() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterSkillRec cq = new CharacterSkillRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Special = data[3].trim();
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.SkillRow = Integer.parseInt(data[1]);
						cq.Rating = Integer.parseInt(data[2]);
						cq.Special = data[3];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating skills.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterSpell() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==3)
				{
					CharacterSpellRec cq = new CharacterSpellRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[2]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.SpellRow = Integer.parseInt(data[1]);
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating skills.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterVehicle() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterVehicleRec cq = new CharacterVehicleRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.VehicleRow = Integer.parseInt(data[1]);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating weapons.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterWeapon() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String qualities = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(qualities.length()>0)
		{
			String[] qs = qualities.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==6)
				{
					CharacterWeaponRec cq = new CharacterWeaponRec();						
					cq.CharacterRow = characterRow;
					cq.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[5]);
					if(toDelete)
					{
						db.delete(cq);
					}
					else if(cq.Row>0)
					{	
						db.select(cq);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						cq.Note = data[4];
						db.update(cq);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(cq.Row).append(SPLITTER);	
						cq.Row = 0;
						cq.WeaponRow = Integer.parseInt(data[1]);
						cq.Quantity = Integer.parseInt(data[2]);
						cq.Equipped = Boolean.parseBoolean(data[3]);
						cq.Note = data[4];
						db.insert(cq);
						b.append(cq.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating weapons.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateCharacterWeaponModifier() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		String modifier = readString("updateString",2000);
		int characterRow = readInt("characterRow");
		int parentRow = readInt("parentRow");
		StringBuilder b = new StringBuilder();
		String delim = "";
		if(modifier.length()>0)
		{
			String[] qs = modifier.split(DELIMITER);			
			for(String q : qs)
			{
				boolean toDelete = false;
				String[] data = q.split(SPLITTER);
				if(data.length==5)
				{
					CharacterWeaponModifierRec wm = new CharacterWeaponModifierRec();						
					wm.CharacterRow = characterRow;
					wm.Row = Integer.parseInt(data[0]);
					toDelete = Boolean.parseBoolean(data[4]);
					if(toDelete)
					{
						db.delete(wm);
					}
					else if(wm.Row>0)
					{	
						db.select(wm);
						wm.Rating = Integer.parseInt(data[2]);
						wm.Mounted = MountType.lookup(data[3]);
						db.update(wm);
					}
					else //new record
					{
						//build string to return old row number matched with new row number
						b.append(delim).append(wm.Row).append(SPLITTER);	
						wm.Row = 0;
						wm.ParentRow = parentRow;
						wm.WeaponModifierRow = Integer.parseInt(data[1]);
						wm.Rating = Integer.parseInt(data[2]);
						wm.Mounted = MountType.lookup(data[3]);
						db.insert(wm);
						b.append(wm.Row);
						delim = DELIMITER;
					}
				}
				else
				{
					return eeJson("Incorrect amount of parameters for updating weapon modifier.");
				}
			}
		}
		return "{ok:1,newRows:"+jsq(b.toString())+"}";
	}
	public String updateDrone() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		CharacterDroneRec rec = new CharacterDroneRec();
		rec.Row = readInt("droneRow");
		if(!db.select(rec))
		{
			return eeJson("Drone not found.");
		}
		if(rec.selectCharacter(db).User != currentUser.Row)
		{
			return okZero;
		}
		rec.CurrentAmount = readInt("count");
		db.update(rec);
		return okOne;
	}
	@SuppressWarnings("unchecked")
	public String updateEquip() throws Exception
	{
		int itemRow = readInt("itemRow");
		if(itemRow<1)
		{
			return "{ok:0}";
		}
		try {
			Class<EquipRec> clazz = (Class<EquipRec>) Class.forName("sr.data.Character"+readString("type",60)+"Rec");
			EquipRec obj = clazz.newInstance();
			obj.Row = itemRow;
			db.select(obj);
			CharacterRec character = new CharacterRec();
			character.Row = obj.CharacterRow;
			if(!db.select(character))
			{
				return eeJson("Character not found.");
			}
			if(character.User != currentUser.Row)
			{
				return okZero;
			}
			obj.Equipped = readBoolean("equipped");
			db.update(obj);
		}
		catch (Exception e){
			return okZero;
		}
		return okOne;
	}
	@SuppressWarnings("unchecked")
	public String updateFireMode() throws Exception
	{
		int itemRow = readInt("itemRow");
		if(itemRow<1)
		{
			return "{ok:0}";
		}
		try {
			Class<CharacterWeaponRec> clazz = (Class<CharacterWeaponRec>) Class.forName("sr.data.Character"+readString("type",60)+"Rec");
			CharacterWeaponRec obj = clazz.newInstance();
			obj.Row = itemRow;
			db.select(obj);
			CharacterRec character = new CharacterRec();
			character.Row = obj.CharacterRow;
			if(!db.select(character))
			{
				return eeJson("Character not found.");
			}
			if(character.User != currentUser.Row)
			{
				return okZero;
			}
			obj.CurrentFireMode = readString("mode",20);
			db.update(obj);
		}
		catch (Exception e){
			return okZero;
		}
		return okOne;
	}
	public String updateJournal() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Nah hommie.");
		}
		JournalRec j = new JournalRec();
		j.Row = readInt("Row");
		if(j.Row>0)
		{
			if(!db.select(j))
			{
				return eeJson("Journal entry not found.");
			}
			if(j.User!= currentUser.Row)
			{
				return eeJson("Incorrect user for journal entry.");
			}
		}
		else
		{
			j.User = currentUser.Row;
		}
		boolean toDelete = readBoolean("deleteEntry");
		if(toDelete)
		{
			db.delete(j);
			j.setTemp("deleteEntry", true);
		}
		else
		{
			j.Time = new JDateTime(readString("Time", 30));
			j.Text = readString("Text",5000);
			j.Title = readString("Title",100);
			j.Type = readString("Type",100);
			j.Archive = readBoolean("Archive");
			if(j.Row==0)
			{
				db.insert(j);
			}
			else
			{
				db.update(j);
			}
		}
		return okOneComma + "journal:" +j.toString() + "}";
	}
	public String updateMap() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Nah hommie.");
		}
		ImageRec image = new ImageRec();
		image.Row= readInt("row");
		if(!db.select(image))
		{
			return eeJson("Image record not found.");
		}
		if(image.User != currentUser.Row && !currentUser.isSysAdmin())
		{
			return okZero;
		}
		image.Data = readString("data",1000);
		image.Zoom = readFloat("zoom");
		db.update(image);
		return okOne;
	}
	public String updateMapDataLayer()throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Guests can't edit map points, sorry.");
		}
		MapDataRec mapData = new MapDataRec();
		mapData.Row= readInt("mapDataRow");
		if(!db.select(mapData))
		{
			return eeJson("Map data record not found.");
		}
		mapData.Layer = LayerType.lookup(readString("newLayer",30));
		db.update(mapData);
		return okOne;
	}
	public String updateMapDataPosition()throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Guests can't edit map points, sorry.");
		}
		MapDataRec mapData = new MapDataRec();
		mapData.Row= readInt("mapDataRow");
		if(!db.select(mapData))
		{
			return eeJson("Map data record not found.");
		}
		mapData.X = readInt("newX");
		mapData.Y = readInt("newY");
		db.update(mapData);
		return okOne;
	}
    public String updateMessageSeen() throws Exception
    {
        MessageThreadUserRec.updateSeen(db,readInt("threadId"),currentUser.Row);
        return "{ok:1,messageCount:"+MessageThreadUserRec.selectUnseenCount(db, currentUser.Row)+"}";
    }
	public String updatePortrait() throws Exception {
		if(currentUser.isGuest())
		{
			return eeJson("Not for guests.");
		}
		ImageRec image = new ImageRec();
		image.Row= readInt("row");		
		if(!db.select(image))
		{
			return eeJson("Image record not found.");
		}
		int userRow = readInt("user");
		if(currentUser.Row != userRow && !currentUser.isSysAdmin())
		{
			return eeJson("Unauthorized.");
		}
		
		image.Data = readString("data",1000);
		String name = readString("name",60);
		if(name.length()>0)
		{
			image.Name = name;
		}
		db.update(image);
		return okOne;
	}
	public String updatePhysical() throws Exception{
		CharacterRec rec = new CharacterRec();
		rec.Row = readInt("characterRow");
		if(!db.select(rec))
		{
			return eeJson("Charcter not found.");
		}
		if(rec.User != currentUser.Row)
		{
			return okZero;
		}
		rec.PhysicalCurrent = readInt("count");
		db.update(rec);
		return okOne;
	}
	public String updateShareRoll() throws Exception
	{
        boolean shareRoll = readBoolean("shareRoll");
        int thread = readInt("threadId");
        if(thread < 1)
        {
    		return "{ok:0}";
        }
        MessageThreadUserRec rec = new MessageThreadUserRec();
        rec.Thread = thread;
        rec.User = currentUser.Row;
        if(db.select(rec))
        {
            rec.ShareRoll = shareRoll;
            db.update(rec);
        }
		return okOne;
	}
	public String updateStun() throws Exception{
		CharacterRec rec = new CharacterRec();
		rec.Row = readInt("characterRow");
		if(!db.select(rec))
		{
			return eeJson("Charcter not found.");
		}
		if(rec.User != currentUser.Row)
		{
			return okZero;
		}
		rec.StunCurrent = readInt("count");
		db.update(rec);
		return okOne;
	}
	public String updateUser() throws Exception
	{
		UserRec rec = new UserRec();
		rec.Row = readInt("row");
		if(rec.Row>0)
		{
			if(!db.select(rec))
			{
				return eeJson("User not found.");
			}
		}
		rec.Name = readString("name",50);
		rec.EMail = readString("email",80);
		rec.Role = UserRole.lookup(readString("role",20));
		if(rec.Role.equals(UserRole.SysAdmin))
		{
			return eeJson("There can be only one.");
		}
		if(rec.Row==0)
		{
			rec.Login = rec.EMail;
			if(rec.isDupLogin(db))
			{
				return eeJson("Email <i>"+rec.EMail +"</i> already exists.");
			}
			db.insert(rec);
			rec.updatePwd(db, UserRec.DEFAULT_PASS);
		}
		else
		{
			db.update(rec);
		}
		return okOneComma + "user:"+rec.toString() + "}";
	}
	public String updateVehicle() throws Exception
	{
		if(currentUser.isGuest())
		{
			return eeJson("Access denied, registered users only.");
		}
		CharacterVehicleRec rec = new CharacterVehicleRec();
		rec.Row = readInt("vehicleRow");
		if(!db.select(rec))
		{
			return eeJson("Vehicle not found.");
		}
		if(rec.selectCharacter(db).User != currentUser.Row)
		{
			return okZero;
		}
		rec.CurrentAmount = readInt("count");
		db.update(rec);
		return okOne;
	}
	@Override
	public void write() throws Exception {
		String result = run(readString("fn", 128));
		if (_security) {
			set("JSON", "{\"security\":1}");
		} else {
			if (result.equals("")) {
				result = eeJson();
			}
			set("JSON", result);
		}
	}

}
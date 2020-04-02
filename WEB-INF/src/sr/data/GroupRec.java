package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

public class GroupRec extends AppRec{
	public static final int GROUP_KEY_LENGTH = 6;
	public static String INACTIVE = "Inactive";
	public static String NAME = "Name";
	public static String PRIVATE = "Private";
	public static String ROW = "Row";
	public static String SHAREKEY = "ShareKey";
	public static String TABLE = "tgroup";
	public static String USER = "User";
	//
	public boolean Inactive = false;
	public String Name = "";
	public boolean Private = false;
	public int Row = 0;
	public String ShareKey = "";
	public int User = 0;
	//
	public static List<GroupRec> selectForCharacter(Database db,int character,boolean showInactive) throws Exception
	{
		return db.selectList("GroupRec.selectForCharacter",GroupRec.class,character,showInactive?2:1);
	}
	public static List<GroupRec> selectForUser(Database db,int user,boolean showInactive) throws Exception
	{
		return db.selectList("GroupRec.selectForUser",GroupRec.class,user,user,showInactive?2:1);
	}
	public static List<CharacterRec> selectCharacters(Database db, int group) throws Exception
	{
		return CharacterRec.selectForGroup(db, group);
	}
	public static GroupRec selectGroup(Database db, int group) throws Exception
	{
		GroupRec c = new GroupRec();
		db.selectFirst("GroupRec.selectGroup",c,group);
		return c;
	}
	public static GroupRec selectGroupByKey(Database db, String key) throws Exception
	{
		GroupRec rec = new GroupRec();
		db.selectFirst("GroupRec.selectGroupByKey", rec, key);
		if(rec.Row>0)
		{
			return rec;
		}
		return null;
	}
	public static boolean isDupKey(Database db,int currentRow,String key) throws Exception
	{
		return 0 < db.selectScalar("GroupRec.isDupKey", -1, currentRow,key);
	}
	@Override
    public void afterDelete(Database db) throws Exception
    {
    	deleteCharacters(db);
    }
	@Override
	public void beforeInsert(Database db) throws Exception {
		if(ShareKey.isEmpty())
		{
			ShareKey = generateKey();
		}
	}
	@Override
	public void beforeUpdate(Database db) throws Exception {
		if(ShareKey.isEmpty())
		{
			ShareKey = generateKey();
		}
	}
	private void deleteCharacters(Database db) throws Exception
	{
	    db.execute("GroupRec.deleteCharacters", Row);
	}
	@Override
	public String getTable() {
		return TABLE;
	}
	private String generateKey(){
		StringBuilder b = new StringBuilder();
		for(int i = 0,z=GROUP_KEY_LENGTH;i<z;i++)
		{
			b.append(StringKit.alphaUpperNumeric((int)(Math.random()*StringKit.ALPHA_UPPER_NUMERIC_STRING.length())));
		}
		return b.toString();
	}
	public boolean isDupKey(Database db) throws Exception
	{
		return isDupKey(db,Row,ShareKey);
	}
	public void updateCharacters(Database db, List<GroupCharacterRec> chars) throws Exception
	{
	    deleteCharacters(db);
	    for (GroupCharacterRec u : chars)
	    {
	    	u.GroupRow = Row;
	    	db.insert(u);
	    }
	}
}
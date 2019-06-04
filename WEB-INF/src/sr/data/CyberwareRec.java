package sr.data;

import java.util.List;

import ir.data.Database;

/**
 * UserRec is a data structure of user information
 */
public class CyberwareRec extends SrRec {
	public enum CyberwareType {
		Cyberware,Bioware;
	}
	public static final String ATTACHABLE = "Attachable";
	public static final String AVAILABILITY = "Availability";
	public static final String BASE = "Base";
	public static final String BONUS = "Bonus";
	public static final String CAPACITY = "Capacity";
	public static final String CONTAINER = "Container";
	public static final String COST = "Cost";
	public static final String ESSENCE = "Essence";
	public static final String MAXRATING = "MaxRating";
	public static final String MINRATING = "MinRating";
	public static final String PART = "Part";
	public static final String SKILL = "Skill";
	public final static String TABLE = "tCyberware";
	public final static String TYPE = "Type";
	public final static String WIRELESS = "Wireless";
	//
	/** Some cyberware can be installed in other cyberware */
	public boolean Attachable = false;
	public String AttributeBoost = "";
	public String Availability = "";
	/** Base cyberware can be installed with out any other cyberware */
	public boolean Base = false;
	public String Bonus = "";
	public String Capacity = "";
	/** Container cyberware holds other cyberware upto it's capacity */
	public boolean Container = false;
	public String Cost = "";
	public String Essence = "";
	public int MaxRating = 1;
	public int MinRating = 1;
	public String Part = "";
	public String Skill = "";
	public CyberwareType Type = CyberwareType.Cyberware;
	public String Wireless = "";
	//
	public static List<CyberwareRec> selectBioware(Database db) throws Exception
	{
		return db.selectList("CyberwareRec.selectWare", CyberwareRec.class,CyberwareType.Bioware.name());
	}
	public static List<CyberwareRec> selectCyberware(Database db) throws Exception
	{
		return db.selectList("CyberwareRec.selectWare", CyberwareRec.class,CyberwareType.Cyberware.name());
	}
	public static List<CyberwareRec> selectCyberwareAttachments(Database db) throws Exception
	{
		return db.selectList("CyberwareRec.selectWareAttachments", CyberwareRec.class,CyberwareType.Cyberware.name());
	}
	public static List<CyberwareRec> selectCyberwareBase(Database db) throws Exception
	{
		return db.selectList("CyberwareRec.selectWareBase", CyberwareRec.class,CyberwareType.Cyberware.name());
	}
	public static List<CyberwareRec> selectBiowareForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("CyberwareRec.selectBiowareForCharacter",CyberwareRec.class,characterRow);
	}
	public static List<CyberwareRec> selectCyberwareForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("CyberwareRec.selectCyberwareForCharacter",CyberwareRec.class,characterRow);
	}
	public CyberwareRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

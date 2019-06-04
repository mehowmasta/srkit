package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;
import sr.data.CharacterRec.CharacterType;

/**
 * UserRec is a data structure of user information
 */
public class GearRec extends SrRec {
	
	public enum GearType {
		Ammunition,Electronic,Explosives,Medical,Demolition,Security,Misc;
		public static void align(Database db)
	    {
	        StringBuilder b = new StringBuilder();
	        String comma="";
	        b.append("alter table tGear modify column Type enum(");
	        for (GearType t : values())
	        {
	            b.append(comma).append("'").append(t.name()).append("'");
	            comma=",";
	        }
	        b.append(") not null");
	        try
	        {
	            db.execute(b.toString());
	            StringKit.println("GearType.align ok.");
	        }
	        catch (Exception e)
	        {
	            StringKit.println("GearType.align: " + e.getMessage());
	        }
	    }	
	}
	public static final String ARMORPENETRATION = "ArmorPenetration";
	public static final String AVAILABILITY = "Availability";
	public static final String BLAST = "Blast";
	public static final String CAPACITY = "Capacity";
	public static final String COST = "Cost";
	public static final String DAMAGEMOD = "DamageMod";
	public static final String DAMAGEVALUE = "DamageValue";
	public static final String DATAPROCESSING = "DataProcessing";
	public static final String EFFECTS = "Effects";
	public static final String FIREWALL = "Firewall";
	public static final String MAX = "Max";
	public static final String PENETRATION = "Penetration";
	public static final String POWER = "Power";
	public static final String RATING = "Rating";
	public static final String SPEED = "Speed";
	public static final String SUBTYPE = "SubType";
	public static final String TABLE = "tGear";
	public static final String TYPE = "Type";
	public static final String VECTOR = "Vector";
	public static final String WIRELESS = "Wireless";
	//
	public String ArmorPenetration = "";
	public String Availability = "";
	public String Blast = "";
	public String Capacity = "";
	public String Cost = "";
	public String DamageMod = "";
	public String DamageValue = "";
	public int DataProcessing = 0;
	public String Effects = "";
	public int Firewall = 0;
	public int Max = 0;
	public int Penetration = 0;
	public int Power = 0;
	public int Rating = 0;
	public String Speed = "";
	public String SupType = "";
	public GearType Type = GearType.Misc;
	public String Vector = "";
	public String Wireless = "";
	//
	public static List<GearRec> selectAmmunition(Database db) throws Exception
	{
		return selectType(db,GearType.Ammunition);
	}
	public static List<GearRec> selectDemolition(Database db) throws Exception
	{
		return selectType(db,GearType.Demolition);
	}
	public static List<GearRec> selectElectronic(Database db) throws Exception
	{
		return selectType(db,GearType.Electronic);
	}
	public static List<GearRec> selectMedical(Database db) throws Exception
	{
		return selectType(db,GearType.Medical);
	}
	public static List<GearRec> selectMisc(Database db) throws Exception
	{
		return selectType(db,GearType.Misc);
	}
	public static List<GearRec> selectSecurity(Database db) throws Exception
	{
		return selectType(db,GearType.Security);
	}
	public static List<GearRec> selectType(Database db,GearType type) throws Exception
	{
		return db.selectList("GearRec.selectType",GearRec.class, type.name());
	}
	public GearRec() {
	}
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

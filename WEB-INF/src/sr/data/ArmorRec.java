package sr.data;

import java.util.List;

import ir.data.Database;

/**
 * UserRec is a data structure of user information
 */
public class ArmorRec extends SrRec {

	public static final String ARMORRATING = "ArmorRating";
	public static final String AVAILABILITY = "Availability";
	public static final String CAPACITY = "Capacity";
	public static final String COST = "Cost";
	public static final String ENVIRONMENT = "Environment";
	public static final String FEATURES = "Features";
	public final static String SPECIALRULES = "SpecialRules";
	public final static String TABLE = "tArmor";
	public final static String WIRELESS = "Wireless";
	//
	public String ArmorRating = "";
	public String Availability = "";
	public String Capacity = "";
	public String Cost = "";
	public String Environment ="";
	public String Features = "";
	public String SpecialRules = "";
	public String Wireless = "";
	//
	public static List<ArmorRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("ArmorRec.selectForCharacter",ArmorRec.class,characterRow);
	}
	public ArmorRec() {
	}
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

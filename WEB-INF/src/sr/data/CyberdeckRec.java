package sr.data;

import java.util.List;

import ir.data.Database;

/**
 * UserRec is a data structure of user information
 */
public class CyberdeckRec extends SrRec {
	public static final String ATTRIBUTEARRAY = "AttributeArray";
	public static final String AVAILABILITY = "Availability";
	public static final String COST = "Cost";
	public static final String DEVICERATING = "DeviceRating";
	public static final String PROGRAM = "Program";
	public static final String SPECIALTY = "Specialty";
	public final static String TABLE = "tCyberdeck";
	//
	public String AttributeArray = "";
	public String Availability = "";
	public String Cost = "";
	public int DeviceRating = 0;
	public String Essence = "";
	public int Program = 0;
	public boolean Specialty = false;
	//
	public static List<CyberdeckRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("CyberdeckRec.selectForCharacter",CyberdeckRec.class,characterRow);
	}
	public CyberdeckRec() {
	}
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

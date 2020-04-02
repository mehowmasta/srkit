package sr.data;

import java.util.List;

import ir.data.Database;
import sr.data.CyberwareRec.CyberwareType;

/**
 * UserRec is a data structure of user information
 */
public class WeaponModifierRec extends SrRec {
	public enum MountType {
		NA,Top,Under,Side,Internal,Barrel,Stock;
		public static MountType lookup(String s)
		{
			for(MountType t : MountType.values())
			{
				if(t.name().equals(s))
				{
					return t;
				}
			}
			return MountType.NA;
		}
	}
	public static final String ATTACHABLE = "Attachable";
	public static final String AVAILABILITY = "Availability";
	public static final String BONUS = "Bonus";
	public static final String COST = "Cost";
	public static final String MAXRATING = "MaxRating";
	public static final String MINRATING = "MinRating";
	public static final String MOUNT = "Mount";
	public final static String TABLE = "tWeaponModifier";
	public final static String TYPE = "Type";
	public final static String WIRELESS = "Wireless";
	//
	/** Some cyberware can be installed in other cyberware */
	public boolean Attachable = false;
	public String Availability = "";
	public String Bonus = "";
	public String Cost = "";
	public int MaxRating = 1;
	public int MinRating = 1;
	public String Mount = "";
	public String Wireless = "";
	//

	public static List<WeaponModifierRec> selectWeaponModifiers(Database db) throws Exception
	{
		return db.selectList("WeaponModifierRec.selectWeaponModifiers", WeaponModifierRec.class);
	}
	public WeaponModifierRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

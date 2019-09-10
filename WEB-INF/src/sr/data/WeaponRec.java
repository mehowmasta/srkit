package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class WeaponRec extends SrRec {
	
	public static final String ACCURACY = "Accuracy";
	public static final String AMMO = "Ammo";
	public static final String ARMORPENETRATION = "ArmorPenetration";
	public static final String AVAILABILITY = "Availability";
	public static final String COST = "Cost";
	public static final String DAMAGEVALUE = "DamageValue";
	public static final String MODIFIERS = "Modifiers";
	public static final String REACH = "Reach";
	public static final String RECOILCOMPENSATION = "RecoilCompensation";
	public final static String SKILL = "Skill";
	public final static String SKILLROW = "SkillRow";
	public final static String STANDARDUPGRADES = "StandardUpgrades";
	public final static String TABLE = "tWeapon";
	public final static String TYPE = "Type";
	public final static String WIRELESS = "Wireless";
	//
	public String Accuracy = "";
	public String Ammo = "";
	public String ArmorPenetration = "";
	public String Availability = "";
	public String Cost = "";
	public String DamageValue = "";
	public String Modifiers = "";
	public String Reach = "";
	public String RecoilCompensation = "";
	public String Skill = "";
	public int SkillRow = 0;
	public String StandardUpgrades = "";
	public String Type = "";
	public String Wireless = "";
	//
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{NAME,COST};
		String[] s = new String[]{"",""};
		for(int i=0,z=t.length;i<z;i++)
		{
			b.append(comma)
			 .append("{name:").append(StringKit.jsq(t[i]))
			 .append(",suffix:").append(StringKit.jsq(s[i]))
			 .append("}");
			comma=",";
		}
		return b.append("]").toString();
	}
	public static List<WeaponRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("WeaponRec.selectForCharacter",WeaponRec.class,characterRow);
	}
	public WeaponRec() {
	}
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

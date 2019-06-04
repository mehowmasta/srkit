package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class CritterPowerRec extends SrRec {
	public enum CritterSpellType {
		Physical,Mana;
	}
	public enum CritterPowerType {
		Critter,Spirit,FreeSpirit,GreaterSpirit;
	}
	public static final String ACTION = "Action";
	public static final String DURATION = "Duration";
	public static final String POWERTYPE = "PowerType";
	public final static String RANGE = "Range";
	public final static String TABLE = "tCritterPower";
	public static final String SPELLTYPE = "SpellType";
	//
	public String Action = "";
	public String Duration = "";
	public CritterPowerType PowerType = CritterPowerType.Critter;
	public String Range = "";
	public CritterSpellType SpellType = CritterSpellType.Physical;
	
	//
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{SPELLTYPE,POWERTYPE,ACTION};
		String[] s = new String[]{"Powers","Powers","Action"};
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
	public static List<CritterPowerRec> selectAll(Database db) throws Exception
	{
		return db.selectList("CritterPowerRec.selectAll", CritterPowerRec.class);
	}
	public static List<CritterPowerRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("CritterPowerRec.selectForCharacter",CritterPowerRec.class,characterRow);
	}
	public CritterPowerRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

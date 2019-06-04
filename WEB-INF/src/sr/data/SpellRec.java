package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class SpellRec extends SrRec {
	public enum SpellType {
		Physical,Mana;
	}
	public static final String CATEGORY = "Category";
	public static final String DAMAGE = "Damage";
	public static final String DRAIN = "Drain";
	public static final String DURATION = "Duration";
	public static final String EFFECTS = "Effects";
	public final static String RANGE = "Range";
	public final static String TABLE = "tSpell";
	public static final String TYPE = "Type";
	//
	public String Category = "";
	public String Damage = "";
	public String Drain = "";
	public String Duration = "";
	public String Effects = "";
	public String Range = "";
	public SpellType Type = SpellType.Physical;
	//
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{CATEGORY,TYPE,EFFECTS};
		String[] s = new String[]{"Spells","Spells",""};
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
	public static List<SpellRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("SpellRec.selectForCharacter",SpellRec.class,characterRow);
	}
	public SpellRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

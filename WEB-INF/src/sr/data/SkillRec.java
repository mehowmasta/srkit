package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class SkillRec extends SrRec {
	public static final String ATTRIBUTE = "Attribute";
	public static final String DEFAULT = "Default";
	public final static String GROUP = "Group";
	public final static String SPECIALIZATION = "Specilization";
	public final static String TABLE = "tSkill";
	public final static String TYPE = "Type";
	//
	public String Attribute = "";
	public boolean Default = true;
	public String Group = "";
	public String Range = "";
	public String Specialization = "";
	public String Type = "";
	//
	public static List<SkillRec> selectAll(Database db) throws Exception
	{
		return db.selectList("SkillRec.selectAll", SkillRec.class);
	}
	public static List<SkillRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("SkillRec.selectForCharacter",SkillRec.class,characterRow);
	}
	public SkillRec() {
	}
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{TYPE,ATTRIBUTE,GROUP};
		String[] s = new String[]{"Skills","Skills","Group"};
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
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

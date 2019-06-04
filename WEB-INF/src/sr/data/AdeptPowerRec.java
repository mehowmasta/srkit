package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class AdeptPowerRec extends SrRec {
	public static final String ACTIVATION = "Activation";
	public static final String COST = "Cost";
	public static final String DRAIN = "Drain";
	public static final String DURATION = "Duration";
	public static final String MAX = "Max";	
	public static final String PREREQUISITE = "Prerequisite";
	public final static String TABLE = "tAdeptPower";
	
	//
	public String Activation;
	public float Cost = 0.0f;
	public boolean Drain = false;
	public String Duration = "";
	public int Max = 0;
	public String Prerequisite = "";
	//
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{"",ACTIVATION};
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
	public static List<AdeptPowerRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("AdeptPowerRec.selectForCharacter",AdeptPowerRec.class,characterRow);
	}
	public AdeptPowerRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

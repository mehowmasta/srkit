package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class DroneRec extends SrRec {
	
	public static final String ACCELERATION = "Acceleration";
	public static final String ARMOR = "Armor";
	public static final String AVAILABILITY = "Availability";
	public static final String BODY = "Body";
	public static final String COST = "Cost";
	public static final String HANDLING = "Handling";
	public static final String PILOT = "Pilot";
	public static final String SENSOR = "Sensor";
	public static final String SIZE = "Size";
	public final static String SPEED = "Speed";
	public final static String STYLE = "Style";
	public final static String TABLE = "tDrone";
	public static final String TYPE = "Type";
	//
	public String Acceleration = "";
	public int Armor = 0;
	public String Availability = "";
	public String Body = "";
	public String Cost = "";
	public String Description = "";
	public String Handling = "";
	public String Name = "";
	public int Pilot = 0;
	public int Row = 0;
	public int Sensor = 0;
	public String Size = "";
	public String Source = "";
	public String Speed = "";
	public String Style = "";
	public String Type = "";
	//
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{SIZE,TYPE};
		String[] s = new String[]{"Size",""};
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
	public static List<DroneRec> selectAll(Database db) throws Exception
	{
		return db.selectList("DroneRec.selectAll", DroneRec.class);
	}
	public static List<DroneRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("DroneRec.selectForCharacter",DroneRec.class,characterRow);
	}
	
	public DroneRec() {
	}
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

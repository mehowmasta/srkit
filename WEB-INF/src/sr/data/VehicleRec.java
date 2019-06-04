package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class VehicleRec extends SrRec {
	
	public static final String ACCELERATION = "Acceleration";
	public static final String ARMOR = "Armor";
	public static final String AVAILABILITY = "Availability";
	public static final String BODY = "Body";
	public static final String COST = "Cost";
	public static final String CRAFT = "Craft";
	public static final String HANDLING = "Handling";
	public static final String PILOT = "Pilot";
	public static final String SEATS = "Seats";
	public static final String SENSOR = "Sensor";
	public final static String SPEED = "Speed";
	public final static String TABLE = "tVehicle";
	public final static String TYPE = "Type";
	//
	public String Acceleration = "";
	public int Armor = 0;
	public String Availability = "";
	public int Body = 0;
	public String Cost = "";
	public String Craft = "";
	public String Description = "";
	public String Handling = "";
	public String Name = "";
	public int Pilot = 0;
	public int Row = 0;
	public String Seats = "";
	public int Sensor = 0;
	public String Source = "";
	public String Speed = "";
	public String Type = "";
	//
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{CRAFT,TYPE};
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
	public static List<VehicleRec> selectAll(Database db) throws Exception
	{
		return db.selectList("VehicleRec.selectAll", VehicleRec.class);
	}
	public static List<VehicleRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("VehicleRec.selectForCharacter",VehicleRec.class,characterRow);
	}
	public VehicleRec() {
	}
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

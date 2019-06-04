package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;
import sr.web.Images;

public class MapDataRec extends AppRec 
{
	public enum LayerType {
        GM,Player,Map;
        public static void align(Database db)
        {
            StringBuilder b = new StringBuilder();
            String comma="";
            b.append("alter table tmapdata modify column Layer enum(");
            for (LayerType t : values())
            {
                b.append(comma).append("'").append(t.name()).append("'");
                comma=",";
            }
            b.append(") not null");
            try
            {
                db.execute(b.toString());
                StringKit.println("MapDataRec.LayerType.align ok.");
            }
            catch (Exception e)
            {
                StringKit.println("MapDataRec.LayerType.align: " + e.getMessage());
            }
        }
        public static LayerType lookup(String name) {
			for (LayerType t : values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return Player;
		}
    };
    public enum ObjectType {
        Character(Images.Learning,true),
        Registry(Images.FingerPrint,true),
        PC(Images.Weapon,false),
        NPC(Images.Weapon,false),
        Weapon(Images.Weapon,true),
        Armor(Images.Armor,true),
        Cyberware(Images.Cyberware,true),
        Cyberdeck(Images.Cyberdeck,true),
        Gear(Images.Gear,true),
        Drone(Images.Rigging,true),
        Vehicle(Images.Vehicle,true),
        Electronic(Images.Electronics,false),
        Security(Images.Security,false),
        Medical(Images.Medical,false),
        Misc(Images.Misc,false),
        Demolition(Images.Explosives,false),
    	Ammunition(Images.Ammo,false);
        public static void align(Database db)
        {
            StringBuilder b = new StringBuilder();
            String comma="";
            b.append("alter table tmapdata modify column Type enum(");
            for (ObjectType t : values())
            {
                b.append(comma).append("'").append(t.name()).append("'");
                comma=",";
            }
            b.append(") not null");
            try
            {
                db.execute(b.toString());
                StringKit.println("MessageType.align ok.");
            }
            catch (Exception e)
            {
                StringKit.println("MessageType.align: " + e.getMessage());
            }
        }
        public static ObjectType lookup(String name) {
			for (ObjectType t : values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return NPC;
		}
    	public static String toJson()
    	{
    		StringBuilder b = new StringBuilder("[");
    		String comma = "";
    		for(ObjectType t : ObjectType.values())
    		{
    			b.append(comma)
    			 .append("{name:").append(StringKit.jsq(t.name()))
    			 .append(",imgSrc:").append(StringKit.jsq(t.image))
    			 .append(",hasPicker:").append(t.hasPicker)
    			 .append("}");
    			comma = ",";
    		}
    		return b.append("]").toString();
    	}
        public final String image;
        public final Boolean hasPicker;
        private ObjectType(String image,boolean hasPicker)
        {
        	this.image = image;
        	this.hasPicker = hasPicker;
        }
    };
	public static final String LAYER = "Layer";
	public static final String MAPROW = "MapRow";
	public static final String MIRROR = "Mirror";
	public static final String NAME = "Name";
	public static final String NOTE = "Note";
	public static final String OBJECTROW = "ObjectRow";
	public static final String OPACITY = "Opacity";
	public static final String ROTATION = "Rotation";
	public static final String ROW = "Row";
	public static final String SIZE = "Size";
	public static final String TABLE = "tMapData";
	public static final String TYPE = "Type";
	public static final String _X = "X";
	public static final String _Y = "Y";
	//
	public LayerType Layer = LayerType.Player; 
	public int MapRow = 0;
	public boolean Mirror = false;
	public String Name = "";
	public String Note = "";
	public int ObjectRow = 0;
	public float Opacity = 0f;
	public int Rotation = 0;
	public int Row = 0;
	public float Size = 0f;
	public ObjectType Type = ObjectType.NPC;
	public int X = 0;
	public int Y = 0;
	//
	public static List<MapDataRec> selectAll(Database db, int mapRow) throws Exception
	{
		return db.selectList("MapDataRec.selectAll", MapDataRec.class, mapRow);
	}
	//
	@Override
	public String getTable() {
		return TABLE;
	}	
}
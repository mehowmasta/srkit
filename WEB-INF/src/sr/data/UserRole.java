package sr.data;

import ir.data.Database;
import ir.util.StringKit;

public enum UserRole
{
	Admin,SysAdmin,GameMaster,Runner,Guest,Wife;
	//
	public static UserRole lookup(String name) {
		for (UserRole t : values()) {
			if (t.name().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return Runner;
	}
	public static void align(Database db)
    {
        StringBuilder b = new StringBuilder();
        String comma="";
        b.append("alter table tUser modify column Role enum(");
        for (UserRole t : values())
        {
            b.append(comma).append("'").append(t.name()).append("'");
            comma=",";
        }
        b.append(") not null");
        try
        {
            db.execute(b.toString());
            StringKit.println("UserRec.align ok.");
        }
        catch (Exception e)
        {
            StringKit.println("UserRec.align: " + e.getMessage());
        }
    }
	public static String selectJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for (UserRole s : values()) {
			b.append(comma).append("{Row:").append(StringKit.jsq(s.name())).append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
}
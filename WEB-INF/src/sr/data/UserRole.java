package sr.data;

import ir.util.StringKit;

public enum UserRole
{
	Admin,SysAdmin,GameMaster,Runner,Guest;
	//
	public static UserRole lookup(String name) {
		for (UserRole t : values()) {
			if (t.name().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return Runner;
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
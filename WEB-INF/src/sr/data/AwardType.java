package sr.data;

import ir.util.StringKit;

public enum AwardType{
	Contact,
	Item,
	Karma,
	Nuyen;

	public static AwardType lookup(String name) {
		for (AwardType t : values()) {
			if (t.name().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return null;
	}
	public static String toJson()
	{
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for(AwardType t : AwardType.values())
		{
			b.append(comma)
			 .append("{Name:").append(StringKit.jsq(t.name()))
			 .append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
}
package sr.data;

import ir.util.StringKit;

/**
 * environmental modifiers are applied to the attack roll.
 * 
 * @author mn
 *
 */
public enum EnvironmentalModifier {
	Vis_Light("Light Rain /Fog /Smoke","-1","Visibility"),
	Vis_Mod("Moderate Rain /Fog /Smoke","-3","Visibility"),
	Vis_Heavy("Heavy Rain /Fog /Smoke","-6","Visibility"),
	Light_Partial("Partial Light /Weak Glare","-1","Light /Glare"),
	Light_Dim("Dim Light /Moderate Glare","-3","Light /Glare"),
	Light_Dark("Total Darkness /Blinding Glare","-6","Light /Glare"),
	Wind_Light("Light Winds","-1","Wind"),
	Wind_Mod("Moderate Winds","-3","Wind"),
	Wind_Strong("Strong Winds","-6","Wind"),
	Range_Med("Medium Range","-1","Range"),
	Range_Long("Long Range","-3","Range"),
	Range_Extreme("Extreme Range","-6","Range");
	public final String text;
	public final String type;
	public final String modifier;
	public static final String[] skills = {};
	private EnvironmentalModifier(String text, String mod, String type) {
		this.text=text;
		this.modifier = mod;
		this.type = type;
	}
	public static String toJson()
	{
		StringBuilder b = new StringBuilder("{skills:");
		b.append(StringKit.jsq(skills))
		 .append(",modifiers:new KeyedArray('name',[");
		String comma = "";
		for(EnvironmentalModifier t : EnvironmentalModifier.values())
		{
			b.append(comma)
			 .append("{name:").append(StringKit.jsq(t.name()))
			 .append(",text:").append(StringKit.jsq(t.text))
			 .append(",type:").append(StringKit.jsq(t.type))
			 .append(",modifier:").append(StringKit.jsq(t.modifier))
			 .append("}");
			comma = ",";
		}
		return b.append("])}").toString();
	}
}

package sr.data;

import sr.web.Images;
import ir.util.StringKit;

public enum ScoreBoardType
{
	Ammo(Images.Ammo,5,30,false),
	Damage(Images.Physical,3,12,true),
	Device(Images.Device,5,10,false),
	Drone(Images.Rigging,6,8,false),
	Mark(Images.Mark,3,3,false),
	Status(Images.Status,3,3,false),
	Overwatch(Images.Overwatch,5,40,false),
	Vehicle(Images.Vehicle,6,15,false);
	public final int columns;
	public final String icon;
	public final int max;
	public final boolean showModifiers;
	
	private ScoreBoardType(String icon,int columns,int max,boolean showModifiers)
	{
		this.icon = icon;
		this.columns = columns;
		this.max = max;
		this.showModifiers = showModifiers;
	}
	public static String toJson()
	{
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for(ScoreBoardType t : ScoreBoardType.values())
		{
			b.append(comma)
			 .append("{name:").append(StringKit.jsq(t.name()))
			 .append(",imgSrc:").append(StringKit.jsq(t.icon))
			 .append(",max:").append(t.max)
			 .append(",showModifiers:").append(t.showModifiers)
			 .append(",columns:").append(t.columns)
			 .append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
}
package sr.data;

import ir.util.StringKit;
import sr.web.Images;

public enum StatusType{
	Acid("Acid",Images.Acid,"DV per turn, Armor reduced by 1 per turn,",true,"DV per turn",0,"","-1DV per turn"),
	Aim("Aim",Images.Aim,"+1 dice pool or +1 Accuracy",true,"Dice Mod",1,"",""),
	Cold("Cold",Images.Snowflake,"Armor reduced to 0",false,"",0,"Armor (1)",""),
	Cover("Cover",Images.Cover,"+ Cover Bonus to Defense dice pool",true,"Dice Mod",2,"",""),
	Disoriented("Disoriented",Images.Disoriented,"-2 dice pool",true,"turns",3,"","Time expires"),
	Electric("Electric",Images.Thunder,"-1 dice pool, -5 initiative",true,"Turns",1,"",""),
	Fire("Fire",Images.Fire,"3 DV per turn ",true,"DV per turn",3,"Armor + Fire Resistance - Fire AP vs Net Hits of Attack","Agility + Intuition (-1DV per Net Hit)"),
	FullDefense("Full Def",Images.Shield,"+ Willpower to Defense dice pool",false,"",0,"",""),
	//Grappling("Grappling",Images.Weapon),
	Nausea("Nausea",Images.Stomach,"Doubles wound modifiers",true,"turns",3,"Body + WillPower (Toxin Power)","Time Expires"),
	Paralysis("Paralysis",Images.Brain,"Unable to move, -2 dice pool",true,"Turns",10,"Body + WillPower (Toxin Power)","Time Expires"),
	Prone("Prone",Images.Prone,"Avoid supressive fire",false,"",0,"","Simple Action"),
	Recoil("Recoil",Images.Recoil,"-1 to dice pool per turn of shooting",true,"Dice Mod",0,"","Stop shooting for one pass"),
	Subdued("Subdued",Images.Subdue,"Prevents all actions",true,"Threshold",0,"[Physical] limit vs Strength + Net Hits of Attack","Unarmed Combat + Strength [Physical] (Net Hits of Attack)"),
	Surprised("Surprised",Images.Surprise,"-10 initiative",false,"",0,"Reaction + Intuition (3)","End of turn");
	//Unconscious("Unconscious",Images.Weapon);
	public final String text;
	public final boolean useInput;
	public final int inputDefault;
	public final String inputLabel;
	public final String description;
	public final String icon;
	public final String resist;
	public final String remove;
	private StatusType(String text,String icon,String description,boolean useInput,String inputLabel, int inputDefault,String resist,String remove)
	{
		this.description = description;
		this.text = text;
		this.icon = icon;
		this.inputDefault = inputDefault;
		this.inputLabel = inputLabel;
		this.useInput = useInput;
		this.resist = resist;
		this.remove = remove;
	}
	public static String toJson()
	{
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for(StatusType t : StatusType.values())
		{
			b.append(comma)
			 .append("{name:").append(StringKit.jsq(t.name()))
			 .append(",imgSrc:").append(StringKit.jsq(t.icon))
			 .append(",inputDefault:").append(t.inputDefault)
			 .append(",inputLabel:").append(StringKit.jsq(t.inputLabel))
			 .append(",useInput:").append(t.useInput)
			 .append(",description:").append(StringKit.jsq(t.description))
			 .append(",text:").append(StringKit.jsq(t.text))
			 .append(",resist:").append(StringKit.jsq(t.resist))
			 .append(",remove:").append(StringKit.jsq(t.remove))
			 .append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
}
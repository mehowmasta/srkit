package sr.data;

import ir.util.StringKit;

public enum MeleeModifier{
AttackerCharge("Attacker making charging attack","+2",false,"",0,0),
AttackerProne("Attacker prone","-1",false,"",0,0),
AttackerCallShot("Attacker making a Called Shot","-4",false,"",0,0),
AttackerPos("Attacker has superior position","+2",false,"",0,0),
AttackerOffHand("Attacker using off-hand weapon","-2",false,"",0,0),
AttackerFriends("Attacker has friends in melee","+1",false,"",0,0),
AttackerWound("Attacker wounded","-[Wound]",true,"Wound",-1,-10),
AttackerTouch("Touch-only attack","+2",false,"",0,0),
DefenderCharge("Defender receiving a charge","+1",false,"",0,0),
DefenderProne("Opponent prone","+1",false,"",0,0);

public final String text;
public final String modifier;
public final boolean useInput;
public final String inputLabel;
public final int inputMax;
public final int inputMin;
public static final String[] skills = {"Blades","Clubs","Unarmed Combat","Exotic Melee Weapon (Specific)"};
private MeleeModifier(String text, String mod, boolean useInput,String inputLabel,int inputMax, int inputMin) {
	this.text = text;
	this.modifier = mod;
	this.useInput = useInput;
	this.inputLabel = inputLabel;
	this.inputMax = inputMax;
	this.inputMin = inputMin;
}
public static String toJson()
{
	StringBuilder b = new StringBuilder("{skills:");
	b.append(StringKit.jsq(skills))
	 .append(",modifiers:new KeyedArray('name',[");
	String comma = "";
	for(MeleeModifier t : MeleeModifier.values())
	{
		b.append(comma)
		 .append("{name:").append(StringKit.jsq(t.name()))
		 .append(",text:").append(StringKit.jsq(t.text))
		 .append(",modifier:").append(StringKit.jsq(t.modifier))
		 .append(",useInput:").append(t.useInput)
		 .append(",inputMax:").append(t.inputMax)
		 .append(",inputMin:").append(t.inputMin)
		 .append(",inputLabel:").append(StringKit.jsq(t.inputLabel))
		 .append("}");
		comma = ",";
	}
	return b.append("])}").toString();
}
}
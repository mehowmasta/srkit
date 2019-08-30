package sr.data;

import ir.util.StringKit;

public enum MeleeModifier{
AttackerCharge("Attacker making charging attack","+2"),
AttackerProne("Attacker prone","-1"),
AttackerCallShot("Attacker making a Called Shot","-4"),
AttackerPos("Attacker has superior position","+2"),
AttackerOffHand("Attacker using off-hand weapon","-2"),
AttackerFriends("Attacker has friends in melee","+1"),
AttackerWound("Attacker wounded","-[Wound]"),
AttackerTouch("Touch-only attack","+2"),
DefenderCharge("Defender receiving a charge","+1"),
DefenderProne("Opponent prone","+1");

public final String text;
public final String modifier;
public static final String[] skills = {"Blades","Clubs","Unarmed Combat","Exotic Melee Weapon (Specific)"};
private MeleeModifier(String text, String mod) {
	this.text = text;
	this.modifier = mod;
}

public static String toJson()
{
	StringBuilder b = new StringBuilder("{skills:");
	b.append(StringKit.jsq(skills))
	 .append(",modifiers:[");
	String comma = "";
	for(MeleeModifier t : MeleeModifier.values())
	{
		b.append(comma)
		 .append("{name:").append(StringKit.jsq(t.name()))
		 .append(",text:").append(StringKit.jsq(t.text))
		 .append(",modifier:").append(StringKit.jsq(t.modifier))
		 .append("}");
		comma = ",";
	}
	return b.append("]}").toString();
}
}
package sr.data;

import ir.util.StringKit;

public enum SocialModifier {
Friendly("Friendly","+2","",false,"",0,0),
Suspicious("Suspicious","-1","",false,"",0,0),
Prejudiced("Prejudiced","-2","",false,"",0,0),
Hostile("Hostile","-3","",false,"",0,0),
Enemy("Enemy","-4","",false,"",0,0),
AdvToNPC("Advantageous to NPC","+1","",false,"",0,0),
AnnoyNPC("Annoying to NPC","-1","",false,"",0,0),
HarmNPC("Harmful to NPC","-3","",false,"",0,0),
DisToNPC("Disastrous to NPC","-4","",false,"",0,0),
ContSpell("Control Thoughts/Emotions spell cast on","-1 per hit","",true,"Hits",-1,-30),
CharStreetRep("Character has (known) street reputation","+[StreetCred]","",true,"StCred",30,1),
SubStreetRep("Subject has (known) street reputation","-[StreetCred]","",true,"StCred",-1,-30),
SubAce("Subject has 'Ace in the hole'","+2","",false,"",0,0),
SubRomantic("Subject has romantic attraction to character","+2","",false,"",0,0),
CharIntox("Character is intoxicated","-1","",false,"",0,0),
CharEvid("Character has plausible-seeming evidence","+1 or +2","Con",true,"Mod",2,1),
SubDistCon("Subject is distracted","+1","Con",false,"",0,0),
SubEvalSit("Subject has time to evaluate situation","-1","Con",false,"",0,0),
CharWrongAttire("Character is wearing the wrong attire or doesn't look right","-2","Etiquette",false,"",0,0),
CharNerv("Character is nervous,agitated, or frenzied","-2","Etiquette",false,"",0,0),
SubDistEti("Subject is distracted","-1","Etiquette",false,"",0,0),
CharImpos("Character is physically imposing","+1 to +3","Intimidation",true,"Mod",3,1),
SubImpos("Subject is physically imposing","-1 to -3","Intimidation",true,"Mod",-1,-3),
CharNum("Characters outnumber the subject(s)","+2","Intimidation",false,"",0,0),
SubNum("Subjects outnumber the character(s)","-2","Intimidation",false,"",0,0),
CharWeap("Character wielding weapon or obvious magic","+2","Intimidation",false,"",0,0),
SubWeap("Subject wielding weapon or obvious magic","-2","Intimidation",false,"",0,0),
CharPain("Character is causing (or has caused) subject physical pain (torture)","+2","Intimidation",false,"",0,0),
SubDang("Subject is oblivious to danger","+2","Intimidation",false,"",0,0),
CharRank("Character has superior rank","+1 to +3","Leadership",true,"Mod",3,1),
SubRank("Subject has superior rank","-1 to -3","Leadership",true,"Mod",-1,-3),
CharAuth("Character is an obvious authority figure","+1","Leadership",false,"",0,0),
CharStrata("Character not part of subjects social strata","-1 to -3","Leadership",true,"Mod",-1,-3),
SubFan("Subject is a fan or devoted to character","+2","Leadership",false,"",0,0),
CharBack("Character lacks background knowledge of situation","-2","Negotiation",false,"",0,0),
CharBlack("Character has blackmail material or heavy bargaining chip","+2","Negotiation",false,"",0,0),
;
//
	
public final String text;
public final String modifier;
public final String skill;
public final boolean useInput;
public final String inputLabel;
public final int inputMax;
public final int inputMin;
public static final String[] skills = {"Con","Instruction","Leadership","Performance","Animal Handling","Etiquette","Intimidation","Negotiation","Impersonation"};
private SocialModifier(String text, String modifier, String skill, boolean useInput,String inputLabel,int inputMax, int inputMin)
{
	this.text = text;
	this.modifier = modifier;
	this.skill = skill;
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
	for(SocialModifier t : SocialModifier.values())
	{
		b.append(comma)
		 .append("{name:").append(StringKit.jsq(t.name()))
		 .append(",text:").append(StringKit.jsq(t.text))
		 .append(",modifier:").append(StringKit.jsq(t.modifier))
		 .append(",skill:").append(StringKit.jsq(t.skill))
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

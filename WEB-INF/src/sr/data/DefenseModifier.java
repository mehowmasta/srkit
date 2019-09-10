package sr.data;

import ir.util.StringKit;

public enum DefenseModifier{
	DefGoodCov("Defender has Good Cover","+4",false,"",0,0),
	DefVeh("Defender inside a moving vehicle","+3",false,"",0,0),
	DefParCov("Defender has Partial Cover","+2",false,"",0,0),
	DefRun("Defender running","+2",false,"",0,0),
	DefCharge("Defender receiving a Charge","+1",false,"",0,0),
	DefReach("Defender has longer reach","+1 per net Reach",true,"Net Reach",10,1),
	DefWound("Defender wounded","-[wound]",true,"Wound",-1,-10),
	DefPrevAtk("Defender has defended against previous attack","-1 per previous attack",true,"Prev Atk",-1,-30),
	DefProne("Defender prone","-2",false,"",0,0),
	DefAreaAtk("Defender targeted by area-effect attack","-2",false,"",0,0),
	DefMelTargRng("Defender in melee targeted by ranged attack","-3",false,"",0,0),
	AtkReach("Attacker has longer reach","-1 per net Reach",true,"Net Reach",-1,-10),
	AtkShotNar("Attacker firing flechette shotgun on narrow spread","-1",false,"",0,0),
	AtkShotMed("Attacker firing flechette shotgun on medium spread","-3",false,"",0,0),
	AtkShotWid("Attacker firing flechette shotgun on wide spread","-5",false,"",0,0),
	AtkBurst("Attacker firing burst or semi-auto burst","-2",false,"",0,0),
	AtkFulAuto("Attacker firing full-auto (Complex)","-9",false,"",0,0),
	;
	
	public final String text;
	public final String modifier;
	public final boolean useInput;
	public final String inputLabel;
	public final int inputMax;
	public final int inputMin;
	public static final String[] skills = {};
	private DefenseModifier(String text, String modifier, boolean useInput,String inputLabel,int inputMax, int inputMin)
	{
		this.text=text;
		this.modifier=modifier;
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
		for(DefenseModifier t : DefenseModifier.values())
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
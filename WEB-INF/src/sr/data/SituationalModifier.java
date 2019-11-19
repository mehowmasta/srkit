package sr.data;

import ir.util.StringKit;

public enum SituationalModifier {
	AtkFireCov("Attacker firing from cover with imaging device","-3",false,"",0,0),
	AtkFireVeh("Attacker firing from a moving vehicle","-2",false,"",0,0),
	AtkMel("Attacker in melee combat","-3",false,"",0,0),
	AtkRun("Attacker running","-2",false,"",0,0),
	AtkOffHand("Attacker using off-hand weapon","-2",false,"",0,0),
	AtkWound("Attacker wounded","-[Wound]",true,"Wound",-1,-10),
	BlndFire("Blind fire","-6",false,"",0,0),
	CallShot("Called shot","-4",false,"",0,0),
	PrevAim("Previously aimed with Take Aim","+1",false,"",0,0),
	WireSmrt("Wireless Smartgun","+1(gear) /+2(implanted)",true,"Mod",2,1),
	;
	public final String text;
	public final String modifier;
	public final boolean useInput;
	public final String inputLabel;
	public final int inputMax;
	public final int inputMin;
	public static final String[] skills = {};
	private SituationalModifier(String text, String modifier, boolean useInput,String inputLabel,int inputMax, int inputMin)
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
		for(SituationalModifier t : SituationalModifier.values())
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

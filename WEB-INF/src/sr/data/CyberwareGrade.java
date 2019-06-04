package sr.data;

import ir.util.StringKit;

public enum CyberwareGrade
{
	Standard(1.0f,0,1.0f),
	Alphaware(0.8f,2,1.2f),
	Betaware(0.7f,4,1.5f),
	Deltaware(0.5f,8,2.5f),
	Used(1.25f,-4,0.75f);
	public final float essenceMultiplier;
	public final int availableModifier;
	public final float costMultiplier;
	
	private CyberwareGrade(float essenceMultiplier,int availableModifier,float costMultiplier)
	{
		this.essenceMultiplier = essenceMultiplier;
		this.availableModifier = availableModifier;
		this.costMultiplier = costMultiplier;
	}
	public static String toJson()
	{
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for(CyberwareGrade t : CyberwareGrade.values())
		{
			b.append(comma)
			 .append("{name:").append(StringKit.jsq(t.name()))
			 .append(",essenceMultiplier:").append(t.essenceMultiplier)
			 .append(",availableModifier:").append(t.availableModifier)
			 .append(",costMultiplier:").append(t.costMultiplier)
			 .append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
}
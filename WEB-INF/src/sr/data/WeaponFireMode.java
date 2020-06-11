package sr.data;

import ir.util.StringKit;

public enum WeaponFireMode{
	
	BF("Burst Fire",ActionType.Complex,-2,3,"")
	,FA("Full Auto (Complex)",ActionType.Complex,-9,10,"")
	,FAS("Full Auto (Simple)",ActionType.Simple,-5,6,"")
	,LB("Long Burst",ActionType.Complex,-5,6,"")
	,SA("Semi-Automatic",ActionType.Simple,0,1,"")
	,SB("Semi-Automatic Burst",ActionType.Simple,-2,3,"")
	,SS("Single-Shot",ActionType.Simple,0,1,"No Recoil")
	;
	private final String text;
	private final ActionType type;
	private final int defenseModifier;
	private final int roundsFired;
	private final String note;
	
	private WeaponFireMode(String text,ActionType type,int defMod, int fired, String note)
	{
		this.text=text;
		this.type=type;
		this.defenseModifier=defMod;
		this.roundsFired=fired;
		this.note=note;
	}
	public static String toJson()
	{
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		for(WeaponFireMode t : WeaponFireMode.values())
		{
			b.append(comma)
			 .append("{name:").append(StringKit.jsq(t.name()))
			 .append(",text:").append(StringKit.jsq(t.text))
			 .append(",actionType:").append(StringKit.jsq(t.type.name()))
			 .append(",defenseModifier:").append(t.defenseModifier)
			 .append(",roundsFired:").append(t.roundsFired)
			 .append(",note:").append(StringKit.jsq(t.note))
			 .append("}");
			comma = ",";
		}
		return b.append("]").toString();
	}
}
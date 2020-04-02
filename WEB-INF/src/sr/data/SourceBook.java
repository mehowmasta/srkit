package sr.data;


public enum SourceBook {
	BulletsAndBandages("Bullets & Bandages","SR5:BB"),
	ChromFlesh("Chrome Flesh","SR5:CF"),
	Core("Core","Core"),	
	CuttingAces("Cutting Aces","SR5:CA"),
	DataTrails("Data Trails","SR5:DT"),
	ForbiddenArcana("Forbidden Arcana","SR5;FA"),
	GunHeaven3("Gun H(e)aven 3","SR5:GH3"),
	HardTargets("Hard Targets","SR5:HT"),
	Rigger50("Rigger 5.0","SR5:R5.0"),
	RunAndGun("Run & Gun","SR5:R&G"),
	RunFaster("Run Faster","SR5:RF"),
	SailAwaySweetSister("Sail Away Sweet Sister","SR5:SASS"),
	ShadowSpells("Shadow Spells","SR5:SSp"),
	StolenSouls("Stolen Souls","SR5:SSo"),
	StreetGrimoire("Street Grimoire","SR5:SG")
	;
	public final String text;
	public final String code;
	private SourceBook(String text,String code)
	{
		this.text=text;
		this.code= code;
	}
}
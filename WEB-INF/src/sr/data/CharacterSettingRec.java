package sr.data;

import ir.data.Database;

public class CharacterSettingRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String TABLE = "tCharacterSetting";
	public static final String TOGGLEPANEL = "TogglePanel";
	
	public int CharacterRow = 0;
	public String TogglePanel = "Skill,Weapon,Armor,Cyberware,Bioware,Gear,Cyberdeck,Drone,Vehicle,AdeptPower,Spell,Quality,Knowledge";
	
	public static CharacterSettingRec selectForCharacter(Database db,int characterRow) throws Exception
	{
		CharacterSettingRec rec = new CharacterSettingRec();
		db.selectFirst("CharacterSettingRec.selectForCharacter", rec, characterRow);
		return rec;
	}
	@Override
	public String getTable() {
		return TABLE;
	}
	
	
}
package sr.data;

import ir.data.Database;

public class CharacterCyberdeckRec extends EquipRec
{
	public static final String CURRENTAMOUNT = "CurrentAmount";
	public static final String CYBERDECKROW = "CyberdeckRow";
	public static final String QUANTITY = "Quantity";
	public static final String TABLE = "tCharacterCyberdeck";
	//
	public int CurrentAmount = 0;
	public int CyberdeckRow = 0;
	public int Quantity = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Device;

	@Override
	public void afterDelete(Database db) throws Exception {
		deleteCyberdeckPrograms(db);
	}
	private void deleteCyberdeckPrograms(Database db) throws Exception {
		db.execute("CharacterCyberdeckRec.deleteCyberdeckPrograms", Row);
	}
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
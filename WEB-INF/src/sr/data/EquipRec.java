package sr.data;

import ir.data.Database;

public abstract class EquipRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String EQUIPPED = "Equipped";
	public static final String ROW = "Row";
	//
	public int CharacterRow = 0;
	public boolean Equipped = false;
	public int Row = 0;

	public CharacterRec selectCharacter(Database db) throws Exception
	{
		CharacterRec rec = new CharacterRec();
		rec.Row = CharacterRow;
		db.select(rec);
		return rec;
	}
}
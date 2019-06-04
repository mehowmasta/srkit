package sr.data;

import ir.data.Database;

public class GroupCharacterRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String GROUPROW = "GroupRow";
	public static final String QUANTITY = "Quantity";
	public static final String ROW = "Row";
	public static final String TABLE = "tGroupCharacter";
	//
	public int CharacterRow = 0;
	public int GroupRow = 0;
	public int Quantity = 0;
	public int Row = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	public static void deleteGroupCharacter(Database db, int groupRow, int characterRow) throws Exception
	{
		db.execute("GroupCharacterRec.deleteGroupCharacter", groupRow,characterRow);
	}
	public static boolean isDup(Database db, int groupRow, int characterRow) throws Exception
	{
		return 0 < db.selectScalar("GroupCharacterRec.isDup", 0, groupRow,characterRow);
	}
}
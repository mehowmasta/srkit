package sr.data;

import ir.data.Database;

public class CharacterCyberwareRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String CYBERWAREROW = "CyberwareRow";
	public static final String GRADE = "Grade";
	public static final String PARENT = "Parent";
	public static final String RATING = "Rating";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterCyberware";
	//
	public int CharacterRow = 0;
	public int CyberwareRow = 0;
	public String Grade = "";
	public int Parent = 0;
	public int Rating = 0;
	public int Row = 0;
	//
	@Override
	public void afterDelete(Database db) throws Exception {
		deleteCyberwareAttachments(db);
	}
	private void deleteCyberwareAttachments(Database db) throws Exception {
		db.execute("CharacterCyberwareRec.deleteCyberwareAttachments", Row);
	}
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
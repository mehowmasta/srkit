package sr.data;

import java.util.List;

import ir.data.Database;

public class CharacterTagRec extends AppRec {
	
	public static final String CHARACTER = "Character";
	public static final String ROW = "Row";
	public static final String TABLE = "tcharactertag";
	public static final String TAG = "Tag";
	public static final String USER = "User";
	//
	public int Character = 0;
	public int Row = 0;
	public String Tag = "";
	public int User = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	public static List<CharacterTagRec> selectForCharacter(Database db,int characterRow) throws Exception
	{
		return db.selectList("CharacterTagRec.selectForCharacter", CharacterTagRec.class, characterRow);
	}
}
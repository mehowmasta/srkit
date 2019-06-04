package sr.data;

public class CharacterSpellRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String ROW = "Row";
	public static final String SPELLROW = "SpellRow";
	public static final String TABLE = "tCharacterSpell";
	//
	public int CharacterRow = 0;
	public int Row = 0;
	public int SpellRow = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
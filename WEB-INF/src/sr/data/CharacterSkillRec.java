package sr.data;

public class CharacterSkillRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String RATING = "Rating";
	public static final String ROW = "Row";
	public static final String SKILLROW = "SkillRow";
	public static final String SPECIAL= "Special";
	public static final String TABLE = "tCharacterSkill";
	//
	public int CharacterRow = 0;
	public int Rating = 0;
	public int Row = 0;
	public int SkillRow = 0;
	public String Special = "";
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
package sr.data;

public class CharacterBiowareRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String BIOWAREROW = "BiowareRow";
	public static final String GRADE = "Grade";
	public static final String RATING = "Rating";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterBioware";
	//
	public int CharacterRow = 0;
	public int BiowareRow = 0;
	public String Grade = "";
	public int Rating = 0;
	public int Row = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
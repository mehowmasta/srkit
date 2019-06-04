package sr.data;

public class CharacterQualityRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String QUALITYROW = "QualityRow";
	public static final String RATING = "Rating";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterQuality";
	//
	public int CharacterRow = 0;
	public int QualityRow = 0;
	public int Rating = 0;
	public int Row = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
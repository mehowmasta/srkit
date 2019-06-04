package sr.data;

public class CharacterCyberwareAttachmentRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String CYBERWAREROW = "CyberwareRow";
	public static final String GRADE = "Grade";
	public static final String PARENTROW ="ParentRow";
	public static final String QUANTITY = "Quantity";
	public static final String RATING = "Rating";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterCyberwareAttachment";
	//
	public int CharacterRow = 0;
	public int CyberwareRow = 0;
	public String Grade = "";
	public int ParentRow = 0;
	public int Quantity = 0;
	public int Rating = 0;
	public int Row = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Device;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
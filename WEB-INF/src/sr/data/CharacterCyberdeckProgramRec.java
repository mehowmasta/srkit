package sr.data;

public class CharacterCyberdeckProgramRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String PARENTROW = "ParentRow";
	public static final String PROGRAMROW ="ProgramRow";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterCyberdeckProgram";
	//
	public int CharacterRow = 0;
	public int ParentRow = 0;
	public int ProgramRow = 0;
	public int Row = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Device;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
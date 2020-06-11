package sr.data;

public class CharacterAdeptPowerRec extends AppRec
{
	public static final String ADEPTPOWERROW = "AdeptPowerRow";
	public static final String CHARACTERROW = "CharacterRow";
	public static final String LEVEL = "Level";
	public static final String NOTE = "Note";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterAdeptPower";
	//
	public int AdeptPowerRow = 0;
	public int CharacterRow = 0;
	public int Level = 0;
	public String Note = "";
	public int Row = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
package sr.data;

public class CharacterGearRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String GEARROW = "GearRow";
	public static final String NOTE = "Note";
	public static final String QUANTITY = "Quantity";
	public static final String RATING = "Rating";
	public static final String TABLE = "tCharacterGear";
	//
	public int CharacterRow = 0;
	public int GearRow = 0;
	public String Note = "";
	public int Rating = 0;
	public int Row = 0;
	public int Quantity = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Device;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
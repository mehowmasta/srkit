package sr.data;

public class CharacterArmorRec extends EquipRec
{
	public static final String ARMORROW = "ArmorRow";
	public static final String NOTE = "Note";
	public static final String QUANTITY = "Quantity";
	public static final String TABLE = "tCharacterArmor";
	//
	public int ArmorRow = 0;
	public String Note = "";
	public int Quantity = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
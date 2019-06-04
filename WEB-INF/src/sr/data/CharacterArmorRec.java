package sr.data;

public class CharacterArmorRec extends EquipRec
{
	public static final String QUANTITY = "Quantity";
	public static final String TABLE = "tCharacterArmor";
	public static final String ARMORROW = "ArmorRow";
	//
	public int Quantity = 0;
	public int ArmorRow = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
package sr.data;

import sr.data.WeaponModifierRec.MountType;

public class CharacterWeaponModifierRec extends AppRec
{
	public static final String CHARACTERROW = "CharacterRow";
	public static final String MOUNTED = "Mounted";
	public static final String NOTE = "Note";
	public static final String PARENTROW ="ParentRow";
	public static final String RATING = "Rating";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterWeaponModifier";
	public static final String WEAPONMODIFIERROW = "WeaponModifierRow";
	//
	public int CharacterRow = 0;
	public int WeaponModifierRow = 0;
	public String Note = "";
	public int ParentRow = 0;
	public int Rating = 0;
	public MountType Mounted = null;
	public int Row = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Device;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
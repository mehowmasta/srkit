package sr.data;

public class CharacterWeaponRec extends EquipRec
{
	public static final String CURRENTAMMOROW = "CurrentAmmoRow";
	public static final String CURRENTAMMOTYPE = "CurrentAmmoType";
	public static final String CURRENTAMOUNT = "CurrentAmount";
	public static final String CURRENTFIREMODE = "CurrentFireMode";
	public static final String NOTE = "Note";
	public static final String PARENT = "Parent";
	public static final String QUANTITY = "Quantity";
	public static final String TABLE = "tCharacterWeapon";
	public static final String WEAPONROW = "WeaponRow";
	//
	/** CharacterGear.ItemRow */
	public int CurrentAmmoRow = 0;
	public String CurrentAmmoType = "";
	public int CurrentAmount = 0;
	public String CurrentFireMode = "";
	public String Note = "";
	public int Parent = 0;
	public int Quantity = 0;
	public int WeaponRow = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Ammo;
	//
	@Override
	public String getTable() {
		return TABLE;
	}
	//
}
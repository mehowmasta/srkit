package sr.data;

public class CharacterContactRec extends AppRec
{
	public static final String AGE = "Age";
	public static final String ARCHETYPE = "Archetype";
	public static final String BLACKMAIL = "Blackmail";
	public static final String CHARACTERROW = "CharacterRow";
	public static final String CONNECTION = "Connection";
	public static final String FAMILY = "Family";
	public static final String FREE = "Free";
	public static final String GROUP = "Group";
	public static final String HOBBIES = "Hobbies";
	public static final String LOYALTY = "Loyalty";
	public static final String METATYPE = "MetaType";
	public static final String NAME = "Name";
	public static final String NOTE = "Note";
	public static final String PAYMENTMETHOD = "PaymentMethod";
	public static final String PERSONALLIFE = "PersonalLife";
	public static final String PORTRAIT = "Portrait";
	public static final String ROW = "Row";
	public static final String SEX = "Sex";
	public static final String TABLE = "tCharacterContact";
	public static final String TYPE = "Type";
	//
	public int Age = 0;
	public String Archetype = "";
	public boolean Blackmail = false;	
	public int CharacterRow = 0;
	public int Connection = 1;
	public boolean Family = false;
	public boolean Free = false;
	public boolean Group = false;
	public String Hobbies = "";
	public int Loyalty = 1;
	public int MetaType = 0;
	public String Name = "";
	public String Note = "";
	public String PaymentMethod = "";
	public String PersonalLife = "";
	public int Portrait = 0;
	public int Row = 0;
	public CharacterRec.CharacterSex Sex = CharacterRec.CharacterSex.M;
	public String Type = "";
	
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
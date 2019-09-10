package sr.data;

import java.util.List;

import ir.data.Database;
import ir.data.IValidator;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class CharacterRec extends AppRec {

	public enum CharacterSex {
		M("Male"), F("Female"), T("Trans");
		public final String text;

		private CharacterSex(String text) {
			this.text = text;
		}
		public static CharacterSex lookup(String sex)
		{
			for(CharacterSex s : values())
			{
				if(s.name().equals(sex))
				{
					return s;
				}
			}
			return CharacterSex.M;
		}
		public static String selectJson() {
			StringBuilder b = new StringBuilder("[");
			String comma = "";
			for (CharacterSex s : CharacterSex.values()) {
				b.append(comma).append("{Row:").append(StringKit.jsq(s.name())).append(",Name:")
						.append(StringKit.jsq(s.text)).append("}");
				comma = ",";
			}
			return b.append("]").toString();
		}
	}

	public enum CharacterType {
		PC, NPC, Critter;
		public static void align(Database db) {
			StringBuilder b = new StringBuilder();
			String comma = "";
			b.append("alter table tCharacter modify column Type enum(");
			for (CharacterType t : values()) {
				b.append(comma).append("'").append(t.name()).append("'");
				comma = ",";
			}
			b.append(") not null");
			try {
				db.execute(b.toString());
				StringKit.println("CharacterType.align ok.");
			} catch (Exception e) {
				StringKit.println("CharacterType.align: " + e.getMessage());
			}
		}

		public static String selectJson() {
			StringBuilder b = new StringBuilder("[");
			String comma = "";
			for (CharacterType s : CharacterType.values()) {
				b.append(comma).append("{Name:").append(StringKit.jsq(s.name())).append("}");
				comma = ",";
			}
			return b.append("]").toString();
		}

		public static CharacterType lookup(String name) {
			for (CharacterType t : values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return PC;
		}
	}
	public enum CharacterAttribute{
		Agility("AGL"),
		Body("Bod"),
		Charisma("CHA"),
		Edge("EDG"),
		Essence("ESS"),
		Intuition("INT"),
		Logic("LOG"),
		Magic("MAG"),
		Reaction("REA"),
		Resonance("RES"),
		Strength("STR"),
		Willpower("WIL");
		private String shortName;
		private CharacterAttribute(String shortName)
		{
			this.shortName=shortName;
		}
		public static String selectJson() {
			StringBuilder b = new StringBuilder("[");
			String comma = "";
			for (CharacterAttribute s : CharacterAttribute.values()) {
				b.append(comma)
				.append("{name:").append(StringKit.jsq(s.name()))
				.append(",shortName:").append(StringKit.jsq(s.shortName))
				.append("}");
				comma = ",";
			}
			return b.append("]").toString();
		}
	};

	public static final String AGE = "Age";
	public static final String AGILITY = "Agility";
	public static final String APPLYMODIFIERS = "ApplyModifiers";
	public static final String ASTRALINITIATIVE = "AstralInitiative";
	public static final String BODY = "Body";
	public static final String CHARISMA = "Charisma";
	public static final String COMPOSURE = "Composure";
	public static final String EDGE = "Edge";
	public static final String EDGEPOINTS = "EdgePoints";
	public static final String ESSENCE = "Essence";
	public static final String HEIGHT = "Height";
	public static final String IMPORTNOTES = "ImportNotes";
	public static final String INACTIVE = "Inactive";
	public static final String INITIATIVE = "Initiative";
	public static final String INITIATIVEDICE = "InitiativeDice";
	public static final String INTUITION = "Intuition";
	public static final String ISIMPORT = "IsImport";
	public static final String JUDGEINTENTIONS = "JudgeIntentions";
	public static final String KARMA = "Karma";
	public static final String LIFESTYLE = "Lifestyle";
	public static final String LIFTCARRY = "LiftCarry";
	public static final String LOGIC = "Logic";
	public static final String MAGIC = "Magic";
	public static final String MATRIXINITIATIVE = "MatrixInitiative";
	public static final String MEMORY = "Memory";
	public static final String MENTALLIMIT = "MentalLimit";
	public static final String METATYPE = "Metatype";
	public static final String MOVEMENT = "Movement";
	public static final String MISC = "Misc";
	public static final String NAME = "Name";
	public static final String NOTE = "Note";
	public static final String NOTORIETY = "Notoriety";
	public static final String NUYEN = "Nuyen";
	public static final String PHYSICALCURRENT = "PhysicalCurrent";
	public static final String PHYSICALLIMIT = "PhysicalLimit";
	public static final String PHYSICALMAX = "PhysicalMax";
	public static final String PORTRAIT = "Portrait";
	public static final String PROFESSIONALRATING = "ProfessionalRating";
	public static final String PUBLICAWARENESS = "PublicAwareness";
	public static final String REACTION = "Reaction";
	public static final String REGISTER = "Register";
	public static final String RESONANCE = "Resonance";
	public static final String ROW = "Row";
	public static final String SEX = "Sex";
	public static final String SOCIALLIMIT = "SocialLimit";
	public static final String STREETCRED = "StreetCred";
	public static final String STRENGTH = "Strength";
	public static final String STUNCURRENT = "StunCurrent";
	public static final String STUNMAX = "StunMax";
	public final static String TABLE = "tCharacter";
	public static final String TOTALKARMA = "TotalKarma";
	public static final String TYPE = "Type";
	public final static String USER = "User";
	public static final String WEIGHT = "Weight";
	public static final String WILLPOWER = "Willpower";
	//
	/** Personal Data */
	public int Age = 0;//
	public int Height = 0;//
	public int Karma = 0;//
	public int Metatype = 37;//
	public String Misc = "";//
	public String Note = "";
	public int Notoriety = 0;//
	public int Portrait = 0;
	public int PublicAwareness = 0;//
	public CharacterSex Sex = CharacterSex.M;//
	public int StreetCred = 0;//
	public int TotalKarma = 0;//
	public int Weight = 0;//
	//
	/** Attributes */
	public int Agility = 1;//
	public int AstralInitiative = 0;//
	public int Body = 1;//
	public int Charisma = 1;//
	public int Composure = 0;//
	public int Edge = 1;//
	public int EdgePoints = 1;//
	public float Essence = 6.00f;//
	public int Initiative = 1;//
	public int Intuition = 1;//
	public int JudgeIntentions = 0;//
	public String LiftCarry = "";//
	public int Logic = 1;//
	public int Magic = 0;//
	public int MatrixInitiative = 0;//
	public int Memory = 0;//
	public int MentalLimit = 1;//
	public String Movement = "";
	public int PhysicalLimit = 1;//
	public int Reaction = 1;//
	public int Resonance = 0;//
	public int SocialLimit = 0;//
	public int Strength = 1;//
	public int Willpower = 1;//
	//
	/** ConditionMonitor */
	public int PhysicalMax = 0;//
	public int PhysicalCurrent = 0;//
	public int StunMax = 0;//
	public int StunCurrent = 0;//
	//
	/** Misc */
	public int InitiativeDice = 1;//
	/** Id's, Lifestyles, Currency */
	public String Lifestyle = "";//
	public int Nuyen = 0;//
	//
	/** Record Data */
	public boolean ApplyModifiers = true;
	public CharacterType Type = CharacterType.PC;//
	public int ProfessionalRating = 0;
	public int Row = 0;
	public String Name = "";
	public int User = 0;//
	public boolean Inactive = false;
	public boolean IsImport = false;
	public String ImportNotes = "";
	public boolean Register = false;
	//

	public static List<AdeptPowerRec> selectCharacterAdeptPowers(Database db, int characterRow) throws Exception {
		return AdeptPowerRec.selectForCharacter(db, characterRow);
	}

	public static List<ArmorRec> selectCharacterArmor(Database db, int characterRow) throws Exception {
		return ArmorRec.selectForCharacter(db, characterRow);
	}

	public static List<CyberwareRec> selectCharacterBioware(Database db, int characterRow) throws Exception {
		return CyberwareRec.selectBiowareForCharacter(db, characterRow);
	}

	public static List<CyberdeckRec> selectCharacterCyberdecks(Database db, int characterRow) throws Exception {
		return CyberdeckRec.selectForCharacter(db, characterRow);
	}

	public static List<CyberwareRec> selectCharacterCyberware(Database db, int characterRow) throws Exception {
		return CyberwareRec.selectCyberwareForCharacter(db, characterRow);
	}

	public static List<DroneRec> selectCharacterDrones(Database db, int characterRow) throws Exception {
		return DroneRec.selectForCharacter(db, characterRow);
	}

	public static ImageRec selectCharacterPortrait(Database db, int user, int imageRow) throws Exception {
		return ImageRec.selectImage(db, user, imageRow);
	}

	public static List<QualityRec> selectCharacterQualities(Database db, int characterRow) throws Exception {
		return QualityRec.selectForCharacter(db, characterRow);
	}

	public static List<SkillRec> selectCharacterSkills(Database db, int characterRow) throws Exception {
		return SkillRec.selectForCharacter(db, characterRow);
	}

	public static List<SpellRec> selectCharacterSpells(Database db, int characterRow) throws Exception {
		return SpellRec.selectForCharacter(db, characterRow);
	}

	public static List<VehicleRec> selectCharacterVehicles(Database db, int characterRow) throws Exception {
		return VehicleRec.selectForCharacter(db, characterRow);
	}

	public static List<WeaponRec> selectCharacterWeapons(Database db, int characterRow) throws Exception {
		return WeaponRec.selectForCharacter(db, characterRow);
	}

	public static List<CharacterRec> selectForGroup(Database db, int group) throws Exception {
		return db.selectList("CharacterRec.selectForGroup", CharacterRec.class, group);
	}
	
	public static List<CharacterRec> selectForRegistry(Database db) throws Exception {
		return db.selectList("CharacterRec.selectForRegistry", CharacterRec.class);
	}
	
	public static List<CharacterRec> selectForPicker(Database db, int user) throws Exception {
		return db.selectList("CharacterRec.selectForPicker", CharacterRec.class, user,user);
	}
	public static List<CharacterRec> selectForUser(Database db, int user) throws Exception {
		return db.selectList("CharacterRec.selectForUser", CharacterRec.class, user);
	}

	public static CharacterRec selectPlayerCharacter(Database db, int user, int characterRow) throws Exception {
		CharacterRec c = new CharacterRec();
		db.selectFirst("CharacterRec.selectPlayerCharacter", c, user, characterRow);
		return c;
	}
	
	public CharacterRec() {
	}

	@Override
	public void afterDelete(Database db) throws Exception {
		deleteAdeptPowers(db);
		deleteArmor(db);
		deleteBioware(db);
		deleteContacts(db);
		deleteCyberdecks(db);
		deleteCyberdeckPrograms(db);
		deleteCyberware(db);
		deleteCyberwareAttachments(db);
		deleteDrones(db);
		deleteGear(db);
		deleteGroupCharacter(db);
		deleteKnowledge(db);
		deleteQualities(db);
		deleteSkills(db);
		deleteSpells(db);
		deleteVehicles(db);
		deleteWeapons(db);
	}

	private void deleteAdeptPowers(Database db) throws Exception {
		db.execute("CharacterRec.deleteAdeptPowers", Row);
	}

	private void deleteArmor(Database db) throws Exception {
		db.execute("CharacterRec.deleteArmor", Row);
	}

	private void deleteBioware(Database db) throws Exception {
		db.execute("CharacterRec.deleteBioware", Row);
	}
	
	private void deleteContacts(Database db) throws Exception {
		db.execute("CharacterRec.deleteContacts", Row);
	}
	
	private void deleteCyberdecks(Database db) throws Exception {
		db.execute("CharacterRec.deleteCyberdecks", Row);
	}

	private void deleteCyberdeckPrograms(Database db) throws Exception {
		db.execute("CharacterRec.deleteCyberdeckPrograms", Row);
	}
	
	private void deleteCyberware(Database db) throws Exception {
		db.execute("CharacterRec.deleteCyberware", Row);
	}
	
	private void deleteCyberwareAttachments(Database db) throws Exception {
		db.execute("CharacterRec.deleteCyberwareAttachments", Row);
	}

	private void deleteDrones(Database db) throws Exception {
		db.execute("CharacterRec.deleteDrones", Row);
	}

	private void deleteGear(Database db) throws Exception {
		db.execute("CharacterRec.deleteGear", Row);
	}

	private void deleteGroupCharacter(Database db) throws Exception {
		db.execute("CharacterRec.deleteGroupCharacter", Row);
	}

	private void deleteKnowledge(Database db) throws Exception {
		db.execute("CharacterRec.deleteKnowledge", Row);
	}

	private void deleteQualities(Database db) throws Exception {
		db.execute("CharacterRec.deleteQualities", Row);
	}

	private void deleteSkills(Database db) throws Exception {
		db.execute("CharacterRec.deleteSkills", Row);
	}

	private void deleteSpells(Database db) throws Exception {
		db.execute("CharacterRec.deleteSpells", Row);
	}

	private void deleteVehicles(Database db) throws Exception {
		db.execute("CharacterRec.deleteVehicles", Row);
	}

	private void deleteWeapons(Database db) throws Exception {
		db.execute("CharacterRec.deleteWeapons", Row);
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}

	public boolean isAwakened() {
		return Magic > 0;
	}

	public boolean isTechnomancer() {
		return Resonance > 0;
	}

	@Override
	public boolean validate(IValidator iv) throws Exception {
		return iv.isOK();
	}

	public void updateArmor(Database db, List<CharacterArmorRec> armor) throws Exception {
		deleteArmor(db);
		for (CharacterArmorRec u : armor) {
			u.CharacterRow = Row;
			db.insert(u);
		}
	}

	public void updateBioware(Database db, List<CharacterBiowareRec> bioware) throws Exception {
		deleteBioware(db);
		for (CharacterBiowareRec u : bioware) {
			u.CharacterRow = Row;
			db.insert(u);
		}
	}

	public void updateCyberdecks(Database db, List<CharacterCyberdeckRec> cyberdecks) throws Exception {
		deleteCyberdecks(db);
		for (CharacterCyberdeckRec u : cyberdecks) {
			u.CharacterRow = Row;
			db.insert(u);
		}
	}

	public void updateCyberware(Database db, List<CharacterCyberwareRec> cyberware) throws Exception {
		deleteCyberware(db);
		for (CharacterCyberwareRec u : cyberware) {
			u.CharacterRow = Row;
			db.insert(u);
		}
	}
}

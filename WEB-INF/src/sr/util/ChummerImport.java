package sr.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.data.Database;
import ir.util.Coerce;
import ir.util.FileKit;
import ir.util.StringKit;
import sr.data.AdeptPowerRec;
import sr.data.ArmorRec;
import sr.data.CharacterAdeptPowerRec;
import sr.data.CharacterArmorRec;
import sr.data.CharacterBiowareRec;
import sr.data.CharacterContactRec;
import sr.data.CharacterCyberdeckRec;
import sr.data.CharacterCyberwareRec;
import sr.data.CharacterDroneRec;
import sr.data.CharacterGearRec;
import sr.data.CharacterKnowledgeRec;
import sr.data.CharacterQualityRec;
import sr.data.CharacterRec;
import sr.data.CharacterSkillRec;
import sr.data.CharacterSpellRec;
import sr.data.CharacterVehicleRec;
import sr.data.CharacterWeaponRec;
import sr.data.CyberdeckRec;
import sr.data.CyberwareRec;
import sr.data.DroneRec;
import sr.data.GearRec;
import sr.data.ImageRec;
import sr.data.QualityRec;
import sr.data.RaceRec;
import sr.data.SkillRec;
import sr.data.SpellRec;
import sr.data.SrRec;
import sr.data.UserRec;
import sr.data.VehicleRec;
import sr.data.WeaponRec;
import sr.web.App;
import sr.data.ImageRec.ImageType;

public class ChummerImport {

	public final CharacterRec c;
	private final List<CharacterAdeptPowerRec> cAdeptPower;
	private final List<CharacterArmorRec> cArmor;
	private final List<CharacterBiowareRec> cBioware;
	private final List<CharacterContactRec> cContacts;
	private final List<CharacterCyberdeckRec> cCyberdecks;
	private final List<CharacterCyberwareRec> cCyberware;
	private final List<CharacterDroneRec> cDrones;
	private final List<CharacterGearRec> cGear;
	private final List<CharacterKnowledgeRec> cKnowledge;
	private final List<CharacterQualityRec> cQualities;
	private final List<CharacterSkillRec> cSkills;
	private final List<CharacterSpellRec> cSpells;
	private final List<CharacterVehicleRec> cVehicles;
	private final List<CharacterWeaponRec> cWeapons;

	private ImageRec image = null;
	private File imageFile = null;
	private final UserRec user;
	private final Database db;
	private final JSONObject json;
	private final List<String> errors;
	public final List<String> noMatch;

	public ChummerImport(Database db, UserRec user, JSONObject json) {
		this.c = new CharacterRec();
		this.user = user;
		this.cAdeptPower = new ArrayList<CharacterAdeptPowerRec>();
		this.cArmor = new ArrayList<CharacterArmorRec>();
		this.cBioware = new ArrayList<CharacterBiowareRec>();
		this.cContacts = new ArrayList<CharacterContactRec>();
		this.cCyberdecks = new ArrayList<CharacterCyberdeckRec>();
		this.cCyberware = new ArrayList<CharacterCyberwareRec>();
		this.cDrones = new ArrayList<CharacterDroneRec>();
		this.cGear = new ArrayList<CharacterGearRec>();
		this.cKnowledge = new ArrayList<CharacterKnowledgeRec>();
		this.cQualities = new ArrayList<CharacterQualityRec>();
		this.cSkills = new ArrayList<CharacterSkillRec>();
		this.cSpells = new ArrayList<CharacterSpellRec>();
		this.cVehicles = new ArrayList<CharacterVehicleRec>();
		this.cWeapons = new ArrayList<CharacterWeaponRec>();
		this.db = db;
		this.json = json;
		this.errors = new ArrayList<String>();
		this.noMatch = new ArrayList<String>();
		parse();
	}

	public boolean hasError() {
		return errors.size() > 0;
	}

	public boolean hasNoMatch() {
		return noMatch.size() > 0;
	}

	public void insert() throws Exception {
		try {
			if (image != null && imageFile != null) {
				image.write(db, imageFile);
				c.Portrait = image.Row;
			}
			c.User = this.user.Row;
			c.IsImport = true;
			for (String s : noMatch) {
				c.ImportNotes += s + "\n";
			}
			c.ImportNotes = c.ImportNotes.substring(0, Math.min(2000, c.ImportNotes.length()));
			//c.ImportNotes += "Drones: in development";
			db.insert(c);
			for (CharacterAdeptPowerRec a : cAdeptPower) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterArmorRec a : cArmor) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterBiowareRec a : cBioware) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterCyberdeckRec a : cCyberdecks) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterCyberwareRec a : cCyberware) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterDroneRec a : cDrones) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterGearRec a : cGear) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterQualityRec a : cQualities) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterSkillRec a : cSkills) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterKnowledgeRec a : cKnowledge) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterSpellRec a : cSpells) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterVehicleRec a : cVehicles) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterWeaponRec a : cWeapons) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
			for (CharacterContactRec a : cContacts) {
				a.CharacterRow = c.Row;
				db.insert(a);
			}
		} catch (Exception e) {
			StringKit.println(e);
		}
	}

	public void parse() {
		JSONObject jCharacter;
		JSONArray jAdeptPowers;
		JSONArray jAttributes;
		JSONArray jArmor;
		JSONArray jContacts;
		JSONArray jCyberware;
		JSONArray jGear;
		JSONArray jQualities;
		JSONArray jSkills;
		JSONArray jSpells;
		JSONArray jVehicles;
		JSONArray jWeapons;
		try {
			jCharacter = json.getJSONObject("characters").getJSONObject("character");
		} catch (Exception e) {
			errors.add("JSON: failed to parse character json object");
			StringKit.println(e);
			return;
		}
		/** Portrait */
		try {
			String base64 = jCharacter.getString("mainmugshotbase64");
			parsePortrait(base64);
		} catch (Exception e) {
			// nvm
		}
		/** Attributes */
		try {
			jAttributes = ((JSONObject) jCharacter.getJSONArray("attributes").get(1)).getJSONArray("attribute");
			parseAttributes(jAttributes);
		} catch (Exception e) {
			errors.add("JSON: failed to parse attribute json object");
			StringKit.println(e);
			return;
		}
		/** AdeptPowers */
		try {
			jAdeptPowers = jCharacter.getJSONObject("powers").getJSONArray("power");
			parseAdeptPowers(jAdeptPowers);
		} catch (Exception e) {
			jAdeptPowers = null;
		}
		/** Qualities */
		try {
			jQualities = jCharacter.getJSONObject("qualities").getJSONArray("quality");
			parseQualities(jQualities);
		} catch (Exception e) {
			jQualities = null;
		}
		/** Skills & Knowledge*/
		try {
			jSkills = jCharacter.getJSONObject("skills").getJSONArray("skill");
			parseSkills(jSkills);
		} catch (Exception e) {
			jSkills = null;
		}
		/** Spells */
		try {
			jSpells = jCharacter.getJSONObject("spells").getJSONArray("spell");
			parseSpells(jSpells);
		} catch (Exception e) {
			jSpells = null;
		}
		/** Weapons */
		try {
			jWeapons = jCharacter.getJSONObject("weapons").getJSONArray("weapon");
			parseWeapons(jWeapons);
		} catch (Exception e) {
			jWeapons = null;
		}
		/** Armor */
		try {
			jArmor = jCharacter.getJSONObject("armors").getJSONArray("armor");
			parseArmor(jArmor);
		} catch (Exception e) {
			jArmor = null;
		}
		/** Gear & Cyberdecks */
		try {
			jGear = jCharacter.getJSONObject("gears").getJSONArray("gear");
			parseGear(jGear);
		} catch (Exception e) {
			jGear = null;
		}
		/** Cyberware */
		try {
			jCyberware = jCharacter.getJSONObject("cyberwares").getJSONArray("cyberware");
			parseCyberware(jCyberware);
		} catch (Exception e) {
			jCyberware = null;
		}
		/** Vehicles */
		try {
			jVehicles = jCharacter.getJSONObject("vehicles").getJSONArray("vehicle");
			parseVehicles(jVehicles);
		} catch (Exception e) {
			jVehicles = null;
		}

		/** Contacts */
		try {
			jContacts = jCharacter.getJSONObject("contacts").getJSONArray("contact");
			parseContacts(jContacts);
		} catch (Exception e) {
			jContacts = null;
		}
		try {
			RaceRec metatype = SrRec.selectByName(db, RaceRec.class, jCharacter.getString("metatype_english"));
			if (metatype != null) {
				c.Metatype = metatype.Row;
			}
			c.Name = jCharacter.getString("alias");
			c.Age = Coerce.toInt(jCharacter.getString("age"));
			c.LiftCarry = jCharacter.getString("liftweight") + " / " + jCharacter.getString("carryweight");
			c.Movement = c.Agility * 2 + " / " + c.Agility * 4;
			c.Karma = Coerce.toInt(jCharacter.getString("karma"));
			c.Nuyen = Coerce.toInt(jCharacter.getString("nuyen"));
			c.PhysicalLimit = Coerce.toInt(jCharacter.getString("limitphysical"));
			c.MentalLimit = Coerce.toInt(jCharacter.getString("limitmental"));
			c.SocialLimit = Coerce.toInt(jCharacter.getString("limitsocial"));
			c.Memory = Coerce.toInt(jCharacter.getString("memory"));
			c.Composure = Coerce.toInt(jCharacter.getString("composure"));
			c.JudgeIntentions = Coerce.toInt(jCharacter.getString("judgeintentions"));
			c.Initiative = Coerce.toInt(jCharacter.getString("initvalue"));
			c.InitiativeDice = Coerce.toInt(jCharacter.getString("initdice"));
			c.AstralInitiative = Coerce.toInt(jCharacter.getString("astralinitvalue"));
			c.MatrixInitiative = Coerce.toInt(jCharacter.getString("matrixarinitvalue"));
			c.PhysicalCurrent = Coerce.toInt(jCharacter.getString("physicalcm"));
			c.PhysicalMax = Coerce.toInt(jCharacter.getString("physicalcmfilled"));
			c.StunCurrent = Coerce.toInt(jCharacter.getString("stuncm"));
			c.StunMax = Coerce.toInt(jCharacter.getString("stuncmfilled"));
			c.PublicAwareness = Coerce.toInt(jCharacter.getString("publicawareness"));
			c.Notoriety = Coerce.toInt(jCharacter.getString("notoriety"));
			c.StreetCred = Coerce.toInt(jCharacter.getString("streetcred"));
		} catch (Exception e) {
			errors.add("JSON: failed to parse personal data");
			StringKit.println(e);
		}
		return;
	}

	private void parseAttributes(JSONArray attributes) {
		Map<String, Field> map = new HashMap<String, Field>();
		try {
			Class<? extends CharacterRec> cls = c.getClass();
			map.put("AGI", cls.getField("Agility"));
			map.put("BOD", cls.getField("Body"));
			map.put("CHA", cls.getField("Charisma"));
			map.put("EDG", cls.getField("Edge"));
			map.put("ESS", cls.getField("Essence"));
			map.put("INT", cls.getField("Intuition"));
			map.put("LOG", cls.getField("Logic"));
			map.put("MAG", cls.getField("Magic"));
			map.put("REA", cls.getField("Reaction"));
			map.put("RES", cls.getField("Resonance"));
			map.put("STR", cls.getField("Strength"));
			map.put("WIL", cls.getField("Willpower"));
		} catch (Exception e) {
			StringKit.println(e);
		}
		for (int i = 0; i < attributes.length(); i++) {
			try {
				JSONObject a = attributes.getJSONObject(i);
				String name = a.getString("name_english");
				Field f = map.get(name);
				if (f == null) {
					String msg = StringKit.format("Attribute: failed to match {0}", name);
					noMatch.add(msg);
					StringKit.println("Chummer Import - " + msg);
					continue;
				}
				if (name.equals("ESS")) {
					f.setFloat(c, (float) a.getDouble("base"));
				} else {
					f.setInt(c, Coerce.toInt(a.getString("base")));
				}
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}

	private void parseAdeptPowers(JSONArray adeptPowers) throws JSONException {
		if (adeptPowers == null) {
			return;
		}
		for (int i = 0; i < adeptPowers.length(); i++) {
			try {
				JSONObject a = adeptPowers.getJSONObject(i);
				String name = a.getString("name");
				AdeptPowerRec rec = AdeptPowerRec.selectByName(db, AdeptPowerRec.class, name);
				if (rec == null) {
					String msg = StringKit.format("AdeptPower: failed to match {0}", name);
					noMatch.add(msg);
					StringKit.println("Chummer Import - " + msg);
					continue;
				}
				CharacterAdeptPowerRec cs = new CharacterAdeptPowerRec();
				cs.AdeptPowerRow = rec.Row;
				cs.Level = Math.max(1, Integer.parseInt(a.getString("rating")));
				cAdeptPower.add(cs);
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}
	
	private void parseArmor(JSONArray armor) throws JSONException {
		if (armor == null) {
			return;
		}
		for (int i = 0; i < armor.length(); i++) {
			try {
				JSONObject a = armor.getJSONObject(i);
				String name = a.getString("name_english");
				ArmorRec rec = ArmorRec.selectByName(db, ArmorRec.class, name);
				if (rec == null) {
					String msg = StringKit.format("Armor: failed to match {0}", name);
					noMatch.add(msg);
					StringKit.println("Chummer Import - " + msg);
					continue;
				}
				CharacterArmorRec cs = new CharacterArmorRec();
				cs.ArmorRow = rec.Row;
				cArmor.add(cs);
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}
	private void parseContacts(JSONArray contacts) throws JSONException {
		if (contacts == null) {
			return;
		}
		for (int i = 0; i < contacts.length(); i++) {
			try {
				JSONObject a = contacts.getJSONObject(i);
				String name = a.getString("name");
				String role = a.getString("role");
				int connection = Integer.parseInt(a.getString("connection"));
				int loyalty = Integer.parseInt(a.getString("loyalty"));
				RaceRec metatype = SrRec.selectByName(db, RaceRec.class, a.getString("metatype"));
				CharacterContactRec cs = new CharacterContactRec();
				cs.Name = name;
				cs.Archetype = role;
				cs.Connection = connection;
				cs.Loyalty = loyalty;
				if(metatype!=null)
				{
					cs.MetaType = metatype.Row;
				}
				cContacts.add(cs);
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}
	private void parseGear(JSONArray gear) throws JSONException {
		if (gear == null) {
			return;
		}
		for (int i = 0; i < gear.length(); i++) {
			try {
				JSONObject a = gear.getJSONObject(i);
				String category = a.getString("category_english");
				String name = a.getString("name_english");
				switch (category) {
				case "Cyberdecks":
					CyberdeckRec cyberdeck = SrRec.selectByName(db, CyberdeckRec.class, name);
					if (cyberdeck == null) {
						String msg = StringKit.format("Cyberdeck: failed to match {0}", name);
						noMatch.add(msg);
						StringKit.println("Chummer Import - " + msg);
						continue;
					}
					CharacterCyberdeckRec cc = new CharacterCyberdeckRec();
					cc.CyberdeckRow = cyberdeck.Row;
					cc.Quantity = Integer.parseInt(a.getString("qty"));
					cCyberdecks.add(cc);
					break;
				default:
					GearRec rec = SrRec.selectByName(db, GearRec.class, name);
					if (rec == null) {
						String msg = StringKit.format("Gear: failed to match {0}", name);
						noMatch.add(msg);
						StringKit.println("Chummer Import - " + msg);
						continue;
					}
					CharacterGearRec cs = new CharacterGearRec();
					cs.GearRow = rec.Row;
					cs.Quantity = Integer.parseInt(a.getString("qty"));
					cs.Rating = Integer.parseInt(a.getString("rating"));
					cGear.add(cs);
					break;
				}
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}

	private void parseCyberware(JSONArray cyberware) throws JSONException {
		if (cyberware == null) {
			return;
		}
		for (int i = 0; i < cyberware.length(); i++) {
			try {
				JSONObject a = cyberware.getJSONObject(i);
				String name = a.getString("name");
				String type = a.getString("improvementsource");
				String location = a.getString("location");
				CyberwareRec rec = CyberwareRec.selectByName(db, CyberwareRec.class, name);
				if (rec == null) {
					if(location.indexOf("null")==-1)
					{
						int leftBracketIndex = name.indexOf("(");
						int rightBracketIndex = name.indexOf(")");
						if(leftBracketIndex>-1)
						{
							String extra = name.substring(leftBracketIndex,rightBracketIndex);
							name = name.substring(0,leftBracketIndex) + location;
							rec = CyberwareRec.selectByName(db, CyberwareRec.class, name);
							if (rec == null) {
								String msg = StringKit.format(type + ": failed to match {0}", name);
								noMatch.add(msg);
								StringKit.println("Chummer Import - " + msg);
								continue;
							}
						}
					}
					else
					{
						String msg = StringKit.format(type + ": failed to match {0}", name);
						noMatch.add(msg);
						StringKit.println("Chummer Import - " + msg);
						continue;
					}
				}
				if(type.indexOf("Cyberware")>-1)
				{
					CharacterCyberwareRec cs = new CharacterCyberwareRec();
					cs.CyberwareRow = rec.Row;
					cs.Rating = Coerce.toInt(a.getString("rating"));
					cs.Grade = a.getString("grade");
					cCyberware.add(cs);
				}
				else if(type.indexOf("Bioware")>-1)
				{
					CharacterBiowareRec cs = new CharacterBiowareRec();
					cs.BiowareRow = rec.Row;
					cs.Rating = Coerce.toInt(a.getString("rating"));
					cs.Grade = a.getString("grade");
					cBioware.add(cs);
				}
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}

	private void parsePortrait(String base64Image) throws Exception {
		if (base64Image == null || base64Image.isEmpty()) {
			return;
		}
		image = new ImageRec();
		image.Type = ImageType.Face;
		image.User = user.Row;
		image.Extension = "png";
		try {
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			imageFile = FileKit.createTemp("temp", image.Extension, new File(App.getRealUploadDir()));
			imageFile.deleteOnExit();
			ImageIO.write(img, image.Extension, imageFile);
		} catch (Exception e) {
			errors.add("JSON: failed to parse portrait");
			StringKit.println(e);
		}

	}

	private void parseQualities(JSONArray qualities) throws JSONException {
		if (qualities == null) {
			return;
		}
		for (int i = 0; i < qualities.length(); i++) {
			try {
				JSONObject a = qualities.getJSONObject(i);
				String name = a.getString("name_english");
				QualityRec rec = QualityRec.selectByName(db, QualityRec.class, name);
				if (rec == null) {
					String msg = StringKit.format("Quality: failed to match {0}", name);
					noMatch.add(msg);
					StringKit.println("Chummer Import - " + msg);
					continue;
				}
				CharacterQualityRec cs = new CharacterQualityRec();
				cs.QualityRow = rec.Row;
				cQualities.add(cs);
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}

	private void parseSkills(JSONArray skills) throws JSONException {
		if (skills == null) {
			return;
		}
		for (int i = 0; i < skills.length(); i++) {
			try {
				JSONObject a = skills.getJSONObject(i);
				String name = a.getString("name");
				boolean isKnowledge = Boolean.parseBoolean(a.getString("knowledge"));
				if(isKnowledge)
				{
					CharacterKnowledgeRec krec = new CharacterKnowledgeRec();
					krec.Name = name;
					krec.Rating = Coerce.toInt(a.getString("rating"));
					krec.Type = CharacterKnowledgeRec.KnowledgeType.lookup(a.getString("skillcategory"));
					if(krec.Type.equals(CharacterKnowledgeRec.KnowledgeType.Language) && krec.Rating==0)
					{
						krec.Native=true;
					}
					cKnowledge.add(krec);
					continue;
				}
				SkillRec rec = SkillRec.selectByName(db, SkillRec.class, name);
				if (rec == null) {
					String msg = StringKit.format("Skill: failed to match {0}", name);
					noMatch.add(msg);
					StringKit.println("Chummer Import - " + msg);
					continue;
				}
				CharacterSkillRec cs = new CharacterSkillRec();
				cs.SkillRow = rec.Row;
				cs.Rating = Coerce.toInt(a.getString("rating"));
				if (cs.Rating == 0) {
					continue;
				}
				// cs.Special = a.getString("skillspecializations");
				cSkills.add(cs);
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}

	private void parseSpells(JSONArray spells) throws JSONException {
		if (spells == null) {
			return;
		}
		for (int i = 0; i < spells.length(); i++) {
			try {
				JSONObject a = spells.getJSONObject(i);
				String name = a.getString("name");
				SpellRec rec = SpellRec.selectByName(db, SpellRec.class, name);
				if (rec == null) {
					String msg = StringKit.format("Spell: failed to match {0}", name);
					noMatch.add(msg);
					StringKit.println("Chummer Import - " + msg);
					continue;
				}
				CharacterSpellRec cs = new CharacterSpellRec();
				cs.SpellRow = rec.Row;
				cSpells.add(cs);
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}

	private void parseVehicles(JSONArray vehicles) throws JSONException {
		if (vehicles == null) {
			return;
		}
		for (int i = 0; i < vehicles.length(); i++) {
			try {
				JSONObject a = vehicles.getJSONObject(i);
				String category = a.getString("category_english");
				String name = a.getString("name_english");
				if (category.indexOf("Drone") > -1) {
					DroneRec drone = SrRec.selectByName(db, DroneRec.class, name);
					if (drone == null) {
						String msg = StringKit.format("Drone: failed to match {0}", name);
						noMatch.add(msg);
						StringKit.println("Chummer Import - " + msg);
						continue;
					}
					CharacterDroneRec cd = new CharacterDroneRec();
					cd.DroneRow = drone.Row;
					cDrones.add(cd);
				} else {
					VehicleRec vehicle = VehicleRec.selectByName(db, VehicleRec.class, name);
					if (vehicle == null) {
						String msg = StringKit.format("Vehicle: failed to match {0}", name);
						noMatch.add(msg);
						StringKit.println("Chummer Import - " + msg);
						continue;
					}
					CharacterVehicleRec cv = new CharacterVehicleRec();
					cv.VehicleRow = vehicle.Row;
					cVehicles.add(cv);
				}
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}

	private void parseWeapons(JSONArray weapons) throws JSONException {
		if (weapons == null) {
			return;
		}
		for (int i = 0; i < weapons.length(); i++) {
			try {
				JSONObject a = weapons.getJSONObject(i);
				String name = a.getString("name_english");
				WeaponRec rec = WeaponRec.selectByName(db, WeaponRec.class, name);
				if (rec == null) {
					String msg = StringKit.format("Weapon: failed to match {0}", name);
					noMatch.add(msg);
					StringKit.println("Chummer Import - " + msg);
					continue;
				}
				CharacterWeaponRec cs = new CharacterWeaponRec();
				cs.WeaponRow = rec.Row;
				cWeapons.add(cs);
			} catch (Exception e) {
				StringKit.println(e);
			}
		}
	}
}

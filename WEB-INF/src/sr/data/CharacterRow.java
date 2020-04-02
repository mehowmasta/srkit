package sr.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.data.BaseRow;
import ir.data.Database;
import ir.data.IRow;
import ir.util.StringKit;

public class CharacterRow
{
	public static class BonusRow extends BaseRow{
		public String Attribute = "";
		public int ItemRow = 0;
		public String Name = "";
		public int Row = 0;
		public String Table = "";
		public int Value = 0;
	}
	public static class SkillRow extends BaseRow{
		public String Skill = "";
		public int ItemRow = 0;
		public String Name = "";
		public int Row = 0;
		public String Table = "";
		public int Value = 0;
	}
	public CharacterRec Character = new CharacterRec();
	public Map<String,ArrayList<BonusRow>> Bonus = new HashMap<String,ArrayList<BonusRow>>();
	public List<AdeptPowerRec> AdeptPowers = new ArrayList<AdeptPowerRec>();
	public List<ArmorRec> Armor = new ArrayList<ArmorRec>();
	public List<CyberwareRec> Bioware = new ArrayList<CyberwareRec>();
	public List<CharacterContactRec> Contacts = new ArrayList<CharacterContactRec>();
	public List<CyberdeckRec> Cyberdecks = new ArrayList<CyberdeckRec>();
	public List<CyberwareRec> Cyberware = new ArrayList<CyberwareRec>();
	public List<CyberwareRec> CyberwareAttachment = new ArrayList<CyberwareRec>();
	public List<DroneRec> Drones = new ArrayList<DroneRec>();
	public List<GearRec> Gear = new ArrayList<GearRec>();
	public List<CharacterKnowledgeRec> Knowledge = new ArrayList<CharacterKnowledgeRec>();
	public RaceRec Metatype = new RaceRec();
	public ImageRec Portrait = new ImageRec();
	public List<ProgramRec> Programs = new ArrayList<ProgramRec>();
	public List<QualityRec> Qualities = new ArrayList<QualityRec>();
	public Map<String,ArrayList<SkillRow>> Skill = new HashMap<String,ArrayList<SkillRow>>();
	public List<SkillRec> Skills = new ArrayList<SkillRec>();
	public List<SpellRec> Spells = new ArrayList<SpellRec>();
	public List<VehicleRec> Vehicles = new ArrayList<VehicleRec>();
	public List<WeaponRec> Weapons = new ArrayList<WeaponRec>();
	public List<WeaponModifierRec> WeaponModifiers = new ArrayList<WeaponModifierRec>();
	//
	@SuppressWarnings("unchecked")
	public static CharacterRow selectCharacter(Database db, int characterRow) throws Exception
	{
		CharacterRow r = new CharacterRow();
		List<List<IRow>> result = db.selectLists("call select_character(?)", 
				Arrays.asList(CharacterRec.class,
						AdeptPowerRec.class,
						ArmorRec.class,
						CyberwareRec.class,
						CharacterContactRec.class,
						CyberdeckRec.class,
						CyberwareRec.class,
						CyberwareRec.class,//this one is for cyberware attachments
						DroneRec.class,
						GearRec.class,
						CharacterKnowledgeRec.class,
						RaceRec.class,
						ImageRec.class,
						ProgramRec.class,
						QualityRec.class,
						SkillRec.class,
						SpellRec.class,
						VehicleRec.class,
						WeaponRec.class,
						WeaponModifierRec.class), 
				characterRow);
		int set =0;
		r.Character = (CharacterRec) result.get(set++).get(0);
        r.AdeptPowers = new ArrayList<AdeptPowerRec>((Collection<? extends AdeptPowerRec>) result.get(set++));
        r.Armor = new ArrayList<ArmorRec>((Collection<? extends ArmorRec>) result.get(set++));
        r.Bioware = new ArrayList<CyberwareRec>((Collection<? extends CyberwareRec>) result.get(set++));
        r.Contacts = new ArrayList<CharacterContactRec>((Collection<? extends CharacterContactRec>) result.get(set++));
        r.Cyberdecks = new ArrayList<CyberdeckRec>((Collection<? extends CyberdeckRec>) result.get(set++));
        r.Cyberware = new ArrayList<CyberwareRec>((Collection<? extends CyberwareRec>) result.get(set++));
        r.CyberwareAttachment = new ArrayList<CyberwareRec>((Collection<? extends CyberwareRec>) result.get(set++));
        r.Drones = new ArrayList<DroneRec>((Collection<? extends DroneRec>) result.get(set++));
        r.Gear = new ArrayList<GearRec>((Collection<? extends GearRec>) result.get(set++));
        r.Knowledge = new ArrayList<CharacterKnowledgeRec>((Collection<? extends CharacterKnowledgeRec>) result.get(set++));
		r.Metatype = (RaceRec) result.get(set++).get(0);
		try {
			r.Portrait = (ImageRec) result.get(set++).get(0);
		}
		catch(Exception e)
		{
			r.Portrait = new ImageRec();
		}
        r.Programs = new ArrayList<ProgramRec>((Collection<? extends ProgramRec>) result.get(set++));
        r.Qualities = new ArrayList<QualityRec>((Collection<? extends QualityRec>) result.get(set++));
        r.Skills = new ArrayList<SkillRec>((Collection<? extends SkillRec>) result.get(set++));
        r.Spells = new ArrayList<SpellRec>((Collection<? extends SpellRec>) result.get(set++));
        r.Vehicles = new ArrayList<VehicleRec>((Collection<? extends VehicleRec>) result.get(set++));
        r.Weapons = new ArrayList<WeaponRec>((Collection<? extends WeaponRec>) result.get(set++));
        r.WeaponModifiers = new ArrayList<WeaponModifierRec>((Collection<? extends WeaponModifierRec>) result.get(set++));
        calculateBonus(r);
        calculateSkill(r);
        return r;
	}
	public static void calculateBonus(CharacterRow c) throws Exception
	{
		for(CyberwareRec a : c.Cyberware)
		{
			if(!a.Bonus.isEmpty())
			{
				processBonus(c,a,a.Bonus,"");				
			}
		}
		for(CyberwareRec a : c.CyberwareAttachment)
		{
			if(!a.Bonus.isEmpty())
			{
				processBonus(c,a,a.Bonus,"tCyberwareAttachment");				
			}
		}
		for(QualityRec a : c.Qualities)
		{
			if(!a.Bonus.isEmpty())
			{
				processBonus(c,a,a.Bonus,"");				
			}
		}
		for(CyberwareRec a : c.Bioware)
		{
			if(!a.Bonus.isEmpty())
			{
				processBonus(c,a,a.Bonus,"tBioware");				
			}
		}
	}
	public static void calculateSkill(CharacterRow c) throws Exception
	{
		for(CyberwareRec a : c.Cyberware)
		{
			if(!a.Skill.isEmpty())
			{
				processSkill(c,a,a.Skill,"");				
			}
		}
		for(CyberwareRec a : c.CyberwareAttachment)
		{
			if(!a.Skill.isEmpty())
			{
				processSkill(c,a,a.Skill,"tCyberwareAttachment");				
			}
		}
		for(QualityRec a : c.Qualities)
		{
			if(!a.Skill.isEmpty())
			{
				processSkill(c,a,a.Skill,"");				
			}
		}
		for(CyberwareRec a : c.Bioware)
		{
			if(!a.Skill.isEmpty())
			{
				processSkill(c,a,a.Skill,"tBioware");				
			}
		}
	}
	public static void processBonus(CharacterRow c , SrRec a, String bonusText, String tableName) throws Exception
	{
		int rating = a.getTemp("Rating",Integer.class,0);
		String[] bonuses = bonusText.split(",");
		for(String b : bonuses)
		{
			String[] parts = b.split(":");
			String attribute = parts[0];
			String value = parts[1];
			BonusRow thisBonus = new BonusRow();
			thisBonus.Row = a.Row;
			thisBonus.ItemRow = a.getTemp("ItemRow", Integer.class,0);
			thisBonus.Table = tableName.isEmpty()?a.getTable():tableName;
			thisBonus.Attribute = attribute;
			thisBonus.Value = value.indexOf("Rating")>-1?rating:Integer.parseInt(value); 
			thisBonus.Name = a.Name;
			ArrayList<BonusRow> bonusList = null;
			if(c.Bonus.containsKey(attribute))
			{
				bonusList = c.Bonus.get(attribute);
				bonusList.add(thisBonus);
			}
			else
			{
				bonusList = new ArrayList<BonusRow>();
				bonusList.add(thisBonus);
				c.Bonus.put(attribute,bonusList);
			}
		}
	}
	public static void processSkill(CharacterRow c , SrRec a, String skillText, String tableName) throws Exception
	{
		int rating = a.getTemp("Rating",Integer.class,0);
		String[] skills = skillText.split(",");
		for(String s : skills)
		{
			String[] parts = s.split(":");
			String skillName = parts[0].replaceAll("_", " ");
			String value = parts[1];
			SkillRow thisSkill = new SkillRow();
			thisSkill.Row = a.Row;
			thisSkill.ItemRow = a.getTemp("ItemRow", Integer.class,0);
			thisSkill.Table = tableName.isEmpty()?a.getTable():tableName;
			thisSkill.Skill = skillName;
			thisSkill.Value = value.indexOf("Rating")>-1?rating:Integer.parseInt(value); 
			thisSkill.Name = a.Name;
			ArrayList<SkillRow> skillList = null;
			if(c.Skill.containsKey(skillName))
			{
				skillList = c.Skill.get(skillName.replaceAll(" ",""));
				skillList.add(thisSkill);
			}
			else
			{
				skillList = new ArrayList<SkillRow>();
				skillList.add(thisSkill);
				c.Skill.put(skillName.replaceAll(" ",""),skillList);
			}
		}
	}
	public String bonusToJson(Map<String,ArrayList<BonusRow>> bonus)
	{
		StringBuilder b = new StringBuilder("{");
		String comma = "";
		for(String k :bonus.keySet())
		{
			b.append(comma).append(k).append(": new KeyedArray('ItemRow',");
			ArrayList<BonusRow> list = bonus.get(k);
			b.append(list.toString())
			.append(")");
			comma = ",";
		}
		return b.append("}").toString();
	}
	public String skillToJson(Map<String,ArrayList<SkillRow>> skills)
	{
		StringBuilder b = new StringBuilder("{");
		String comma = "";
		for(String k :skills.keySet())
		{
			b.append(comma).append(k).append(": new KeyedArray('ItemRow',");
			ArrayList<SkillRow> list = skills.get(k);
			b.append(list.toString())
			.append(")");
			comma = ",";
		}
		return b.append("}").toString();
	}
	public String toJson()
	{
		StringBuilder b = new StringBuilder();
		b.append(Character.toString()).deleteCharAt(b.length() - 1)
		 .append(",").append(StringKit.jsq("AdeptPower")).append(":").append("new KeyedArray('ItemRow',"+AdeptPowers.toString()+")")
		 .append(",").append(StringKit.jsq("Armor")).append(":").append("new KeyedArray('ItemRow',"+Armor.toString()+")")
		 .append(",").append(StringKit.jsq("Bioware")).append(":").append("new KeyedArray('ItemRow',"+Bioware.toString()+")")
		 .append(",").append(StringKit.jsq("Bonus")).append(":").append(bonusToJson(Bonus))
		 .append(",").append(StringKit.jsq("Contact")).append(":").append("new KeyedArray('Row',"+Contacts.toString()+")")
		 .append(",").append(StringKit.jsq("Cyberdeck")).append(":").append("new KeyedArray('ItemRow',"+Cyberdecks.toString()+")")
		 .append(",").append(StringKit.jsq("Program")).append(":").append("new KeyedArray('ItemRow',"+Programs.toString()+")")
		 .append(",").append(StringKit.jsq("Cyberware")).append(":").append("new KeyedArray('ItemRow',"+Cyberware.toString()+")")
		 .append(",").append(StringKit.jsq("CyberwareAttachment")).append(":").append("new KeyedArray('ItemRow',"+CyberwareAttachment.toString()+")")
		 .append(",").append(StringKit.jsq("Drone")).append(":").append("new KeyedArray('ItemRow',"+Drones.toString()+")")
		 .append(",").append(StringKit.jsq("Gear")).append(":").append("new KeyedArray('ItemRow',"+Gear.toString()+")")
		 .append(",").append(StringKit.jsq("Knowledge")).append(":").append("new KeyedArray('ItemRow',"+Knowledge.toString()+")")
		 .append(",").append(StringKit.jsq("Metatype")).append(":").append(Metatype.toString())
		 .append(",").append(StringKit.jsq("Portrait")).append(":").append(Portrait.toString())
		 .append(",").append(StringKit.jsq("Quality")).append(":").append("new KeyedArray('ItemRow',"+Qualities.toString()+")")
		 .append(",").append(StringKit.jsq("Skill")).append(":").append("new KeyedArray('Row',"+Skills.toString()+")")
		 .append(",").append(StringKit.jsq("SkillBonus")).append(":").append(skillToJson(Skill))
		 .append(",").append(StringKit.jsq("Spell")).append(":").append("new KeyedArray('ItemRow',"+Spells.toString()+")")
		 .append(",").append(StringKit.jsq("Vehicle")).append(":").append("new KeyedArray('ItemRow',"+Vehicles.toString()+")")
		 .append(",").append(StringKit.jsq("Weapon")).append(":").append("new KeyedArray('ItemRow',"+Weapons.toString()+")")
		 .append(",").append(StringKit.jsq("WeaponModifier")).append(":").append("new KeyedArray('ItemRow',"+WeaponModifiers.toString()+")")
		 .append("}");
		return b.toString();
	}
}
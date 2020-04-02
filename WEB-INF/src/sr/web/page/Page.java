package sr.web.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ir.util.StringKit;
import sr.web.Images;
/**
 * Describes pages in the application and provides menu structure and related authorities.
 * Names here are mapped to jsps by name().toLowerCase().jsp.
 */
public enum Page
{
  Index("Index",Images.Password),
  Error("Error",Images.Dead),
  AdeptPowerList("Adept Power List",Images.AdeptPower),
  AmmoList("Ammo",Images.Ammo),
  ArmorList("Armor",Images.Armor),
  BiowareList("Bioware",Images.Bioware),
  Budget("Budget",Images.Report),
  ChangePassword("Change Password",Images.Learning),
  CharacterCreation("Create Runner",Images.Learning),
  CharacterDetail("Character Detail",Images.Learning),
  CharacterList("Runners",Images.Learning),
  CritterPowerList("Critter Powers",Images.Spell),
  CyberdeckList("Cyberdecks",Images.Cyberdeck),
  CyberwareList("Cyberware",Images.Cyberware),
  Database("Database",Images.Database){
	    @Override
	    public Page[] getChildren() {
	      return new Page[]{Metatype,Qualities,SkillList,Magic,Matrix,Gear};
	    }
	  },
  DemolitionList("Demolition",Images.Explosives),
  DroneList("Drones",Images.Rigging),
  ElectronicList("Electronics",Images.Electronics),
  Gear("Gear",Images.Gear){
	    @Override
	    public Page[] getChildren() {
	      return new Page[]{WeaponList,ArmorList,CyberwareList,BiowareList,CyberdeckList,DroneList,VehicleList,AmmoList,WeaponModifierList,ElectronicList,SecurityList,DemolitionList,MedicalList,MiscList};
	    }
	  },
  GmBoard("GM Board",Images.GmBoard),
  GroupDetail("Team Detail",Images.Group),
  GroupList("Teams",Images.Group),
  Home("Home",Images.Home){
	    @Override
	    public Page[] getChildren() {
	      return new Page[]{CharacterList,GroupList,SINRegistryList,Database,GmBoard,MapList,PortraitList,Budget,UserList,Preferences};
	    }
	  },
  Magic("Magic",Images.Spell),
  MapDetail("Map Detail",Images.Map),
  MapList("Maps",Images.Map),
  Matrix("Matrix",Images.Matrix),
  MatrixActions("Matrix Actions",Images.MatrixActions),
  MedicalList("Medical",Images.Medical),
  Metatype("Metatype",Images.Race),
  MentorSpiritList("Mentor Spirits",Images.Spell),
  MiscList("Misc",Images.Misc),
  NegativeQualitiesList("Negative Qualities List",Images.Quality),
  PortraitList("Portraits",Images.Portrait),
  PositiveQualitiesList("Positive Qualities List",Images.Quality),
  Preferences("Preferences",Images.Preferences),
  Programs("Programs",Images.Matrix),
  Qualities("Qualities",Images.Quality),
  Rigging("Rigging",Images.Rigging),
  SecurityList("Security",Images.Security),
  SINRegistryList("SIN Registry",Images.FingerPrint),
  SpellList("Spells",Images.Spell),
  SkillList("Skills",Images.Skill),
  Skills("Skills",Images.Skill),
  UserList("User List",Images.Users){
	    @Override
	    public boolean allowGuest() {
	      return false;
	    }
	  },
  VehicleList("Vehicles",Images.Vehicle),
  WeaponList("Weapons",Images.Weapon),
  WeaponModifierList("Weapon Mods",Images.WeaponMod),
  Upload("Upload",Images.Upload)
  ;
  private static final Map<String, Page> jspMap = new HashMap<String,Page>();
  private static final Map<String, Page> nameMap = new HashMap<String,Page>();
  private static final Page[] noChildren=new Page[0];
  static
  {
    for (Page p : values())
    {
      jspMap.put(p.jsp().toLowerCase(),p);
      nameMap.put(p.name(),p);
    }
  }
  public final String titleKey;
  public final String image;
  public static Page lookupName(String name)
  {
    return nameMap.get(name);
  }
  public static Page lookupUrl(String url)
  {
    String key = StringKit.getFileName(url).toLowerCase();
    return jspMap.get(key);
  }
  private Page(String titleKey,String img)
  {
    this.titleKey = titleKey;
    this.image = img;
  }
	public boolean allowGuest()
	{
		return true;
	}
  
  public Page[] getChildren() {
    return noChildren;
  }
  public List<Page> getNonNullChildren() {
    List<Page>list = new ArrayList<Page>(Arrays.asList(getChildren()));
    for (int i=list.size()-1;i>=0;i--)
    {
      if (list.get(i)==null)
      {
        list.remove(i);
      }
    }
    return list;
  }
  public String[] getOtherMethods() {
    return new String[0];
  }
  public String jsp()
  {
    return name().toLowerCase() + ".jsp";
  }
  public boolean sysAdminOnly()
  {
    return false;
  }
  public String title() throws Exception
  {
    return titleKey;
  }
  public String toJson() throws Exception
  {
    return new StringBuilder()
        .append("{\"name\":").append(StringKit.jsq(name()))
        .append(",\"title\":").append(StringKit.jsq(titleKey))
        .append(",\"url\":").append(jsp())
        .append("}").toString();
  }
  @Override
  public String toString()
  {
    return jsp();
  }
}

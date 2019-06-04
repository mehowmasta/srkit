package sr.web;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ir.util.StringKit;

/**
 * Constant bundle of image file names
 */
public abstract class Images
{
  protected static String _IconPath = App.getIconDir() + "/white/";
  public static String Acid = "acid.svg";
  public static String AdeptPower = "vision.svg";
  public static String Aim = "aim.svg";
  public static String Ammo = "ammo.svg";
  public static String Armor = "bathrobe.svg";
  public static String Attribute = "muscle.svg";
  public static String Back = "racing.svg";
  public static String Bioware = "molecular-1.svg";
  public static String Book = "book.svg";
  public static String Brain = "brain.svg";
  public static String Bug = "bug.svg";
  public static String Calendar = "calendar.svg";
  public static String Cancel = "cancel.svg";
  public static String Checkmark = "checkmark.svg";
  public static String Clear = "bulldozer.svg";
  public static String Clock = "clock.svg";
  public static String Comm = "console.svg";
  public static String Cover = "firewall.svg";
  public static String Cyberdeck = "laptop.svg";
  public static String Cyberware = "elearning.svg";
  public static String Database = "database.svg";
  public static String Dead = "dead.svg";
  public static String Device = "networking (3).svg";
  public static String Dice = "dice.svg";
  public static String Disoriented = "disoriented.svg";
  public static String Electronics = "circuit.svg";
  public static String Explosives = "blast.svg";
  public static String FingerPrint = "fingerprints.svg";
  public static String Fire = "fire.svg";
  public static String Gear = "school-bag.svg";
  public static String GmBoard = "blogger.svg";
  public static String Group = "leadership.svg";
  public static String Info =  "faq.svg";
  public static String Home = "house.svg";
  public static String Learning =  "learning-1.svg";
  public static String License =  "gpl3.svg";
  public static String Lift =  "lift.svg";
  public static String Magic = "magic.svg";
  public static String Map = "map.svg";
  public static String Mark = "cloud-network.svg";
  public static String Matrix =  "cloud-network.svg";
  public static String MatrixActions = "network.svg";
  public static String Medical = "syringe.svg";
  public static String Message = "messsage.svg";
  public static String Misc = "smartwatch.svg";
  public static String Notebook = "notebook.svg";
  public static String Overwatch = "satellite.svg";
  public static String Password = "passport.svg";
  public static String Physical = "stethoscope.svg";
  public static String Player = "warrior.svg";
  public static String Poison = "poison.svg";
  public static String Portrait = "photo.svg";
  public static String Preferences = "support (2).svg";
  public static String Prone = "prone.svg";
  public static String Quality = "rating.svg";
  public static String Rating = "review.svg";
  public static String Recoil = "recoil.svg";
  public static String Rigging = "mars-rover.svg";
  public static String Security = "cctv.svg";
  public static String Search = "footprints.svg";
  public static String Shield = "shield.svg";
  public static String Skill = "exercise.svg";
  public static String Snowflake = "snowflake.svg";
  public static String Sort = "synchronisation.svg";
  public static String Spell = "meteor.svg";
  public static String Status = "customer.svg";
  public static String Stomach = "stomach.svg";
  public static String Stun = "stethoscope.svg";
  public static String Surprise = "fireworks.svg";
  public static String Subdue = "handcuffs.svg";
  public static String Race = "genetic.svg";
  public static String Thunder = "thunder.svg";
  public static String Top = "spaceship.svg";
  public static String Trash = "trash.svg";
  public static String Users = "teamwork.svg";
  public static String Weapon = "shooting.svg";
  public static String Vehicle = "jeep.svg";
  public static String Upload = "upload.svg";
  
  public static void check() throws Exception
  {
    Set<String>fieldNames = new HashSet<String>();
    Set<String>fileNames = new HashSet<String>();
    Field[] fields = Images.class.getFields();
    for (Field field : fields)
    {
      int fieldMod = field.getModifiers();
      if (Modifier.isStatic(fieldMod) && Modifier.isPublic(fieldMod) && field.getType().equals(String.class))
      {
        String fieldName = field.getName();
        String value = (String)field.get(null);
        java.io.File file = new java.io.File(App.getRealPath(value));
        if (!file.exists())
        {
          StringKit.println("Images." + fieldName + " value '" + value + "' file does not exist.");
        }
        fileNames.add(file.getName().toLowerCase());
      }
    }
    String realDir = App.getRealImageDir();
    String[] files = new File(realDir).list();
    List<String>constantOutput=new ArrayList<String>();
    Set<String>newFileNames = new HashSet<String>();
    for (int i = 0; files != null && i < files.length; i++)
    {
      String fn = files[i].toLowerCase();
      if (! StringKit.getExtension(fn).equals("svg"))
      {
        continue;
      }
      if (fileNames.contains(fn))
      {
        continue;
      }
      if (! newFileNames.add(fn))
      {
        StringKit.println("Images: multiple files with case-insensitive name '" + fn + "'");
        continue;
      }
      String fieldName = StringKit.chop(fn, 4);
      while(fieldName.startsWith("_"))
      {
        fieldName = fieldName.substring(1);
      }
      fieldName = StringKit.camelCaseJavaName(fieldName);
      if (! fieldNames.add(fieldName))
      {
        StringKit.println("Images: no constant generated for " + fn + ": field name already used.");
      }
      else
      {
        constantOutput.add("public static String " + fieldName + " = _ImagePath + \"" + fn + "\";");
      }
    }
    if (constantOutput.size()>0)
    {
      StringKit.println("*** svg files in images folders without constants:");
      for (String v : constantOutput)
      {
        System.out.println(v);
      }
    }
  }
  //
  public static String get(String fileName)
  {
    return _IconPath + fileName;
  }
  public static String getIconPath()
  {
	  return _IconPath;
  }
  public static void setIconPath(String iconFolder)
  {
	  _IconPath = App.getIconDir() + "/" + iconFolder + "/";
  }
}



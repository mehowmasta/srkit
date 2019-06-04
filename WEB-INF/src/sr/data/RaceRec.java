package sr.data;

import java.util.List;

import ir.data.Database;

/**
 * UserRec is a data structure of user information
 */
public class RaceRec extends SrRec {
	public static final String ADDITIONALKARMA = "AdditionalKarma";
	public static final String AGILITY = "Agility";
	public static final String BODY = "Body";
	public static final String CHARISMA = "Charisma";	
	public static final String EDGE = "Edge";
	public static final String ESSENCE = "Essence";
	public static final String INITIATIVE = "Initiative";
	public static final String INTUITION = "Intuition";
	public static final String LOGIC = "Logic";
	public static final String MAGIC = "Magic";
	public static final String METAVARIANT = "Metavariant";
	public final static String REACTION = "Reaction";
	public final static String RESONANCE = "Resonance";
	public final static String SHAPESHIFTER = "ShapeShifter";
	public final static String STRENGTH = "Strength";
	public final static String TABLE = "tRace";
	public final static String TRAITS = "Traits";
	public static final String WILLPOWER = "Willpower";
	//
	public int AdditionalKarma = 0;
	public String Agility = "";
	public String Body = "";
	public String Charisma = "";
	public String Edge = "";
	public String Essence = "";
	public String Initiative = "";
	public String Intuition = "";
	public String Logic = "";
	public String Magic = "";
	public String Metavariant = "";
	public String Reaction = "";
	public String Resonance = "";
	public boolean ShapeShifter;
	public String Strength = "";
	public String Traits = "";
	public String Willpower = "";
	//
	public static List<RaceRec> selectAll(Database db) throws Exception
	{
		return db.selectList("RaceRec.selectAll", RaceRec.class);
	}
	public RaceRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}

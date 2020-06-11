package sr.data;

import ir.data.Database;

/** rating system for characters shared with other users, users can vote on a character 1-5 */
public class CharacterRatingRec extends AppRec
{
	public static final String CHARACTER = "Character";
	public static final String RATING = "Rating";
	public static final String TABLE = "tcharacterrating";
	public static final String USER = "User";
	//
	public int Character = 0;
	public int Rating = 0;
	public int User = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	public static int selectForCharacter(Database db,int characterRow) throws Exception
	{
		return db.selectScalar("CharacterRatingRec.selectForCharacter", 0, characterRow);
	}
}
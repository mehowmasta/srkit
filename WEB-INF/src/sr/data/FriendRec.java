package sr.data;

import java.util.List;

import ir.data.Database;

public class FriendRec extends AppRec
{
	public static final String CONFIRMED = "Confirmed";
	public static final String FRIEND = "Friend";
	public static final String ROW = "Row";
	public static final String TABLE = "tFriend";
	public static final String USER = "User";
	//
	public boolean Confirmed = false;
	public int Friend=0;
	public int Row=0;
	public int User = 0;
	@Override
	public String getTable() {
		return TABLE;
	}
	//
	public static List<FriendRec> selectFriendship(Database db, int user, int friend) throws Exception
	{
		return db.selectList("FriendRec.selectFriendship",FriendRec.class,user,friend,friend,user);
	}
}
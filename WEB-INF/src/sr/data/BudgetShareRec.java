package sr.data;

import java.util.List;

import ir.data.Database;

public class BudgetShareRec extends AppRec {

	public static final String TABLE = "tbudgetshare";
	public static final String SHAREE = "Sharee";
	public static final String SHARER = "Sharer";
	
	public int Sharee = 0;
	public int Sharer = 0;
	//
	public static void addShare(Database db, int user1, int user2) throws Exception
	{
		db.execute("BudgetShareRec.addShare", BudgetShareRec.class, user1,user2);
		db.execute("BudgetShareRec.addShare", BudgetShareRec.class, user2,user1);
		return;
	}	
	public static void deleteShare(Database db, int user1, int user2) throws Exception
	{
		db.execute("BudgetShareRec.deleteShare", BudgetShareRec.class, user1,user2,user1,user2);
		return;
	}	
	/** select a list of users that are sharing their budget with you */
	public static List<UserRec> selectSharers(Database db,int user) throws Exception
	{
		return db.selectList("BudgetShareRec.selectSharers",UserRec.class,user);
	}
	@Override
	public String getTable() {
		return TABLE;
	}
}
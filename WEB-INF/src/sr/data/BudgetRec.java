package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.JDate;
import ir.util.StringKit;

public class BudgetRec extends AppRec {

	public enum BudgetType{
		Income,Expense;	
		public static BudgetType lookup(String name) {
			for (BudgetType t : values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return Expense;
		}
	}
	public enum BudgetCategory{
		Cable_Satellite(true),
		Car(true),
		Car_Insurance(true),
		Car_Loan(true),
		Clothing(true),
		Entertainment(true),
		Dining(true),
		Gas(true),
		Gift(true),
		Groceries(true),
		Health_Beauty(true),
		Household(true),
		Internet(true),
		Internet_Services(true),
		Life_Insurance(true),
		Misc(true),
		Mortgage(true),
		Pets(true),
		Phone(true),
		Property_Insurance(true),
		Property_Tax(true),
		Renovations(true),
		Rent(true),
		School_Loan(true),
		Utilities(true),
		Inheritance(false),
		Other_Income(false),
		Refund(false),
		Pay_Check(false),
		;
        public static BudgetCategory lookup(String name) {
			for (BudgetCategory t : values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return Misc;
		}
    	public static String toJson()
    	{
    		StringBuilder b = new StringBuilder("[");
    		String comma = "";
    		for(BudgetCategory t : BudgetCategory.values())
    		{
    			b.append(comma)
    			 .append("{name:").append(StringKit.jsq(t.name()))
    			 .append(",isExpense:").append(t.isExpense)
    			 .append(",text:").append(StringKit.jsq(t.name().replaceAll("_", " ")))
    			 .append("}");
    			comma = ",";
    		}
    		return b.append("]").toString();
    	}
		public final boolean isExpense;
		private BudgetCategory(boolean isExpense)
		{
			this.isExpense = isExpense;
		}
	}
	public static final String AMOUNT = "Amount";
	public static final String CATEGORY = "Category";
	public static final String NAME = "Name";
	public static final String NOTE = "Note";
	public static final String ROW = "Row";
	public static final String TABLE  = "tBudget";
	public static final String TIME = "Time";
	public static final String TYPE = "Type";
	public static final String USER = "User";
	
	public float Amount = 0;
	public BudgetCategory Category = BudgetCategory.Misc;
	public String Name = "";
	public String Note = "";
	public int Row = 0;
	public JDate Time = JDate.today();
	public BudgetType Type = BudgetType.Expense;
	public int User = 0;
	//
	public static List<BudgetRec> selectList(Database db, int user, JDate from, JDate to) throws Exception
	{
		return db.selectList("BudgetRec.selectList", BudgetRec.class,user, from, to,user, from, to);
	}	
	@Override
	public String getTable() {
		return TABLE;
	}
	
	
}
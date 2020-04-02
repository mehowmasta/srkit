package sr.web.page;

import java.util.List;

import ir.util.JDate;
import ir.web.JControl;
import sr.data.BudgetRec;
import sr.data.BudgetShareRec;
import sr.data.UserRec;
import sr.data.BudgetRec.BudgetCategory;
import sr.data.BudgetRec.BudgetType;

/**
 * Controls similarly named jsp file
 */
public class BudgetPage extends AppBasePage {

	public JControl ctlCategory;
	public JControl ctlRow;
	public JControl ctlName;
	public JControl ctlNote;
	public JControl ctlTime;
	public JControl ctlType;
	public JControl ctlAmount;
	public JDate to;
	public JDate from;
	
	
	@Override
	public boolean hasInfo()
	{
		return false;
	}
	@Override
	protected void init() throws Exception {
		BudgetRec b = new BudgetRec();
		mapControl(ctlRow,b,BudgetRec.ROW);
		mapControl(ctlCategory,b,BudgetRec.CATEGORY);
		mapControl(ctlName,b,BudgetRec.NAME);
		mapControl(ctlNote,b,BudgetRec.NOTE);
		mapControl(ctlType,b,BudgetRec.TYPE);
		mapControl(ctlTime,b,BudgetRec.TIME);
		mapControl(ctlAmount, b, BudgetRec.AMOUNT);
		ctlRow.setHidden();
		ctlAmount.setSize(12);
		ctlAmount.setStyle("text-align:right;");
		ctlNote.setTextArea(8, 0);
		ctlCategory.setEvents("onchange='view.afterChangeCategory();'");
		to = JDate.today();
		from = JDate.today().addMonths(-3);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		List<UserRec> sharers = BudgetShareRec.selectSharers(db, currentUser.Row);
		set("BudgetBlank",new BudgetRec());
		set("BudgetCategory",BudgetCategory.toJson());
		set("BudgetList",BudgetRec.selectList(db,currentUser.Row,from,to));
		set("BudgetSharers",sharers);
		set("BudgetHasSharers",sharers.size()>0);
		set("From",jsq(from));
		set("To",jsq(to));
		ctlType.addValues(BudgetType.class,db);
		ctlCategory.addValues(BudgetCategory.class,db);
	}
}

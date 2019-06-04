package sr.web.page;

import java.util.List;

import sr.data.MatrixActionRec;

/**
 * Controls similarly named jsp file
 */
public class MatrixActionsPage extends AppBasePage {

	public List<MatrixActionRec> actions;
	@Override
	public boolean hasInfo()
	{
		return true;
	}
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	public boolean hasSort()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		actions = MatrixActionRec.selectAll(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Actions",actions.toString());
		set("SortType",MatrixActionRec.getSortTypeJson());
	}
}

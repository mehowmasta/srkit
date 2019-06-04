package sr.web.page;

import java.util.List;

import sr.data.CritterPowerRec;

/**
 * Controls similarly named jsp file
 */
public class CritterPowerListPage extends AppBasePage {

	public List<CritterPowerRec> powers;
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
		powers = CritterPowerRec.selectAll(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Powers",powers.toString());
		set("SortType",CritterPowerRec.getSortTypeJson());
	}
}

package sr.web.page;

import java.util.List;

import sr.data.AdeptPowerRec;
import sr.data.SrRec;

/**
 * Controls similarly named jsp file
 */
public class AdeptPowerListPage extends AppBasePage {

	public List<AdeptPowerRec> powers;
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
		powers = SrRec.selectAll(db,AdeptPowerRec.class);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Powers",powers.toString());
		set("SortType",AdeptPowerRec.getSortTypeJson());
	}
}

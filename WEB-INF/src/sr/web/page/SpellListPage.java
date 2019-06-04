package sr.web.page;

import java.util.List;

import sr.data.SpellRec;
import sr.data.SrRec;

/**
 * Controls similarly named jsp file
 */
public class SpellListPage extends AppBasePage {

	public List<SpellRec> spells;
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
		spells = SrRec.selectAll(db,SpellRec.class);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Spells",spells.toString());
		set("SortType",SpellRec.getSortTypeJson());
	}
}

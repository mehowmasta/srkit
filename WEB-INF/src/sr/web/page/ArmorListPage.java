package sr.web.page;

import java.util.List;

import sr.data.ArmorRec;
import sr.data.SrRec;

/**
 * Controls similarly named jsp file
 */
public class ArmorListPage extends AppBasePage {

	public List<ArmorRec> armor;
	/*
	@Override
	public boolean hasInfo()
	{
		return true;
	}
	@Override
	public boolean hasSort()
	{
		return true;
	}
	*/
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		armor = SrRec.selectAll(db,ArmorRec.class);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Armor",armor.toString());
	}
}

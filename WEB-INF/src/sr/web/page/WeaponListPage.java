package sr.web.page;

import java.util.List;

import sr.data.SrRec;
import sr.data.WeaponRec;

/**
 * Controls similarly named jsp file
 */
public class WeaponListPage extends AppBasePage {

	public List<WeaponRec> weapons;
	/*
	@Override
	public boolean hasInfo()
	{
		return true;
	}
	 */
	@Override
	public boolean hasSort()
	{
		return true;
	}
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		weapons = SrRec.selectAll(db,WeaponRec.class);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Weapons",weapons.toString());
		set("SortType",WeaponRec.getSortTypeJson());
	}
}

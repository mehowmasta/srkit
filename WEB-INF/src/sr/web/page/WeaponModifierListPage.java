package sr.web.page;

import java.util.List;

import sr.data.SrRec;
import sr.data.WeaponModifierRec;
import sr.data.WeaponRec;

/**
 * Controls similarly named jsp file
 */
public class WeaponModifierListPage extends AppBasePage {

	public List<WeaponModifierRec> weaponModifiers;
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
		weaponModifiers = SrRec.selectAll(db,WeaponModifierRec.class);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("WeaponModifiers",weaponModifiers.toString());
		set("SortType",WeaponRec.getSortTypeJson());
	}
}

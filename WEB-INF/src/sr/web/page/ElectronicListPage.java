package sr.web.page;

import java.util.List;

import sr.data.GearRec;

/**
 * Controls similarly named jsp file
 */
public class ElectronicListPage extends AppBasePage {

	public List<GearRec> gear;
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
		gear = GearRec.selectElectronic(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Gear",gear.toString());
	}
}

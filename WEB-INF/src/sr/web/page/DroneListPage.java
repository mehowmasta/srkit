package sr.web.page;

import java.util.List;

import sr.data.DroneRec;

/**
 * Controls similarly named jsp file
 */
public class DroneListPage extends AppBasePage {

	public List<DroneRec> drones;
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
		drones = DroneRec.selectAll(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Drones",drones.toString());
		set("SortType",DroneRec.getSortTypeJson());
	}
}

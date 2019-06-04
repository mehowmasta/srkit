package sr.web.page;

import java.util.List;

import sr.data.VehicleRec;

/**
 * Controls similarly named jsp file
 */
public class VehicleListPage extends AppBasePage {

	public List<VehicleRec> vehicles;	
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
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		vehicles = VehicleRec.selectAll(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Vehicles",vehicles.toString());
		set("SortType",VehicleRec.getSortTypeJson());
	}
}

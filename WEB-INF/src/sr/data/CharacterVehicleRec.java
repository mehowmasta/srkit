package sr.data;

import ir.data.Database;

public class CharacterVehicleRec extends EquipRec
{
	public static final String CURRENTAMOUNT = "CurrentAmount";
	public static final String QUANTITY = "Quantity";
	public static final String TABLE = "tCharacterVehicle";
	public static final String VEHICLEROW = "VehicleRow";
	//
	public int CurrentAmount = 0;
	public int Quantity = 0;
	public int VehicleRow = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Vehicle;
	@Override
	public String getTable() {
		return TABLE;
	}
}
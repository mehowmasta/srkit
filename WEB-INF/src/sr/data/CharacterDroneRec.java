package sr.data;

public class CharacterDroneRec extends EquipRec
{
	public static final String CURRENTAMOUNT = "CurrentAmount";
	public static final String DRONEROW = "DroneRow";
	public static final String QUANTITY = "Quantity";
	public static final String TABLE = "tCharacterDrone";
	//
	public int CurrentAmount = 0;
	public int DroneRow = 0;
	public int Quantity = 0;
	public final ScoreBoardType GridType = ScoreBoardType.Drone;
	@Override
	public String getTable() {
		return TABLE;
	}
	
}
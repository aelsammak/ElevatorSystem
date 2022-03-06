package floorsubsystem;

/**
 * The Floor class represents a single Floor in the FloorSubSystem. 
 * 
 * @author Kareem El-Hajjar
 * @version 3.0
 * 
 */
public class Floor {
	
	private final int floorNumber;
	
	/**
	 * Parameterized constructor
	 * 
	 * @param floorNumber, the floor's number
	 */
	public Floor(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	/**
	 * Getter for floorNumber attribute
	 * 
	 * @return int - the floorNumber attribute
	 */
	public int getFloorNumber() {
		return floorNumber;
	}
	
}
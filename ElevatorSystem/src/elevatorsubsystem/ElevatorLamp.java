package elevatorsubsystem;

/**
 * The ElevatorLamp class represents the Elevator's lamps
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class ElevatorLamp {
	
	private final int floorNumber;
	private boolean isTurnedOn;
	
	/**
	 * Constructor used to create an instance of the ElevatorLamp class
	 * 
	 * @param floorNumber - the floor number
	 */
	public ElevatorLamp(int floorNumber) {
		this.floorNumber = floorNumber;
		this.isTurnedOn = false;
	}
	
	/**
	 * This method is used to turn on the ElevatorLamp
	 */
	public void turnOn() {
		this.isTurnedOn = true;
	}
	
	/**
	 * This method is used to turn off the ElevatorLamp
	 */
	public void turnOff() {
		this.isTurnedOn = false;
	}
	
	/**
	 * Getter for the isTurnedOn attribute
	 * 
	 * @return boolean - true if the lamp is turned on, else false
	 */
	public boolean isTurnedOn() {
		return this.isTurnedOn;
	}


	/**
	 * Getter for the floorNumber attribute
	 * 
	 * @return int - the floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

}

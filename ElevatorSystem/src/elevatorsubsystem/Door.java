package elevatorsubsystem;

/**
 * The Door class represents the Elevator's doors
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class Door {
	
	private final int elevatorNumber;
	private boolean isOpen;
	
	/**
	 * Constructor used to create an instance of the Door class 
	 * 
	 * @param elevatorNumber -  the elevatorNumber
	 */
	public Door(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		this.isOpen = false;
	}

	/**
	 * Getter for the elevatorNumber attribute
	 * 
	 * @return int - the elevator number
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * This method is responsible for opening the doors
	 */
	public void open() {
		this.isOpen = true;
	}
	
	/**
	 * This method is responsible for closing the doors
	 */
	public void close() {
		this.isOpen = false;
	}

	/**
	 * This method is responsible for checking to see if the Doors are open or closed.
	 * 
	 * @return boolean - true if the Doors are open, else false
	 */
	public boolean isOpen() {
		return this.isOpen;
	}
	
}

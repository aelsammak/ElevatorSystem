package elevatorsubsystem;

/**
 * The ElevatorButton class represents the Elevator's buttons
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class ElevatorButton {
	
	private final int floorNumber;
	private boolean isPressed;
	
	/**
	 * Constructor used to create an instance of the ElevatorButton class
	 * 
	 * @param floorNumber - the floor number
	 */
	public ElevatorButton(int floorNumber) {
		this.floorNumber = floorNumber;
		this.isPressed = false;
	}

	/**
	 * Getter for the floorNumber attribute
	 * 
	 * @return int - the floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Getter for the isPressed attribute
	 * 
	 * @return boolean - true if the button is pressed, else false
	 */
	public boolean isPressed() {
		return isPressed;
	}
	
	/**
	 * This method is used to turn on the ElevatorButton
	 */
	public void turnOn() {
		this.isPressed = true;
	}
	
	/**
	 * This method is used to turn off the ElevatorButton
	 */
	public void turnOff() {
		this.isPressed = false;
	}
	
}

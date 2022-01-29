package floorsubsystem;

/**
 * This class represents the floor button and all its functionality
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class FloorButton {
	
	private boolean isPressed;
	private final int floorNumber;
	private final boolean isUp;
	
	/**
	 * Parameterized constructor 
	 * @param floorNumber, the number of the floor that is associated with the button
	 * @param isUp, true if button is for going up, false otherwise
	 */
	public FloorButton(int floorNumber, boolean isUp) {
		this.floorNumber = floorNumber;
		this.isUp = isUp;
		this.isPressed = false;
	}
	
	/**
	 * Getter for isPressed attribute
	 * @return boolean - the isPressed attribute
	 */
	public boolean isPressed() {
		return isPressed;
	}
	
	/**
	 * Getter for the isUp attribute
	 * @return boolean - the isUp attribute
	 */
	public boolean isUp() {
		return isUp;
	}
	
	/**
	 * Getter for the floorNumber attribute
	 * @return int - the floorNumber attribute
	 */
	public int getFloorNumber() {
		return this.floorNumber;
	}

	/**
	 * Turn the button on
	 */
	public void turnOn() {
		this.isPressed = true;  
	}
	
	/**
	 * Turn the button off
	 */
	public void turnOff() {
		this.isPressed = false;  
	}
}

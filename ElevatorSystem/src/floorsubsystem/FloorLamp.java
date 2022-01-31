package floorsubsystem;

/**
 * This class represents the floor lamp and all its functionality
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class FloorLamp {
	private boolean isTurnedOn;
	
	/**
	 * Default constructor
	 */
	public FloorLamp() {
		this.isTurnedOn = false;
	}

	/**
	 * Getter for the isTurnedOn attribute
	 * 
	 * @return boolean - the isTurnedOn attribute
	 */
	public boolean isTurnedOn() {
		return isTurnedOn;
	}
	
	/**
	 * Turn the lamp on
	 */
	public void turnOn() {
		this.isTurnedOn = true;
	}
	
	/**
	 * Turn the lamp off
	 */
	public void turnOff() {
		this.isTurnedOn = false;
	}
	
}

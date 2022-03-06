package floorsubsystem;

/**
 * Middle floor variant of the Floor class
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class MiddleFloor extends Floor {
	
	private final FloorButton upBtn;
	private final FloorButton downBtn;
	private final FloorLamp upLamp;
	private final FloorLamp downLamp;
	
	/**
	 * Parameterized constructor 
	 * 
	 * @param floorNumber, the floor's number
	 * @param scheduler, the scheduler associated with the floor
	 */
	public MiddleFloor(int floorNumber) {
		super(floorNumber);
		this.upBtn = new FloorButton(floorNumber, true);
		this.downBtn = new FloorButton(floorNumber, false);
		this.upLamp = new FloorLamp();
		this.downLamp = new FloorLamp();
	}
	
	/**
	 * Turn on the up button
	 */
	public void turnOnUpButton() {
		this.upBtn.turnOn();
	}
	
	/**
	 * Turn off the up button
	 */
	public void turnOffUpButton() {
		this.upBtn.turnOff();
	}
	
	/**
	 * Turn on the down button
	 */
	public void turnOnDownButton() {
		this.downBtn.turnOn();
	}
	
	/**
	 * Turn off the down button
	 */
	public void turnOffDownButton() {
		this.downBtn.turnOff();
	}

	/**
	 * Getter for the upLamp attribute
	 * 
	 * @return FloorLamp - the upLamp attribute
	 */
	public FloorLamp getUpLamp() {
		return upLamp;
	}
	
	/**
	 * Getter for the downLamp attribute
	 * 
	 * @return FloorLamp - the downLamp attribute
	 */
	public FloorLamp getDownLamp() {
		return downLamp;
	}
	
	/**
	 * Turn on the up lamp
	 */
	public void turnOnUpLamp() {
		this.upLamp.turnOn();
	}
	
	/**
	 * Turn off the up lamp
	 */
	public void turnOffUpLamp() {
		this.upLamp.turnOff();
	}
	
	/**
	 * Turn on the down lamp
	 */
	public void turnOnDownLamp() {
		this.downLamp.turnOn();
	}
	
	/**
	 * Turn off the down lamp
	 */
	public void turnOffDownLamp() {
		this.downLamp.turnOff();
	}
	
	
}

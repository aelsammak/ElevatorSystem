package floorsubsystem;

import scheduler.Scheduler;

/**
 * Bottom floor variant in the Floor class
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class BottomFloor extends Floor {
	
	private final FloorButton upBtn;
	private final FloorLamp upLamp;
	
	/**
	 * Parameterized constructor 
	 * @param floorNumber, the floor's number
	 * @param scheduler, the scheduler associated with the floor
	 */
	public BottomFloor(int floorNumber) {
		super(floorNumber);
		this.upBtn = new FloorButton(floorNumber, true);
		this.upLamp = new FloorLamp();
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
	 * Getter for the upLamp attribute
	 * @return FloorLamp - the upLamp attribute
	 */
	public FloorLamp getUpLamp() {
		return upLamp;
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
}

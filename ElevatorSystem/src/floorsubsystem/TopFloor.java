package floorsubsystem;

import scheduler.Scheduler;

/**
 * Top floor variant of the Floor class
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class TopFloor extends Floor {
	
	private final FloorButton downBtn;
	private final FloorLamp downLamp;
	
	/**
	 * Parameterized constructor 
	 * 
	 * @param floorNumber, the floor's number
	 * @param scheduler, the scheduler associated with the floor
	 */
	public TopFloor(int floorNumber, Scheduler scheduler) {
		super(floorNumber, scheduler);
		this.downBtn = new FloorButton(floorNumber, false);
		this.downLamp = new FloorLamp();
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
	 * Getter for the downLamp attribute
	 * 
	 * @return FloorLamp - the downLamp attribute
	 */
	public FloorLamp getDownLamp() {
		return downLamp;
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

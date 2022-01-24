/**
 * 
 */
package scheduler;

import java.util.Date;

import floorsubsystem.Floor;

/**
 * @author Cam
 *
 */
public class FloorEvent extends Event {
	
	private int buttonPressed;
	private Floor floor;
	
	public FloorEvent(Floor floor, int buttonPressed, Date time) {
		super(time);
		this.floor = floor;
		this.buttonPressed = buttonPressed;
	}
	
	public int getButtonPressed() {
		return buttonPressed;
	}
	
	public Floor getFloor() {
		return floor;
	}
	
}

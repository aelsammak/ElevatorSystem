/**
 * 
 */
package scheduler;

import floorsubsystem.Floor;

/**
 * @author Cam
 *
 */
public class FloorEvent extends Event {
	
	private boolean isUpButton;
	private Floor floor;
	
	public FloorEvent(Floor floor, boolean isUpButton, long timeLeftTillEvent) {
		super(timeLeftTillEvent);
		this.floor = floor;
		this.setUpButton(isUpButton);
	}
	
	public Floor getFloor() {
		return floor;
	}

	public boolean isUpButton() {
		return isUpButton;
	}

	public void setUpButton(boolean isUpButton) {
		this.isUpButton = isUpButton;
	}
	
}

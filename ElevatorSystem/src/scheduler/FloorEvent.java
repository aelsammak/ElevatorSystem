/**
 * 
 */
package scheduler;

import floorsubsystem.Floor;

/**
 * FloorEvent class is responsible for holding all the information necessary when a passenger calls for an elevator from a floor
 * @author Cam Sommerville, Erica Morgan
 * @version 1.0
 *
 */
public class FloorEvent extends Event {
	
	private boolean isUpButton;
	private Floor floor;
	
	/**
	 * Constructor for a floor event. Sets the floor, which direction was pressed, and time until event
	 * @param floor The floor the passenger is waiting on
	 * @param isUpButton A boolean for the direction of the button. True if up, false if down.
	 * @param timeLeftTillEvent The time left in seconds, until the button is pushed.
	 */
	public FloorEvent(Floor floor, boolean isUpButton, long timeLeftTillEvent) {
		super(timeLeftTillEvent);
		this.floor = floor;
		this.setUpButton(isUpButton);
	}
	
	/**
	 * Gets the floor where the passenger called the elevator
	 * @return A floor
	 */
	public Floor getFloor() {
		return floor;
	}

	/**
	 * Determines the direction of the button that was pressed by passenger who called elevator
	 * @return A boolean true if up button was pressed, false if down button 
	 */
	public boolean isUpButton() {
		return isUpButton;
	}

	/**
	 * Sets whether the up or down button was pressed
	 * @param isUpButton A boolean true if up button was pressed, false if down button
	 */
	public void setUpButton(boolean isUpButton) {
		this.isUpButton = isUpButton;
	}
	
}

/**
 * 
 */
package scheduler;

/**
 * The FloorEvent class represents an event that occurs when a floor button is pressed requesting for the Elevator's arrival
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 *
 */
import floorsubsystem.Floor;

public class FloorEvent extends Event {
	
	private boolean isUpButton;
	private Floor floor;
	
	/**
	 * Constructor used to create an instance of the FloorEvent class
	 * 
	 * @param floor - the floor that the button is pressed on 
	 * @param isUpButton - true if up button, else false
	 * @param timeLeftTillEvent - the time left until the event
	 */
	public FloorEvent(Floor floor, boolean isUpButton, long timeLeftTillEvent) {
		super(timeLeftTillEvent);
		this.floor = floor;
		this.setUpButton(isUpButton);
	}
	
	/**
	 * Getter for the floor attribute
	 * 
	 * @return Floor - the floor that the button is pressed on 
	 */
	public Floor getFloor() {
		return floor;
	}

	/**
	 * Getter for the isUpButton attribute
	 * 
	 * @return boolean - true if up button, else false
	 */
	public boolean isUpButton() {
		return isUpButton;
	}

	/**
	 * Setter for the isUpButton attribute
	 * 
	 * @param isUpButton - true if up button, else false
	 */
	public void setUpButton(boolean isUpButton) {
		this.isUpButton = isUpButton;
	}
	
	/**
	 * Method for testing purposes
	 * @return String - formatted string about FloorEvent
	 */
	@Override
	public String toString() {
		return super.toString() + " Floor number of Event: " + this.floor.getFloorNumber() + " Direction: " + this.isUpButton; 
	}
	
//	/**
//	 * Compare to method used by the Priority Queue
//	 * 
//	 *  @param e - the event to compare
//	 */
//    @Override
//    public int compareTo(Event e) {
//        long otherEventTime = e.getTimeLeftTillEvent();
//        return (this.getTimeLeftTillEvent() < otherEventTime ? -1  : 1);
//    }
	
}

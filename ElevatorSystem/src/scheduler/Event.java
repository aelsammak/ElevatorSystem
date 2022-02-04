/**
 * 
 */
package scheduler;

/**
 * The Event class represents the event that occurs when a button is pressed
 * 
 * @author Kareem El-Hajjar
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class Event {
	
	private long timeLeftTillEvent;
	
	/**
	 * Constructor used to create an instance of the Event class
	 * 
	 * @param timeLeftTillEvent - the time left until the event
	 */
	public Event(long timeLeftTillEvent) {
		this.timeLeftTillEvent = timeLeftTillEvent;
	}

	/**
	 * Getter for the timeLeftTillEvent attribute
	 * 
	 * @return long - the time left until the event
	 */
	public long getTimeLeftTillEvent() {
		return timeLeftTillEvent;
	}

	/**
	 * Setter for the timeLeftTillEvent attribute
	 */
	public void setTimeLeftTillEvent(long timeLeftTillEvent) {
		this.timeLeftTillEvent = timeLeftTillEvent;
	}
	
	/**
	 * Method for testing purposes
	 * @return String - formatted string about event
	 */
	public String toString() {
		return "Time until event: " + this.timeLeftTillEvent;  
	}
	
}

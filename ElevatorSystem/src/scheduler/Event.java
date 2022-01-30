/**
 * 
 */
package scheduler;

/**
 * Event class is responsible for holding the information when a passenger carries out an action.
 * The parent class used for elevator and floor events.
 * @author Cam Sommerville, Erica Morgan
 * @version 1.0
 *
 */
public class Event implements Comparable<Event> {
	
	private long timeLeftTillEvent;
	
	/**
	 * Contructor for the event. Sets how much time is left until event should happen.
	 * @param timeLeftTillEvent The time left until event, in seconds.
	 */
	public Event(long timeLeftTillEvent) {
		this.timeLeftTillEvent = timeLeftTillEvent;
	}

	/**
	 * Get the time left until the event should occur
	 * @return The time left until event in seconds
	 */
	public long getTimeLeftTillEvent() {
		return timeLeftTillEvent;
	}

	/**
	 * Sets the time left until the event should occur
	 * @param timeLeftTillEvent The time left until event in seconds
	 */
	public void setTimeLeftTillEvent(long timeLeftTillEvent) {
		this.timeLeftTillEvent = timeLeftTillEvent;
	}
	
    @Override
    /**
     * Method used to compare the priority of two events.
     * The event with less time left until event will be given a higher priority.
     * @param e The event with which this event is being compared
     * @return An int 1 if the current event is given a higher priority, and -1 otherwise.
     */
    public int compareTo(Event e) {
        long otherEventTime = e.getTimeLeftTillEvent();
        return (timeLeftTillEvent < otherEventTime ? 1  : -1);
    }
	
}

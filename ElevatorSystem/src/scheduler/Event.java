/**
 * 
 */
package scheduler;

import java.util.Date;

/**
 * @author Cam
 *
 */
public class Event implements Comparable<Event> {
	
	private long timeLeftTillEvent;
	
	public Event(long timeLeftTillEvent) {
		this.timeLeftTillEvent = timeLeftTillEvent;
	}

	public long getTimeLeftTillEvent() {
		return timeLeftTillEvent;
	}

	public void setTimeLeftTillEvent(long timeLeftTillEvent) {
		this.timeLeftTillEvent = timeLeftTillEvent;
	}
	
    @Override
    public int compareTo(Event e) {
        long otherEventTime = e.getTimeLeftTillEvent();
        return (timeLeftTillEvent < otherEventTime ? 1  : -1);
    }
	
}

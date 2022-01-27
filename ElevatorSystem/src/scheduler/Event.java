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
	
	private Date time;
	private long timeLeftTillEvent;
	
	public Event(Date time) {
		this.time = time;
	}

	public long getTimeLeftTillEvent() {
		return timeLeftTillEvent;
	}

	public void setTimeLeftTillEvent(long timeLeftTillEvent) {
		this.timeLeftTillEvent = timeLeftTillEvent;
	}

	public Date getTime() {
		return time;
	}
	
    @Override
    public int compareTo(Event e) {
        long currentEventTime = time.getTime();
        long otherEventTime = e.getTime().getTime();
        return (currentEventTime < otherEventTime ? -1  : 1);
    }
	
}

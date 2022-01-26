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
	
	@Override
	public int compareTo(Event o) {
		return 0;
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
	
}

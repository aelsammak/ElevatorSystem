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
	
	public Event(Date time) {
		this.time = time;
		
	}
	
	@Override
	public int compareTo(Event o) {
		return 0;
	}
	
}

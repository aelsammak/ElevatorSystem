package scheduler;

import java.util.Comparator;

/**
 * The FloorEventComparator is a comparator used to sort the FloorEvent priority queue in terms of shortest time till event.
 * 
 * @author Adi El-Sammak
 * @author Kareeem El-Hajjar
 *
 */
public class FloorEventComparator implements Comparator<FloorEvent> {
	
	/**
	 * This method is used to compare two FloorEvents based on their timeLeftTillEvent
	 * If e1 is higher priority than e2 return 1, else if e1 is lower priority than e2 return -1, else return 0
	 * 
	 * @param e1 - FloorEvent 1
	 * @param e2 - FloorEvent 2
	 * 
	 * @return int - 1, -1 or 0
	 */
    @Override
    public int compare(FloorEvent e1, FloorEvent e2) {
        if (e1.getTimeLeftTillEvent() < e2.getTimeLeftTillEvent()) {
            return 1;
        } else if (e1.getTimeLeftTillEvent() > e2.getTimeLeftTillEvent()) {
            return -1;
        } else {
            return 0;
        }
    }

}

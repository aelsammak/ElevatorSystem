package scheduler;

import java.util.Comparator;

/**
 * The ElevatorEventComparator is a comparator used to sort the ElevatorEvent priority queue in terms of shortest time till event.
 * 
 * @author Adi El-Sammak
 * @author Kareeem El-Hajjar
 *
 */
public class ElevatorEventComparator implements Comparator<ElevatorEvent> {
	
	/**
	 * This method is used to compare two ElevatorEvents based on their timeLeftTillEvent
	 * If e1 is higher priority than e2 return 1, else if e1 is lower priority than e2 return -1, else return 0
	 * 
	 * @param e1 - ElevatorEvent 1
	 * @param e2 - ElevatorEvent 2
	 * 
	 * @return int - 1, -1 or 0
	 */
    @Override
    public int compare(ElevatorEvent e1, ElevatorEvent e2) {
        if (e1.getTimeLeftTillEvent() < e2.getTimeLeftTillEvent()) {
            return -1;
        } else if (e1.getTimeLeftTillEvent() > e2.getTimeLeftTillEvent()) {
            return 1;
        } else {
            return 0;
        }
    }

}

package scheduler;

import java.util.Comparator;

public class ElevatorEventComparator implements Comparator<ElevatorEvent> {
	
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

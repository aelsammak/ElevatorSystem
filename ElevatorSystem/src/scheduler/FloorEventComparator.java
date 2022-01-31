package scheduler;

import java.util.Comparator;

public class FloorEventComparator implements Comparator<FloorEvent> {
	
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

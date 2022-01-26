package elevatorsubsystem;

import floorsubsystem.Floor;
import static java.lang.Math.abs;

public class ArrivalSensor {
	
	private static final long DISTANCE_BETWEEN_FLOORS = (long) 3.02 ;
	private static final long MAX_SPEED = (long) 1.80 ;

    public ArrivalSensor() {
      
    }

    // time is in milliseconds
    public long getTimeBetweenFloors(Floor currentFloor, Floor destinationFloor) {
    	long distanceBetweenFloors = abs((destinationFloor.getFloorNumber() - currentFloor.getFloorNumber()) * DISTANCE_BETWEEN_FLOORS);
    	return ((distanceBetweenFloors / MAX_SPEED) * 1000);
    }
    
    public synchronized void simulateElevatorMovement(Floor currentFloor, Floor destinationFloor) {
    	try {
			wait(getTimeBetweenFloors(currentFloor, destinationFloor));
		} catch (Exception e) {
			System.out.println("In method simulateElevatorMovement()");
			e.printStackTrace();
		}
    }

}

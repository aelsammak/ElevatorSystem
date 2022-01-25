package elevatorsubsystem;

import floorsubsystem.Floor;
import static java.lang.Math.abs;

public class ArrivalSensor {
	
	// TODO - NEED REAL VALUES
	private static final long DISTANCE_BETWEEN_FLOORS = (long) 3.0 ;
	private static final long MAX_SPEED = (long) 1.1 ;

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

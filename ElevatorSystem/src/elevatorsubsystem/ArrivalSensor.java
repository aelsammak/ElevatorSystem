package elevatorsubsystem;

import floorsubsystem.Floor;
import static java.lang.Math.abs;

public class ArrivalSensor {
	
	private static final long DISTANCE_BETWEEN_FLOORS = (long) 3.02;
	private static final long MAX_SPEED = (long) 1.80;
	private static final long TIME_BETWEEN_ONE_FLOOR = (long) (DISTANCE_BETWEEN_FLOORS / MAX_SPEED) * 1000;
	private Elevator elevator;

    public ArrivalSensor(Elevator elevator) {
    	this.elevator = elevator; 
    }

    // time is in milliseconds
//    public long getTimeBetweenFloors(Floor currentFloor, Floor destinationFloor) {
//    	long distanceBetweenFloors = abs((destinationFloor.getFloorNumber() - currentFloor.getFloorNumber()) * DISTANCE_BETWEEN_FLOORS);
//    	return ((distanceBetweenFloors / MAX_SPEED) * 1000);
//    }
    
    public synchronized void simulateElevatorMovement(Floor currentFloor, Floor destinationFloor) {
    	try {
    		for (int i = 0; i < abs(destinationFloor.getFloorNumber() - currentFloor.getFloorNumber()); i++) {
    			wait(TIME_BETWEEN_ONE_FLOOR);
    			elevator.changeCurrentFloor();
    		}
		} catch (Exception e) {
			System.out.println("In method simulateElevatorMovement()");
			e.printStackTrace();
		}
    }

}

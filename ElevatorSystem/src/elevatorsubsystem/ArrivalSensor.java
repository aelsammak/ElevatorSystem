package elevatorsubsystem;

import floorsubsystem.Floor;
import static java.lang.Math.abs;

import common.Config;

/**
 * The ArrivalSensor class represents the Elevator's ArrivalSensor
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class ArrivalSensor {
	
	private Elevator elevator;
	private Config config;

	/**
	 * Constructor used to create an instance of the ArrivalSensor class 
	 * 
	 * @param elevator -  the Elevator that the ArrivalSensor is part of
	 */
    public ArrivalSensor(Elevator elevator, Config config) {
    	this.elevator = elevator;
    	this.config = config;
    }
    
    /**
     * This method is responsible for simulating Elevator movement by changing the Elevator's position with respect to time
     * 
     * @param currentFloor - the current floor
     * @param destinationFloor - the destination floor
     */
    public synchronized void simulateElevatorMovement(Floor currentFloor, Floor destinationFloor) {
    	try {
    		for (int i = 0; i < abs(destinationFloor.getFloorNumber() - currentFloor.getFloorNumber()); i++) {
    			wait(config.getLongProperty("TIME_BETWEEN_ONE_FLOOR"));
    			elevator.changeCurrentFloor();
    		}
		} catch (Exception e) {
			System.out.println("In method simulateElevatorMovement()");
			e.printStackTrace();
		}
    }

}

package elevatorsubsystem;

import static java.lang.Math.abs;

import common.Common;

/**
 * The ArrivalSensor class represents the Elevator's ArrivalSensor
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class ArrivalSensor {
	
	private Elevator elevator;
	
	/**
	 * Constructor used to create an instance of the ArrivalSensor class 
	 * 
	 * @param elevator -  the Elevator that the ArrivalSensor is part of
	 */
    public ArrivalSensor(Elevator elevator) {
    	this.elevator = elevator;
    }
    
    /**
     * This method is responsible for simulating Elevator movement by changing the Elevator's position with respect to time
     * 
     * @param currentFloor - the current floor
     * @param destinationFloor - the destination floor
     */
    public synchronized void simulateElevatorMovement(int currentFloor, int destinationFloor) {
    	try {
    		for (int i = 0; i < abs(destinationFloor - currentFloor); i++) {
    			wait(Common.config.getLongProperty("TIME_BETWEEN_ONE_FLOOR"));
    			elevator.updatePosition();
    		}
    		elevator.notifyElevatorArrival();
		} catch (Exception e) {
			System.out.println("In method simulateElevatorMovement()");
			e.printStackTrace();
		}
    }

}

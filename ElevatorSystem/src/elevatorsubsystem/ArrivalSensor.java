package elevatorsubsystem;

import static java.lang.Math.abs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

import common.Common;

/**
 * The ArrivalSensor class represents the Elevator's ArrivalSensor
 * 
 * @author Adi El-Sammak
 * @version 3.0
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
    			if (i == 0) {
    		    	try {
    		    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
    					writer.write("Time of Elevator Starting Movement : " + LocalTime.now() + "\n");
    					writer.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			} else if (i == 1) {
    		    	try {
    		    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
    					writer.write("Time of Elevator Stop Movement : " + LocalTime.now() + "\n");
    					writer.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    			wait(Common.config.getLongProperty("TIME_BETWEEN_ONE_FLOOR"));
    			elevator.updatePosition();
    			if (elevator.handleError()) {
					return; 
    			}
    		}	
    		elevator.notifyElevatorArrival();
		} catch (Exception e) {
			System.out.println("In method simulateElevatorMovement()");
			e.printStackTrace();
		}
    }

}

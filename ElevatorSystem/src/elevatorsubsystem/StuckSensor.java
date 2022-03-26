package elevatorsubsystem;

import static java.lang.Math.abs;

import common.Common;

public class StuckSensor {
	
	private Elevator elevator;
	
	/**
	 * Constructor used to create an instance of the StuckSensor class 
	 * 
	 * @param elevator -  the Elevator that the ArrivalSensor is part of
	 */
    public StuckSensor(Elevator elevator) {
    	this.elevator = elevator;
    }
    
    /**
     * This method is responsible for simulating Elevator movement by changing the Elevator's position with respect to time
     * 
     * @param currentFloor - the current floor
     * @param destinationFloor - the destination floor
     */
    public synchronized void simulateStuck() {
    	try {
    		//System.out.println("STARTING SIMULATE STUCK WAIT");
			wait(Common.config.getLongProperty("TIME_STUCK"));
			//System.out.println("FINISHED SIMULATE STUCK WAIT");
    		elevator.notifyElevatorUnStuck();
		} catch (Exception e) {
			//System.out.println("In method simulateStuck()");
			//e.printStackTrace();
		}
    }
}

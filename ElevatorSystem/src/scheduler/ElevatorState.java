package scheduler;

/**
 *  Keeps track of elevator state
 *  
 * @author Cam Sommerville
 * @author Erica Morgan
 * @version 3.0
 */
public class ElevatorState {
	
	private int elevatorNumber; 
	private int currentFloorNumber;
	private int motorDir;
	private int targetFloor;
		
	/**
	 * Default Constructor initializes state: Elevator to be at Floor #1, IDLE and have a targetFloor #1
	 * 
	 * @param elevatorNumber - the elevator number
	 */
	public ElevatorState (int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		this.currentFloorNumber = 1;
		this.motorDir = 0;
		this.targetFloor = 1;
	}
	
	/**
	 * Constructor for testing 
	 * @param elevatorNumber
	 * @param currentFloor
	 * @param dir
	 * @param targetFloor
	 */
	public ElevatorState (int elevatorNumber, int currentFloor, int dir, int targetFloor) {
		this.elevatorNumber = elevatorNumber;
		this.currentFloorNumber = currentFloor;
		this.motorDir = dir;
		this.targetFloor = targetFloor;
	}
	
	/**
	 * Getter for the elevatorNumber Attribute
	 * 
	 * @return int - elevatorNumber
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}
	
	/**
	 * Getter for the currentFloorNumber Attribute
	 * 
	 * @return int - currentFloorNumber
	 */
	public int getCurrentFloorNumber() {
		return currentFloorNumber;
	}
	
	/**
	 * Setter for the currentFloorNumber Attribute
	 *
	 * @param currentFloorNumber - the currentFloorNumber
	 */
	public void setCurrentFloorNumber(int currentFloorNumber) {
		this.currentFloorNumber = currentFloorNumber;
	}
	
	/**
	 * Getter for the motorDir Attribute
	 * 
	 * @return int - motorDir
	 */
	public int getMotorDir() {
		return this.motorDir;
	}
	
	/**
	 * Setter for the motorDir Attribute
	 *
	 * @param motorDir - the motorDir
	 */
	public void setMotorDir(int motorDir) { 
		this.motorDir = motorDir; 
	}

	/**
	 * Getter for the targetFloor Attribute
	 * 
	 * @return int - targetFloor
	 */
	public int getTargetFloor() { 
		return this.targetFloor; 
	}

	/**
	 * Setter for the targetFloor Attribute
	 *
	 * @param targetFloor - the targetFloor
	 */
	public void setTargetFloor(int targetFloor) { 
		this.targetFloor = targetFloor; 
	}
	
	public String toString() {
		return "Elevator #: " + this.elevatorNumber + " Current Floor: " + this.currentFloorNumber + " Elevator State: " + this.motorDir + " Destination Floor: " + this.targetFloor;
	}

}

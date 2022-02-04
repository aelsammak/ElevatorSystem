package scheduler;

import elevatorsubsystem.Elevator;
import floorsubsystem.Floor;

/**
 * The ElevatorEvent class represents an event that occurs when a button is pressed within the Elevator
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class ElevatorEvent extends Event {
	
	private Elevator elevator;
	private Floor destinationFloor;
	
	/**
	 * Constructor used to create an instance of the ElevatorEvent class
	 * 
	 * @param elevator - the Elevator that the button is pressed in 
	 * @param destinationFloor - the requested destination floor
	 * @param timeLeftTillEvent - the time left until the event
	 */
	public ElevatorEvent(Elevator elevator, Floor destinationFloor, long timeLeftTillEvent) {
		super(timeLeftTillEvent);
		this.elevator = elevator;
		this.destinationFloor = destinationFloor;
	}
	
	/**
	 * Getter for the elevator attribute
	 * 
	 * @return Elevator - the Elevator that the button is pressed in
	 */
	public Elevator getElevator() {
		return elevator;
	}
	
	/**
	 * Getter for the destinationFloor attribute
	 * 
	 * @return Floor - the requested destination floor
	 */
	public Floor getDestinationFloor() {
		return destinationFloor;
	}
	
	/**
	 * Method for testing purposes
	 * @return String - formatted string about ElevatorEvent
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + " Elevator Number: " + this.elevator.getElevatorNumber() + " Destination: " + this.destinationFloor.getFloorNumber();
	}
}

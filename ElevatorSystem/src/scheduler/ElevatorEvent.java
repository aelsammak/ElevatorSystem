package scheduler;

import elevatorsubsystem.Elevator;
import floorsubsystem.Floor;

/**
 * ElevatorEvent class is responsible for holding all the information necessary when a passenger pushes a button in the elevator
 * @author Cam Sommerville, Erica Morgan
 * @version 1.0
 *
 */
public class ElevatorEvent extends Event {
	
	private Elevator elevator;
	private Floor destinationFloor;
	
	/**
	 * Constructor for the elevator event. Sets the elevator and floor involved in the event.
	 * @param elevator The elevator where the button was pushed
	 * @param destinationFloor The passengers destination floor 
	 * @param timeLeftTillEvent The time left until the event happens in seconds
	 */
	public ElevatorEvent(Elevator elevator, Floor destinationFloor, long timeLeftTillEvent) {
		super(timeLeftTillEvent);
		this.elevator = elevator;
		this.destinationFloor = destinationFloor;
	}
	
	/**
	 * Gets the elevator where button was pushed
	 * @return An elevator 
	 */
	public Elevator getElevator() {
		return elevator;
	}
	
	/**
	 * Gets the destination floor passenger pushed in elevator
	 * @return A floor
	 */
	public Floor getDestinationFloor() {
		return destinationFloor;
	}
	
}

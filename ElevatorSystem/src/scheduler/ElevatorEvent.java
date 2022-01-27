package scheduler;

import java.util.Date;

import elevatorsubsystem.Elevator;
import floorsubsystem.Floor;

public class ElevatorEvent extends Event {
	
	private Elevator elevator;
	private Floor destinationFloor;
	
	public ElevatorEvent(Elevator elevator, Floor destinationFloor, Date time) {
		super(time);
		this.elevator = elevator;
		this.destinationFloor = destinationFloor;
	}
	
	public Elevator getElevator() {
		return elevator;
	}
	
	public Floor getDestinationFloor() {
		return destinationFloor;
	}
	
}

package scheduler;

import java.util.*;

import elevatorsubsystem.Elevator;
import floorsubsystem.Floor;

public class Scheduler {
	
	private List<Elevator> elevators;
	private List<Floor> floors;
	
	public Scheduler() {
		elevators = new ArrayList<Elevator>();
	}
	
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	public void removeElevator(Elevator elevator) {
		elevators.remove(elevator);
	}
	
	public void receiveFloorInputs() {
		//call corresponding floor methods
	}
	
	private void updateFloorLamps() {
		//send message to activate floor lamps
	}
	
	public void receiveElevatorInputs() {
		//call corresponding elevator methods
	}
	
	private void updateElevatorLamps() {
		
	}
	
	private void activateElevatorMotor() {
		
	}
	
	private void activateElevatorDoor() {
		
	}
}

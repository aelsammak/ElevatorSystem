package scheduler;

import java.util.*;

import elevatorsubsystem.Elevator;
import floorsubsystem.Floor;

public class Scheduler extends Thread {
	
	private List<Elevator> elevators;
	private List<Floor> floors;
	private final PriorityQueue<Event> eventQueue;
	
	public Scheduler() {
		elevators = new ArrayList<Elevator>();
		this.eventQueue = new PriorityQueue<>();
	}
	
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	public void removeElevator(Elevator elevator) {
		elevators.remove(elevator);
	}
	
	public void handleFloorEvent(FloorEvent floorEvent) {
		//call corresponding floor methods
	}
	
	private void updateFloorLamps() {
		//send message to activate floor lamps
	}
	
	public void handleElevatorEvent(ElevatorEvent elevatorEvent) {
		//call corresponding elevator methods
	}
	
	private void updateElevatorLamps() {
		
	}
	
	private void activateElevatorMotor() {
		
	}
	
	private void activateElevatorDoor() {
		
	}
	
	public synchronized void moveElevatorToServePerson(Floor personsFloor, Floor destinationFloor) {
		// while the Elevator's motor is moving (NOT idle), call this.wait()
		
		// Once the Elevator's motor is idle: 
		// call moveElevatorToPersonsFloor(personsFloor)
		// call moveElevatorToRequestedDestination(destinationFloor)
	}
	
	public void moveElevatorToPersonsFloor(Floor personsFloor) {
		// if the Elevator.getCurrentFloor().getFloorNumber() != personsFloor.getFloorNumber() then, call elevators.get(0).moveToFloor(personsFloor)
		// else open the Elevator's doors (elevators.get(0).openDoors()) and call elevatorArrivesAtFloor(personsFloor)
	}
	
	public void moveElevatorToRequestedDestination(Floor destinationFloor) {
		System.out.println("Elevator button " + destinationFloor.getFloorNumber() + " has been pressed");
		// Turn on the ElevatorButton AND ElevatorLamp corresponding to the destinationFloor's floor number
		// if the Elevator.getCurrentFloor().getFloorNumber() != destinationFloor.getFloorNumber() then, call elevators.get(0).moveToFloor(destinationFloor)
		// else open the Elevator's doors (elevators.get(0).openDoors()) and call elevatorArrivesAtFloor(destinationFloor)
		// set the Elevator's motor to the idle elevatorState
		// call this.notifyAll()
	}
	
	public synchronized void elevatorArrivesAtFloor(Floor currentFloor) {
		// turns off floor lamps and unpress the corresponding floor button
		// tells elevator to close doors
	}
	
	public boolean hasEvents() {
		// return boolean indicating weather or not the priority queues in all other floors is empty or not
		return false; //placeholder
	}
	
	public long getElapsedTime() {
		//return time elapsed so far
		return (long) 1.0; //placeholder
	}
	
	@Override
	public void run() {
		// Get current date
		// get start time
		// While(this.hasEvents) : update date and get time passed
	}

	public PriorityQueue<Event> getEventQueue() {
		return eventQueue;
	}
}

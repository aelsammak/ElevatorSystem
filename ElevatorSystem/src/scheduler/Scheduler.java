package scheduler;

import java.util.*;

import elevatorsubsystem.Elevator;
import floorsubsystem.*;
import common.ElevatorState;

/**
 * Scheduler class is responsible for storing and dispatching elevators in response to passenger requests
 * @author Cam Sommerville, Erica Morgan
 * @version 1.0
 */
public class Scheduler extends Thread {
	
	private List<Elevator> elevators;
	private List<Floor> floors;
	private final PriorityQueue<Event> eventQueue;
	private long elapsedTime;
	
	/**
	 * Constructor for the Scheduler.
	 * Initializes the list of elevators, the event Priority Queue and sets the elaped time to 0.
	 */
	public Scheduler() {
		elevators = new ArrayList<Elevator>();
		this.eventQueue = new PriorityQueue<>();
		this.elapsedTime = 0;
	}
	
	/**
	 * Adds an elevator to the list of elevators handled by the scheduler.
	 * @param elevator The Elevator to be added to list of elevators.
	 */
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	/**
	 *  Removes an elevator from the list of elevators handled by the scheduler.
	 * @param elevator The Elevator to be removed from the list of elevators.
	 */
	public void removeElevator(Elevator elevator) {
		elevators.remove(elevator);
	}
	
	private synchronized void handleEvent() {
		if (!eventQueue.isEmpty()) {
			Event currentEvent = eventQueue.peek();
			if (currentEvent instanceof FloorEvent) {
				handleFloorEvent((FloorEvent) currentEvent);
			} else {
				handleElevatorEvent((ElevatorEvent) currentEvent);
			}
		}
	} 
	
	public void handleFloorEvent(FloorEvent floorEvent) {
		moveElevatorToPersonsFloor(floorEvent.getFloor());
		eventQueue.poll();
	}
	
	public void handleElevatorEvent(ElevatorEvent elevatorEvent) {
		moveElevatorToRequestedDestination(elevatorEvent.getDestinationFloor());
		eventQueue.poll();
	}
	
	public synchronized void addEvents(FloorEvent floorEvent) {
		eventQueue.add(floorEvent);
		eventQueue.add(elevators.get(0).getElevatorEventQueue().poll());
	}
	
	/**
	 * Method to move the elevator to the floor of the person requesting an elevator and opens the door.
	 * calls the elevatorArrivesAtFloor method to handle the lamp and button updates when the elevator arrives.
	 * @param personsFloor Floor where the person is requesting an elevator.
	 */
	public void moveElevatorToPersonsFloor(Floor personsFloor) {
		// while the Elevator's motor is moving (NOT idle), call this.wait()
		while(elevators.get(0).getMotorState() != ElevatorState.IDLE){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();//<<<<<<<<<------------------------------
			}
		}
		// Once the Elevator's motor is idle:
		if (elevators.get(0).getCurrentFloor().getFloorNumber() != personsFloor.getFloorNumber()) {
			elevators.get(0).moveToFloor(personsFloor);
			elevators.get(0).openDoors();
			elevatorArrivesAtFloor(elevators.get(0), personsFloor);
		}
		this.notifyAll();
		// if the Elevator.getCurrentFloor().getFloorNumber() != personsFloor.getFloorNumber() then, call elevators.get(0).moveToFloor(personsFloor)
		// else open the Elevator's doors (elevators.get(0).openDoors()) and call elevatorArrivesAtFloor(personsFloor)
	}
	
	/**
	 * Method to move the elevator to the floor requested by the person in the elevator and opens the door.
	 * calls the elevatorArrivesAtFloor method to handle the lamp and button updates when the elevator arrives.
	 * @param destinationFloor Floor the passenger has requested to travel to.
	 */
	public void moveElevatorToRequestedDestination(Floor destinationFloor) {
		// while the Elevator's motor is moving (NOT idle), call this.wait()
		while(elevators.get(0).getMotorState() != ElevatorState.IDLE){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();//<<<<<<<<<------------------------------
			}
		}
		// Once the Elevator's motor is idle:
		System.out.println("Elevator button " + destinationFloor.getFloorNumber() + " has been pressed");
		elevators.get(0).getElevatorButtons()[destinationFloor.getFloorNumber() - 1].turnOn();
		elevators.get(0).getElevatorLamps()[destinationFloor.getFloorNumber() - 1].turnOn();// Turn on the ElevatorButton AND ElevatorLamp corresponding to the destinationFloor's floor number
		if (elevators.get(0).getCurrentFloor().getFloorNumber() != destinationFloor.getFloorNumber()){
			elevators.get(0).moveToFloor(destinationFloor);
		}// if the Elevator.getCurrentFloor().getFloorNumber() != destinationFloor.getFloorNumber() then, call elevators.get(0).moveToFloor(destinationFloor)
		else {
			elevators.get(0).openDoors();
			elevatorArrivesAtFloor(elevators.get(0),destinationFloor);
		}
		// else open the Elevator's doors (elevators.get(0).openDoors()) and call elevatorArrivesAtFloor(destinationFloor)
		elevators.get(0).setMotorState(ElevatorState.IDLE);// set the Elevator's motor to the idle elevatorState
		this.notifyAll();
	}
	
	/**
	 * Turns off all Lamps and Buttons at the floor the elevator has just arrived at.
	 * Closes the elevator doors so it's ready to service next request.
	 * @param elevator Elevator that just arrived.
	 * @param currentFloor floor the elevator arrived at.
	 */
	public synchronized void elevatorArrivesAtFloor(Elevator elevator, Floor currentFloor) {
		if (currentFloor instanceof MiddleFloor) {  // turns off floor lamps and unpress the corresponding floor button
			((MiddleFloor) currentFloor).getUpLamp().turnOff();
			((MiddleFloor) currentFloor).getDownLamp().turnOff();
			((MiddleFloor) currentFloor).turnOffDownButton();
			((MiddleFloor) currentFloor).turnOffUpButton();
		}
		else if(currentFloor instanceof TopFloor) {
			((TopFloor) currentFloor).getDownLamp().turnOff();
			((TopFloor) currentFloor).turnOffDownButton();
		}
		else {
			((BottomFloor) currentFloor).getUpLamp().turnOff();
			((BottomFloor) currentFloor).turnOffUpButton();
		}
		elevator.closeDoors();
	}
	
	public Floor getFloorByIndex(int floorIndex) {
		return floors.get(floorIndex);
	}
	
	public long getElapsedTime() {
		return elapsedTime;
	}
	
    public boolean hasEvents() {
        for(Floor floor : floors) {
            if (!floor.hasEvents()) {
                return false;
            }
        }
        
        for(Elevator elevator : elevators) {
            if (!elevator.hasEvents()) {
                return false;
            }
        }
        
        return true;
    }
    
    public List<Elevator> getElevators() {
    	return elevators;
    }
    
    public List<Floor> getFloors() {
    	return floors;
    }
    
    public void setFloors(List<Floor> floors) {
    	this.floors = floors;
    }
	
	@Override
	public void run() {
        Date d = new Date();
        long startTime = d.getTime();
        while(hasEvents()) {
            d = new Date();
            elapsedTime = (d.getTime() - startTime) / 1000;
            handleEvent();
        }
	}

	public PriorityQueue<Event> getEventQueue() {
		return eventQueue;
	}
}

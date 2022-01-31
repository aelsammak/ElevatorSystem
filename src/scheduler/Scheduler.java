package scheduler;

import java.util.*;

import elevatorsubsystem.Elevator;
import floorsubsystem.*;
import common.ElevatorState;

/**
 * Scheduler class is responsible for storing and dispatching elevators in response to passenger requests
 * 
 * @author Cam Sommerville
 * @author Erica Morgan
 * @version 1.0
 * 
 */
public class Scheduler extends Thread {
	
	private List<Elevator> elevators;
	private List<Floor> floors;
	private Queue<Event> eventQueue;
	private long elapsedTime;
	
	/**
	 * Constructor for the Scheduler.
	 * Initializes the list of elevators, the event Priority Queue and sets the elapsed time to 0.
	 */
	public Scheduler() {
		elevators = new ArrayList<Elevator>();
		this.eventQueue = new LinkedList<>();
		this.elapsedTime = 0;
	}
	
	/**
	 * Adds an elevator to the list of elevators handled by the scheduler.
	 * 
	 * @param elevator The Elevator to be added to list of elevators.
	 */
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
	/**
	 *  Removes an elevator from the list of elevators handled by the scheduler.
	 *  
	 * @param elevator The Elevator to be removed from the list of elevators.
	 */
	public void removeElevator(Elevator elevator) {
		elevators.remove(elevator);
	}
	
	/**
	 * Method to determine the next type of event in the event queue and call the corresponding handler function.
	 */
	private synchronized void handleEvent() {
		if (!eventQueue.isEmpty()) {
			Event currentEvent = eventQueue.peek();
			if (currentEvent instanceof FloorEvent) {
				handleFloorEvent((FloorEvent) currentEvent);
			} else if (currentEvent instanceof ElevatorEvent) {
				handleElevatorEvent((ElevatorEvent) currentEvent);
			}
		}
	} 
	
	/**
	 * Method that handles a floor event.
	 * It calls moveElevatorToPersonsFloor() to moves the elevator to the person's floor to pick them up and removes the floor event from the event queue
	 * 
	 * @param floorEvent The floor event created when a passenger pushes a button to call an elevator
	 */
	public void handleFloorEvent(FloorEvent floorEvent) {
		moveElevatorToPersonsFloor(floorEvent.getFloor());
		eventQueue.poll();
	}
	
	/**
	 * Method that handles an elevator event.
	 * It calls moveElevatorToRequestedDestination() to move the elevator to the persons' destination floor and removes the elevator event from the event queue
	 * 
	 * @param elevatorEvent The elevator event created when a passenger pushes a destination floor from within elevator
	 */
	public void handleElevatorEvent(ElevatorEvent elevatorEvent) {
		moveElevatorToRequestedDestination(elevatorEvent.getDestinationFloor());
		eventQueue.poll();
	}
	
	/**
	 * Method that adds a floor event and corresponding elevator event to the event queue
	 * 
	 * @param floorEvent The floor event created when a passenger pushes a button to call an elevator
	 */
	public void addEvents(FloorEvent floorEvent) {
		eventQueue.add(floorEvent);
		eventQueue.add(elevators.get(0).getElevatorEventQueue().poll());
	}
	
	/**
	 * Method to move the elevator to the floor of the person requesting an elevator and opens the door.
	 * calls the elevatorArrivesAtFloor method to handle the lamp and button updates when the elevator arrives.
	 * 
	 * @param personsFloor Floor where the person is requesting an elevator.
	 */
	public void moveElevatorToPersonsFloor(Floor personsFloor) {
		while(elevators.get(0).getMotorState() != ElevatorState.IDLE){
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (elevators.get(0).getFloorNumber() != personsFloor.getFloorNumber()) {
			elevators.get(0).moveToFloor(personsFloor);
			this.notifyAll();
		} else {
			System.out.println("The elevator is already on floor: " + personsFloor.getFloorNumber());
			elevators.get(0).openDoors();
			elevatorArrivesAtFloor(elevators.get(0), personsFloor);
		}
		
	}
	
	/**
	 * Method to move the elevator to the floor requested by the person in the elevator and opens the door.
	 * calls the elevatorArrivesAtFloor method to handle the lamp and button updates when the elevator arrives.
	 * 
	 * @param destinationFloor Floor the passenger has requested to travel to.
	 */
	public void moveElevatorToRequestedDestination(Floor destinationFloor) {
		while(elevators.get(0).getMotorState() != ElevatorState.IDLE){
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Elevator button " + destinationFloor.getFloorNumber() + " has been pressed");
		elevators.get(0).getElevatorButtons()[destinationFloor.getFloorNumber() - 1].turnOn();
		elevators.get(0).getElevatorLamps()[destinationFloor.getFloorNumber() - 1].turnOn();
		
		if (elevators.get(0).getFloorNumber() != destinationFloor.getFloorNumber()) {
			elevators.get(0).moveToFloor(destinationFloor);
		} else {
			System.out.println("The elevator is already on floor: " + destinationFloor.getFloorNumber());
			elevators.get(0).openDoors();
			elevatorArrivesAtFloor(elevators.get(0), destinationFloor);
		}
		
		this.notifyAll();
	}
	
	/**
	 * Turns off all Lamps and Buttons at the floor the elevator has just arrived at.
	 * Closes the elevator doors so it's ready to service next request.
	 * 
	 * @param elevator Elevator that just arrived.
	 * @param currentFloor floor the elevator arrived at.
	 * @param isElevatorEvent true if ElevatorEvent, else false
	 */
	public synchronized void elevatorArrivesAtFloor(Elevator elevator, Floor currentFloor) {
		if (currentFloor instanceof MiddleFloor) {
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
	
	/**
	 * Gets the floor object from the list of floors at the given index
	 * 
	 * @param floorIndex The index at which the floor is found in the floors list
	 * @return A floor object from the list of floors at the corresponding index
	 */
	public Floor getFloorByIndex(int floorIndex) {
		return floors.get(floorIndex);
	}
	
	/**
	 * Method to return the elapsed time since the start of the scheduler thread in seconds
	 * 
	 * @return A long for the elapsed time in seconds
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	/**
	 * Checks if there are any floor events on each floor and elevator events in each elevator
	 * 
	 * @return boolean true if there are any events and false otherwise
	 */
    public boolean hasEvents() {
        for (Elevator elevator : elevators) {
            if (elevator.hasEvents()) {
                return true;
            }
        }
    	
        for (Floor floor : floors) {
            if (floor.hasEvents()) {
                return true;
            }
        }
        
        if (!eventQueue.isEmpty()) {
        	return true;
        }
        
        return false;
    }
    
    /**
     * Gets a list of all elevators handled by scheduler
     * 
     * @return list of elevators
     */
    public List<Elevator> getElevators() {
    	return elevators;
    }
    
    /**
     * Gets a list of all floors handled by scheduler
     * 
     * @return list of floor objects
     */
    public List<Floor> getFloors() {
    	return floors;
    }
    
    /**
     * Creates the list of floors handled by the scheduler
     * 
     * @param floors A list of floor objects
     */
    public void setFloors(List<Floor> floors) {
    	this.floors = floors;
    }
	
	/**
	 * Method to be executed when scheduler thread starts. 
	 * Runs so long as there are floor and elevator events to be handled
	 * Updates the elapsed time since the start of scheduler thread
	 */
    @Override
	public void run() {
        Date d = new Date();
        long startTime = d.getTime();
        while(hasEvents()) {
            d = new Date();
            elapsedTime = (d.getTime() - startTime) / 1000;
            handleEvent();
        }
        System.out.println("SCHEDULER THREAD IS DONE");
	}

	/**
	 * Method to get the queue of events containing floor and elevator events
	 * 
	 * @return The queue of events
	 */
	public Queue<Event> getEventQueue() {
		return eventQueue;
	}
}

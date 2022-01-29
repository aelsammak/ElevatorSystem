package scheduler;

import java.util.*;

import elevatorsubsystem.Elevator;
import floorsubsystem.*;
import common.ElevatorState;

public class Scheduler extends Thread {
	
	private List<Elevator> elevators;
	private List<Floor> floors;
	private final PriorityQueue<Event> eventQueue;
	private long elapsedTime;
	
	public Scheduler() {
		elevators = new ArrayList<Elevator>();
		this.eventQueue = new PriorityQueue<>();
		this.elapsedTime = 0;
	}
	
	public void addElevator(Elevator elevator) {
		elevators.add(elevator);
	}
	
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
		// tells elevator to close doors !?!?!? no reference to which elevator
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

package elevatorsubsystem;

import java.util.PriorityQueue;
import common.Common;
import common.ElevatorState;
import floorsubsystem.BottomFloor;
import floorsubsystem.Floor;
import floorsubsystem.TopFloor;
import scheduler.ElevatorEvent;
import scheduler.ElevatorEventComparator;
import scheduler.Scheduler;

/**
 * The Elevator class represents the a single Elevator in the ElevatorSubSystem. 
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class Elevator extends Thread {
	
	private Scheduler scheduler;
	private Floor currentFloor;
	private ArrivalSensor arrivalSensor;
	private final int elevatorNumber;
	private final ElevatorButton[] elevatorButtons;
	private final ElevatorLamp[] elevatorLamps;
	private final Motor motor;
	private final Door door;
	private final PriorityQueue<ElevatorEvent> elevatorEventQueue;
	private static final long LOAD_UNLOAD_TIME = (long) 12.43;
	
	/**
	 * Constructor used to create an instance of the Elevator class
	 * 
	 * @param elevatorNumber - the elevator number
	 * @param scheduler - the system's scheduler
	 */
	public Elevator(int elevatorNumber, Scheduler scheduler) {
		this.elevatorNumber = elevatorNumber;
		this.scheduler = scheduler; 
		this.motor = new Motor(elevatorNumber);
		this.door = new Door(elevatorNumber);
		this.arrivalSensor = new ArrivalSensor(this);
		this.elevatorEventQueue = new PriorityQueue<>(new ElevatorEventComparator());
		this.currentFloor = scheduler.getFloors().get(0);
		
		this.elevatorButtons = new ElevatorButton[Common.NUM_FLOORS];
		this.elevatorLamps = new ElevatorLamp[Common.NUM_FLOORS];
		
        for (int i = 0; i < Common.NUM_FLOORS; i++) {
        	elevatorButtons[i] = new ElevatorButton(i);
        	elevatorLamps[i] = new ElevatorLamp(i);
        }
	}
	
	/**
	 * This method is responsible for moving the Elevator to the passed in destinationFloor and then notify the scheduler of the arrival
	 * 
	 * @param destinationFloor - the floor to which the Elevator must move to
	 */
	public synchronized void moveToFloor(Floor destinationFloor) {
		while (this.getMotorState() != ElevatorState.IDLE) {
			try {
				this.wait();
			} catch (Exception e) {
				System.out.println("In method moveToFloor()");
				e.printStackTrace();
			}
		}
		
		simulateElevatorArrival(destinationFloor);
		
		this.notifyAll();
	}
	
	/**
	 * This method is responsible for simulating the Elevator's movement and arrival at the destination floor
	 * 
	 * @param destinationFloor - the floor to which the Elevator must move to
	 */
	private void simulateElevatorArrival(Floor destinationFloor) {
		if (elevatorShouldMove(destinationFloor)) {
			arrivalSensor.simulateElevatorMovement(currentFloor, destinationFloor);
			this.motor.setElevatorState(ElevatorState.IDLE);
			openDoors();
			scheduler.elevatorArrivesAtFloor(this, currentFloor);
		}
	}
	
	/**
	 * This method is responsible for deciding if the Elevator should move based on the destinationFloor and the currentFloor of the Elevator
	 * 
	 * @param infoStr - the informational string to print to the terminal
	 * @param destinationFloor - the floor to which the Elevator must move to
	 * @return boolean - true if the Elevator should move, else false
	 */
	private boolean elevatorShouldMove(Floor destinationFloor) {
		boolean shouldMove = false;
		if (destinationFloor instanceof TopFloor) {
			if (destinationFloor.getFloorNumber() > currentFloor.getFloorNumber()) {
				this.motor.setElevatorState(ElevatorState.MOVING_UP);
				System.out.println("Elevator is moving up to floor " + destinationFloor.getFloorNumber());
				shouldMove = true;
			} else {
				this.motor.setElevatorState(ElevatorState.IDLE);
				System.out.println("Elevator is already at Top Floor");
			}
		} else if (destinationFloor instanceof BottomFloor) {
			if (destinationFloor.getFloorNumber() < currentFloor.getFloorNumber()) {
				this.motor.setElevatorState(ElevatorState.MOVING_DOWN);
				System.out.println("Elevator is moving down to floor " + destinationFloor.getFloorNumber());
				shouldMove = true;
			} else {
				this.motor.setElevatorState(ElevatorState.IDLE);
				System.out.println("Elevator is already at Bottom Floor");
			}
		} else {
			if (destinationFloor.getFloorNumber() > currentFloor.getFloorNumber()) {
				this.motor.setElevatorState(ElevatorState.MOVING_UP);
				System.out.println("Elevator is moving up to floor " + destinationFloor.getFloorNumber());
				shouldMove = true;
			} else if (destinationFloor.getFloorNumber() < currentFloor.getFloorNumber()) {
				this.motor.setElevatorState(ElevatorState.MOVING_DOWN);
				System.out.println("Elevator is moving down to floor " + destinationFloor.getFloorNumber());
				shouldMove = true;
			} else {
				this.motor.setElevatorState(ElevatorState.IDLE);
				System.out.println("Elevator is already at Floor " + destinationFloor.getFloorNumber());
			}
		}
		
		return shouldMove;
	}
	
	/**
	 * This method is resonsible for opening the Elevator doors
	 */
	public void openDoors() {
		System.out.println("Elevator has arrived at floor " + currentFloor.getFloorNumber());
		this.door.open();
	}
	
	/**
	 * Returns Floor number at which the Elevator is on
	 * 
	 * @return int - the floor number
	 */
	public int getFloorNumber() {
		return currentFloor.getFloorNumber();
	}

	
	/**
	 * This method is responsible for closing the Elevator doors while taking load/unload times into account
	 */
	public synchronized void closeDoors() {
    	
        try {
            wait(LOAD_UNLOAD_TIME * 1000);
        } catch (InterruptedException e) {
        	System.out.println("In method closeDoors()");
            e.printStackTrace();
        }
        
        this.door.close();
        System.out.println("Elevator has closed doors at floor " + currentFloor.getFloorNumber());
        elevatorButtons[currentFloor.getFloorNumber() - 1].turnOff();
        elevatorLamps[currentFloor.getFloorNumber() - 1].turnOff();
    }

	/**
	 * Getter for the currentFloor attribute (Elevators position)
	 * 
	 * @return Floor - the currentFloor
	 */
	public Floor getCurrentFloor() {
		return currentFloor;
	}
	
	/**
	 * This method is responsible for changing the Elevator's position
	 */
	public void changeCurrentFloor() {
		int indexedCurrentFloorNumber = currentFloor.getFloorNumber() - 1;
		if (motor.getElevatorState() == ElevatorState.MOVING_UP) {
			currentFloor = scheduler.getFloorByIndex(indexedCurrentFloorNumber + 1);
		} else {
			currentFloor = scheduler.getFloorByIndex(indexedCurrentFloorNumber - 1);
		}
	}
	
	/**
	 * Getter for the Motor's ElevatorState attribute
	 * 
	 * @return ElevatorState - the state of the Elevator
	 */
	public ElevatorState getMotorState() {
		return motor.getElevatorState();
	}
	
	/**
	 * Setter for the motor's Elevator state
	 * 
	 * @param state - the elevator state
	 */
	public void setMotorState(ElevatorState state) {
		motor.setElevatorState(state);
	}


	/**
	 * Getter for elevatorNumber attribute
	 * 
	 * @return int - the elevatorNumber
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}


	/**
	 * Getter for array of elevatorButtons
	 * 
	 * @return ElevatorButton[] - the elevatorButtons
	 */
	public ElevatorButton[] getElevatorButtons() {
		return elevatorButtons;
	}


	/**
	 * Getter for the array of elevatorLamps
	 * 
	 * @return ElevatorLamp[] - the elevatorLamps
	 */
	public ElevatorLamp[] getElevatorLamps() {
		return elevatorLamps;
	}
	
	/**
	 * This method is responible for checking if the PriorityQueue of ElevatorEvents isEmpty 
	 * 
	 * @return boolean - true if the PriorityQueue of ElevatorEvents is NOT empty, else false 
	 */
	public boolean hasEvents() {
		return !elevatorEventQueue.isEmpty();
	}
	
	/**
	 * The run() method which will be invoked once start() has been called on the Elevator thread.  
	 */
	@Override
	public void run() {
        while(scheduler.hasEvents() && scheduler.isAlive()) {
        	
        }
        System.out.println("ELEVATOR THREAD IS DONE");
	}

	/**
	 * Getter for the PriorityQueue of ElevatorEvents
	 * 
	 * @return PriorityQueue<ElevatorEvent> - PriorityQueue of ElevatorEvents
	 */
	public PriorityQueue<ElevatorEvent> getElevatorEventQueue() {
		return elevatorEventQueue;
	}
	
	/**
	 * This method is responsible for adding ElevatorEvents to the PriorityQueue of ElevatorEvents
	 * 
	 * @param elevatorEvent - the elevatorEvent to add
	 */
	public void addElevatorEvent(ElevatorEvent elevatorEvent) {
		this.elevatorEventQueue.add(elevatorEvent);
	}

}

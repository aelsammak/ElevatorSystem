package elevatorsubsystem;

import java.util.LinkedList;
import java.util.Queue;
import common.Common;
import common.ElevatorState;
import floorsubsystem.BottomFloor;
import floorsubsystem.Floor;
import floorsubsystem.TopFloor;
import scheduler.ElevatorEvent;
import scheduler.Scheduler;

public class Elevator extends Thread {
	
	private Scheduler scheduler;
	private Floor currentFloor;
	private ArrivalSensor arrivalSensor;
	private final int elevatorNumber;
	private final ElevatorButton[] elevatorButtons;
	private final ElevatorLamp[] elevatorLamps;
	private final Motor motor;
	private final Door door;
	private final Queue<ElevatorEvent> elevatorEventQueue;
	private static final long LOAD_UNLOAD_TIME = (long) 12.43;
	
	public Elevator(int elevatorNumber, Scheduler scheduler) {
		this.elevatorNumber = elevatorNumber;
		this.scheduler = scheduler; 
		this.motor = new Motor(elevatorNumber);
		this.door = new Door(elevatorNumber);
		this.arrivalSensor = new ArrivalSensor(this);
		this.elevatorEventQueue = new LinkedList<>();
		
		this.elevatorButtons = new ElevatorButton[Common.NUM_FLOORS];
		this.elevatorLamps = new ElevatorLamp[Common.NUM_FLOORS];
		
        for (int i = 0; i <= Common.NUM_FLOORS; i++) {
        	elevatorButtons[i] = new ElevatorButton(i);
        	elevatorLamps[i] = new ElevatorLamp(i);
        }
	}
	
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
	
	private void simulateElevatorArrival(Floor destinationFloor) {
		String infoStr = "";
		if (elevatorShouldMove(infoStr, destinationFloor)) {
			System.out.println(infoStr);
			arrivalSensor.simulateElevatorMovement(currentFloor, destinationFloor);
			this.motor.setElevatorState(ElevatorState.IDLE);
			openDoors();
			scheduler.elevatorArrivesAtFloor(currentFloor);
		} else {
			System.out.println(infoStr);
		}
	}
	
	private boolean elevatorShouldMove(String infoStr, Floor destinationFloor) {
		boolean shouldMove = false;
		if (destinationFloor instanceof TopFloor) {
			if (destinationFloor.getFloorNumber() < currentFloor.getFloorNumber()) {
				this.motor.setElevatorState(ElevatorState.MOVING_DOWN);
				infoStr = "Elevator is moving down to floor " + destinationFloor.getFloorNumber();
				shouldMove = true;
			} else {
				this.motor.setElevatorState(ElevatorState.IDLE);
				infoStr = "Elevator is already at Top Floor";
			}
		} else if (destinationFloor instanceof BottomFloor) {
			if (destinationFloor.getFloorNumber() > currentFloor.getFloorNumber()) {
				this.motor.setElevatorState(ElevatorState.MOVING_UP);
				infoStr = "Elevator is moving up to floor " + destinationFloor.getFloorNumber();
				shouldMove = true;
			} else {
				this.motor.setElevatorState(ElevatorState.IDLE);
				infoStr = "Elevator is already at Bottom Floor";
			}
		} else {
			if (destinationFloor.getFloorNumber() > currentFloor.getFloorNumber()) {
				this.motor.setElevatorState(ElevatorState.MOVING_UP);
				infoStr = "Elevator is moving up to floor " + destinationFloor.getFloorNumber();
			} else {
				this.motor.setElevatorState(ElevatorState.MOVING_DOWN);
				infoStr = "Elevator is moving down to floor " + destinationFloor.getFloorNumber();
			}
			shouldMove = true;
		}
		
		return shouldMove;
	}
	
	public void openDoors() {
		System.out.println("Elevator has arrived at floor " + currentFloor.getFloorNumber());
		this.door.open();
	}

	public synchronized void closeDoors() {
    	
        try {
            wait(LOAD_UNLOAD_TIME * 1000);
        } catch (InterruptedException e) {
        	System.out.println("In method closeDoors()");
            e.printStackTrace();
        }
        
        this.door.close();
        System.out.println("Elevator has closed doors at floor " + currentFloor.getFloorNumber());
        elevatorButtons[currentFloor.getFloorNumber()].turnOff();
        elevatorLamps[currentFloor.getFloorNumber()].turnOff();
    }

	/**
	 * @return the currentFloor
	 */
	public Floor getCurrentFloor() {
		return currentFloor;
	}
	
	public void changeCurrentFloor() {
		int indexedCurrentFloorNumber = currentFloor.getFloorNumber() - 1;
		if (motor.getElevatorState() == ElevatorState.MOVING_UP) {
			currentFloor = scheduler.getFloorByIndex(indexedCurrentFloorNumber + 1);
		} else {
			currentFloor = scheduler.getFloorByIndex(indexedCurrentFloorNumber - 1);
		}
	}
	
	public ElevatorState getMotorState() {
		return motor.getElevatorState();
	}


	/**
	 * @return the elevatorNumber
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}


	/**
	 * @return the elevatorButtons
	 */
	public ElevatorButton[] getElevatorButtons() {
		return elevatorButtons;
	}


	/**
	 * @return the elevatorLamps
	 */
	public ElevatorLamp[] getElevatorLamps() {
		return elevatorLamps;
	}
	
	public boolean hasEvents() {
		return !elevatorEventQueue.isEmpty();
	}
	
	@Override
	public void run() {
        while(scheduler.hasEvents()) {

        }
	}

	public Queue<ElevatorEvent> getElevatorEventQueue() {
		return elevatorEventQueue;
	}
	
	public void addElevatorEvent(ElevatorEvent elevatorEvent) {
		this.elevatorEventQueue.add(elevatorEvent);
	}

}

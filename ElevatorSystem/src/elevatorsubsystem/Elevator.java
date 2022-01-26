package elevatorsubsystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.Common;
import common.ElevatorState;
import floorsubsystem.Floor;
import scheduler.Scheduler;

public class Elevator extends Thread {
	
	private Scheduler scheduler;
	private List<Floor> destinations;
	private Floor currentFloor;
	private ArrivalSensor arrivalSensor;
	private Set<Integer> passengers;
	private final int elevatorNumber;
	private final ElevatorButton[] elevatorButtons;
	private final ElevatorLamp[] elevatorLamps;
	private final Motor motor;
	private final Door door;
	private static final long LOAD_UNLOAD_TIME = (long) 12.43;
	
	public Elevator(int elevatorNumber, Scheduler scheduler) {
		this.elevatorNumber = elevatorNumber;
		this.scheduler = scheduler;
		this.destinations = new ArrayList<>(); 
		this.passengers = new HashSet<>();
		this.motor = new Motor(elevatorNumber);
		this.door = new Door(elevatorNumber);
		this.arrivalSensor = new ArrivalSensor();
		
		this.elevatorButtons = new ElevatorButton[Common.NUM_FLOORS];
		this.elevatorLamps = new ElevatorLamp[Common.NUM_FLOORS];
		
        for (int i = 0; i <= Common.NUM_FLOORS; i++) {
        	elevatorButtons[i] = new ElevatorButton(i);
        	elevatorLamps[i] = new ElevatorLamp(i);
        }
	}
	
	
	public synchronized void moveToFloor(Floor destinationFloor) {
		while(motor.getElevatorState() != ElevatorState.IDLE) {
			try {
				this.wait();
			} catch (Exception e) {
				System.out.println("In method moveToFloor()");
				e.printStackTrace();
			}
		}
		
		String infoStr;
		if (destinationFloor.getFloorNumber() > currentFloor.getFloorNumber()) {
			this.motor.setElevatorState(ElevatorState.MOVING_UP);
			infoStr = "Elevator is moving up to floor " + destinationFloor.getFloorNumber();
		} else {
			this.motor.setElevatorState(ElevatorState.MOVING_DOWN);
			infoStr = "Elevator is moving down to floor " + destinationFloor.getFloorNumber();
		}
		
		System.out.println(infoStr);
		arrivalSensor.simulateElevatorMovement(currentFloor, destinationFloor);
		this.currentFloor = destinationFloor;
		
		this.motor.setElevatorState(ElevatorState.IDLE);
		openDoors();
		scheduler.elevatorArrivesAtFloor(currentFloor);
		
		this.notifyAll();
		
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
	
	public List<Floor> getDestinations() {
		return destinations;
	}

	/**
	 * @return the currentFloor
	 */
	public Floor getCurrentFloor() {
		return currentFloor;
	}


	/**
	 * @return the passengers
	 */
	public Set<Integer> getPassengers() {
		return passengers;
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
	
	@Override
	public void run() {
//        while(scheduler.hasEvents())
//        {
//
//        }
	}
}

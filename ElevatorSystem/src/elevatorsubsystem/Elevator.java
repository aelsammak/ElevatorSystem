package elevatorsubsystem;

import java.util.HashSet;
import java.util.Set;

import common.Common;

public class Elevator extends Thread {
	
	// private Scheduler scheduler;
	// private Floor[] destinations; KEEP TRACK OF DESTINATIONS
	// private Floor currentFloor;
	private ArrivalSensor arrivalSensor;
	private final int elevatorNumber;
	private final Set<Integer> passengers;
	private final ElevatorButton[] elevatorButtons;
	private final Motor motor;
	private final Door door;
	
	public Elevator(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		this.passengers = new HashSet<>();
		this.motor = new Motor(this.elevatorNumber);
		this.door = new Door(elevatorNumber);
		this.arrivalSensor = new ArrivalSensor(this);
		
		this.elevatorButtons = new ElevatorButton[Common.NUM_FLOORS];
		
        for (int i = 0; i <= Common.NUM_FLOORS; i++) {
        	elevatorButtons[i] = new ElevatorButton(i);
        }
		
	}
	
	@Override
	public void run() {
		
	}
}

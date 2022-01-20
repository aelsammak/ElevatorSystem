package elevatorsubsystem;

import common.ElevatorState;

public class Motor {
	
	private final int elevatorNumber;
	private ElevatorState elevatorState;
	
	public Motor(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		this.setElevatorState(ElevatorState.IDLE);
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public ElevatorState getElevatorState() {
		return elevatorState;
	}

	public void setElevatorState(ElevatorState elevatorState) {
		this.elevatorState = elevatorState;
	}

}

package scheduler;

import java.util.Date;

import elevatorsubsystem.Elevator;

public class ElevatorEvent extends Event {
	
	private Elevator elevator;
	private int buttonPressed;
	//private ElevatorState elevatorState;
	
	public ElevatorEvent(Elevator elevator, int buttonPressed, Date time) {
		super(time);
		this.elevator = elevator;
		this.buttonPressed = buttonPressed;
	}
	
	public Elevator getElevator() {
		return elevator;
	}
	
	public int getButtonPressed() {
		return buttonPressed;
	}
	
}

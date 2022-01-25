package elevatorsubsystem;

public class ElevatorButton {
	
	private final int floorNumber;
	private boolean isPressed;
	
	public ElevatorButton(int floorNumber) {
		this.floorNumber = floorNumber;
		this.isPressed = false;
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public boolean isPressed() {
		return isPressed;
	}
	
	public void turnOn() {
		this.isPressed = true;
	}
	
	public void turnOff() {
		this.isPressed = false;
	}
	
}

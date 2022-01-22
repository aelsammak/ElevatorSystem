package elevatorsubsystem;

public class ElevatorButton {
	
	private final int floorNumber;
	private boolean isPressed;
	private final ElevatorLamp lamp;
	
	public ElevatorButton(int floorNumber) {
		this.floorNumber = floorNumber;
		this.isPressed = false;
		this.lamp = new ElevatorLamp(floorNumber);
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public boolean isPressed() {
		return isPressed;
	}
	
	public void turnOn() {
		this.isPressed = true;
		this.lamp.turnOn();
	}
	
	public void turnOff() {
		this.isPressed = false;
		this.lamp.turnOff();
	}
	
}

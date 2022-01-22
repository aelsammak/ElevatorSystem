package floorsubsystem;

public class FloorButton {
	private boolean isPressed;
	private final int floorNumber;
	private final boolean isUp;
	private final FloorLamp lamp;
	
	public FloorButton(int floorNumber, boolean isUp) {
		this.floorNumber = floorNumber;
		this.isUp = isUp;
		this.isPressed = false;
		this.lamp = new FloorLamp();
	}
	
	public boolean isPressed() {
		return isPressed;
	}
	
	public boolean isUp() {
		return isUp;
	}
	
	public int getFloorNumber() {
		return this.floorNumber;
	}

	public FloorLamp getLamp() {
		return lamp;
	}

	public void turnOn() {
		this.isPressed = true;  // may need time
		this.lamp.turnOn();
	}
	
	public void turnOff() {
		this.isPressed = false;  // may need time
		this.lamp.turnOff();
	}
	
	
	
	
}

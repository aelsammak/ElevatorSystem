package floorsubsystem;

public class FloorButton {
	private boolean isPressed;
	private final int floorNumber;
	private final boolean isUp;
	
	
	public FloorButton(int floorNumber, boolean isUp) {
		this.floorNumber = floorNumber;
		this.isUp = isUp;
		this.isPressed = false;
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

	public void turnOn() {
		this.isPressed = true;  
	}
	
	public void turnOff() {
		this.isPressed = false;  
	}
	
	
	
	
}

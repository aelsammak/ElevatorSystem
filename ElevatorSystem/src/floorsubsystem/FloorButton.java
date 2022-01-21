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

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}
	
	public boolean isUp() {
		return isUp;
	}
	
	public int getFloorNumber() {
		return this.floorNumber;
	}

	
	
	
}

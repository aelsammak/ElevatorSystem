package elevatorsubsystem;

public class Door {
	
	private final int elevatorNumber;
	private boolean isOpen;
	
	public Door(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		this.isOpen = false;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public boolean open() {
		return this.isOpen = true;
	}
	
	public boolean close() {
		return this.isOpen = false;
	}

	public boolean isOpen() {
		return this.isOpen;
	}
	
}

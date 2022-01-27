package elevatorsubsystem;

public class ElevatorLamp {
	
	private final int floorNumber;
	private boolean isTurnedOn;
	
	public ElevatorLamp(int floorNumber) {
		this.floorNumber = floorNumber;
		this.isTurnedOn = false;
	}
	
	public void turnOn() {
		this.isTurnedOn = true;
	}
	
	public void turnOff() {
		this.isTurnedOn = false;
	}
	
	public boolean isTurnedOn() {
		return this.isTurnedOn;
	}


	public int getFloorNumber() {
		return floorNumber;
	}

}

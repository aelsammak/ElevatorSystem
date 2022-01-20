package elevatorsubsystem;

public class ElevatorLamp {
	
	private final int elevatorNumber;
	private final int floorNumber;
	private boolean isTurnedOn;
	
	public ElevatorLamp(int elevatorNumber, int floorNumber) {
		this.elevatorNumber = elevatorNumber;
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

	public int getElevatorNumber() {
		return elevatorNumber;
	}


	public int getFloorNumber() {
		return floorNumber;
	}

}

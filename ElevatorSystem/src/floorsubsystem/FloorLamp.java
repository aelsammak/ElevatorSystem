package floorsubsystem;

public class FloorLamp {
	private boolean isTurnedOn;
	
	public FloorLamp() {
		this.isTurnedOn = false;
	}

	public boolean isTurnedOn() {
		return isTurnedOn;
	}
	
	public void turnOn() {
		this.isTurnedOn = true;
	}
	
	public void turnOff() {
		this.isTurnedOn = false;
	}
	
}

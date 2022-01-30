package floorsubsystem;

import scheduler.Scheduler;

public class BottomFloor extends Floor {
	
	private final FloorButton upBtn;
	private final FloorLamp upLamp;
	
	public BottomFloor(int floorNumber, Scheduler scheduler) {
		super(floorNumber, scheduler);
		this.upBtn = new FloorButton(floorNumber, true);
		this.upLamp = new FloorLamp();
	}
	
	public void turnOnUpUpButton() {
		this.upBtn.turnOn();
	}
	
	public void turnOffUpButton() {
		this.upBtn.turnOff();
	}
	
	public FloorLamp getUpLamp() {
		return upLamp;
	}
}

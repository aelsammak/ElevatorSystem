package floorsubsystem;

import scheduler.Scheduler;

public class TopFloor extends Floor {
	
	private final FloorButton downBtn;
	private final FloorLamp downLamp;
	
	public TopFloor(int floorNumber, Scheduler scheduler) {
		super(floorNumber, scheduler);
		this.downBtn = new FloorButton(floorNumber, false);
		this.downLamp = new FloorLamp();
	}
	
	public void turnOnDownButton() {
		this.downBtn.turnOn();
	}
	
	public void turnOffDownButton() {
		this.downBtn.turnOff();
	}
	
	public FloorLamp getDownLamp() {
		return downLamp;
	}

}

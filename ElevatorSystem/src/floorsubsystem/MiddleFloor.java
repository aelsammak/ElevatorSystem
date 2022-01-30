package floorsubsystem;

import scheduler.Scheduler;

public class MiddleFloor extends Floor {
	
	private final FloorButton upBtn;
	private final FloorButton downBtn;
	private final FloorLamp upLamp;
	private final FloorLamp downLamp;
	
	public MiddleFloor(int floorNumber, Scheduler scheduler) {
		super(floorNumber, scheduler);
		this.upBtn = new FloorButton(floorNumber, true);
		this.downBtn = new FloorButton(floorNumber, false);
		this.upLamp = new FloorLamp();
		this.downLamp = new FloorLamp();
	}
	
	public void turnOnUpButton() {
		this.upBtn.turnOn();
	}
	
	public void turnOffUpButton() {
		this.upBtn.turnOff();
	}
	
	public void turnOnDownButton() {
		this.downBtn.turnOn();
	}
	
	public void turnOffDownButton() {
		this.downBtn.turnOff();
	}

	public FloorLamp getUpLamp() {
		return upLamp;
	}

	public FloorLamp getDownLamp() {
		return downLamp;
	}
}

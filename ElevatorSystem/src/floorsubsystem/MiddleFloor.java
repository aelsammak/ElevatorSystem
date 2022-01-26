package floorsubsystem;

import java.util.PriorityQueue;

import scheduler.FloorEvent;
import scheduler.Scheduler;

public class MiddleFloor extends Floor {
	
	private final FloorButton upBtn;
	private final FloorButton downBtn;
	
	private final FloorLamp upLamp;
	private final FloorLamp downLamp;
	
	public MiddleFloor(int floorNumber, Scheduler scheduler, PriorityQueue<FloorEvent> floorEventQueue) {
		super(floorNumber, scheduler, floorEventQueue);
		this.upBtn = new FloorButton(floorNumber, true);
		this.downBtn = new FloorButton(floorNumber, false);
		this.upLamp = new FloorLamp();
		this.downLamp = new FloorLamp();
	}
	
	public void pressDownButton() {
		this.downBtn.turnOn();
	}
	
	public void pressUpButton() {
		this.upBtn.turnOn();
	}

	public FloorLamp getUpLamp() {
		return upLamp;
	}

	public FloorLamp getDownLamp() {
		return downLamp;
	}
}

package floorsubsystem;

import java.util.PriorityQueue;

import scheduler.FloorEvent;
import scheduler.Scheduler;

public class BottomFloor extends Floor {
	
	private final FloorButton upBtn;
	
	private final FloorLamp upLamp;
	
	public BottomFloor(int floorNumber, Scheduler scheduler, PriorityQueue<FloorEvent> floorEventQueue) {
		super(floorNumber, scheduler, floorEventQueue);
		this.upBtn = new FloorButton(floorNumber, true);
		this.upLamp = new FloorLamp();
	}
	
	public void pressUpButton() {
		this.upBtn.turnOn();
	}

	public FloorLamp getUpLamp() {
		return upLamp;
	}
}

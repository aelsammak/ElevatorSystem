package floorsubsystem;

import java.util.PriorityQueue;

import scheduler.FloorEvent;
import scheduler.Scheduler;

public class TopFloor extends Floor {
	
	private final FloorButton downBtn;
	
	private final FloorLamp downLamp;
	
	public TopFloor(int floorNumber, Scheduler scheduler, PriorityQueue<FloorEvent> eventQueue) {
		super(floorNumber, scheduler, eventQueue);
		this.downBtn = new FloorButton(floorNumber, false);
		this.downLamp = new FloorLamp();
	}
	
	public void pressDownButton() {
		this.downBtn.turnOn();
	}

	public FloorLamp getDownLamp() {
		return downLamp;
	}

}

<<<<<<< HEAD
package floorsubsystem;

import java.util.List;

import common.Person;

public class BottomFloor extends Floor {
	
	private final FloorButton upBtn;
	
	private final FloorLamp upLamp;
	
	public BottomFloor(int floorNumber, List<Person> peopleWaiting) {
		super(floorNumber, peopleWaiting);
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
=======
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
	
	public void pressUpButton() {
		this.upBtn.turnOn();
	}

	public FloorLamp getUpLamp() {
		return upLamp;
	}
}
>>>>>>> refs/heads/AdiImpl

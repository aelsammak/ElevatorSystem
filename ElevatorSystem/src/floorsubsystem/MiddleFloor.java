package floorsubsystem;

import java.util.List;

import common.Person;

public class MiddleFloor extends Floor {
	
	private final FloorButton upBtn;
	private final FloorButton downBtn;
	
	public MiddleFloor(int floorNumber, List<Person> peopleWaiting) {
		super(floorNumber, peopleWaiting);
		this.upBtn = new FloorButton(floorNumber, true);
		this.downBtn = new FloorButton(floorNumber, false);
	}
	
	public void pressDownButton() {
		this.downBtn.turnOn();
	}
	
	public void pressUpButton() {
		this.upBtn.turnOn();
	}
}

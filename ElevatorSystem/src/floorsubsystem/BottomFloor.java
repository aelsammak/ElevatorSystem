package floorsubsystem;

import java.util.List;

import common.Person;

public class BottomFloor extends Floor {
	
	private final FloorButton upBtn;
	
	public BottomFloor(int floorNumber, List<Person> peopleWaiting) {
		super(floorNumber, peopleWaiting);
		this.upBtn = new FloorButton(floorNumber, true);
	}
	
	public void pressUpButton() {
		this.upBtn.turnOn();
	}
}

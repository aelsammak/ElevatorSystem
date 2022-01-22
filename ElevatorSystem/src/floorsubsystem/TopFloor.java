package floorsubsystem;

import java.util.List;

import common.Person;

public class TopFloor extends Floor {
	
	private final FloorButton downBtn;
	
	public TopFloor(int floorNumber, List<Person> peopleWaiting) {
		super(floorNumber, peopleWaiting);
		this.downBtn = new FloorButton(floorNumber, false);
	}
	
	public void pressDownButton() {
		this.downBtn.turnOn();
	}
}

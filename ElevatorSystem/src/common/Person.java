package common;

import floorsubsystem.FloorLamp;

public class Person {
	private final String arrivalTime;
	private int currentFloorNumber;
	private final int destinationFloorNumber;
	private final boolean isUp;
	
	public Person(String arrivalTime, int currentFloorNumber, int destinationFloorNumber, boolean isUp) {
		this.arrivalTime = arrivalTime;
		this.currentFloorNumber = currentFloorNumber;
		this.destinationFloorNumber = destinationFloorNumber;
		this.isUp = isUp;
	}
	
	public int getCurrentFloorNumber() {
		return currentFloorNumber;
	}

	public void setCurrentFloorNumber(int currentFloorNumber) {
		this.currentFloorNumber = currentFloorNumber;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public int getDestinationFloorNumber() {
		return destinationFloorNumber;
	}

	public boolean isUp() {
		return isUp;
	}
}

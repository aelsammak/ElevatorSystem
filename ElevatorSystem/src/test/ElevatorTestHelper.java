package test;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import common.Common;
import common.Common.ELEV_ERROR;
import elevatorsubsystem.ElevatorSubsystem;
import elevatorsubsystem.MotorState;
import floorsubsystem.FileLoader;
import floorsubsystem.FloorSubsystem;
import scheduler.ElevatorState;
import scheduler.Scheduler;

/**
 * Just a helper class to mimic the work of the schedule on a series of elevators, code taken from the Scheduler class
 * @author Ben Herriott
 *
 */
public class ElevatorTestHelper {
	private ElevatorState[] elevatorStates;
	public boolean isStuck; 
	
	public ElevatorTestHelper() {
		this.elevatorStates = new ElevatorState[Common.NUM_ELEVATORS];
	}
	
	public void setSpecficElevatorState(int i, ElevatorState ev) {
		this.elevatorStates[i] = ev; 
	}
	
	public void setElevatorState(ElevatorState[] ev) {
		this.elevatorStates = ev; 
	}
	
	public MotorState handleError(MotorState ms, boolean doorsOpen, int floor) {
		// check if fault has not occurred, if it hasnt then return false, else continue till end and return true
		boolean isHardFault = false;
		isStuck = true;
		if (floor != -1 && doorsOpen) { 
			isHardFault = true;
		}
		Common.ELEV_ERROR errorType;
		MotorState motorState;
		if (isHardFault) {
			errorType = ELEV_ERROR.STUCK_BETWEEN;
			motorState = MotorState.HARD_FAULT;
			System.out.println("/************HARD FAULT************\\");
			System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + 1 
					+ " is stuck between Floor #" + floor + " and Floor #" 
					+ (ms == MotorState.MOVING_UP ? floor + 1 : floor - 1));
			System.out.println("/**********************************\\");
		} else {
			errorType = ELEV_ERROR.DOOR_CLOSE;
			motorState = MotorState.TRANSIENT_FAULT;
			System.out.println("/************TRANSIENT FAULT************\\");
			System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + 1 
					+ " is stuck door " + (doorsOpen ?  "open" : "closed") + " at Floor #" + 1);
			System.out.println("/**********************************\\");
		}
		
		return motorState; 
	}
		
	
	public int findClosestElevator(int floorNumber, boolean isFloorBtnUp) {
		int result = 0;
		int[] distances = new int[Common.NUM_ELEVATORS];
		
		// Find every elevator's distance fomr the current floor
		for (int i = 0; i < Common.NUM_ELEVATORS; i++) {
			if (elevatorStates[i] != null) {
				int dis = findDistance(floorNumber, isFloorBtnUp, elevatorStates[i]);
				distances[i] = dis;
			} else{
				distances[i] = 99999;
			}
		}
	
		// Find the closest elevator 
		int index = 0;
		int min = distances[0];
		
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] < min) {
				min = distances[i];
				index = i;
			}
		}
		
		result = index + 1;
		return result;
	}
	
	public int findDistance(int floor, boolean isUp, ElevatorState elevatorState) {
		int distance = 0;
		int elevCurrPosition = elevatorState.getCurrentFloorNumber();
		int elevDir = elevatorState.getMotorDir();
		int elevDest = elevatorState.getTargetFloor();
	
		int floorDiff = elevCurrPosition - floor; 
		if (elevCurrPosition == elevDest) {
			distance = Math.abs(floorDiff);
		} else if ((floorDiff > 0 && !isUp && elevDir == -1) || (floorDiff < 0 && isUp && elevDir == 1)) {
			distance = Math.min(Math.abs(elevCurrPosition - floor), Math.abs(elevCurrPosition - floor));
		} else { 
			if (elevDir == -1) { 
				distance = elevCurrPosition + floor;
			} else { 
				distance = (Common.NUM_FLOORS - elevCurrPosition) + (Common.NUM_FLOORS - floor);
			}
		}
		return distance;
	}
}

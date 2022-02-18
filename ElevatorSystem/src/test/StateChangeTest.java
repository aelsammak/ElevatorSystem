package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import common.Common;
import common.Config;
import elevatorsubsystem.Elevator;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.Scheduler;
import scheduler.SchedulerState;
import elevatorsubsystem.ElevatorState;

/**
 * File contains test cases specifically focused on testing state changes of elevators as they move up and down floors.
 *  
 * @author Ben Herriott
 * @version 1.0 
 */
public class StateChangeTest {
	private Elevator ele1; 
	private Scheduler scheduler;
	private ArrayList<Floor> f; 
	
	// Initializes variables for the test cases within the file
	@Before 
	public void initializeState() {
		Common.SIMULATION_START_DATE = new Date(); 
		scheduler = new Scheduler();
        f = new ArrayList<Floor>(); 
		Common.setMaxNumFloors(3);
		Config config = null;
		try {
			config = new Config();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        FloorSubsystem.createFloors(4, scheduler, f);
		ele1 = new Elevator(1, scheduler, config); 
		scheduler.addElevator(ele1);
	}
	
	/**
	 * Test whether the current state of the Elevator will be correctly changed 
	 * when the elevator attempts to move floor, test move up
	 * 
	 */
	@Test
	public void switchToMoveStateUp() {
		// check initial state is IDLE
		assertEquals(ele1.getMotorState(), ElevatorState.IDLE);
		// mimics calls to move elevators, method used within core logic of elevator movement to change the state
		// parse a higher floor to visit
		ele1.elevatorShouldMove(f.get(2));
		assertEquals(ele1.getMotorState(), ElevatorState.MOVING_UP);
	}
	
	/**
	 * Test whether the current state of the Elevator will be correctly changed 
	 * when the elevator attempts to move floor, test move down
	 * 
	 */
	@Test
	public void switchToMoveStateDown() {
		// moving the elevator to the 2nd floor 
		ele1.setMotorState(ElevatorState.MOVING_UP);
		ele1.changeCurrentFloor();
		
		// Setting to IDLE as we've reached the second floor
		ele1.setMotorState(ElevatorState.IDLE);
		assertEquals(ele1.getMotorState(), ElevatorState.IDLE);
		
		// mimic movement to ground level
		ele1.elevatorShouldMove(f.get(0));
		assertEquals(ele1.getMotorState(), ElevatorState.MOVING_DOWN);
	}
	
	/**
	 * Testing changing the state of the Scheduler
	 * 
	 */
	@Test
	public void switchSchedulerState() {
		assertEquals(scheduler.getSchedulerState(), SchedulerState.IDLE);
		
		// This test is subject to change 
		scheduler.setSchedulerState(SchedulerState.HANDLING_EVENTS);
		assertEquals(scheduler.getSchedulerState(), SchedulerState.HANDLING_EVENTS);
	}

}

package test;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import scheduler.ElevatorState;


/**
 * Tests the implemented elevator selection algorithm to see based on a floor request which elevator will be selected to best facilitate the request. 
 * 
 * @author Ben Herriott
 * @version 1.0
 */
public class ElevatorTest {
	private ElevatorTestHelper ev; 
	
	@Before
	public void initElevatorRequests() {
		ev = new ElevatorTestHelper();
	}
	
	/**
	 * Test all elevators are at the first floor which one is selected
	 */
	@Test
	public void testBaseElevatorSelection() {
		ElevatorState[] states = {new ElevatorState(1), new ElevatorState(2), new ElevatorState(3), new ElevatorState(4)}; 
		ev.setElevatorState(states);
		assertEquals(1, ev.findClosestElevator(2, true)); 
	}
	
	/**
	 * There is an elevator at IDLE at the current floor
	 */
	@Test
	public void testElevatorAtCurrentFloor() {
		ElevatorState[] states = {new ElevatorState(1), new ElevatorState(2,2,0,2), new ElevatorState(3), new ElevatorState(4)}; 
		System.out.println(states[1]);
		ev.setElevatorState(states);
		assertEquals(2, ev.findClosestElevator(2, true)); 
	}
	
	/**
	 * There is an elevator moving along the same pass as the selected floor
	 */
	@Test
	public void testFindClosestElevatorCorrectDirection() {
		ElevatorState[] states = {new ElevatorState(1), new ElevatorState(2,4,-1,1), new ElevatorState(3), new ElevatorState(4)}; 
		System.out.println(states[1]);
		ev.setElevatorState(states);
		assertEquals(2, ev.findClosestElevator(3, false)); 
	}
	
	/**
	 * There is an elevator moving along the same pass as the selected floor (direction is not correct)
	 */
	@Test
	public void testFindClosestElevatorIncorrectDirection() {
		ElevatorState[] states = {new ElevatorState(1), new ElevatorState(2,4,1,6), new ElevatorState(3), new ElevatorState(4)}; 
		System.out.println(states[1]);
		ev.setElevatorState(states);
		assertEquals(1, ev.findClosestElevator(3, false)); 
	}
	
	/**
	 * Elevator moving up towards the correct floor but not all the way there
	 */
	@Test
	public void testFindClosestElevatorCorrectDirection1() {
		ElevatorState[] states = {new ElevatorState(2,4,1,6), new ElevatorState(2,3,-1,1), new ElevatorState(3), new ElevatorState(4)}; 
		System.out.println(states[1]);
		ev.setElevatorState(states);
		assertEquals(1, ev.findClosestElevator(7, true)); 
	}
	
	/**
	 * Elevators same distance but moving in opposite direction which one is selected
	 */
	@Test
	public void testSelectElevatorMovingThroughSameRoute() {
		ElevatorState[] states = {new ElevatorState(1), new ElevatorState(2,4,-1,1), new ElevatorState(2,4,1,6), new ElevatorState(4)}; 
		System.out.println(states[1]);
		ev.setElevatorState(states);
		assertEquals(3, ev.findClosestElevator(5, true)); 
	}
	
	/**
	 * Elevators same distance but moving in opposite direction which one is selected (floor is out of bounds of elevator moving the correct way)
	 */
	@Test
	public void testSelectElevatorMovingThroughSameRouteInvalid() {
		ElevatorState[] states = {new ElevatorState(1), new ElevatorState(2,4,-1,1), new ElevatorState(2,4,1,6), new ElevatorState(4)}; 
		System.out.println(states[1]);
		ev.setElevatorState(states);
		assertEquals(1, ev.findClosestElevator(5, false)); 
	}
	
	/**
	 * ALL Elevators far from floor but one is already descending in the correct direction 
	 */
	@Test
	public void testHighElevatorStartPositionDirectionBased() {
		ElevatorState[] states = {new ElevatorState(1,14,1,16), new ElevatorState(2,16,-1,12), new ElevatorState(2,14,1,16), new ElevatorState(4,4,1,16)}; 
		System.out.println(states[1]);
		ev.setElevatorState(states);
		assertEquals(2, ev.findClosestElevator(5, false)); 
	}
}

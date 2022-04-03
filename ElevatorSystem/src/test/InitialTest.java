package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.junit.Before;
import org.junit.Test;

import common.Config;
import elevatorsubsystem.Elevator;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.FileLoader;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.Scheduler;
/**
 * File contains test cases specifically focused on testing reading and parsing of data from csv's to the floor and elevator subsystems.
 *  
 * @author Ben Herriott
 * @version 1.0 
 */
public class InitialTest {
	
	private FileLoader fileLoader;
	private HashMap<Integer, ArrayList<Integer>> m; 
	
	@Before
	public void initFileParseTest() throws Exception {
		fileLoader = new FileLoader();
	}
	
	// Test elevator parse of request locations for an elevator from the csv
	@Test
	public void testElevatorRequestLocationParse() throws Exception {
		while (fileLoader.nextLine()) {}
		
		m = fileLoader.getDestinations();
		assertTrue(m.containsKey(2));
		assertTrue(m.containsKey(4)); 
		assertTrue(m.containsKey(1)); 
	}
	
	// Test the destination that are parsed from the csv
	@Test
	public void testElevatorRequestDestinationParse() throws Exception {
		while (fileLoader.nextLine()) {}
		
		m = fileLoader.getDestinations();

		assertTrue(m.get(1).contains(10));
		assertTrue(m.get(12).contains(1));
		assertTrue(m.get(14).contains(7));
	}
	
	// Test the direction of the request as parsed from the csv
	@Test
	public void testElevatorDirectionParse() throws Exception {
		ArrayList<Boolean> dir = new ArrayList<Boolean>();
		
		dir.add(fileLoader.isUp());
		while (fileLoader.nextLine()) {
			dir.add(fileLoader.isUp());
		}
		
		assertEquals(true, dir.get(0)); 
		assertEquals(false, dir.get(1)); 
		assertEquals(true, dir.get(2)); 
	}
	
	// Test the faults are read correctly by the fileloader
	@Test
	public void testElevatorFaultsAreParsed() throws Exception {
		while (fileLoader.nextLine()) {}
		
		HashMap<Integer, LocalTime> faults = fileLoader.getFaults(); 
		assertTrue(faults.containsKey(1)); 
		assertTrue(faults.containsKey(2));  
	}
}

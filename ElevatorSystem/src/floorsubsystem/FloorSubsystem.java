package floorsubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import common.Common;
import scheduler.FloorEvent;
import scheduler.Scheduler;

import static java.lang.Math.abs;

/**
 * This class represents the floor sub-system, responsible for generating floor events and creating the floors
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class FloorSubsystem {
	
	/**
	 * Generates the Floors and the FloorEvents
	 * 
	 * @param scheduler, the scheduler associated with the floor sub-system
	 * @param filename, the name of the file that contains the event information
	 * @throws FileNotFoundException
	 */
	public static void generateFloorsAndEvents(Scheduler scheduler, String filename) throws FileNotFoundException {
		List<Floor> floors = new ArrayList<>();
		List<Integer> originalFloors = new ArrayList<>();
		List<Long> timeList = new ArrayList<>();
		List<Boolean> isUpList = new ArrayList<>();
		
        int maxFloorNumber = 0;
        
        Scanner scanner = new Scanner(new File(filename));
        
        boolean isFirstDate = true;
        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().split(",");
            
            if (line.length != 0) {
                
                int originalFloor = Integer.parseInt(line[1]);
                int destinationFloor = Integer.parseInt(line[3]);
                
                // Fill originalFloors
                originalFloors.add(originalFloor);
                
                // Fill timeList
                Date currentDate = null;
    			try {
    				currentDate = Common.CSV_DATE_FORMAT.parse("01-01-2022 "+ line[0]);
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}
    			
				/* Set the SIMULATION_START_DATE to the first date */
				if (isFirstDate) {
					Common.SIMULATION_START_DATE = currentDate;
					isFirstDate = false;
				}
    			
                timeList.add(abs(Common.SIMULATION_START_DATE.getTime() - currentDate.getTime())/1000);
                
                // Fill isUpList
                if(line[2].strip().toUpperCase().equals("UP")) {
                	isUpList.add(true);
                } else if(line[2].strip().toUpperCase().equals("DOWN")){
                	isUpList.add(false);
                }
                
                // Find max floor
                if(originalFloor >= destinationFloor) {
                	if(originalFloor > maxFloorNumber) {
                		maxFloorNumber = originalFloor;
                	} 
                } else {
                	if(destinationFloor > maxFloorNumber) {
                		maxFloorNumber = destinationFloor;
                	}
                }
            } else {
            	break;
            }
        }
        
        Common.setMaxNumFloors(maxFloorNumber);

        scanner.close();
        
        createFloors(maxFloorNumber, scheduler, floors);
        
        createFloorEvents(originalFloors, floors, isUpList, timeList);
	}
	
	/**
	 * Create the floors and fill the scheduler's floor list
	 * 
	 * @param maxFloorNumber, the top floor's number
	 * @param scheduler, the scheduler associated with the floor sub-system
	 * @param floors, the list of floors to add to the scheduler
	 */
	public static void createFloors(int maxFloorNumber, Scheduler scheduler, List<Floor> floors) {
        for(int floorNumber = 0; floorNumber < maxFloorNumber; floorNumber++) {
        	if(floorNumber == 0) {
        		floors.add(new BottomFloor(floorNumber + 1, scheduler));
        	} else if(floorNumber == maxFloorNumber - 1) {
        		floors.add(new TopFloor(floorNumber + 1, scheduler));
        	} else {
        		floors.add(new MiddleFloor(floorNumber + 1, scheduler));
        	}
        }
        scheduler.setFloors(floors);
	}
	
	/**
	 * Create the FloorEvent's and fill each's floor eventQueue 
	 * 
	 * @param originalFloors, the list of floors from which the elevator is called 
	 * @param floors, the list of all floors
	 * @param isUpList, the list of button press directions
	 * @param timeList, the list of event times
	 */
	private static void createFloorEvents(List<Integer> originalFloors, List<Floor> floors, List<Boolean> isUpList, List<Long> timeList) {
		int tempIndex = 0;
        for(int floorNumber : originalFloors) {
        	Floor currentFloor = floors.get(floorNumber - 1);
        	currentFloor.addFloorEvent(new FloorEvent(currentFloor, isUpList.get(tempIndex), timeList.get(tempIndex)));
        	tempIndex++;
        }
	}
}

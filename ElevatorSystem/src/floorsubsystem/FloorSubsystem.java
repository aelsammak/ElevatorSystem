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

public class FloorSubsystem {
	
	
	public static void generateFloorsAndEvents(Scheduler scheduler, String filename) throws FileNotFoundException {
		List<Floor> floors = new ArrayList<>();
		List<Integer> originalFloors = new ArrayList<>();
		List<Long> timeList = new ArrayList<>();
		List<Boolean> isUpList = new ArrayList<>();
		
        int maxFloorNumber = 0;
        
        Scanner scanner = new Scanner(new File(filename));
        
        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().split(",");
            
            int originalFloor = Integer.parseInt(line[1]);
            int destinationFloor = Integer.parseInt(line[3]);
            
            // Fill originalFloors
            originalFloors.add(originalFloor);
            
            // Fill timeList
            Date currentDate = null;
			try {
				currentDate = Common.CSV_DATE_FORMAT.parse("01-01-2022 "+ line[0]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            timeList.add(abs(Common.SIMULATION_START_DATE.getTime() - currentDate.getTime())/1000);
            
            // Fill isUpList
            if(line[2].strip().toUpperCase() == "UP") {
            	isUpList.add(true);
            } else if(line[2].strip().toUpperCase() == "DOWN"){
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
        }
        
        Common.setMaxNumFloors(maxFloorNumber);

        scanner.close();
        
        createFloors(maxFloorNumber, scheduler, floors);
        
        createFloorEvents(originalFloors, floors, isUpList, timeList);
	}
	
	private static void createFloors(int maxFloorNumber, Scheduler scheduler, List<Floor> floors) {
        for(int floorNumber = 0; floorNumber < maxFloorNumber; floorNumber++) {
        	if(floorNumber == 0) {
        		floors.add(new BottomFloor(floorNumber+1, scheduler));
        	} else if(floorNumber == maxFloorNumber - 1) {
        		floors.add(new TopFloor(floorNumber+1, scheduler));
        	} else {
        		floors.add(new MiddleFloor(floorNumber+1, scheduler));
        	}
        }
        scheduler.setFloors(floors);
	}
	
	private static void createFloorEvents(List<Integer> originalFloors, List<Floor> floors, List<Boolean> isUpList, List<Long> timeList) {
        for(int floorNumber : originalFloors) {
        	int tempIndex = 0;
        	Floor currentFloor = floors.get(floorNumber - 1);
        	currentFloor.addFloorEvent(new FloorEvent(currentFloor, isUpList.get(tempIndex), timeList.get(tempIndex)));
        	tempIndex++;
        }
	}
}

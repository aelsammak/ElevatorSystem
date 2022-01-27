package floorsubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Scanner;

import common.Common;
import scheduler.FloorEvent;
import scheduler.Scheduler;

import static java.lang.Math.abs;

public class FloorSubsystem {
	
	
	public static void generateFloorsAndEvents(Scheduler scheduler, String filename) throws FileNotFoundException{
		List<Floor> floors = new ArrayList<>();
		List<Integer> originalFloors = new ArrayList<>();
		List<Long> timeList = new ArrayList<>();
		List<Boolean> isUpList = new ArrayList<>();
		
        int max_floor_number = 0;
        
        Scanner scanner = new Scanner(new File(filename));
        
        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().split(",");
            
            int originalFloor = Integer.parseInt(line[1]);
            int destinationFloor = Integer.parseInt(line[3]);
            
            // Fill originalFloors
            originalFloors.add(originalFloor);
            
            // Fill timeList
            Date currentDate = Common.CSV_DATE_FORMAT.parse("01-01-2022 "+ line[0]);
            timeList.add(abs(Common.SIMULATION_START_DATE.getTime() - currentDate.getTime())/1000);
            
            // Fill isUpList
            if(line[2].strip().toUpperCase() == "UP") {
            	isUpList.add(true);
            } else if(line[2].strip().toUpperCase() == "DOWN"){
            	isUpList.add(false);
            }
            
            // Find max floor
            if(originalFloor >= destinationFloor) {
            	if(originalFloor > max_floor_number) {
            		max_floor_number = originalFloor;
            	} 
            } else {
            	if(destinationFloor > max_floor_number) {
            		max_floor_number = destinationFloor;
            	}
            }
        }

        scanner.close();
        
        for(int i = 0; i < max_floor_number; i++) {
        	if(i == 0) {
        		floors.add(new BottomFloor(i+1, scheduler));
        	} else if(i == max_floor_number - 1) {
        		floors.add(new TopFloor(i+1, scheduler));
        	} else {
        		floors.add(new MiddleFloor(i+1, scheduler));
        	}
        }
        
        for(int floorNumber : originalFloors) {
        	int tempIndex = 0;
        	Floor currentFloor = floors.get(floorNumber - 1);
        	currentFloor.addFloorEvent(new FloorEvent(currentFloor, isUpList.get(tempIndex), timeList.get(tempIndex)));
        	tempIndex++;
        }
	}
}

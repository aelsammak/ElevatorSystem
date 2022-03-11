package floorsubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import common.Common;

/**
 * The FileLoader is responsible for reading the instructions from the simulation.csv file
 * 
 * @author Kareem El-Hajjar
 * @author Adi El-Sammak
 * @version 3.0
 */
public class FileLoader {
	
	private File simulationFile = new File(Common.config.getProperty("csvFileName"));
    private Scanner scanner;
    private String currLine;
    private String[] csvSimInfo;
    private LocalTime localTime;
    private HashMap<Integer, ArrayList<Integer>> destinations; 
    private boolean reachedEndOFile;
    private static String SIMULATION_START_TIME = "8:00:00"; 

    /**
     * Default constructor for FileLoader 
     * 
     * @throws Exception - If file not found
     */
    public FileLoader() throws Exception {
    	localTime = LocalTime.now(); // Get LocalTime
    	reachedEndOFile = false;
    	
        try {
            scanner = new Scanner(simulationFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            reachedEndOFile = true;
        }

        destinations = new HashMap<Integer, ArrayList<Integer>>();
        this.nextLine();
    }
    
    /**
     * Getter for the destinations attribute.
     * 
     * @return HashMap<Integer, ArrayList<Integer>> - the HashMap containing the destinations
     */
    public HashMap<Integer, ArrayList<Integer>> getDestinations() {
    	return destinations;
    }

    /**
     * Adds destinations to the destinations HashMap based on departure floor
     */
    private void pushDestinations() {
        if (!destinations.containsKey(departFloor())){
            destinations.put(departFloor(), new ArrayList<Integer>());
        }
        
        destinations.get(departFloor()).add(destFloor());
    }
    
    
    /**
     * Return the departure floor for the current instruction
     * 
     * @return int - the departure floor number
     */
    public int departFloor() {
        int result = 0;
        try{
            result = Integer.parseInt(csvSimInfo[1]);
        } catch(Exception e) {
            System.out.println("ERROR: Parsing the simulation file failed");
        }
        return result;
    }
    
    /**
     * Return the destination floor for the current instruction
     * 
     * @return int - the destination floor number
     */
    private int destFloor() {
        int result = 0;
        try {
            result = Integer.parseInt(csvSimInfo[3]);
        } catch(Exception e) {
            System.out.println("ERROR: Parsing the simulation file failed");
        }
        return result;
    }
    
    /**
     * Returns whether or not there is another instruction in the simulation.
     * 
     * @return boolean - true if there is another instruction, false otherwise
     */
    public boolean hasNextInstruction() {
        return !reachedEndOFile && scanner.hasNextLine();
    }

    /**
     * Reads a line in the file
     * 
     * @return String - the next line in the file as a String
     */
    private String readLine() {
        return reachedEndOFile ? "" : scanner.nextLine();
    }

    /**
     * Switches to the next instruction in the file
     * 
     * @return boolean - true if there is another instruction, false otherwise
     * @throws Exception - incorrect file format
     */
    public boolean nextLine() throws Exception {
        if (hasNextInstruction()) {
            currLine = readLine();
            
            if(!currLine.equals("")) {
            	
                // Split line by comma: Time | Departure floor | Direction | Destination floor
                csvSimInfo = currLine.split(",");
                
                if (csvSimInfo.length == 0) {
                    throw new Exception("ERROR: Parsing the simulation file format is not supported");
                }
                pushDestinations();
                return true;
            }
        }
        return false;
    }

    /**
     * Get the time of the current instruction and apply appropriate offset
     * 
     * @return LocalTime - the time of the instruction
     */
    @SuppressWarnings("deprecation")
	public LocalTime getTime() {
    	int[] offsets = findOffsets();
    	
    	Date currentDate = null;
		try {
			currentDate = Common.TIMESTAMP_FORMAT.parse(csvSimInfo[0]);
			currentDate.setHours(localTime.getHour() + offsets[0]);
			currentDate.setMinutes(localTime.getMinute() + offsets[1]);
			currentDate.setSeconds(localTime.getSecond() + offsets[2]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return LocalDateTime.ofInstant(currentDate.toInstant(),
    		    ZoneId.systemDefault()).toLocalTime();
    }
    
    /**
     * Find the offsets of LocalTime with respect to the simulation start time
     * 
     * @return int[] - the list of offsets
     */
    private int[] findOffsets() {
    	int[] offsets = new int[3];
    	String[] simStart = SIMULATION_START_TIME.split(":");
    	
    	for (int i = 0; i < simStart.length; i++) {
    		if (i == 0) {
    			int hours = Integer.parseInt(simStart[i]);
    			int diff;
    			if (Integer.parseInt(csvSimInfo[0].split(":")[0]) < hours) {
    				diff = hours + Integer.parseInt(csvSimInfo[0].split(":")[0]);
    			} else {
    				diff = Integer.parseInt(csvSimInfo[0].split(":")[0]) - hours;
    			}
    			offsets[0] = diff;
    		} else if (i == 1) {
    			int minutes = Integer.parseInt(simStart[i]);
    			int diff;
    			if (Integer.parseInt(csvSimInfo[0].split(":")[1]) < minutes) {
    				diff = minutes + Integer.parseInt(csvSimInfo[0].split(":")[1]);
    			} else {
    				diff =  Integer.parseInt(csvSimInfo[0].split(":")[1]) - minutes;
    			}
    			offsets[1] = diff;
    		} else {
    			int seconds = Integer.parseInt(simStart[i]);
    			int diff;
    			if (Integer.parseInt(csvSimInfo[0].split(":")[2]) < seconds) {
    				diff = seconds + Integer.parseInt(csvSimInfo[0].split(":")[2]);
    			} else {
    				diff = Integer.parseInt(csvSimInfo[0].split(":")[2]) - seconds;
    			}
    			offsets[2] = diff;
    		}
    	}
    	return offsets;
    }

    /**
     * Returns the direction of the current instruction
     * 
     * @return boolean - the direction of the instruction
     */
    public boolean isUp() {
    	return csvSimInfo[2].strip().toUpperCase().equals("UP");
    }
    
}

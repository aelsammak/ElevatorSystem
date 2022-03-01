package common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Common class is responsible for storing common attributes for other classes within the system to access
 * 
 * @version 1.0
 * 
 */
public class Common {
	
	public static int NUM_FLOORS;
	public static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static Date SIMULATION_START_DATE;
    
    /**
     * Setter for the NUM_FLOORS attribute
     * 
     * @param maxFloors - the total number of floors
     */
    public static void setMaxNumFloors(int maxFloors) {
    	NUM_FLOORS = maxFloors;
    }

}

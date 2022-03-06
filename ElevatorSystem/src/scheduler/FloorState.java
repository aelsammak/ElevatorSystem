package scheduler;

/**
 *  Keeps track of floor state
 *  
 * @author Cam Sommerville
 * @author Erica Morgan
 * @version 3.0
 */
public class FloorState {
	
    private int floorNumber;
    private int UP;
    private int DOWN;

    /**
     * Default Constructor initializes state: both buttons/lamps are off on floorNumber
     *
     * @param floorNumber - the floor number
     */
    public FloorState(int floorNumber) {
        this.setFloorNumber(floorNumber);
        this.UP = 0;
        this.DOWN = 0;
    }

	/**
	 * Getter for the UP Attribute
	 * 
	 * @return int - 1 if UP is ON, else OFF
	 */
    public int getUp() {
        return UP;
    }

	/**
	 * Getter for the DOWN Attribute
	 * 
	 * @return int - 1 if DOWN is ON, else OFF
	 */
    public int getDown() {
        return DOWN;
    }

	/**
	 * Setter for the UP Attribute
	 *
	 * @param UP - the UP
	 */
    public void setUp(int UP) {
        this.UP = UP;
    }

	/**
	 * Setter for the DOWN Attribute
	 *
	 * @param DOWN - the DOWN
	 */
    public void setDown(int DOWN) { 
    	this.DOWN = DOWN; 
    }

	/**
	 * @return the floorNumber
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * @param floorNumber the floorNumber to set
	 */
	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}
}

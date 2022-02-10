package elevatorsubsystem;

/**
 * The Motor class represents the Elevator's motor
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class Motor {
	
	private final int elevatorNumber;
	private ElevatorState elevatorState;
	
	/**
	 * Constructor used to create an instance of the Motor class
	 * 
	 * @param elevatorNumber - the elevator number
	 */
	public Motor(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		this.setElevatorState(ElevatorState.IDLE);
	}

	/**
	 * Getter for the elevatorNumber attribute
	 * 
	 * @return int - the elevator number
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * Getter for the elevatorState attribute
	 * 
	 * @return ElevatorState - the elevator state
	 */
	public ElevatorState getElevatorState() {
		return elevatorState;
	}

	/**
	 * Setter for the elevatorState
	 * 
	 * @param elevatorState - the elevator state
	 */
	public void setElevatorState(ElevatorState elevatorState) {
		this.elevatorState = elevatorState;
	}

}

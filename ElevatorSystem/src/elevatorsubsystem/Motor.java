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
	private MotorState state;
	
	/**
	 * Constructor used to create an instance of the Motor class
	 * 
	 * @param elevatorNumber - the elevator number
	 */
	public Motor(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		this.setState(MotorState.IDLE);
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
	public MotorState getState() {
		return state;
	}

	/**
	 * Setter for the elevatorState
	 * 
	 * @param elevatorState - the elevator state
	 */
	public void setState(MotorState state){
		this.state = state;
	}

}

package elevatorsubsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;

import common.Common;
import common.RPC;
import floorsubsystem.FileLoader;

/**
 * The Elevator class represents a single Elevator in the ElevatorSubSystem. 
 * 
 * @author Adi El-Sammak
 * @version 3.0
 *
 */
public class Elevator extends Thread {
	
	private int currentFloor;
	private ArrivalSensor arrivalSensor;
	private final int elevatorNumber;
	private final ElevatorButton[] elevatorButtons;
	private final ElevatorLamp[] elevatorLamps;
	private final Motor motor;
	private final Door door;
	private ArrayList<Integer> destinationFloors;
	private int targetFloor;
	private RPC transmitter;
	private InetAddress addr;
	private FileLoader fileLoader;
	
	public Elevator(int elevatorNumber, int currentFloor, int destPort, int recPort, FileLoader fileLoader) throws UnknownHostException {
		this.fileLoader = fileLoader;
		this.elevatorNumber = elevatorNumber;
		motor = new Motor(elevatorNumber);
		door = new Door(elevatorNumber);
		arrivalSensor = new ArrivalSensor(this);
		destinationFloors = new ArrayList<Integer>();
		this.currentFloor = currentFloor;
		this.addr = InetAddress.getLocalHost();
		transmitter = new RPC(addr, destPort, recPort);
		
		elevatorButtons = new ElevatorButton[Common.NUM_FLOORS];
		elevatorLamps = new ElevatorLamp[Common.NUM_FLOORS];
		
        for (int i = 0; i < Common.NUM_FLOORS; i++) {
        	elevatorButtons[i] = new ElevatorButton(i);
        	elevatorLamps[i] = new ElevatorLamp(i);
        }
	}

	public void moveToFloor(int floorNumber) {
		
		targetFloor = floorNumber;
		destinationFloors.add((Integer)floorNumber);

		//If statements to checks the location of the destination floor relative to the current floor
		if(currentFloor > floorNumber) {
			setMotorState(MotorState.MOVING_DOWN);
		} else if (currentFloor < floorNumber) {
			setMotorState(MotorState.MOVING_UP);
		} else {
			setMotorState(MotorState.IDLE);
			System.out.println("ELEVATOR: Elevator #" + elevatorNumber + " | CurrentFloor: " + currentFloor + " == TargetFloor: " + targetFloor  + " | MotorState: " + getMotorState() + " @ time = " + LocalTime.now());
			removeDestinationFloor(floorNumber);
			return;
		}
		
		System.out.println("ELEVATOR: Elevator #" + elevatorNumber + " | CurrentFloor: " + currentFloor + " | MotorState: " + getMotorState() + " | TargetFloor: " + targetFloor + " @ time = " + LocalTime.now());
		
		sendAndReceive();
		
		arrivalSensor.simulateElevatorMovement(currentFloor, targetFloor);
	}

	public void notifyElevatorArrival() {
		openDoors();
		setMotorState(MotorState.IDLE); // set state to idle
		System.out.println("\nELEVATOR: Elevator #" + elevatorNumber + " | ARRIVED at Floor #" + targetFloor + " | MotorState: " + getMotorState() + " @ time = " + LocalTime.now());
		removeDestinationFloor(targetFloor);  //calls method remove floor to remove it from the arraylist
		
		if (elevatorButtons[currentFloor - 1].isPressed()) {
			elevatorButtons[currentFloor - 1].turnOff();
	        elevatorLamps[currentFloor - 1].turnOff();
	        System.out.println("ELEVATOR: Turning OFF Elevator #" + elevatorNumber + " car button " + currentFloor + " @ time = " + LocalTime.now());
		}
		
		sendAndReceive();
		closeDoors();
		servePassengerToDestFloor();
	}
	
	private void servePassengerToDestFloor() {
		
		if (fileLoader.getDestinations().containsKey(currentFloor)) {
			if (!fileLoader.getDestinations().get(currentFloor).isEmpty()) {
				int nextDest = fileLoader.getDestinations().get(currentFloor).get(0);
				fileLoader.getDestinations().get(currentFloor).remove((Integer) nextDest);
				System.out.println("\nELEVATOR: Elevator #" + elevatorNumber + " car button " + nextDest + " has been pressed @ time = " + LocalTime.now());
				elevatorButtons[nextDest - 1].turnOn();
		        elevatorLamps[nextDest - 1].turnOn();
				moveToFloor(nextDest);
			}
		}
	}
	
	public void updatePosition() {
		if (getMotorState() == MotorState.MOVING_UP) {
			currentFloor++;
		} else if (getMotorState() == MotorState.MOVING_DOWN) {
			currentFloor--;
		}
	}
	
	
	public void removeDestinationFloor(int floorNumber) {
		destinationFloors.remove((Integer)floorNumber);
	}
	
	/**
	 * This method is resonsible for opening the Elevator doors
	 */
	public void openDoors() {
		this.door.open();
	}
	
	/**
	 * Returns Floor number at which the Elevator is on
	 * 
	 * @return int - the floor number
	 */
	public int getCurrentFloorNumber() {
		return currentFloor;
	}
	
	public void setCurrentFloorNumber(int floorNumber) {
		currentFloor = floorNumber;
	}
	
	/**
	 * This method is responsible for closing the Elevator doors while taking load/unload times into account
	 */
	public void closeDoors() {
        this.door.close();
    }
	
	/**
	 * Getter for the Motor's ElevatorState attribute
	 * 
	 * @return ElevatorState - the state of the Elevator
	 */
	public MotorState getMotorState() {
		return motor.getState();
	}
	
	/**
	 * Setter for the motor's Elevator state
	 * 
	 * @param state - the elevator state
	 */
	public void setMotorState(MotorState state) {
		motor.setState(state);
	}
	
	/**
	 * Getter for elevatorNumber attribute
	 * 
	 * @return int - the elevatorNumber
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}


	/**
	 * Getter for array of elevatorButtons
	 * 
	 * @return ElevatorButton[] - the elevatorButtons
	 */
	public ElevatorButton[] getElevatorButtons() {
		return elevatorButtons;
	}

	/**
	 * Getter for the array of elevatorLamps
	 * 
	 * @return ElevatorLamp[] - the elevatorLamps
	 */
	public ElevatorLamp[] getElevatorLamps() {
		return elevatorLamps;
	}
	
	public void sendAndReceive() {
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, getMotorState(), targetFloor);
		transmitter.sendPacket(msg);
		msg = transmitter.receivePacket();
	}
	
	// receive method that first sends an ack check msg to ElevatorSubsystem
	// and then receives instructions for a specific elevator
	public void receive() {
		byte[] ackcheckMsg;  // byte array variables for the msgs
		byte[] receiveMsg;
		
		ackcheckMsg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.CHECK); // using the Common.java to encode check msg
		transmitter.sendPacket(ackcheckMsg);  // sends the msg request to elevator subsystem using UDP

		receiveMsg = transmitter.receivePacket();  // stores the elevatorSubsystem's response in byte array
		
		if (receiveMsg == null) {
			return;
		}
		
		if (Common.findType(receiveMsg) != Common.MESSAGETYPE.ACKNOWLEDGEMENT) {
			int received[] = Common.decode(receiveMsg); //decode the received msg that stores the info in an integer array
			moveToFloor(received[1]); //Common.java identifies msg[1] as destination floor
		}

	}

	/**
	 * The run() method which will be invoked once start() has been called on the Elevator thread.  
	 */
	@Override
	public void run() {
		while (true) {
			receive();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

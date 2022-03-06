package common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import elevatorsubsystem.MotorState;

/**
 * The Common class is responsible for storing common attributes for other classes within the system to access
 * 
 * @version 3.0
 * 
 */
public class Common {
	
	public static Config config;
	
    static {
    	try {
			config = new Config();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public static int NUM_FLOORS = config.getIntProperty("NUM_FLOORS");
	public static int NUM_ELEVATORS = config.getIntProperty("NUM_ELEVATORS");
	public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
	
	public enum MESSAGETYPE {
		INVALID			((byte) -1),
		ELEVATOR		((byte) 0),
		FLOOR			((byte) 1),
		SCHEDULER		((byte) 2),
		ACKNOWLEDGEMENT	((byte) 3);

		private final byte value;
		
		private MESSAGETYPE(byte b){
			this.value = b;
		}

		public static MESSAGETYPE findMsgType(byte b) {
			for (MESSAGETYPE msgType: MESSAGETYPE.values()) {
				if(msgType.value == b) return msgType;
			}
			return INVALID;
		}
	}


	public enum ACKOWLEDGEMENT {
		INVALID			((byte) -1),
		CHECK			((byte) 0),
		RECEIVED		((byte) 1),
		NO_MSG			((byte) 2);

		private final byte value;
		
		private ACKOWLEDGEMENT(byte b){
			this.value = b;
		}

		/* Determine which Ack type the byte corresponds to */
		public static ACKOWLEDGEMENT findAcknowledgement(byte b) {
			for (ACKOWLEDGEMENT ack : ACKOWLEDGEMENT.values()) {
				if(ack.value == b) return ack;
			}
			return INVALID;
		}
	}

	/**
	 * This method is used to encode the Elevator Msg into bytes.
	 * The byte[] will be encoded using the following schema below:
	 * Useful bytes: 
	 * byte[0] -> MESSAGETYPE.ELEVATOR.value (0), 
	 * byte[2] -> elevatorNumber, 
	 * byte[4] -> currentFloorOfElev, 
	 * byte[6] -> motorState [1 for UP, -1 for DOWN, 0 for IDLE],
	 * byte[8] -> targetFloor
	 * Useless bytes (separator == 127): byte[1], byte[3], byte[5], byte[7], byte[9]
	 * 
	 * @param elevatorNumber - the elevatorNumber
	 * @param currentFloor - the currentFloor
	 * @param state - the MotorState
	 * @param targetFloor - the targetFloor
	 * @return byte[] - the elevator msg converted to bytes
	 */
	public static byte[] encodeElevMsgIntoBytes(int elevatorNumber, int currentFloor, MotorState state, int targetFloor) {
		byte[] msg = new byte[10];
		msg[0] = MESSAGETYPE.ELEVATOR.value;
		msg[1] = (byte)127;
		msg[2] = (byte)elevatorNumber;
		msg[3] = (byte)127;
		msg[4] = (byte)currentFloor;
		msg[5] = (byte)127;
		if(state == MotorState.MOVING_UP) {
			msg[6] = (byte)1;
		} else if (state == MotorState.MOVING_DOWN) {
			msg[6] = (byte)-1;
		} else {
			msg[6] = (byte)0;
		}
		msg[7] = (byte)127;
		msg[8] = (byte)targetFloor;
		msg[9] = (byte)127;
		return msg;
	}
	
	/**
	 * This method is used to encode the Floor Msg into bytes.
	 * The byte[] will be encoded using the following schema below:
	 * Useful bytes: 
	 * byte[0] -> MESSAGETYPE.FLOOR.value (1), 
	 * byte[2] -> floorNumber, 
	 * byte[4] -> isFloorBtnUp
	 * Useless bytes (separator == 127): byte[1], byte[3], byte[5]
	 * 
	 * @param floorNumber - the floorNumber
	 * @param isFloorBtnUp - is the Floor Btn Up
	 * @return byte[] - the floor msg converted to bytes
	 */
	public static byte[] encodeFloorMsgIntoBytes(int floorNumber, boolean isFloorBtnUp) {
		byte[] msg = new byte[6];
		msg[0] = MESSAGETYPE.FLOOR.value;
		msg[1] = (byte)127;
		msg[2] = (byte)floorNumber;
		msg[3] = (byte)127;
		msg[4] = isFloorBtnUp ? (byte) 1 : (byte) 0;
		msg[5] = (byte)127;
		return msg;
	}
	
	/**
	 * This method is used to encode the Scheduler Msg into bytes.
	 * The byte[] will be encoded using the following schema below:
	 * Useful bytes: 
	 * byte[0] -> MESSAGETYPE.SCHEDULER.value (2), 
	 * byte[2] -> elevatorNumber, 
	 * byte[4] -> sharedFloorNumber, 
	 * byte[6] -> isFloorBtnUp
	 * Useless bytes (separator == 127): byte[1], byte[3], byte[5], byte[7]
	 * 
	 * @param elevatorNumber - the elevatorNumber
	 * @param sharedFloorNumber - the sharedFloorNumber
	 * @param isFloorBtnUp - is the Floor Btn Up
	 * @return byte[] - the scheduler msg converted to bytes
	 */
	public static byte[] encodeSchedulerMsgIntoBytes(int elevatorNumber, int sharedFloorNumber, boolean isFloorBtnUp) {
		byte[] msg = new byte[8];
		msg[0] = MESSAGETYPE.SCHEDULER.value;
		msg[1] = (byte)127;
		msg[2] = (byte)elevatorNumber;
		msg[3] = (byte)127;
		msg[4] = (byte)sharedFloorNumber;
		msg[5] = (byte)127;
		msg[6] = isFloorBtnUp ? (byte) 1 : (byte) 0;
		msg[7] = (byte)127;
		return msg;
	}
	
	/**
	 * This method is used to encode the AckMsg into bytes.
	 * The byte[] will be encoded using the following schema below:
	 * Schema: byte[0] -> MESSAGETYPE.ACKNOWLEDGEMENT.value (3), byte[2] -> ack.value, 
	 * 
	 * @param ack - the ack
	 * @return byte[] - the AckMsg converted to bytes
	 */
	public static byte[] encodeAckMsgIntoBytes(ACKOWLEDGEMENT ack) {
		byte[] msg = new byte[2];
		msg[0] = MESSAGETYPE.ACKNOWLEDGEMENT.value;
		msg[1] = ack.value;
		return msg;
	}

	/**
	 * This method is used to find the type of msg.
	 * Different types include: INVALID, ELEVATOR, FLOOR, SCHEDULER, ACKNOWLEDGEMENT
	 *
	 * @param msg - the byte[] of message
	 * @return MESSAGETYPE - the type of message
	 */
	public static MESSAGETYPE findType(byte[] msg){
		return MESSAGETYPE.findMsgType(msg[0]);
	}

	/**
	 * This method is used to figure out which type of message is received by the subsystem and decode it using the corresponding decode method.
	 * 
	 * @param msg - the msg in bytes
	 * @return int[] - decoded message in an int[]
	 */
	public static int[] decode(byte[] msg) {
		switch(findType(msg)) {
			case ELEVATOR:
				return decodeElevMsgFromBytes(msg);
			case FLOOR:
				return decodeFloorMsgFromBytes(msg);
			case SCHEDULER:
				return decodeSchedulerMsgFromBytes(msg);
			case ACKNOWLEDGEMENT:
				return decodeAckMsgFromBytes(msg);
			default:
				return null;
		}
	}
	
	/**
	 * This method is used to decode an Elevator msg from bytes. The decoded message will be returned into an int[].
	 * Indexes of the int[] are as follows:
	 * int[0] -> elevatorNumber,
	 * int[1] -> currentFloorOfElev, 
	 * int[2] -> motorState [1 for UP, -1 for DOWN, 0 for IDLE],
	 * int[3] -> targetFloor
	 * 
	 * @param msg - the elev msg to decode
	 * @return int[] - the array containing the decoded useful data
	 */
	private static int[] decodeElevMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[4];
		decodedMsg[0] = (int)msg[2];
		decodedMsg[1] = (int)msg[4];
		decodedMsg[2] = (int)msg[6];
		decodedMsg[3] = (int)msg[8];
		return decodedMsg;
	}
	
	/**
	 * This method is used to decode an Elevator msg that was received by the Scheduler in bytes to a meaningful string.
	 * 
	 * @param msg - the elev msg to decode
	 * @return String - the elevator's message to Scheduler
	 */
	public static String decodeSchedulerFromElevMsgToString(int[] decodedMsg) {
		String motorState;
		if (decodedMsg[2] == 1) {
			motorState = "MOVING UP";
		} else if (decodedMsg[2] == -1) {
			motorState = "MOVING DOWN";
		} else {
			motorState = "IDLE";
			return "Elevator #" + decodedMsg[0] + " | CurrentFloor: " + decodedMsg[1] + " == TargetFloor: " + decodedMsg[3] + " | MotorState: " + motorState;
		}
		
		return "Elevator #" + decodedMsg[0] + " | CurrentFloor: " + decodedMsg[1] + " | MotorState: " + motorState + " | TargetFloor: " + decodedMsg[3];
	}
	
	/**
	 * This method is used to decode an Floor msg from bytes. The decoded message will be returned into an int[].
	 * Indexes of the int[] are as follows:
	 * int[0] -> floorNumber, 
	 * int[1] -> isFloorBtnUp
	 * 
	 * @param msg - the floor msg to decode
	 * @return int[] - the array containing the decoded useful data
	 */
	private static int[] decodeFloorMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[2];
		decodedMsg[0] = (int)msg[2];
		decodedMsg[1] = (int)msg[4];
		return decodedMsg;
	}
	
	/**
	 * This method is used to decode a floor msg that was received by the Scheduler in bytes to a meaningful string.
	 * 
	 * @param msg - the floor msg to decode
	 * @return String - the floor's message to Scheduler
	 */
	public static String decodeSchedulerFromFloorMsgToString(int[] decodedMsg) {
		String dirBtn = "DOWN"; 
		if (decodedMsg[1] == 1) {
			dirBtn = "UP";
		}
		return "Floor #" + decodedMsg[0] + " " + dirBtn + " button has been pressed";
	}
	
	/**
	 * This method is used to decode an Scheduler msg from bytes. The decoded message will be returned into an int[].
	 * Indexes of the int[] are as follows:
	 * int[0] -> elevatorNumber, 
	 * int[1] -> sharedFloorNumber, 
	 * int[2] -> isFloorBtnUp
	 * 
	 * @param msg - the floor msg to decode
	 * @return int[] - the array containing the decoded useful data
	 */
	private static int[] decodeSchedulerMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[3];
		decodedMsg[0] = (int)msg[2];
		decodedMsg[1] = (int)msg[4];
		decodedMsg[2] = (int)msg[6];
		return decodedMsg;
	}
	
	/**
	 * This method is used to decode a floor msg that will be sent by the Scheduler in bytes to a meaningful string.
	 * 
	 * @param msg - the scheduler's floor msg to decode
	 * @return String - the Scheduler's message to the floor
	 */
	public static String decodeSchedulerFloorMsgToString(int[] decodedMsg) {
		if (decodedMsg[2] == 0) {
			return "Notify Floor #" + decodedMsg[1] + " to turn OFF buttons & lamps if ON";
		} else {
			return "";
		}
	}
	
	/**
	 * This method is used to decode a elevator msg that will be sent by the Scheduler in bytes to a meaningful string.
	 * 
	 * @param msg - the scheduler's elevator msg to decode
	 * @return String - the Scheduler's message to the elevator
	 */
	public static String decodeSchedulerElevMsgToString(int[] decodedMsg) {
		String dirBtn = "DOWN"; 
		if (decodedMsg[2] == 1) {
			dirBtn = "UP";
		}
		return "Elevator #" + decodedMsg[0] + " | Assigned To: Floor #" + decodedMsg[1] + " | FloorButton: " + dirBtn;
	}

	
	/**
	 * This method is used to decode an Ack msg from bytes. The decoded message will be returned into an int[].
	 * Indexes of the int[] are as follows:
	 * int[0] -> ack.value
	 * 
	 * @param msg - the Ack msg to decode
	 * @return int[] - the array containing the decoded useful data
	 */
	private static int[] decodeAckMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[1];
		decodedMsg[0] = 1;
		return decodedMsg;
	}

	/**
	 * This method is used to find the type of Acknowledgement.
	 * Different types include: INVALID, CHECK, RECEIVED, NO_MSG
	 *
	 * @param msg - byte[] of ack message
	 * @return ACKOWLEDGEMENT - the ack that this message belongs to
	 */
	public static ACKOWLEDGEMENT findAcknowledgement(byte[] msg){
		return ACKOWLEDGEMENT.findAcknowledgement(msg[1]);
	}

}

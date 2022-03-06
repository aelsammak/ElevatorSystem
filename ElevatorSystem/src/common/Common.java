package common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import common.Common.ACKOWLEDGEMENT;
import common.Common.MESSAGETYPE;
import elevatorsubsystem.MotorState;

/**
 * The Common class is responsible for storing common attributes for other classes within the system to access
 * 
 * @version 1.0
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

		// Determine which type the byte corresponds to
		public static ACKOWLEDGEMENT findAcknowledgement(byte b) {
			for (ACKOWLEDGEMENT ack : ACKOWLEDGEMENT.values()) {
				if(ack.value == b) return ack;
			}
			return INVALID;
		}
	}

	/**
	 * encode the data in the form 0(for elevator), 127(separator), elevator number, 127(separator), current floor, 127(separator), 1(Up) or -1(Down) or 0(Idle), 127(separator), destination floor, 127(end file)
	 * 
	 * @param curr
	 * @param state
	 * @param dest
	 * @return message to send
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
	 * encode the data in the form: 1(floor message),127(separator),floor clicked, 127(separator), 0(down) or 1(up)
	 * 
	 * @param floor clicked
	 * @param dir true for up, false for down
	 * @return message to send
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
	 * encode the data in the form: 2(for scheduler), 127(separator), elevt#, 127(separator), floor# (shared by floor and elevt), 127(separator), floor button to dismiss 0(down) or 1(up), 127(separator)
	 * 
	 * @param elevt, floor, dir
	 * @return message to send
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
	 * encode confirmation message into byte[]
	 */
	public static byte[] encodeAckMsgIntoBytes(ACKOWLEDGEMENT ack) {
		byte[] msg = new byte[2];
		msg[0] = MESSAGETYPE.ACKNOWLEDGEMENT.value;
		msg[1] = ack.value;
		return msg;
	}

	/**
	 *
	 * @param msg byte[] of message
	 * @return TYPE that this message belongs to
	 */
	public static MESSAGETYPE findType(byte[] msg){
		return MESSAGETYPE.findMsgType(msg[0]);
	}

	/**
	 * decode the byte array received by a subsystem. See specialized decode methods for return format
	 * 
	 * @param msg
	 * @return int[] of what was received
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
				return decodeConfirmationMsgFromBytes(msg);
			default:
				return null;
		}
	}
	
	/**
	 * decode an elevator message. return in form of index 0: current floor, index 1: direction (1 = up,0=Idle,-1=down),index 2: destination floor
	 * 
	 * @param msg
	 * @return int[] containing the decoded data
	 */
	private static int[] decodeElevMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[4];
		decodedMsg[0] = (int)msg[2];
		decodedMsg[1] = (int)msg[4];
		decodedMsg[2] = (int)msg[6];
		decodedMsg[3] = (int)msg[8];
		return decodedMsg;
	}
	
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
	 * decode floor message. return in the form index 0: floor number, index 1: direction 0(down) or 1(up)
	 * 
	 * @param msg
	 * @return int[] containing decoded data
	 */
	private static int[] decodeFloorMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[2];
		decodedMsg[0] = (int)msg[2];
		decodedMsg[1] = (int)msg[4];
		return decodedMsg;
	}
	
	public static String decodeSchedulerFromFloorMsgToString(int[] decodedMsg) {
		String dirBtn = "DOWN"; 
		if (decodedMsg[1] == 1) {
			dirBtn = "UP";
		}
		return "Floor #" + decodedMsg[0] + " " + dirBtn + " button has been pressed";
	}
	
	/**
	 * decode scheduler message. return in the form index 0: elevator number, index 1: floor, index 2: direction
	 * 
	 * @param msg
	 * @return int[] containing decoded data
	 */
	private static int[] decodeSchedulerMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[3];
		decodedMsg[0] = (int)msg[2];
		decodedMsg[1] = (int)msg[4];
		decodedMsg[2] = (int)msg[6];
		return decodedMsg;
	}
	
	public static String decodeSchedulerFloorMsgToString(int[] decodedMsg) {
		if (decodedMsg[2] == 0) {
			return "Notify Floor #" + decodedMsg[1] + " to turn OFF buttons & lamps if ON";
		} else {
			return "";
		}
	}
	
	public static String decodeSchedulerElevMsgToString(int[] decodedMsg) {
		String dirBtn = "DOWN"; 
		if (decodedMsg[2] == 1) {
			dirBtn = "UP";
		}
		return "Elevator #" + decodedMsg[0] + " | Assigned To: Floor #" + decodedMsg[1] + " | FloorButton: " + dirBtn;
	}

	private static int[] decodeConfirmationMsgFromBytes(byte[] msg) {
		int[] decodedMsg = new int[1];
		decodedMsg[0] = 1;
		return decodedMsg;
	}

	/**
	 *
	 * @param msg byte[] of message
	 * @return CONFIRMATION that this message belongs to
	 */
	public static ACKOWLEDGEMENT findAcknowledgement(byte[] msg){
		return ACKOWLEDGEMENT.findAcknowledgement(msg[1]);
	}

}

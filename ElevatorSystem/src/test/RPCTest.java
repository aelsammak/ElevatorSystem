package test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import common.Common;
import common.RPC;
import elevatorsubsystem.MotorState;

/**
 * Tests all mechanisms of RPC within the application, from encoding to decoding to a simple send/recieve test
 * @author Ben Herriott
 *
 */
public class RPCTest {
	
	/**
	 * Tests encoding of an communication moving an elevator up
	 */
	@Test
	public void testEncodeElevatorUp() {
		int elevatorNumber = 1;
		int currentFloor = 2;
		MotorState currentState = MotorState.MOVING_UP;
		int targetFloor = 5;
		
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, currentState, targetFloor);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Elevator.", 0==(int)msg[0]);
		assertTrue("Returns expected elevator number.", elevatorNumber == (int)msg[2]);
		assertTrue("Returns expected currentFloor number.", currentFloor == (int)msg[4]);
		assertTrue("Returns the expected MotorState.", 1 == (int)msg[6]);
		assertTrue("Returns expected target floor.", targetFloor == (int)msg[8]);
	}
	
	/**
	 * Test encoding of the elevator error: stuck between
	 */
	@Test
	public void testEncodeElevatorErrorStuckBetween() {
		int elevatorNumber = 1;
		int currentFloor = 2;
		int targetFloor = 5;
		boolean dir = true; 
		
		byte[] msg = Common.encodeElevError(Common.ELEV_ERROR.STUCK_BETWEEN, elevatorNumber, currentFloor, targetFloor, dir);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Elevator Error.", 4==(int)msg[0]);
		assertTrue("Returns expected elevator number.", elevatorNumber == (int)msg[2]);
		assertEquals(currentFloor, (int)msg[3]);
		assertTrue("Returns the expected target floor", targetFloor == (int)msg[4]);
		assertTrue("Returns expected target floor.", 1 == (int)msg[5]);
	}
	
	/**
	 * Test encoding of the elevator error: door close
	 */
	@Test
	public void testEncodeElevatorErrorDoorClose() {
		int elevatorNumber = 1;
		int currentFloor = 5;
		int targetFloor = 2;
		boolean dir = false; 
		
		byte[] msg = Common.encodeElevError(Common.ELEV_ERROR.DOOR_CLOSE, elevatorNumber, currentFloor, targetFloor, dir);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Elevator Error.", 4==(int)msg[0]);
		assertTrue("Returns expected elevator number.", elevatorNumber == (int)msg[2]);
		assertEquals(currentFloor, (int)msg[3]);
		assertTrue("Returns the expected target floor", targetFloor == (int)msg[4]);
		assertTrue("Returns expected target floor.", 0 == (int)msg[5]);
	}
	
	/**
	 * Test encoding of the start message
	 */
	@Test
	public void testEncodeStartMSG() {
		byte[] msg = Common.encodeStartMsg(false); 
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Elevator Error.", 5==(int)msg[0]);
		assertEquals(0, (int)msg[2]);
	}
	
	/**
	 * Tests encoding of an communication moving an elevator down
	 */
	@Test
	public void testEncodeElevatorDown() {
		int elevatorNumber = 1;
		int currentFloor = 9;
		MotorState currentState = MotorState.MOVING_DOWN;
		int targetFloor = 5;
		
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, currentState, targetFloor);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Elevator.", 0==(int)msg[0]);
		assertTrue("Returns expected elevator number.", elevatorNumber == (int)msg[2]);
		assertTrue("Returns expected currentFloor number.", currentFloor == (int)msg[4]);
		assertTrue("Returns the expected MotorState.", -1 == (int)msg[6]);
		assertTrue("Returns expected target floor.", targetFloor == (int)msg[8]);
	}
	
	/**
	 * Tests encoding of an communication moving an elevator down
	 */
	@Test
	public void testEncodeElevatorIdle() {
		int elevatorNumber = 1;
		int currentFloor = 2;
		MotorState currentState = MotorState.IDLE;
		int targetFloor = 5;
		
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, currentState, targetFloor);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Elevator.", 0==(int)msg[0]);
		assertTrue("Returns expected elevator number.", elevatorNumber == (int)msg[2]);
		assertTrue("Returns expected currentFloor number.", currentFloor == (int)msg[4]);
		assertTrue("Returns the expected MotorState.", 0 == (int)msg[6]);
		assertTrue("Returns expected target floor.", targetFloor == (int)msg[8]);
	}
	
	/**
	 * Test encoding of GUI Elevator
	 */
	@Test
	public void testEncodeGUIFloor() {
		int floorNumber = 1; 
		boolean dir = true;
		boolean isPressed = true; 
		
		byte[] msg = Common.encodeFloorToGUIMsgIntoBytes(floorNumber, dir, isPressed);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertEquals(6, (int) msg[0]); 
		assertEquals(floorNumber, (int) msg[2]); 
		assertEquals(dir ? 1 : 0, (int) msg[4]); 
		assertEquals(isPressed ? 1 : 0, (int) msg[6]); 
	}
	
	/**
	 * Test encoding of GUI Elevator
	 */
	@Test
	public void testEncodeGUIElevator() {
		int elevatorNumber = 1; 
		int currentFloor = 2;
		MotorState currentState = MotorState.MOVING_UP;
		int carButtonClick = 3; 
		int targetFloor = 4; 
		boolean printTextFlag = true; 
		
		byte[] msg = Common.encodeElevToGUIMsgIntoBytes(elevatorNumber, currentFloor, currentState, carButtonClick, targetFloor, printTextFlag);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertEquals(7, (int) msg[0]); 
		assertEquals(elevatorNumber, (int) msg[2]); 
		assertEquals(currentFloor, (int) msg[4]); 
		assertEquals(1, (int) msg[6]); 
		assertEquals(carButtonClick, (int) msg[8]); 
		assertEquals(targetFloor, (int) msg[10]); 
		assertEquals(1, (int) msg[12]); 
	}
	
	/**
	 * Test DECODE of GUI Elevator
	 */
	@Test
	public void testDecodeGUIElevator() {
		int elevatorNumber = 1; 
		int currentFloor = 2;
		MotorState currentState = MotorState.MOVING_UP;
		int carButtonClick = 3; 
		int targetFloor = 4; 
		boolean printTextFlag = true; 
		
		byte[] msg = Common.encodeElevToGUIMsgIntoBytes(elevatorNumber, currentFloor, currentState, carButtonClick, targetFloor, printTextFlag);
		int[] decodedMsg = Common.decode(msg); 
		
		assertTrue("Message is in expected format.", decodedMsg instanceof int[]);
		assertEquals(elevatorNumber, decodedMsg[0]); 
		assertEquals(currentFloor, decodedMsg[1]); 
		assertEquals(1, decodedMsg[2]); 
		assertEquals(carButtonClick, decodedMsg[3]); 
		assertEquals(targetFloor, decodedMsg[4]); 
		assertEquals(printTextFlag ? 1 : 0, decodedMsg[5]); 
	}
	
	/**
	 * Test DECODE of GUI Floor Msg
	 */
	@Test
	public void testDecodeFloorGUIMSG() {
		int floorNumber = 1; 
		boolean dir = true;
		boolean isPressed = true; 
		
		byte[] msg = Common.encodeFloorToGUIMsgIntoBytes(floorNumber, dir, isPressed);
		
		int [] decodedMsg = Common.decode(msg); 
		
		assertTrue("Message is in expected format.", decodedMsg instanceof int[]);
		assertEquals(floorNumber, decodedMsg[0]); 
		assertEquals(dir ? 1 : 0, decodedMsg[1]); 
		assertEquals(isPressed ? 1 : 0, decodedMsg[2]);  
	}
	
	/**
	 * Test encoding of GUI Elevator ERROR
	 */
	@Test
	public void testEncodeGUIElevatorError() {
		int elevNum = 1; 
		boolean isStuck = true;
		
		byte[] msg = Common.encodeElevErrorToGUI(elevNum, isStuck);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertEquals(8, (int) msg[0]); 
		assertEquals(elevNum, (int) msg[2]); 
		assertEquals(isStuck ? 1 : 0, (int) msg[4]); 
	}
	
	/**
	 * Test Decoding of GUI Elevator ERROR
	 */
	@Test
	public void testDecodeGUIElevatorError() {
		int elevNum = 1; 
		boolean isStuck = true;
		
		byte[] msg = Common.encodeElevErrorToGUI(elevNum, isStuck);
		int[] decodedMsg = Common.decode(msg);
		
		
		assertTrue("Message is in expected format.", decodedMsg instanceof int[]);
		assertEquals(elevNum, decodedMsg[0]); 
		assertEquals(isStuck ? 1 : 0, decodedMsg[1]); 
	}
	
	
	/**
	 * Tests encoding of an communication of a request for floor button up push
	 */
	@Test
	public void testEncodeFloorUp() {
		int floorNumber = 5;
		boolean isUp = true;
		byte[] msg = Common.encodeFloorMsgIntoBytes(floorNumber, isUp);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Floor.", 1==(int)msg[0]);
		assertTrue("Returns expected floor number.", floorNumber == (int)msg[2]);
		assertTrue("Returns expected elevator direction.", 1 == (int)msg[4]);
	}
	
	/**
	 * Tests encoding of an communication of a request for floor button down push
	 */
	@Test
	public void testEncodeFloorDown() {
		int floorNumber = 5;
		boolean isUp = false;
		byte[] msg = Common.encodeFloorMsgIntoBytes(floorNumber, isUp);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Floor.", 1==(int)msg[0]);
		assertTrue("Returns expected floor number.", floorNumber == (int)msg[2]);
		assertTrue("Returns expected elevator direction.", 0 == (int)msg[4]);
	}
	
	/**
	 * Tests encoding for communication sent by the scheduler 
	 */
	@Test
	public void testEncodeScheduler() {
		int floorNumber = 6;
		int elevatorNumber = 1;
		boolean isUp = true;
		byte[] msg = Common.encodeSchedulerMsgIntoBytes(elevatorNumber,floorNumber,isUp);
		
		assertTrue("Message is in expected format.", msg instanceof byte[]);
		assertTrue("Message type is of expected type Scheduler.", 2 == (int) msg[0]);
		assertTrue("Returns expected floor number.", floorNumber == (int)msg[4]);
		assertTrue("Returns expected elevator number.", elevatorNumber == (int)msg[2]);
		assertTrue("Returns expected elevator direction.", 1 == (int)msg[6]);
	}
	
	
	/**
	 * Tests decoding of an communication of a request for elevator up push 
	 */
	@Test
	public void testDecodeElevatorUp() {
		int elevatorNumber = 1;
		int currentFloor = 2;
		MotorState currentState = MotorState.MOVING_UP;
		int targetFloor = 5;
		
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, currentState, targetFloor);
		
		int[] decodedMsg = Common.decode(msg);
		
		assertTrue("Message is of expected type int array.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 4.", 4 == decodedMsg.length);
		assertTrue("Returns the expected elevator number.", elevatorNumber == decodedMsg[0]);
		assertTrue("Returns the expected currentFloor number.", currentFloor == decodedMsg[1]);
		assertTrue("Returns the expected MotorState.", 1 == decodedMsg[2]);
		assertTrue("Returns the expected target floor.", targetFloor == decodedMsg[3]);
	}
	
	/**
	 * Tests decoding of an communication of a request for elevator down push 
	 */
	@Test
	public void testDecodeElevatorDown() {
		int elevatorNumber = 1;
		int currentFloor = 2;
		MotorState currentState = MotorState.MOVING_DOWN;
		int targetFloor = 5;
		
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, currentState, targetFloor);
		
		int[] decodedMsg = Common.decode(msg);
		
		assertTrue("Message is of expected type int array.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 4.", 4 == decodedMsg.length);
		assertTrue("Returns the expected elevator number.", elevatorNumber == decodedMsg[0]);
		assertTrue("Returns the expected currentFloor number.", currentFloor == decodedMsg[1]);
		assertTrue("Returns the expected MotorState.", -1 == decodedMsg[2]);
		assertTrue("Returns the expected target floor.", targetFloor == decodedMsg[3]);
	}
	
	/**
	 * Test decoding of the start message
	 */
	@Test
	public void testDecodeStartMSG() {
		byte[] msg = Common.encodeStartMsg(false); 
		int[] decodedMsg = Common.decode(msg);
		
				
		assertTrue("Message is in expected format.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 4.", 2 == decodedMsg.length);
		assertEquals(5, decodedMsg[0]);
		assertEquals(-106, decodedMsg[1]);
	}
	
	/**
	 * Tests decoding of Elevator errors messages specifically stuck between
	 */
	@Test
	public void testDecodeElevatorErrorStuckBetween() {
		int elevatorNumber = 1;
		int currentFloor = 2;
		int targetFloor = 5;
		boolean dir = true; 
		
		byte[] msg = Common.encodeElevError(Common.ELEV_ERROR.STUCK_BETWEEN, elevatorNumber, currentFloor, targetFloor, dir);
		
		int[] decodedMsg = Common.decode(msg); 
		
		assertTrue("Message is in expected format.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 4.", 4 == decodedMsg.length);
		assertTrue("Returns expected elevator number.", elevatorNumber == decodedMsg[0]);
		assertTrue("Returns the expected currentFloor number.", currentFloor == decodedMsg[1]);
		assertTrue("Returns the expected target floor", targetFloor == decodedMsg[2]);
		assertTrue("Returns expected target floor.", 1 == decodedMsg[3]);
	}
	
	/**
	 * Tests decoding of Elevator errors messages specifically door close
	 */
	@Test
	public void testDecodeElevatorErrorDoorClose() {
		int elevatorNumber = 1;
		int currentFloor = 5;
		int targetFloor = 2;
		boolean dir = false; 
		
		byte[] msg = Common.encodeElevError(Common.ELEV_ERROR.DOOR_CLOSE, elevatorNumber, currentFloor, targetFloor, dir);
		
		int[] decodedMsg = Common.decode(msg); 
		
		assertTrue("Message is in expected format.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 4.", 4 == decodedMsg.length);
		assertTrue("Returns expected elevator number.", elevatorNumber == decodedMsg[0]);
		assertTrue("Returns the expected currentFloor number.", currentFloor == decodedMsg[1]);
		assertTrue("Returns the expected target floor", targetFloor == decodedMsg[2]);
		assertTrue("Returns expected target floor.", 0 == decodedMsg[3]);
	}
	
	/**
	 * Tests decoding of an communication of a request for elevator IDLE
	 */
	@Test
	public void testDecodeElevatorIdle() {
		int elevatorNumber = 1;
		int currentFloor = 2;
		MotorState currentState = MotorState.IDLE;
		int targetFloor = 5;
		
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, currentState, targetFloor);
		
		int[] decodedMsg = Common.decode(msg);
		
		assertTrue("Message is of expected type int array.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 4.", 4 == decodedMsg.length);
		assertTrue("Returns the expected elevator number.", elevatorNumber == decodedMsg[0]);
		assertTrue("Returns the expected currentFloor number.", currentFloor == decodedMsg[1]);
		assertTrue("Returns the expected MotorState.", 0 == decodedMsg[2]);
		assertTrue("Returns the expected target floor.", targetFloor == decodedMsg[3]);
	}
	
	/**
	 * Tests decoding of an communication of a request for floor up button push
	 */
	@Test
	public void testDecodeFloorUp() {
		int floor = 5;
		boolean isUp = true;
		byte[] msg = Common.encodeFloorMsgIntoBytes(floor, isUp);
		
		int[] decodedMsg = Common.decode(msg);

		assertTrue("Message is of expected type int array.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 2.", decodedMsg.length == 2);
		assertTrue("Returns the expected floor number.", floor == decodedMsg[0]);
		assertTrue("Returns the expected elevator direction.", 1 == decodedMsg[1]);
	}
	
	/**
	 * Tests decoding of an communication of a request for floor button down push
	 */
	@Test
	public void testDecodeFloorDown() {
		int floor = 5;
		boolean isUp = false;
		byte[] msg = Common.encodeFloorMsgIntoBytes(floor, isUp);
		
		int[] decodedMsg = Common.decode(msg);

		assertTrue("Message is of expected type int array.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 2.", decodedMsg.length == 2);
		assertTrue("Returns the expected floor number.", floor == decodedMsg[0]);
		assertTrue("Returns the expected elevator direction.", 0 == decodedMsg[1]);
	}
	
	/**
	 * Tests decoding of an communication of a request for scheduler communication
	 */
	@Test
	public void testDecodeScheduler() {
		int floor = 6;
		int elevatorNumber = 1;
		boolean isUp = true;
		byte[] msg = Common.encodeSchedulerMsgIntoBytes(elevatorNumber, floor, isUp);
		int[] decodedMsg = Common.decode(msg);
		assertTrue("Message is of expected type int array.", decodedMsg instanceof int[]);
		assertTrue("Message is of expected length 1.", decodedMsg.length == 3);
		assertTrue("Returns expected elevator number.", elevatorNumber == decodedMsg[0]);
		assertTrue("Return the expected floor number.", floor == decodedMsg[1]);
		assertTrue("return the expected elevator direction.", 1 == decodedMsg[2]);
	}
	
	/**
	 * Tests for a basic send/receive request with RPC 
	 */
	@Test
	public void testRPCSendAndReceive() {
		RPC sender;
		try {
			sender = new RPC(InetAddress.getLocalHost(),3,4);
			RPC receiver = new RPC(InetAddress.getLocalHost(),4,3);
			byte[] data = Common.encodeFloorMsgIntoBytes(10, false);
			sender.sendPacket(data);
			byte[] received = receiver.receivePacket();
			assertArrayEquals(data,received);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
}

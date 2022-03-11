package test;

import static org.junit.Assert.assertArrayEquals;
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

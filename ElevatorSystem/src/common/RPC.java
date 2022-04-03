package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * The RPC class is used as helper class for RPC communication.
 */
public class RPC {
	
	private final InetAddress destAddress;
    private final int destinationPort, receivePort;
    private DatagramSocket sendReceiveSocket;
    private final int BUF_SIZE = 100;

    /**
     * Constructor for the RPC class.
     * 
     * @param destAddress - the destination address
     * @param destinationPort - the destination port
     * @param receivePort - the receive port
     */
    public RPC(InetAddress destAddress, int destinationPort, int receivePort) {
        this.destAddress = destAddress;
        this.destinationPort = destinationPort;
        this.receivePort = receivePort;
        initSocket();
    }
    
    /**
     * This method is responsible for creating the sendReceive socket
     */
    private void initSocket() {
        try {
            sendReceiveSocket = new DatagramSocket(receivePort);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This method is responsible for sending a packet
     * 
     * @param msg - the bytes to send
     */
    public void sendPacket(byte[] msg) {
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, destAddress, destinationPort);
        
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * This method is responsible for receiving a packet
     * 
     * @return byte[] - the bytes received from the packet
     */
    public byte[] receivePacket() {
        byte[] data = new byte[BUF_SIZE];

        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        
        try {
            sendReceiveSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
        	return null;
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        
        byte[] result = new byte[receivePacket.getLength()];
        
        for(int i = 0; i < result.length; ++i) result[i] = data[i];
        
        return result;
    }
    
    /**
     * This method is used to timeout the socket to the milliseconds provided.
     * 
     * @param ms - the milliseconds to timeout socket to
     */
    public void setSocketTimeout(int ms) {
    	try {
			sendReceiveSocket.setSoTimeout(ms);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }

}
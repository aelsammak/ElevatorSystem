package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class RPC {
	
	private final InetAddress destAddress;
    private final int destinationPort, receivePort;
    private DatagramSocket sendReceiveSocket;
    private final int BUF_SIZE = 100;

    public RPC(InetAddress destAddress, int destinationPort, int receivePort) {
        this.destAddress = destAddress;
        this.destinationPort = destinationPort;
        this.receivePort = receivePort;
        initSocket();
    }
    
    private void initSocket() {
        try {
            sendReceiveSocket = new DatagramSocket(receivePort);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendPacket(byte[] msg) {
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, destAddress, destinationPort);
        
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    
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
    
    public void setSocketTimeout(int ms) {
    	try {
			sendReceiveSocket.setSoTimeout(ms);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }

}

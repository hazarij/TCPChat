import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * A class that sends data from System.in on a given socket.
 */
class RoomSender implements Runnable {
	// socket to send on
	private Socket sock;
	private Queue<byte[]> messages;
	
	public RoomSender (Socket sock) {
		this.sock = sock;
		this.messages = new ConcurrentLinkedQueue<byte[]>();
	}
	
	public void run() {
		try {
			final InputStreamReader inFromClient = new InputStreamReader(sock.getInputStream());
			final DataOutputStream outToClient = new DataOutputStream(sock.getOutputStream());
			
			Thread t = new Thread(new Runnable() {           
	            public void run() {
	            	try {
		            	int readByte = inFromClient.read();
		            	while (readByte != -1) {
			                if (readByte == (int) 0x01) {
			                	List<Byte> messageBytesList = new ArrayList<Byte>();
			                	while (readByte != (int) 0x02) {
			                		readByte = inFromClient.read();
			                		messageBytesList.add((byte) readByte);
			                	}
			                	
			                	byte[] messageBytes = new byte[messageBytesList.size()];
			                	
			                	for (int i = 0; i < messageBytesList.size(); i++)
			                		messageBytes[i] = messageBytesList.get(i);
			                	
			                	messages.add(messageBytes);
			                }
		            	}
	            	} catch (IOException e) {
	            		e.printStackTrace();
	            	}
	            } 
	        });
	        t.start();
			
			while (true) {
				if (!messages.isEmpty()) {
					// send message
					outToClient.write(messages.poll());
					outToClient.flush();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String byteListToString(List<Byte> l) {
	    if (l == null) {
	        return "";
	    }
	    byte[] array = new byte[l.size()];
	    int i = 0;
	    for (Byte current : l) {
	        array[i] = current;
	        i++;
	    }
	    return new String(array);
	}
}
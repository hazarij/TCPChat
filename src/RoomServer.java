import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/*
 * A class that sends data from System.in on a given socket.
 */
class RoomServer implements Runnable {
	// socket to send on
	private Socket sock;
	private Queue<byte[]> messages;
	private Set<DataOutputStream> outStreams;
	
	public RoomServer (Socket sock, Queue<byte[]> messages, Set<DataOutputStream> outStreams) {
		this.sock = sock;
		this.messages = messages;
		this.outStreams = outStreams;
	}
	
	public void run() {
		try {
			final InputStreamReader inFromClient = new InputStreamReader(sock.getInputStream());
			//final DataOutputStream outToClient = new DataOutputStream(sock.getOutputStream());
			
			Thread t = new Thread(new Runnable() {           
	            public void run() {
	            	try {
		            	int readByte = 0;
		            	
		            	while (readByte != -1) {
		            		readByte = inFromClient.read();
			                if (readByte == (int) 0x01) {
			                	//System.out.println("SERVER GOT SOMETHING!"+readByte);
			                	List<Byte> messageBytesList = new ArrayList<Byte>();
			                	messageBytesList.add((byte) readByte);
			                	
			                	byte[] unameLengthBytes = new byte[4];
			                	for (int i = 0; i < 4; i++) {
			                		readByte = inFromClient.read();
			                		messageBytesList.add((byte) readByte);
			                		unameLengthBytes[i] = (byte) readByte;
			                	}
			                	for (int i = 0; i < ByteUtils.bytesToInt(unameLengthBytes); i++) {
			                		readByte = inFromClient.read();
			                		messageBytesList.add((byte) readByte);
			                	}
			                	byte[] messageLengthBytes = new byte[4];
			                	for (int i = 0; i < 4; i++) {
			                		readByte = inFromClient.read();
			                		messageBytesList.add((byte) readByte);
			                		messageLengthBytes[i] = (byte) readByte;
			                	}
			                	for (int i = 0; i < ByteUtils.bytesToInt(messageLengthBytes); i++) {
			                		readByte = inFromClient.read();
			                		messageBytesList.add((byte) readByte);
			                	}
			                	for (int i = 0; i < 9; i++) {
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
					byte[] m = messages.poll();
					for (DataOutputStream outToClient : outStreams) {
						try {
							outToClient.write(m);
							outToClient.flush();
						} catch (NullPointerException e) {
							
						}
					}
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
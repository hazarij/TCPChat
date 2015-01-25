import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
 * A class that reads incoming data on a given socket.
 */
class Reader implements Runnable {
	// socket to send on
	private Socket sock;
	
	public Reader (Socket sock) {
		this.sock = sock;
	}
	
	public void run() {
		try {
			InputStreamReader inFromServer = new InputStreamReader(sock.getInputStream());
			
			int readByte = 0;
        	while (readByte != -1) {
        		readByte = inFromServer.read();
        		if (readByte == (int) 0x01) {
                	//System.out.println("CLIENT GOT SOMETHING!"+readByte);
                	List<Byte> messageBytesList = new ArrayList<Byte>();
                	messageBytesList.add((byte) readByte);
                	
                	byte[] unameLengthBytes = new byte[4];
                	for (int i = 0; i < 4; i++) {
                		readByte = inFromServer.read();
                		messageBytesList.add((byte) readByte);
                		unameLengthBytes[i] = (byte) readByte;
                	}
                	for (int i = 0; i < ByteUtils.bytesToInt(unameLengthBytes); i++) {
                		readByte = inFromServer.read();
                		messageBytesList.add((byte) readByte);
                	}
                	byte[] messageLengthBytes = new byte[4];
                	for (int i = 0; i < 4; i++) {
                		readByte = inFromServer.read();
                		messageBytesList.add((byte) readByte);
                		messageLengthBytes[i] = (byte) readByte;
                	}
                	for (int i = 0; i < ByteUtils.bytesToInt(messageLengthBytes); i++) {
                		readByte = inFromServer.read();
                		messageBytesList.add((byte) readByte);
                	}
                	for (int i = 0; i < 9; i++) {
                		readByte = inFromServer.read();
                		messageBytesList.add((byte) readByte);
                	}
                	
                	byte[] messageBytes = new byte[messageBytesList.size()];
                	
                	for (int i = 0; i < messageBytesList.size(); i++)
                		messageBytes[i] = messageBytesList.get(i);
                	
                	Message m = new Message(messageBytes);
                	
                	System.out.println(m.getUsername()+":\t"+m.getMessage());
                }
        		
//        		System.out.println(readByte);
//        		readByte = inFromServer.read();
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
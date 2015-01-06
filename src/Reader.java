import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
			
			while (true) {
				int readByte = inFromServer.read();
				if (readByte != -1) 
					System.out.print((char) readByte);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
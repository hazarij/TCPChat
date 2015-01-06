import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * A class that sends data from System.in on a given socket.
 */
class Sender implements Runnable {
	// socket to send on
	private Socket sock;
	
	public Sender (Socket sock) {
		this.sock = sock;
	}
	
	public void run() {
		try {
			DataOutputStream outToClient = new DataOutputStream(sock.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			int readByte = in.read();
			while (readByte != -1) {
				outToClient.write(readByte);
				outToClient.flush();
				readByte = in.read();
			}
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
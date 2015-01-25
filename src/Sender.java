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
	private String username;
	
	public Sender (Socket sock, String username) {
		this.sock = sock;
		this.username = username;
	}
	
	public void run() {
		try {
			DataOutputStream outToClient = new DataOutputStream(sock.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			String readLine = in.readLine();
			while (readLine != null) {
				Message m = new Message(username, readLine, System.currentTimeMillis());
				byte[] bytes = m.getBytes();
				
				outToClient.write(bytes);
				outToClient.flush();
				readLine = in.readLine();
			}
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
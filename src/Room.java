import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/*
 * A Server that, when contacted, sends and receives data to/from
 * the contacting client
 */
class Room implements Runnable {
	
	private String name;
	private String description;
	private String host;
	private int port;
	private Set<String> users;
	
	public Room(String name, String description, String host, int port, String username) {
		this.name = name;
		this.description = description;
		this.host = host;
		this.port = port;
		this.users = new HashSet<String>();
		this.users.add(username);
	}
	
	public void run() {
		
		ServerSocket servSock;
		try {
			servSock = new ServerSocket(port);
			
			// wait for a connection, start new threads for sending/receiving data
			// when contacted
			while (true) {
				Socket connectionSocket = servSock.accept();
				RoomSender sender = new RoomSender(connectionSocket);
				Thread s = new Thread(sender);
				s.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
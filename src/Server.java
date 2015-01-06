import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * A Server that, when contacted, sends and receives data to/from
 * the contacting client
 */
class Server {
	
	public static void main(String args[]) throws Exception {
		// ensure correct program usage
		if (args.length != 0) {
			System.out.println("Usage: java Server");
			System.exit(1);
		}
		
		ServerSocket servSock = new ServerSocket(46100);
		
		// wait for a connection, start new threads for sending/receiving data
		// when contacted
		Socket connectionSocket = servSock.accept();
		Sender sender = new Sender(connectionSocket);
		Thread s = new Thread(sender);
		s.start();
		Reader reader = new Reader(connectionSocket);
		Thread r = new Thread(reader);
		r.start();
	}
}
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

/*
 * A Server that, when contacted, sends and receives data to/from
 * the contacting client
 */
class Room implements Runnable {
	private String name;
	private String description;
	private String host;
	private int port;
	private Queue<byte[]> messages;
	private Set<DataOutputStream> outStreams;
	
	private static final Random rand = new Random();
	private static Connection conn;
	
	final static String ADD_ROOM_SQL = "insert into rooms (room_id, name, description, host, port) "
			+ "values (" +rand.nextInt(Integer.MAX_VALUE)+ ", ?, ?, ?, ?);";
	static PreparedStatement addRoomStatement;
	
	private static void prepareStatements() throws Exception {
		addRoomStatement = conn.prepareStatement(ADD_ROOM_SQL);
	}
	
	public Room(String name, String description, String host, int port) {
		this.name = name;
		this.description = description;
		this.host = host;
		this.port = port;
		this.messages = new ConcurrentLinkedQueue<byte[]>();
		this.outStreams = new CopyOnWriteArraySet<DataOutputStream>();
	}
	
	public void run() {
		
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://ec2-54-235-76-253.compute-1.amazonaws.com:5432/d7cusktdcbqqdj?username=rralvrdgeksflk&password=v1qVMUeKw1ff6jt2smrmF9kxqQ&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", "rralvrdgeksflk", "v1qVMUeKw1ff6jt2smrmF9kxqQ");
			conn.setAutoCommit(true); //by default automatically commit after each statement 
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prepareStatements();
			
			addRoomStatement.clearParameters();
			addRoomStatement.setString(1, name);
			addRoomStatement.setString(2, description);
			addRoomStatement.setString(3, host);
			addRoomStatement.setInt(4, port);
			addRoomStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ServerSocket servSock;
		try {
			servSock = new ServerSocket(port);
			
			// wait for a connection, start new threads for sending/receiving data
			// when contacted
			while (true) {
				Socket connectionSocket = servSock.accept();
				DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
				outStreams.add(out);
				System.out.println("new connection");
				RoomServer server = new RoomServer(connectionSocket, messages, outStreams);
				Thread s = new Thread(server);
				s.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
}
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TCPChat {
	private static Scanner in;
	private static Connection conn;
	
	final static String FIND_USER_SQL = "select * from users where username = ?;";
		static PreparedStatement findUserStatement;
	final static String ALL_ROOMS_SQL = "select * from rooms;";
		static PreparedStatement allRoomsStatement;
	final static String ROOM_INFO_SQL = "select * from rooms where room_id = ?;";
		static PreparedStatement roomInfoStatement;
	final static String USERS_IN_ROOM_SQL = "select u.name from users u, users_rooms ur where u.username = ur.username and r.room_id = ?;";
		static PreparedStatement usersInRoomStatement;

	public static void main(String[] args) throws Exception {
//		Message m = new Message ("hazarij", "hello there, this is Jordan!", System.currentTimeMillis());
//		System.out.println(m.getUsername());
//		System.out.println(m.getMessage());
//		System.out.println(new Timestamp(m.getTimestamp()));
//		byte[] b = m.getBytes();
//		Message m2 = new Message(b);
//		System.out.println(m2.getUsername());
//		System.out.println(m2.getMessage());
//		System.out.println(new Timestamp(m2.getTimestamp()));
		
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://ec2-54-235-76-253.compute-1.amazonaws.com:5432/d7cusktdcbqqdj?username=rralvrdgeksflk&password=v1qVMUeKw1ff6jt2smrmF9kxqQ&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", "rralvrdgeksflk", "v1qVMUeKw1ff6jt2smrmF9kxqQ");
		conn.setAutoCommit(true); //by default automatically commit after each statement 
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		prepareStatements();
		
		in = new Scanner(System.in);
		boolean loggedIn = false;
		while (!loggedIn) {
			System.out.print("Enter username: ");
			String uname = in.next();
			
			findUserStatement.clearParameters();
			findUserStatement.setString(1, uname);
			ResultSet userSet = findUserStatement.executeQuery();
			if (userSet.next()) {
				System.out.print("Enter password: ");
				String pword = in.next();
				
				if (userSet.getString(2).equals(pword)) {
					loggedIn = true;
					System.out.println("Welcome, "+uname+"!\n");
					mainMenu();
				} else {
					System.out.println("ERROR: incorrect password!\n");
				}
			} else {
				System.out.println("ERROR: username does not exist!\n");
			}
		}
	}
	
	private static void mainMenu() {
		
	}
	
	private static void prepareStatements() throws Exception {
		findUserStatement = conn.prepareStatement(FIND_USER_SQL);
	}
}

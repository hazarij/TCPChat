import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCPChat {
	private static Scanner in;
	private static Connection conn;
	private static List<Room> allRooms;
	private static String userHost;
	private static String username;
	private static boolean inRoom;
	
	final static String FIND_USER_SQL = "select * from users where username = ?;";
		static PreparedStatement findUserStatement;
	final static String ALL_ROOMS_SQL = "select * from rooms;";
		static PreparedStatement allRoomsStatement;
	final static String ROOM_INFO_SQL = "select * from rooms where room_id = ?;";
		static PreparedStatement roomInfoStatement;
	final static String USERS_IN_ROOM_SQL = "select u.name from users u, users_rooms ur where u.username = ur.username and r.room_id = ?;";
		static PreparedStatement usersInRoomStatement;
		
	private static void prepareStatements() throws Exception {
		findUserStatement = conn.prepareStatement(FIND_USER_SQL);
		allRoomsStatement = conn.prepareStatement(ALL_ROOMS_SQL);
		roomInfoStatement = conn.prepareStatement(ROOM_INFO_SQL);
		usersInRoomStatement = conn.prepareStatement(USERS_IN_ROOM_SQL);
	}

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
		
		
		inRoom = false;
		allRooms = new ArrayList<Room>();
		InetAddress local = InetAddress.getLocalHost();
		userHost = local.getHostAddress();
		
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://ec2-54-235-76-253.compute-1.amazonaws.com:5432/d7cusktdcbqqdj?username=rralvrdgeksflk&password=v1qVMUeKw1ff6jt2smrmF9kxqQ&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", "rralvrdgeksflk", "v1qVMUeKw1ff6jt2smrmF9kxqQ");
		conn.setAutoCommit(true); //by default automatically commit after each statement 
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		prepareStatements();
		
		in = new Scanner(System.in);
		boolean loggedIn = false;
		while (!loggedIn) {
			System.out.print("Enter username: ");
			username = in.next();
			
			findUserStatement.clearParameters();
			findUserStatement.setString(1, username);
			ResultSet userSet = findUserStatement.executeQuery();
			if (userSet.next()) {
				System.out.print("Enter password: ");
				String pword = in.next();
				
				if (userSet.getString(2).equals(pword)) {
					loggedIn = true;
					System.out.println("Welcome, "+username+"!\n");
					mainMenu();
				} else {
					System.out.println("ERROR: incorrect password!\n");
				}
			} else {
				System.out.println("ERROR: username does not exist!\n");
			}
		}
	}
	
	private static void mainMenu() throws SQLException {
		while (!inRoom) {
			System.out.print("\nMAIN MENU: "
					+ "\n\t[1]\tView Rooms"
					+ "\n\t[2]\tCreate Room"
					+ "\n\t[3]\tSign Out"
					+ "\nPlease make a selection: ");
			int choice = in.nextInt();
			in.nextLine();
			
			if (choice == 1) {
				roomsMenu();
			} else if (choice == 2) {
				createRoom();
			} else if (choice == 3) {
				System.out.println("\nGoodbye!");
				System.exit(1);
			} else {
				System.out.println("\nINVALID CHOICE! Please choose from the numbers on the left.");
			}
		}
	}
	
	private static void roomsMenu () throws SQLException {
		allRoomsStatement.clearParameters();
		ResultSet allRoomsSet = allRoomsStatement.executeQuery();
		
		while (!inRoom) {
			allRooms.clear();
			System.out.println("\nAvailable Rooms:");
			int i = 1;
			while (allRoomsSet.next()) {
				String name = allRoomsSet.getString(2);
				String description = allRoomsSet.getString(3);
				String host = allRoomsSet.getString(4);
				int port = allRoomsSet.getInt(5);
				
				Room currRoom = new Room(name, description, host, port);
				
				allRooms.add(currRoom);
				
				System.out.println("\t["+i+"]\tView \""+name+"\"");
				i++;
			}
			System.out.println("\t["+i+"]\tGo back to main menu");
			System.out.print("Please make a selection: ");
			int choice = in.nextInt();
			in.nextLine();
			
			if (choice > 0 && choice <= allRooms.size()) {
				roomInfo(allRooms.get(choice-1));
			} else if (choice == allRooms.size()+1) {
				mainMenu();
			} else {
				System.out.println("INVALID CHOICE! Please choose from the numbers on the left.\n");
			}
		}
	}
	
	private static void roomInfo (Room room) throws SQLException {
		while (!inRoom) {
			System.out.println("\nROOM NAME: "+room.getName());
			System.out.println("DESCRIPTION: "+room.getDescription());
			System.out.println("\t[1]\tJoin Room");
			System.out.println("\t[2]\tGo back to room list");
			System.out.println("\t[3]\tGo back to main menu");
			System.out.print("Please make a selection: ");
			int choice = in.nextInt();
			in.nextLine();
			
			if (choice == 1) {
				inRoom = true;
				roomView(room);
			} else if (choice == 2) {
				roomsMenu();
			} else if (choice == 3) {
				mainMenu();
			} else {
				System.out.println("INVALID CHOICE! Please choose from the numbers on the left.\n");
			}
		}
	}
	
	private static void createRoom() throws SQLException {
		System.out.println("\n CREATE ROOM:");
		System.out.print("\t Enter room name: ");
		String name = in.nextLine();
		System.out.print("\n\t Enter description: ");
		String description = in.nextLine();
		int port = findAPortNum();
		
		Room room = new Room(name, description, userHost, port);
		Thread t = new Thread(room);
		t.start();
	}
	
	private static int findAPortNum() {
		ServerSocket ss = null;
		int num = -1;
		try {
			ss = new ServerSocket(0);
			num = ss.getLocalPort();
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return num;
	}
	
	private static void roomView (Room room) {
		Socket clientSocket;
		try {
			clientSocket = new Socket(room.getHost(), room.getPort());
			
			Sender sender = new Sender(clientSocket, username);
			Thread s = new Thread(sender);
			s.start();
			
			Reader reader = new Reader(clientSocket);
			Thread r = new Thread(reader);
			r.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

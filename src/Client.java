import java.net.Socket;

class Client {
	public static void main(String args[]) throws Exception {
		Socket clientSocket = new Socket("localhost", 46100);
		
		Sender sender = new Sender(clientSocket);
		Thread s = new Thread(sender);
		s.start();
		
		Reader reader = new Reader(clientSocket);
		Thread r = new Thread(reader);
		r.start();
	}
}
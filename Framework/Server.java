public class Server {
	public static void main(String[] args) {
		Thread server;
		server = ServerThread.getInstance();		
		server.start();
	}
}

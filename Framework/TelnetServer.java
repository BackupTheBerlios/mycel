import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class TelnetServer extends Thread implements ServerModule {
	private int port = -1;
	
	public TelnetServer(int port) {
		super();
		this.port = port;
		this.setName("Telnet-Server");
	}
	
	public String getModuleName() {
		return "Telnet-Server";
	}
	
	public void run() {
		ServerSocket socket;
		Socket csock;
		Thread thread;
		
		System.out.println(this.getClass().getName() + ": Telnet-Server wird gestartet...");
		try {
			socket = new ServerSocket(this.port);
		}
		catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.err.println(ioe.getStackTrace());
			return;
		}
		try {
			socket.setSoTimeout(1000);
		}
		catch (SocketException se) {
			System.err.println(se.getLocalizedMessage());
			System.err.println(se.getStackTrace());
			return;
		}
		System.out.println(this.getClass().getName() + ": Warte auf neue Verbindung...");
		while (!Thread.interrupted()) {
			try {
				csock = socket.accept();
				System.out.println(this.getClass().getName() + ": Neue Verbindung empfangen.");
				thread = new Thread(new Shell(new TelnetClient(csock)));
				thread.setName("Shell");
				thread.start();
			}
			catch (SocketTimeoutException ste) {
				// Nothing to do.
			}
			catch (IOException ioe) {
				System.err.println(ioe.getLocalizedMessage());
				System.err.println(ioe.getStackTrace());
			}			
		}
		System.out.println(this.getClass().getName() + ": Telnet-Server wird gestoppt...");
		try {
			socket.close();
		}
		catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.err.println(ioe.getStackTrace());
		}
		System.out.println(this.getClass().getName() + ": Telnet-Server ist gestoppt.");
	}
}

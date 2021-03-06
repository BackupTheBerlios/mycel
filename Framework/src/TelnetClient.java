import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TelnetClient implements ShellConnection {
	private Socket socket = null;
	
	public TelnetClient(Socket socket) {
		super();
		this.socket = socket;
		System.out.println(this.getClass().getName() + ": Neuer Client.");
	}
	
	public void close() throws IOException {
		this.socket.close();
	}
	
	public InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}
	
	public OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}
	
	public String getHostName() {
		return this.socket.getInetAddress().getHostName();
	}
	
	public String getHostAddress() {
		return this.socket.getInetAddress().getHostAddress();
	}
	
	public int getHostPort() {
		return this.socket.getPort();
	}
	
	public String getLocalName() {
		return this.socket.getLocalAddress().getHostName();
	}
	
	public String getLocalAddress() {
		return this.socket.getLocalAddress().getHostAddress();
	}
	
	public int getLocalPort() {
		return this.socket.getLocalPort();
	}
	
	public void setEcho(boolean echo) {
	}
	
	public boolean getEcho() {
		return false;
	}
	
	public void setEchoChar(int echoChar) {
	}
}

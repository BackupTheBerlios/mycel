import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;



public class Shell extends Thread {

	private ShellConnection connection = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	
	public Shell(ShellConnection connection) {
		super();
		this.connection = connection;
		this.setName("Shell");
		System.out.println(this.getClass().getName() + ": Neue Shell.");
	}
	
	public void run() {
		Thread thread;
		String command;
		
		System.out.println(this.getClass().getName() + ": Neue Shell wird gestartet...");
		thread = Thread.currentThread();
		try {
			this.br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			this.pw = new PrintWriter(connection.getOutputStream());
		}
		catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.err.println(ioe.getStackTrace());
			return;
		}
		System.out.println("Verbindung:");
		System.out.println("\tHost Name: " + this.connection.getHostName());
		System.out.println("\tHost Adresse: " + this.connection.getHostAddress());
		System.out.println("\tHost Port: " + this.connection.getHostPort());
		System.out.println("\tLokaler Name: " + this.connection.getLocalName());
		System.out.println("\tLokale Adresse: " + this.connection.getLocalAddress());
		System.out.println("\tLokaler Port: " + this.connection.getLocalPort());
		if (login()) {
			while (((command = this.prompt()) != null) && (!command.equals("halt"))) {
				// Hier muessten die Befehle hin.
			}
			if (command.equals("halt")) {
				ServerThread.getInstance().interrupt();
			}
		}
		System.out.println(this.getClass().getName() + ": Shell wird gestoppt...");
		try {
			this.connection.close();
		}
		catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.err.println(ioe.getStackTrace());
		}
		System.out.println(this.getClass().getName() + ": Shell ist gestoppt.");
		//ServerThread.getInstance().interrupt();
	}
	
	private boolean login() {
		String username;
		String password;
		
		this.print("username: ");
		try {
			username = this.br.readLine();
		}
		catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.err.println(ioe.getStackTrace());
			return false;
		}
		//this.println(username);
		
		this.print("password: ");
		try {
			password = this.br.readLine();
		}
		catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.err.println(ioe.getStackTrace());
			return false;
		}
		//this.println(password);
		return true;
	}
	
	private String prompt() {
		String command = null;
		
		this.print("server~: # ");
		
		try {
			command = this.br.readLine();
		}
		catch (IOException ioe) {
			System.err.println(ioe.getLocalizedMessage());
			System.err.println(ioe.getStackTrace());
			return null;
		}
		return command;
	}
	
	private void println(String s) {
		/*
		int index;
		char ch;
		
		System.out.println(s);
		for (index = 0; index < s.length(); index++) {
			ch = s.charAt(index);
			System.out.println("\t" + ch + "\t" + (int)ch);
		}
		*/
		this.pw.println(s);
		this.pw.flush();
	}

	private void print(String s) {
		/*
		int index;
		char ch;
		
		System.out.println(s);
		for (index = 0; index < s.length(); index++) {
			ch = s.charAt(index);
			System.out.println("\t" + ch + "\t" + (int)ch);
		}
		*/
		this.pw.print(s);
		this.pw.flush();
	}
}

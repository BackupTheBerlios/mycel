import java.util.Vector;

public class ServerThread extends Thread {
	private static Thread serverThread = null;
	private Vector modules = null;
	
	private ServerThread() {
		super();
		this.setName("Server");
		this.initialize();
	}
	
	private void initialize() {
		if (this.modules == null) {
			this.modules = new Vector();
		}
	}
	
	synchronized public static Thread getInstance() {
		if (serverThread == null) {
			serverThread = new ServerThread();
		}
		return serverThread;
	}
	
	public void run() {
		Thread thread;
		int index;
		
		// Server starten
		System.out.println("Starte Server...");
		// Module laden
		thread = new TelnetServer(8023);
		this.modules.addElement(thread);
		thread = new RawConServer(6000);
		this.modules.addElement(thread);
		// Module starten
		System.out.println("Starte Module...");
		for (index = 0; index < this.modules.size(); index++) {
			thread = (Thread)this.modules.elementAt(index);
			System.out.print("\t Modul " + ((ServerModule)thread).getModuleName());
			thread.start();
			System.out.println(" gestartet.");
		}
		System.out.println("Module gestartet."); 
		// Befehle der Module entgegen nehmen.
		System.out.println("Server gestartet.");
		thread = Thread.currentThread();
		while (!Thread.interrupted()) {
			Thread.yield();
		}
		System.out.println("Stoppe Server...");
		// Module stoppen
		System.out.println("Stoppe Module...");
		for (index = 0; index < this.modules.size(); index++) {
			thread = (Thread)this.modules.elementAt(index);
			System.out.print("\t Modul " + ((ServerModule)thread).getModuleName());
			thread.interrupt();
			while (thread.isAlive()) {
				Thread.yield();
			}
			System.out.println(" gestoppt.");
		}
		// Server stoppen
		System.out.println("Server gestoppt.");
		this.printPS();
	}
	
	private void printPS() {
		ThreadGroup group;
		Thread[] tarray;
		Thread thread;
		int index;
		
		group = Thread.currentThread().getThreadGroup();
		tarray = new Thread[group.activeCount()];
		group.enumerate(tarray);
		for (index = 0; index < tarray.length; index++) {
			thread = tarray[index];
			System.out.print("Thread " + index + ": ");
			if (thread != null) {
				System.out.print("Name = " + thread.getName());
				System.out.print("; Alive = " + thread.isAlive());
				System.out.print("; Daemon = " + thread.isDaemon());
				System.out.println("; Interrupted = " + thread.isInterrupted());
			}
			else {
				System.out.println("Tot");
			}
		}
	}
}

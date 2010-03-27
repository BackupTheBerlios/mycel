/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mycel.common.ConnectionEvent;
import org.mycel.common.ConnectionListener;

/**
 * 
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class ConnectionManager implements Runnable {
	/** Die maximale Zeit in Millisekunden bis zum Timout beim Warten auf eine Verbindung. */
	public final static int MAX_TIMEOUT = 1000;
	
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(ConnectionManager.class);
	
	/** Die einzelnen Verbindungen inklusive ihrer Threads. */
	private Hashtable connections = new Hashtable();
	/** Der Server-Socket. */
	private ServerSocket socket = null;
	/** Der Port für eingehende Verbindungen. */
	private int port = -1;
	/** Der Listener für alle Verbindungen. */
	private ConnectionListener connectionListener = null;
	/** Die Listener. */
	private Vector connectionListeners = new Vector();
	
	/**
	 * Dies ist der Standard Konstruktor.
	 * @param port Der Port für eingehende Verbindungen.
	 * @throws IllegalArgumentException Wenn der Port nicht im zulässigen Bereich zwischen 0 und 65535 lag.
	 */
	public ConnectionManager(final int port) {
		super();
		if ((port < 0) || (port > 65535)) {
			throw new IllegalArgumentException("Port is out of range: Lower bounds: 0; Upper bounds: 65535; Port = " + port);
		}
		this.port = port;
		this.initialize();
	}
	
	/**
	 * Initialisiert das Objekt.
	 */
	private void initialize() {
		this.connectionListener = new ConnectionListener() {
			public final void dataReceived(final ConnectionEvent e) {
				processConnectionEvent(e);
			}
		};
	}
	
	/**
	 * Öffnet den Server-Socket.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 * @throws SecurityException Wenn der angegebene Port nicht geoffnet werden konnte.
	 * @throws IllegalStateException Wenn der Port nicht im zulässigen Bereich zwischen 0 und 65535 lag.
	 */
	private void open() throws IOException, SecurityException {
		if ((this.port < 0) || (this.port > 65535)) {
			throw new IllegalStateException("Port is out of range: Lower bounds: 0; Upper bounds: 65535; Port = " + this.port);
		}
		this.socket = new ServerSocket(this.port);
		this.socket.setSoTimeout(MAX_TIMEOUT);
	}
	
	/**
	 * Schließt den Server-Socket sowie alle Verbindungen zu den Clients.
	 * Diese Methode ist synchronisiert.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 */
	private void close() throws IOException {
		Enumeration e;
		
		try {
			synchronized (this.connections) {
				e = this.connections.keys();
				while (e.hasMoreElements()) {
					ClientConnection client = (ClientConnection) e.nextElement();
					Thread thread = (Thread) this.connections.get(client);
					thread.interrupt();
				}
			}
			this.socket.close();
		} finally {
			this.socket = null;
		}
	}
	
	/**
	 * Fügt eine Verbindung der Verbindungsverwaltung hinzu.
	 * Diese Methode ist synchronisiert.
	 * @param client Die Verbindung zum Client.
	 * @param thread Der zur Verbindung gehörende Thread.
	 */
	private void addConnection(final ClientConnection client, final Thread thread) {
		synchronized (this.connections) {
			this.connections.put(client, thread);
		}
	}
	
	/**
	 * Prüft die Verbindungen zu den Clients.
	 */
	private void checkConnections() {
		Enumeration e;
		
		synchronized (this.connections) {
			e = this.connections.keys();
			while (e.hasMoreElements()) {
				ClientConnection client = (ClientConnection) e.nextElement();
				if (client.isClosed()) {
					Thread thread = (Thread) this.connections.get(client);
					thread.interrupt();
					this.connections.remove(client);
				}
			}
		}
	}
	
	/**
	 * Nimmt eingehende Verbindungen an.
	 */
	public void run() {
		try {
			this.open();
		} catch (Throwable t) {
			log.fatal(t.getLocalizedMessage(), t);
			return;
		}
		log.trace("run(): ConnectionManager gestartet.");
		while (!Thread.interrupted()) {
			try {
				Socket clientSocket = this.socket.accept();
				ClientConnection clientConnection = new ClientConnection(clientSocket);
				log.trace("run(): Verbindung zu " + clientConnection.getHostAddress().toString() + "@" + clientConnection.getHostPort() + " aufgebaut.");
				clientConnection.addConnectionListener(new ConnectionListener() {
					public final void dataReceived(final ConnectionEvent e) {
						log.info("Element '" + e.getInformation().getName() + "' empfangen.");
					}					
				});
				clientConnection.addConnectionListener(this.connectionListener);
				Thread thread = new Thread(clientConnection, "ClientConnection(" + clientConnection.getHostAddress().toString() + "@" + clientConnection.getHostPort() + ")");
				thread.start();
				this.addConnection(clientConnection, thread);
			} catch (SocketTimeoutException ste) {
				// Keine Meldung
			} catch (Throwable t) {
				log.warn("run(): " + t.getLocalizedMessage(), t);
			}
		}
		try  {
			this.close();
		} catch (IOException ioe) {
			log.error("run(): " + ioe.getLocalizedMessage(), ioe);
		}
		log.trace("run(): ConnectionManager gestoppt.");
	}
	
	
	/**
	 * Informiert alle registrierten <code>ConnectionListener</code>.
	 * @param e Das Ereignis.
	 */
	protected void processConnectionEvent(final ConnectionEvent e) {
		synchronized (this.connectionListeners) {
			for (int index = 0; index < this.connectionListeners.size(); index++) {
				ConnectionListener listener = (ConnectionListener) this.connectionListeners.elementAt(index);
				listener.dataReceived(e);
			}
		}
	}
	
	/**
	 * Fügt einen <code>ConnectionListener</code> hinzu.
	 * @param l Der <code>ConnectionListener</code>.
	 */
	public void addConnectionListener(final ConnectionListener l) {
		synchronized (this.connectionListeners) {
			this.connectionListeners.addElement(l);
		}
	}
	
	/**
	 * Entfernt einen <code>ConnectionListener</code>.
	 * @param l Der <code>ConnectionListener</code>.
	 */
	public void removeConnectionListener(final ConnectionListener l) {
		synchronized (this.connectionListeners) {
			this.connectionListeners.removeElement(l);
		}
	}
	
	/**
	 * Gibt einen Array mit allen registrierten <code>ConnectionListener</code> zurück.
	 * @return Der Array.
	 */
	public ConnectionListener[] getConnectionListener() {
		ConnectionListener[] listeners;
			
		synchronized (this.connectionListeners) {
			listeners = new ConnectionListener[this.connectionListeners.size()];
			for (int index = 0; index < listeners.length; index++) {
				listeners[index] = (ConnectionListener) this.connectionListeners.elementAt(index);
			}
		}
		return listeners;
	}
	
	/**
	 * Sendet eine Nachricht über die Verbindung zum Client.
	 * @param message Die Nachricht.
	 */
	public void sendMessage(final Message message) {
		Enumeration e;
		
		log.trace("sendMessage(): Sende Nachricht...");
		synchronized (this.connections) {
			e = this.connections.keys();
			while (e.hasMoreElements()) {
				ClientConnection connection = (ClientConnection) e.nextElement();
				if (!connection.isClosed()) {
					if (connection == message.getClientConnection()) {
						connection.addElementToQueue(message.getInformation());
						log.trace("sendMessage(): Nachricht gesendet.");
						return;
					}
				}
			}
		}
		log.warn("sendMessage(): Konnte Nachricht nicht senden: Verbindung unbekannt.");
	}
}

/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.mycel.common.ConnectionEvent;
import org.mycel.common.ConnectionListener;

/**
 * Die Verbindung zum Server.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class ServerConnection implements Runnable {
	/** Die maximale Zeit in Millisekunden bis zum Timout. */
	public final static int MAX_TIMEOUT = 5000;
	
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(ServerConnection.class);
	
	/** Die Socket-Verbindung zum Server. */
	private Socket socket = null;
	/** Die Adresse des Servers. */
	private InetAddress address = null;
	/** Der Port des Servers. */
	private int port = -1;
	/** Der <code>InputStream</code> der Verbindung. */
	private InputStream in = null;
	/** Der <code>OutputStream</code> der Verbindung. */	
	private OutputStream out = null;
	/** Die Listener. */
	private Vector connectionListeners = new Vector();
	/** Die Warteschlange für die zu sendenden Elemente. */
	private Vector elementQueue = new Vector();
	
	/**
	 * Dies ist der Standard Konstruktor.
	 */
	public ServerConnection() {
		super();
	}
	
	/**
	 * Erstellt eine Verbindungsobjekt zum Server und setzt die Adresse sowie den Port des Servers.
	 * @param address Die Adresse.
	 * @param port Der Port.
	 * @throws NullPointerException Wenn die Adresse nicht gesetzt war.
	 * @throws IllegalArgumentException Wenn der Port nicht im zulässigen Bereich zwischen 0 und 65535 lag.
	 */
	public ServerConnection(final InetAddress address, final int port) {
		super();
		if (address == null) {
			throw new NullPointerException("address is null.");
		}
		this.address = address;	
		if ((port < 0) || (port > 65535)) {
			throw new IllegalArgumentException("port is out of range: Lower bounds: 0; Upper bounds: 65535; Port = " + port);
		}
		this.port = port;
	}
	
	/**
	 * Setzt die Adresse des Servers.
	 * @param address Die Adresse.
	 */
	public void setHostAddress(final InetAddress address) {
		this.address = address;
	}
	
	/**
	 * Gibt die Adresse des Servers zurück.
	 * @return Die Adresse oder <code>null</code>, wenn diese noch nicht gesetzt wurde.
	 */
	public InetAddress getHostAddress() {
		return this.address;
	}
	
	/**
	 * Setzt den Port des Servers.
	 * @param port Der Port.
	 * @throws IllegalArgumentException Wenn der Port nicht im zulässigen Bereich zwischen 0 und 65535 lag.
	 */
	public void setHostPort(final int port) {
		if ((port < 0) || (port > 65535)) {
			throw new IllegalArgumentException("port is out of range: Lower bounds: 0; Upper bounds: 65535; Port = " + port);
		}
		this.port = port;
	}
	
	/**
	 * Gibt den Port des Servers zurück.
	 * @return Der Port oder <code>-1</code>, wenn dieser noch nicht gesetzt wurde. 
	 */
	public int getHostPort() {
		return this.port;
	}
	
	/**
	 * Öffnet die Verbindung zum Server.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 * @throws SocketException Wenn beim Erstellen des Sockets ein Fehler auftrat.
	 * @throws NullPointerException Wenn die Adresse nicht gesetzt war.
	 * @throws IllegalStateException Wenn der Port nicht im zulässigen Bereich zwischen 0 und 65535 lag.
	 */
	private void open() throws IOException, SocketException {
		log.trace("open()");
		if (this.address == null) {
			throw new NullPointerException("address is null.");
		}
		if ((this.port < 0) || (this.port > 65535)) {
			throw new IllegalStateException("port is out of range: Lower bounds: 0; Upper bounds: 65535; Port = " + this.port);
		}
		this.socket = new Socket(this.address, this.port);
		this.socket.setSoTimeout(MAX_TIMEOUT);
		this.in = this.socket.getInputStream();
		this.out = this.socket.getOutputStream();
		log.trace("open(): Verbindung zu " + this.address.toString() + "@" + this.port + " aufgebaut.");
	}
	
	/**
	 * Schließt die Verbindung zum Server.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 */
	private void close() throws IOException {
		try {
			this.in = null;
			this.out = null;
			if (this.socket != null) {
				this.socket.close();
			}
		} finally {
			this.socket = null;
		}
	}
	
	/**
	 * Prüft, ob der Verbindung geschlossen ist.
	 * @return <code>true</code>, wenn die Verbindung geschlossen ist, ansonsten <code>false</code>.
	 */
	public synchronized boolean isClosed() {
		return (this.socket == null);
	}
	
	/**
	 * Versendet ein XML Element an den Server.
	 * @param element Das Element.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 */
	private void sendElement(final Element element) throws IOException {
		XMLOutputter outputter = new XMLOutputter();
		Document doc = new Document();
		
		log.trace("sendElement(): Sende Nachricht...");
		doc.setRootElement(element);
		outputter.output(doc, this.out);
		log.trace("sendElement(): Nachricht gesendet.");
	}
	
	/**
	 * Empfämgt ein XML Element vom Server.
	 * @return Das Element oder <code>null</code>, wenn kein Element empfangen wurde.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 * @throws JDOMException Wenn ein Parser-Fehler auftrat.
	 */
	private Element receiveElement() throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		StringBuffer sb;
		StringReader sr;
		
		if (this.in.available() == 0) {
			return null;
		}
		log.trace("receiveElement(): Empfange Nachricht...");
		sb = new StringBuffer();
		while (this.in.available() > 0) {
			int data = this.in.read();
			if (data != -1) {
				sb.append((char) data);
			}
		}
		sr = new StringReader(sb.toString());
		doc = builder.build(sr);
		log.trace("receiveElement(): Nachricht empfangen.");
		return doc.getRootElement();
	}
	
	/**
	 * Baut die Verbindung zum Server auf, versorgt die Verbindung und schließt sie wieder.
	 * In dieser Methode wird die Verbindung zum Server aufgebaut. Anschließend
	 * werden empfange Pakete weitergeleitet und Pakete, die versendet werden sollen,
	 * dem Server übermittelt.
	 * Ist eine Verbindung nicht möglich, wird eine Verbindung vom Server abgebaut
	 * oder der Thread darüber informiert, dass er sich beenden soll, wird die
	 * Verbindung geschlossen und die Methode verlassen.
	 */
	public void run() {
		int available = 0;
		Element element = null;
		
		// Verbindung oeffnen.
		try {
			this.open();
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t);
			try {
				this.close();
			} catch (Throwable t2) {
				log.error(t2.getLocalizedMessage(), t2);
			}
			return;
		}
		
		while ((!Thread.interrupted()) && (!this.isClosed())) {
			// Empfangen
			try {
				available = this.in.available();
			} catch (IOException ioe) {
				log.error(ioe.getLocalizedMessage(), ioe);
				available = 0;
			}
			if (available > 0) {
				element = null;
				try {
					element = this.receiveElement();
				} catch (IOException ioe) {
					log.error(ioe.getLocalizedMessage(), ioe);
				} catch (JDOMException jde) {
					log.error(jde.getLocalizedMessage(), jde);
				}
				if (element != null) {
					this.processConnectionEvent(element);
				}
			} else {
				Thread.yield();
			}
			// Senden
			synchronized (this.elementQueue) {
				if (this.elementQueue.size() > 0) {
					log.trace("run(): Nachricht in der Schlange gefunden.");
					element = (Element) this.elementQueue.elementAt(0);
					this.elementQueue.removeElementAt(0);
					try {
						this.sendElement(element);
					} catch (IOException ioe) {
						log.error("run(): Problem beim Versenden eines Elementes.", ioe);
						// ROADMAP Probleme sollten dem System mitgeteilt werden.
					}
				}
			}
		}
		
		// Verbindung schliessen.
		try {
			this.close();
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t);
		}
	}
	
	/**
	 * Informiert alle registrierten <code>ConnectionListener</code>.
	 * @param element Das Element.
	 */
	protected void processConnectionEvent(final Element element) {
		ConnectionEvent e = new ConnectionEvent(element);
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
	 * Fügt ein Element der Nachrichten Warteschlange für die zu sendenden Elemente hinzu.
	 * @param element Das Element.
	 */
	public void addElementToQueue(final Element element) {
		synchronized (this.elementQueue) {
			this.elementQueue.addElement(element);
		}
	}
}

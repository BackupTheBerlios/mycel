/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
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
import org.mycel.common.User;

/**
 * Die Verbindung zu einem Client.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class ClientConnection implements Runnable {
	/** Die maximale Zeit in Millisekunden bis zum Timout. */
	public final static int MAX_TIMEOUT = 5000;
	
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(ClientConnection.class);
	
	/** Die Socket-Verbindung zum Client. */
	private Socket socket = null;
	/** Der <code>InputStream</code> der Verbindung. */
	private InputStream in = null;
	/** Der <code>OutputStream</code> der Verbindung. */	
	private OutputStream out = null;
	/** Die Listener. */
	private Vector connectionListeners = new Vector();
	/** Der der Verbindung zugeordnete Benutzer. */
	private User user = null;
	/** Die Warteschlange für die zu sendenden Elemente. */
	private Vector elementQueue = new Vector();
	
	/**
	 * Dies ist der Standard Konctruktor.
	 * @param socket Die Socket-Verbindung zum Server.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 */
	public ClientConnection(final Socket socket) throws IOException {
		super();
		this.socket = socket;
		this.in = this.socket.getInputStream();
		this.out = this.socket.getOutputStream();
	}
	
	/**
	 * Gibt die Adresse des Clients zurück.
	 * @return Die Adresse oder <code>null</code>, wenn die Verbindung geschlossen ist.
	 */
	public InetAddress getHostAddress() {
		if (this.isClosed()) {
			return null;
		} else {
			return this.socket.getInetAddress();
		}
	}
	
	/**
	 * Gibt den Port des Clients zurück.
	 * @return Der Port oder <code>-1</code>, wenn die Verbindung geschlossen ist.
	 */
	public int getHostPort() {
		if (this.isClosed()) {
			return -1;
		} else {
			return this.socket.getPort();
		}
	}
	
	/**
	 * Setzt den Benutzer für diese Verbindung.
	 * @param user Der Benutzer.
	 */
	public void setUser(final User user) {
		this.user = user;
	}
	
	/**
	 * Gibt den Benutzer der Verbindung zurück.
	 * @return Der Benutzer oder <code>null</code>, wenn der Benutzer noch nicht authentifiziert wurde.
	 */
	public User getUser() {
		return this.user;
	}
	
	/**
	 * Schließt die Verbindung zum Client.
	 * @throws IOException Wenn ein Ein-/Ausgabe-Fehler auftrat.
	 */
	private void close() throws IOException {
		try {
			this.in = null;
			this.out = null;
			this.socket.close();
		} finally {
			this.socket = null;
		}
	}
	
	/**
	 * Prüft, ob die Verbindung zum Client geschlossen wurde.
	 * @return <code>true</code>, wenn die Verbindung geschlossen wurde, ansonsten <code>false</code>.
	 */
	public synchronized boolean isClosed() {
		return (this.socket == null);
	}
	
	/**
	 * Versendet ein XML Element an den Client.
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
	 * Empfämgt ein XML Element vom Client.
	 * @return Das Element.
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
	 * Versorgt die Verbindung uns schließt sie wieder.
	 * In dieser Methode wird die Verbindung zum Client versorgt. Es werden empfange
	 * Pakete weitergeleitet und Pakete, die versendet werden sollen, dem Client
	 * übermittelt.
	 * Wird eine Verbindung vom Client abgebaut oder der Thread darüber informiert,
	 * dass er sich beenden soll, wird die Verbindung geschlossen und die Methode
	 * verlassen.
	 */
	public void run() {
		int available = 0;
		Element element = null;
		
		log.trace("run(): ClientConnection gestartet.");
		while ((!Thread.interrupted()) && (!this.isClosed())) {
			// Empfangen
			try {
				available = this.in.available();
			} catch (IOException ioe) {
				log.error(ioe.getLocalizedMessage(), ioe);
				available = 0;
			}
			if (available > 0) {
				log.trace("run(): Nachricht kommt an...");
				element = null;
				try {
					element = this.receiveElement();
				} catch (IOException ioe) {
					log.error(ioe.getLocalizedMessage(), ioe);
				} catch (JDOMException jde) {
					log.error(jde.getLocalizedMessage(), jde);
				}
				if (element != null) {
					Message message = new Message(element, this);
					this.processConnectionEvent(message);
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
						log.error("run() Problem beim Versenden eines Elementes.", ioe);
						// ROADMAP Probleme sollten dem System mitgeteilt werden.
					}
				}
			}
		}
		
		// Verbindung schliessen.
		try {
			this.close();
		} catch (Throwable t) {
			log.error("run(): " + t.getLocalizedMessage(), t);
		}
		log.trace("run(): ClientConnection gestoppt.");
	}
	
	/**
	 * Informiert alle registrierten <code>ConnectionListener</code>.
	 * @param element Das Element.
	 */
	private void processConnectionEvent(final Message message) {
		ConnectionEvent e = new ConnectionEvent(message);
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

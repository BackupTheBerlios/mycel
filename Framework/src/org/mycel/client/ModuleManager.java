/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.client;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mycel.common.ConnectionEvent;
import org.mycel.common.ConnectionListener;
import org.mycel.common.User;

/**
 * Bildet die Schnittstelle zwischen den einzelnen Modulen und der Verbindung zum Server.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class ModuleManager implements Runnable {
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(ModuleManager.class);
	
	/** Die einzelnen Module. */
	private Vector modules = new Vector();
	/** Der Benutzer. */
	private User user = null;
	/** Die Verbindung zum Server. */
	private ServerConnection connection = null;
	/** Der Listener für die Verbindung. */
	private ConnectionListener connectionListener = null;
	/** Die Nachrichten Warteschlange für eingehende Nachrichten. */
	private Vector elementQueue = new Vector();
	
	/**
	 * Erstellt einen Modul-Manager.
	 * @param user Der Benutzer.
	 * @throws NullPointerException Wenn der Benutzer <code>null</code> ist.
	 */
	public ModuleManager(final User user) {
		super();
		if (user == null) {
			throw new NullPointerException("User is null.");
		}
		this.user = user;
		this.initialize();
	}
	
	/**
	 * Initialisiert das Objekt.
	 */
	private void initialize() {
		this.connectionListener = new ConnectionListener() {
			public final void dataReceived(final ConnectionEvent e) {
				log.trace("Element = " + e.getInformation().getName());
				addElementToQueue(e.getInformation());
			}
		};
	}
	
	/**
	 * Setzt die Verbindung zum Server.
	 * @param connection Die Verbindung zum Server.
	 */
	public void setServerConnection(final ServerConnection connection) {
		if (this.connection != null) {
			this.connection.removeConnectionListener(this.connectionListener);
		}
		this.connection = connection;
		if (this.connection != null) {
			this.connection.addConnectionListener(this.connectionListener);
		}
	}
	
	/**
	 * Sendet eine Nachricht über die Server Verbindung.
	 * @param element Die Nachricht.
	 * @param module Das Modul, das die Nachricht versenden möchte.
	 */
	public void sendInformation(final Element element, final ClientModule module) {
		Element rootElement;
		
		rootElement = new Element("module");
		rootElement.setAttribute("name", module.getModuleName());
		rootElement.addContent(element);
		this.connection.addElementToQueue(rootElement);
	}
	
	/**
	 * Holt die Nachrichten von der Server-Verbindung ab und stellt sie den Modulen zu.
	 */
	public void run() {
		Element element, subelement;
		String name;
		
		while (!Thread.interrupted()) {
			if (this.getQueueSize() > 0) {
				element = this.getElementFromQueue();
				if (element.getName().equals("module")) {
					name = element.getAttributeValue("name");
					if ((name != null) && (!name.equals(""))) {
						if (element.getChildren().size() == 1) {
							subelement = (Element) element.getChildren().get(0);
							this.processModules(name, subelement);
						} else {
							log.error("run(): Modul-Nachricht: Kein oder zuviel Inhalt (Anzahl der Unterelement: " + element.getChildren().size() + ")");
						}
					} else {
						log.error("run(): Modul-Nachricht: Name nicht gesetzt.");
					}
				} else {
					log.error("run(): Unbekannter Nachrichten-Typ '" + element.getName() + "'.");
				}
			} else {
				Thread.yield();
			}
		}
	}
	
	/**
	 * Fügt der Nachrichten Wartenschlage für eingehende Nachrichten eine Nachricht hinzu.
	 * Die Methode ist intern synchronisiert.
	 * @param element Die Nachricht.
	 */
	protected final void addElementToQueue(final Element element) {
		synchronized (this.elementQueue) {
			this.elementQueue.addElement(element);
		}
	}
	
	/**
	 * Gibt die älteste Nachricht aus der Nachrichten Wartenschlage für eingehende Nachrichten zurück.
	 * Die Methode ist intern synchronisiert.
	 * @return Die Nachricht oder <code>null</code>, wenn keine Nachricht vorliegt.
	 */
	protected final Element getElementFromQueue() {
		synchronized (this.elementQueue) {
			if (this.elementQueue.size() > 0) {
				Element element = (Element) this.elementQueue.elementAt(0);
				this.elementQueue.removeElementAt(0);
				return element;
			} else {
				return null;
			}
		}
	}
	
	/**
	 * Gibt die Anzahl der Nachrichten in der Nachrichten Wartenschlage für eingehende Nachrichten zurück.
	 * Die Methode ist intern synchronisiert.
	 * @return Die Anzahl.
	 */
	protected final int getQueueSize() {
		synchronized (this.elementQueue) {
			return this.elementQueue.size();
		}
	}
	
	/**
	 * Sucht das entsprechende Modul und übergibt ihm die Nachricht.
	 * Die Methode ist intern synchronisiert.
	 * @param name Der Name des Moduls.
	 * @param element Die Nachricht.
	 */
	protected void processModules(final String name, final Element element) {
		ClientModule module;
		int index, size;
		
		synchronized (this.modules) {
			size = this.modules.size();
			if (size > 0) {
				for (index = 0; index < size; index++) {
					module = (ClientModule) this.modules.elementAt(index);
					if (module.getModuleName().equals(name)) {
						module.addElementToQueue(element);
						return;
					}
				}
			}
			log.error("processModules() Kein Modul mit dem Namen '" + name + "' gefunden.");
		}
	}
	
	/**
	 * Fügt ein Modul hinzu.
	 * Die Methode ist intern synchronisiert.
	 * @param module Das Modul.
	 */
	public void addModule(final ClientModule module) {
		synchronized (this.modules) {
			this.modules.addElement(module);
			module.setModuleManager(this);
		}
	}
	
	/**
	 * Entfernt ein Modul.
	 * Die Methode ist intern synchronisiert.
	 * @param module Das Modul.
	 */
	public void removeModule(final ClientModule module) {
		synchronized (this.modules) {
			if (this.modules.removeElement(module)) {
				module.setModuleManager(null);
			}
		}
	}
}

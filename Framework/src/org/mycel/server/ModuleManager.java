/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.server;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mycel.common.ConnectionEvent;
import org.mycel.common.ConnectionListener;

/**
 * Bildet die Schnittstelle zwischen den einzelnen Modulen und den Verbindungen der Clients.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class ModuleManager implements Runnable {
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(ModuleManager.class);
	
	/** Die einzelnen Module. */
	private Vector modules = new Vector();
	/** Die Verbindungen zu den Clients. */
	private ConnectionManager connection = null;
	/** Der Listener für die Verbindung. */
	private ConnectionListener connectionListener = null;
	/** Die Nachrichten Warteschlange für eingehende Nachrichten. */
	private MessageQueue messageQueue = new MessageQueue();
	
	/**
	 * Erstellt einen Modul-Manager.
	 */
	public ModuleManager() {
		super();
		this.initialize();
	}
	
	/**
	 * Initialisiert das Objekt.
	 */
	private void initialize() {
		this.connectionListener = new ConnectionListener() {
			public final void dataReceived(final ConnectionEvent e) {
				log.trace("Element = " + e.getInformation().getName());
				messageQueue.addMessage(e.getMessage());
			}
		};
	}
	
	/**
	 * Setzt die Verbindung zum Server.
	 * @param connection Die Verbindung zum Server.
	 */
	public void setConnectionManager(final ConnectionManager connection) {
		if (this.connection != null) {
			this.connection.removeConnectionListener(this.connectionListener);
		}
		this.connection = connection;
		if (this.connection != null) {
			this.connection.addConnectionListener(this.connectionListener);
		}
	}
	
	/**
	 * Sendet eine Nachricht über die Verbindung zum Client.
	 * @param message Die Nachricht.
	 * @param module Das Modul, das die Nachricht versenden möchte.
	 */
	public void sendMessage(final Message message, final ServerModule module) {
		Element rootElement;
		
		log.trace("sendMessage(): Sende Nachricht...");
		rootElement = new Element("module");
		rootElement.setAttribute("name", module.getModuleName());
		rootElement.addContent(message.getInformation());
		message.setInformation(rootElement);
		this.connection.sendMessage(message);
		log.trace("sendMessage(): Nachricht gesendet..");
	}
	
	/**
	 * Holt die Nachrichten von der Server-Verbindung ab und stellt sie den Modulen zu.
	 */
	public void run() {
		Message message;
		Element element;
		String name;
		
		log.trace("run(): ModuleManager gestartet.");
		while (!Thread.interrupted()) {
			if (this.messageQueue.getSize() > 0) {
				message = this.messageQueue.removeMessage();
				element = message.getInformation();
				if (element.getName().equals("module")) {
					name = element.getAttributeValue("name");
					if ((name != null) && (!name.equals(""))) {
						if (element.getChildren().size() == 1) {
							Element subElement = (Element) element.getChildren().get(0);
							subElement.detach();
							message.setInformation(subElement);
							this.processModules(name, message);
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
		log.trace("run(): ModuleManager gestoppt.");
	}
	
	/**
	 * Sucht das entsprechende Modul und übergibt ihm die Nachricht.
	 * Die Methode ist intern synchronisiert.
	 * @param name Der Name des Moduls.
	 * @param element Die Nachricht.
	 */
	protected void processModules(final String name, final Message message) {
		ServerModule module;
		int index, size;
		
		synchronized (this.modules) {
			size = this.modules.size();
			if (size > 0) {
				for (index = 0; index < size; index++) {
					module = (ServerModule) this.modules.elementAt(index);
					if (module.getModuleName().equals(name)) {
						module.addMessage(message);
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
	public void addModule(final ServerModule module) {
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
	public void removeModule(final ServerModule module) {
		synchronized (this.modules) {
			if (this.modules.removeElement(module)) {
				module.setModuleManager(null);
			}
		}
	}
}

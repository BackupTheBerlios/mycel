/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 12.06.2004
 */
package org.mycel.client;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

/**
 * Ein Module f�r den Client.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 12.06.2004
 */
public abstract class ClientModule {
	/** Das Logging-Objekt f�r diese Klasse. */
	private static Log log = LogFactory.getLog(ClientModule.class);
	
	/** Die Nachrichten Warteschlange f�r eingehende Nachrichten. */
	private Vector elementQueue = new Vector();
	/** Die Modul-Verwaltung, bei der das Modul angemeldet ist. */
	private ModuleManager moduleManager = null;
	
	/**
	 * Dies ist der Standard Konstruktor.
	 */
	public ClientModule() {
		super();
	}
	
	/**
	 * Setzt die Modul-Verwaltung, bei der das Modul angemeldet ist. 
	 * @param moduleManager Die Modul-Verwaltung.
	 */
	public void setModuleManager(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}
	
	/**
	 * Gibt die Modul-Verwaltung zur�ck., bei der das Modul angemeldet ist.
	 * @return Die Modul-Verwaltung oder <code>null</code>, wenn das Modul bei keiner Modul-Verwaltung angemeldet ist.
	 */
	public ModuleManager getModuleManager() {
		return this.moduleManager;
	}
	
	/**
	 * F�gt der Nachrichten Wartenschlage f�r eingehende Nachrichten eine Nachricht hinzu.
	 * Die Methode ist intern synchronisiert.
	 * @param element Die Nachricht.
	 */
	public final void addElementToQueue(final Element element) {
		synchronized (this.elementQueue) {
			this.elementQueue.addElement(element);
		}
	}
	
	/**
	 * Gibt die �lteste Nachricht aus der Nachrichten Wartenschlage f�r eingehende Nachrichten zur�ck.
	 * Die Methode ist intern synchronisiert.
	 * @return Die Nachricht oder <code>null</code>, wenn keine Nachricht vorliegt.
	 */
	public final Element getElementFromQueue() {
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
	 * Gibt die Anzahl der Nachrichten in der Nachrichten Wartenschlage f�r eingehende Nachrichten zur�ck.
	 * Die Methode ist intern synchronisiert.
	 * @return Die Anzahl.
	 */
	public final int getQueueSize() {
		synchronized (this.elementQueue) {
			return this.elementQueue.size();
		}
	}
	
	/**
	 * Gibt den Modul-Namen zur�ck.
	 * Dies ist f�r die richtige Zustellung der Nachrichten wichtig sowie f�r die
	 * Anzeige der Module.
	 * @return Der Modul-Name.
	 */
	public abstract String getModuleName();
}

/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 14.06.2004
 */
package org.mycel.server;


/**
 * Ein Module f�r den Server.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 14.06.2004
 */
public abstract class ServerModule {
	/** Die Nachrichten Warteschlange f�r eingehende Nachrichten. */
	private MessageQueue messageQueue = new MessageQueue();
	/** Die Modul-Verwaltung, bei der das Modul angemeldet ist. */
	private ModuleManager moduleManager = null;
	
	/**
	 * Dies ist der Standard Konstruktor.
	 */
	public ServerModule() {
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
	 * @param message Die Nachricht.
	 */
	public void addMessage(final Message message) {
		this.messageQueue.addMessage(message);
	}
	
	/**
	 * Gibt die Warteschlange f�r eingehenede Nachrichten zur�ck.
	 * @return Die Warteschlange.
	 */
	public MessageQueue getMessageQueue() {
		return this.messageQueue;
	}
	
	/**
	 * Gibt den Modul-Namen zur�ck.
	 * Dies ist f�r die richtige Zustellung der Nachrichten wichtig sowie f�r die
	 * Anzeige der Module.
	 * @return Der Modul-Name.
	 */
	public abstract String getModuleName();
}

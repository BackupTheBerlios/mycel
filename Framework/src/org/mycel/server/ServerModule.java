/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 14.06.2004
 */
package org.mycel.server;


/**
 * Ein Module für den Server.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 14.06.2004
 */
public abstract class ServerModule {
	/** Die Nachrichten Warteschlange für eingehende Nachrichten. */
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
	 * Gibt die Modul-Verwaltung zurück., bei der das Modul angemeldet ist.
	 * @return Die Modul-Verwaltung oder <code>null</code>, wenn das Modul bei keiner Modul-Verwaltung angemeldet ist.
	 */
	public ModuleManager getModuleManager() {
		return this.moduleManager;
	}
	
	/**
	 * Fügt der Nachrichten Wartenschlage für eingehende Nachrichten eine Nachricht hinzu.
	 * Die Methode ist intern synchronisiert.
	 * @param message Die Nachricht.
	 */
	public void addMessage(final Message message) {
		this.messageQueue.addMessage(message);
	}
	
	/**
	 * Gibt die Warteschlange für eingehenede Nachrichten zurück.
	 * @return Die Warteschlange.
	 */
	public MessageQueue getMessageQueue() {
		return this.messageQueue;
	}
	
	/**
	 * Gibt den Modul-Namen zurück.
	 * Dies ist für die richtige Zustellung der Nachrichten wichtig sowie für die
	 * Anzeige der Module.
	 * @return Der Modul-Name.
	 */
	public abstract String getModuleName();
}

/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 12.06.2004
 */
package org.mycel.server;

import java.util.Vector;

import org.mycel.common.User;


/**
 * Dies ist eine Warteschlange für Nachrichten.
 * Alle Methoden sind intern synchronisiert, so dass Objekte dieser Klasse von
 * mehreren Threads gleichzeitig angesprochen werden können. Dabei funktioniert die
 * Klasse nach dem First-In First-Out Prinzip.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 12.06.2004
 */
public class MessageQueue {
	/** Die Liste der Nachrichten. */
	public Vector messages = new Vector();
	
	/**
	 * Dies ist der Standard Konstruktor.
	 */
	public MessageQueue() {
		super();
	}
	
	/**
	 * Fügt eine Nachricht hinzu.
	 * @param message Die Nachricht.
	 */
	public void addMessage(final Message message) {
		synchronized (this.messages) {
			this.messages.addElement(message);
		}
	}
	
	/**
	 * Holt die ältestete Nachricht aus der Schlange und löscht sie dort.
	 * @return Die Nachricht oder <code>null</code>, wenn keine Nachrichten vorliegen.
	 */
	public Message removeMessage() {
		Message message = null;
		synchronized (this.messages) {
			if (this.messages.size() > 0) {
				message = (Message) this.messages.elementAt(0);
				this.messages.removeElementAt(0);
			}
		}
		return message;
	}
	
	/**
	 * Holt die älteste Nachricht eines bestimmten Benutzer aus der Schlange und löscht sie dort.
	 * @param user Der Benutzer.
	 * @return Die Nachricht oder <code>null</code>, wenn keine Nachrichten vorliegen.
	 */
	public Message removeMessage(final User user) {
		Message message;
		int index, size;
		synchronized (this.messages) {
			size = this.messages.size();
			if (size > 0) {
				for (index = 0; index < size; index++) {
					message = (Message) this.messages.elementAt(index);
					if (message.getUser().equals(user)) {
						this.messages.removeElementAt(index);
						return message;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Gibt die Anzahl der Nachrichten in der Schlange zurück.
	 * @return Die Anzahl.
	 */
	public int getSize() {
		synchronized (this.messages) {
			return this.messages.size();
		}
	}
}

/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 14.06.2004
 */
package org.mycel.common;

import org.jdom.Element;
import org.mycel.server.*;

/**
 * Ein Ereignis für Verbindungen.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 14.06.2004
 */
public class ConnectionEvent {
	/** Die Nachrichicht. */
	private Element information = null;
	/** Die gesamte Nachricht. */
	private Message message = null;
	
	/**
	 * Erstellt ein Verbindungsereignis.
	 * @param information Die Nachricht.
	 * @throws NullPointerException Wenn die Nachricht <code>null</code> ist.
	 */
	public ConnectionEvent(final Element information) {
		super();
		if (information == null) {
			throw new NullPointerException("Information is null.");
		}
		this.information = information;
	}
	
	/**
	 * Erstellt eine Verbindungsereignis.
	 * @param message Die gesamte Nachricht.
	 * @throws NullPointerException Wenn die gesamte Nachricht <code>null</code> ist.
	 */
	public ConnectionEvent(final Message message) {
		super();
		if (message == null) {
			throw new NullPointerException("Message is null.");
		}
		this.information = message.getInformation();
		this.message = message;
	}
	
	/**
	 * Gibt die Nachricht zurück.
	 * @return Die Nachricht.
	 */
	public Element getInformation() {
		return this.information;
	}
	
	/**
	 * Gibt die gesamte Nachricht zurück.
	 * @return Die gesamte Nachricht oder <code>null</code>, wenn die gesamte Nachricht nicht gesetzt wurde.
	 */
	public Message getMessage() {
		return this.message;
	}
}

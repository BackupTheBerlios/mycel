/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 12.06.2004
 */
package org.mycel.server;

import org.jdom.Element;
import org.mycel.common.User;

/**
 * Eine Nachricht.
 * Besteht aus den Informationen an sich und dem Benutzer.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 12.06.2004
 */
public class Message {
	/** Die Information. */
	private Element information = null;
	/** Die Verbindung zum Client. */
	private ClientConnection connection = null;
	
	/**
	 * Erstellt die Nachricht.
	 * @param element Die Information.
	 * @param user Die Verbindung zum Client.
	 * @throws NullPointerException Wenn die Information <code>null</code> ist.
	 * @throws NullPointerException Wenn die Verbindung <code>null</code> ist.
	 */
	public Message(final Element information, final ClientConnection connection) {
		super();
		if (information == null) {
			throw new NullPointerException("Information is null.");
		}
		if (connection == null) {
			throw new NullPointerException("Connection is null.");
		}
		this.information = information;
		this.connection = connection;
	}
	
	/**
	 * Erstellt eine Antwort auf eine Anfrage.
	 * @param information Die Nachricht.
	 * @param request Die Anfrage.
	 * @throws NullPointerException Wenn die Information <code>null</code> ist.
	 * @throws NullPointerException Wenn die Anfrage <code>null</code> ist.
	 */
	public Message(final Element information, final Message request) {
		super();
		if (information == null) {
			throw new NullPointerException("Information is null.");
		}
		if (request == null) {
			throw new NullPointerException("Request is null.");
		}
		this.information = information;
		this.connection = request.connection;
	}
	
	/**
	 * Gibt die Information zurück.
	 * @return Die Information.
	 */
	public Element getInformation() {
		return this.information;
	}
	
	/**
	 * Setzt die Information.
	 * @param information Die Information.
	 */
	public void setInformation(final Element information) {
		this.information = information;
	}
	
	/**
	 * Gibt die Verbindung zum Client zurück.
	 * @return Die Verbindung.
	 */
	protected ClientConnection getClientConnection() {
		return this.connection;
	}
	
	/**
	 * Gibt den Benutzer zurück.
	 * @return Der Benutzer oder <code>null</code>, wenn der Verbindung zum Client kein Benutzer zugeordnet ist.
	 */
	public User getUser() {
		return this.connection.getUser();
	}
}

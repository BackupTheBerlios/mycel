/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 07.06.2004
 */
package org.mycel.common;

import java.util.EventListener;

/**
 * Eine Listener für Ereignisse der Verbindungen.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 07.06.2004
 */
public interface ConnectionListener extends EventListener {
	/**
	 * Wird aufgerufen, wenn die Verbindung Daten empfange hat.
	 * @param e Das Ereignis.
	 */
	void dataReceived(final ConnectionEvent e);
}

/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 19.06.2004
 */
package org.mycel.pim.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mycel.server.Message;
import org.mycel.server.ServerModule;

/**
 * 
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 19.06.2004
 */
public class ChatModule extends ServerModule implements Runnable {
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(ChatModule.class);
	
	public ChatModule() {
		super();
	}
	
	public String getModuleName() {
		return "chat";
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			if (this.getMessageQueue().getSize() > 0) {
				Message message = this.getMessageQueue().removeMessage();
				if (message != null) {
					log.trace("run(): Nachricht empfangen.");
					message = new Message(message.getInformation(), message);
					log.trace("run(): Sende Nachricht...");
					this.getModuleManager().sendMessage(message, this);
					log.trace("run(): Nachricht gesendet.");
				}
			}
			Thread.yield();
		}
	}
}

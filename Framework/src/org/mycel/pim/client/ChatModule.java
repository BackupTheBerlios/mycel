/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 17.06.2004
 */
package org.mycel.pim.client;

import javax.swing.JTextArea;

import org.jdom.Element;
import org.mycel.client.ClientModule;

/**
 * Das Client Modul für einen Chat.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 17.06.2004
 */
public class ChatModule extends ClientModule implements Runnable {
	private JTextArea jTextArea = null;
	
	public ChatModule(final JTextArea jTextArea) {
		super();
		if (jTextArea == null) {
			throw new IllegalArgumentException("Parameter jTextArea is null.");
		}
		this.jTextArea = jTextArea;
	}
	
	public String getModuleName() {
		return "chat";
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			if (this.getQueueSize() > 0) {
				Element element = this.getElementFromQueue();
				if (element != null) {
					String username = element.getAttributeValue("username");
					String text = element.getText();
					this.jTextArea.append(username + "> " + text + "\n");
				}
			} else {
				Thread.yield();
			}
		}
	}
	
	public void sendText(final String username, final String text) {
		Element element = new Element("message");
		element.setAttribute("username", username);
		element.setText(text);
		this.getModuleManager().sendInformation(element, this);
	}
}

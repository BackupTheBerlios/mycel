/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.pim.server;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mycel.server.ConnectionManager;
import org.mycel.server.ModuleManager;

/**
 * 
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class Server {
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(Server.class);
	
	public static void main(String[] args) {
		ConnectionManager connectionManager;
		Thread connectionThread, managerThread, chatThread;
		int port = 20000;
		ModuleManager mmanager;
		ChatModule chatModule;
		
		try {
			// Start-Konfiguration laden.
			log.trace("main(): Lege Modul-Manager an...");
			mmanager = new ModuleManager();
			log.trace("main(): Lege Connection Manager an...");
			connectionManager = new ConnectionManager(port);
			log.trace("main(): Lege Module an...");
			chatModule = new ChatModule();
			log.trace("main(): Konfiguriere ModuleManager...");
			mmanager.setConnectionManager(connectionManager);
			mmanager.addModule(chatModule);
			
			// Verbindung aufbauen.
			log.trace("main(): Lege Thread fuer Connection Manager an...");
			connectionThread = new Thread(connectionManager, "ConnectionManager");
			log.trace("main(): Starte Thread...");
			connectionThread.start();
			log.trace("main(): Lege Thread fuer den ModuleManager an...");
			managerThread = new Thread(mmanager, "ModuleManager");
			log.trace("main(): Starte Thread...");
			managerThread.start();
			log.trace("main(): Lege Threads fuer die Module an...");
			chatThread = new Thread(chatModule, "ChatModule");
			log.trace("main(): Starte Module...");
			chatThread.start();
			
			// Meldung ausgeben
			JOptionPane.showMessageDialog(null,
				"Soll der Server heruntergefahren werden?",
				"Server herunterfahren?",
				JOptionPane.QUESTION_MESSAGE);
			
			// Herunterfahren.
			log.trace("main(): Stoppe Modul-Threads...");
			chatThread.interrupt();
			log.trace("main(): Konfiguriere ModuleManager...");
			mmanager.removeModule(chatModule);
			mmanager.setConnectionManager(null);
			log.trace("main(): Stoppe Thread...");
			connectionThread.interrupt();
			log.trace("main(): Stoppe ModuleManager-Thread...");
			managerThread.interrupt();
		} catch (Throwable t) {
			log.fatal("main(): " + t.getLocalizedMessage(), t);
			return;
		}
		System.exit(0);
	}
}

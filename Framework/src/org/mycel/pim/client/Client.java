/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.pim.client;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mycel.client.ModuleManager;
import org.mycel.client.ServerConnection;
import org.mycel.common.User;

/**
 * 
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class Client {
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(Client.class);
	
	public static void main(String[] args) {
		User user;
		ServerConnection connection;
		Thread connectionThread, managerThread, chatThread;
		InetAddress address;
		int port = 20000;
		ModuleManager mmanager;
		ChatModule chatModule;
		JChat jChat;
		
		try {
			// Start-Konfiguration laden.
			log.trace("main(): Lege Benutzer an...");
			user = new User("gerrit-hohl", "rulez");
			log.trace("main(): Lege ModuleManager an...");
			mmanager = new ModuleManager(user);
			log.trace("main(): Hole Adresse des Servers...");
			address = InetAddress.getByName("localhost");
			log.trace("main(): Lege Verbindung an...");
			connection = new ServerConnection(address, port);
			log.trace("main(): Lege Module an...");
			log.trace("main():\tChat Modul...");
			jChat = new JChat();
			chatModule = jChat.getChatModule();
			log.trace("main(): Konfiguriere ModuleManager...");
			mmanager.setServerConnection(connection);
			mmanager.addModule(chatModule);
			
			// Verbindung aufbauen.
			log.trace("main(): Lege Thread fuer Verbindung an...");
			connectionThread = new Thread(connection, "ServerConnection");
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
			
			// Programm starten...
			jChat.setVisible(true);
			
			// Verbindung abbauen.
			log.trace("main(): Stoppe Modul-Threads...");
			chatThread.interrupt();
			log.trace("main(): Konfiguriere ModuleManager...");
			mmanager.removeModule(chatModule);
			mmanager.setServerConnection(null);
			log.trace("main(): Stoppe Verbindung-Thread...");
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

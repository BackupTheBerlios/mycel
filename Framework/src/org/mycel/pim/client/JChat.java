/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 17.06.2004
 */
package org.mycel.pim.client;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Das Fenster für einen Chat.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 17.06.2004
 */
public class JChat extends JDialog {
	/** Das Logging-Objekt für diese Klasse. */
	private static Log log = LogFactory.getLog(JChat.class);
	
	private ChatModule module = null;
	
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTextArea jTextArea = null;
	private JTextField jTextField = null;
	
	public JChat() {
		super();
		this.initialize();
	}
	
	private void initialize() {
		this.setSize(640, 480);
		this.setTitle("Chat");
		this.setModal(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		this.setContentPane(this.getJContentPane());
	}
	
	private JPanel getJContentPane() {
		if (this.jContentPane == null) {
			this.jContentPane = new JPanel();
			this.jContentPane.setLayout(new BorderLayout());
			this.jContentPane.add(this.getJScrollPane(), "Center");
			this.jContentPane.add(this.getJTextField(), "South");
		}
		return this.jContentPane;
	}
	
	private JScrollPane getJScrollPane() {
		if (this.jScrollPane == null) {
			this.jScrollPane = new JScrollPane();
			this.jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			this.jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			this.jScrollPane.setViewportView(this.getJTextArea());
		}
		return this.jScrollPane;
	}
	
	private JTextArea getJTextArea() {
		if (this.jTextArea == null) {
			this.jTextArea = new JTextArea();
			this.jTextArea.setEditable(false);
		}
		return this.jTextArea;
	}
	
	private JTextField getJTextField() {
		if (this.jTextField == null) {
			this.jTextField = new JTextField();
			this.jTextField.addKeyListener(new KeyAdapter() {
				public final void keyReleased(final KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						String text = jTextField.getText();
						sendText(text);
						jTextField.setText("");
					}
				}
			});
		}
		return this.jTextField;
	}
	
	private void sendText(final String text) {
		log.trace("text = " + text);
//		this.getJTextArea().append(text + "\n");
		this.module.sendText("Grizzly", text);
	}
	
	public ChatModule getChatModule() {
		if (this.module == null) {
			this.module = new ChatModule(this.getJTextArea());
		}
		return this.module;
	}
}

/*
 * (c) Copyright 2004 Gerrit Hohl (gerrit.hohl@freenet.de)
 * Erstellt am 06.06.2004
 */
package org.mycel.common;

/**
 * Eine Benutzer.
 * @author Gerrit Hohl (gerrit.hohl@freenet.de)
 * @version <b>1.0</b>, 06.06.2004
 */
public class User {
	/** Der Benutzername. */
	private String username = null;
	/** Das Passwort des Benutzers. */
	private String password = null;
	
	/**
	 * Erstellt einen Gast-Benutzer ohne Passwort.
	 * @param username Der Benutzername.
	 * @throws NullPointerException Wenn der Benutzername <code>null</code> ist.
	 */
	private User(final String username) {
		super();
		if (username == null) {
			throw new NullPointerException("Username is null.");
		}
		this.username = username;
	}
	
	/**
	 * Erstellt einen Benutzer.
	 * @param username Der Benutzername.
	 * @param password Das Passwort.
	 * @throws NullPointerException Wenn der Benutzername <code>null</code> ist.
	 * @throws NullPointerException Wenn das Passwort <code>null</code> ist.
	 */
	public User(final String username, final String password) {
		super();
		if (username == null) {
			throw new NullPointerException("Username is null.");
		}
		if (password == null) {
			throw new NullPointerException("Password is null.");
		}
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Gibt den Benutzernamen zurück.
	 * @return Der Benutzername.
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Gibt das Passwort zurück.
	 * @return Das Passwort oder <code>null</code>, wenn es sich um einen Gast-Benutzer handelt.
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Prüft, ob es sich bei dem Benutzer um einen Gast handelt.
	 * @return <code>true</code>, wenn der Benutzer ein Gast ist, ansonsten <code>false</code>.
	 */
	public boolean isGuest() {
		return (this.password == null);
	}
	
	/**
	 * Gibt den Hash-Code für das Objekt zurück.
	 * @return Der Hash-Code.
	 */
	public int hashCode() {
		int result = 17;
		result += 37 * this.username.hashCode();
		if (this.password != null) {
			result += 37 * this.password.hashCode();
		} else {
			result += 37;
		}
		return result;
	}
	
	/**
	 * Vergleicht dieses Objekt mit einem anderen Objekt.
	 * @return <code>true</code>, wenn die Objekte gleich sind, ansonsten <code>false</code>.
	 */
	public boolean equals(final Object obj) {
		if (obj instanceof User) {
			User user = (User) obj;
			if (this.username.equals(user.username) && (this.password == null)) {
				return true;
			}
			if (this.username.equals(user.username) && this.password.equals(user.password)) {
				return true;
			}
		}
		return false;
	}
}

/*
 * Copyright (c) 2004 Gerrit Hohl
 */
package org.mycel.util.logging;

import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;

/**
 * @author Gerrit Hohl, gerrit.hohl@freenet.de
 * @version <b>1.0</b>, 01.05.2004
 */
public class ConsoleLog implements Log {
	/** Nichts wird protokolliert. */
	public static final int LOG_LEVEL_OFF = 0;
	/** Alle fatalen Fehler werden protokolliert. */
	public static final int LOG_LEVEL_FATAL = 1;
	/** Alle Fehler werden protkolliert. */
	public static final int LOG_LEVEL_ERROR = 2;
	/** Alle Fehler und Warnungen werden protokolliert. */
	public static final int LOG_LEVEL_WARN = 3;
	/** Alle Fehler, Warnungen und Informationen werden protokolliert. */
	public static final int LOG_LEVEL_INFO = 4;
	/** Alle Fehler, Warnungen, Informationen und Debug-Meldungen werden protokolliert. */
	public static final int LOG_LEVEL_DEBUG = 5;
	/** Alle Fehler, Warnungen, Informationen und Meldungen werden protokolliert. */
	public static final int LOG_LEVEL_TRACE = 6;
	/** Alles wird protokolliert. */
	public static final int LOG_LEVEL_ALL = 7;
	
	/** Der Name der zu protokollierenden Klasse. */
	private String name = null;
	/** Der Prefix des Namens der zu protokollierenden Klasse. */
	private String prefix = null;
	/** Der aktuelle Log-Level. */
	private int currentLogLevel = LOG_LEVEL_INFO;
	/** Ob der Name der Instanz mit ausgedruckt werden soll. */
	private boolean showLogName = false;
	/** Ob der letzte Teil des Name der Instanz mit ausgedruckt werden soll. */
	private boolean showShortName = true;
	/** Das Objekt zur Datumsformatierung. */
	private DateFormat dateFormater = null;
	
	/**
	 * Der Standard Konstruktor.
	 * @param name Der Name der zu protokollierenden Klasse.
	 */
	public ConsoleLog(String name) {
		super();
		
		Properties properties = System.getProperties();
		String property = null;
		String packageName = this.getClass().getPackage().getName();
		if (name.lastIndexOf('.') > 0) {
			this.prefix = name.substring(0, name.lastIndexOf('.'));
			this.name = name.substring(name.lastIndexOf('.') + 1);
		} else {
			this.name = name;
		}
		
		// Default Log
		property = properties.getProperty(packageName + ".defaultlog");
		if (property != null) {
			if (property.equals("off")) {
				this.currentLogLevel = LOG_LEVEL_OFF;
			} else if (property.equals("trace")) {
				this.currentLogLevel = LOG_LEVEL_TRACE;
			} else if (property.equals("debug")) {
				this.currentLogLevel = LOG_LEVEL_DEBUG;
			} else if (property.equals("info")) {
				this.currentLogLevel = LOG_LEVEL_INFO;
			} else if (property.equals("warn")) {
				this.currentLogLevel = LOG_LEVEL_WARN;
			} else if (property.equals("error")) {
				this.currentLogLevel = LOG_LEVEL_ERROR;
			} else if (property.equals("fatal")) {
				this.currentLogLevel = LOG_LEVEL_FATAL;
			} else if (property.equals("all")) {
				this.currentLogLevel = LOG_LEVEL_ALL;
			}
		}
		
		// Show log name
		property = properties.getProperty(packageName + ".showlogname");
		if (property != null) {
			this.showLogName = Boolean.valueOf(property).booleanValue();
		}
		
		// Show short longname
		property = properties.getProperty(packageName + ".showShortLogname");
		if (property != null) {
			this.showShortName = Boolean.valueOf(property).booleanValue();
		}
		
		// Show date time
		property = properties.getProperty(packageName + ".showdatetime");
		if (property != null) {
			if (Boolean.valueOf(property).booleanValue()) {
				this.dateFormater = DateFormat.getDateTimeInstance();
			}
		}
	}
	
	/**
	 * Gibt eine Meldung in der Konsole aus.
	 * @param level Der Level des Logging.
	 * @param arg0 Die Meldung.
	 */
	private void printMessage(String level, Object arg0) {
		StringBuffer sb = new StringBuffer();
		
		// Datum
		if (this.dateFormater != null) {
			sb.append("[");
			sb.append(this.dateFormater.format(new Date()));
			sb.append("] ");
		}
		// Level
		sb.append("[");
		sb.append(level);
		sb.append("] ");
		// Name
		if (this.showLogName) {
			sb.append("[");
			if ((!this.showShortName) && (this.prefix != null)) {
				sb.append(this.prefix);
			}
			sb.append(this.name);
			sb.append("] ");
		}
		// Meldung
		if (arg0 != null) {
			sb.append(arg0.toString());
		} else {
			sb.append("null");
		}
		// Ausgabe.
		System.err.println(sb.toString());
	}
	
	/**
	 * Gibt eine Meldung in der Konsole aus.
	 * @param level Der Level des Logging.
	 * @param arg0 Die Meldung.
	 * @param arg1 Der Fehler.
	 */
	private void printMessage(String level, Object arg0, Throwable arg1) {
		this.printMessage(level, arg0);
		arg1.printStackTrace(System.err);
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>trace</code> aus.
	 * @param arg0 Die Meldung.
	 * @param arg1 Der Fehler.
	 */
	public void trace(Object arg0, Throwable arg1) {
		if (this.isTraceEnabled()) {
			this.printMessage("trace", arg0, arg1);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>trace</code> aus.
	 * @param arg0 Die Meldung.
	 */
	public void trace(Object arg0) {
		if (this.isTraceEnabled()) {
			this.printMessage("trace", arg0);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>debug</code> aus.
	 * @param arg0 Die Meldung.
	 * @param arg1 Der Fehler.
	 */
	public void debug(Object arg0, Throwable arg1) {
		if (this.isDebugEnabled()) {
			this.printMessage("debug", arg0, arg1);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>debug</code> aus.
	 * @param arg0 Die Meldung.
	 */
	public void debug(Object arg0) {
		if (this.isDebugEnabled()) {
			this.printMessage("debug", arg0);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>info</code> aus.
	 * @param arg0 Die Meldung.
	 * @param arg1 Der Fehler.
	 */
	public void info(Object arg0, Throwable arg1) {
		if (this.isInfoEnabled()) {
			this.printMessage("info", arg0, arg1);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>info</code> aus.
	 * @param arg0 Die Meldung.
	 */
	public void info(Object arg0) {
		if (this.isInfoEnabled()) {
			this.printMessage("info", arg0);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>warn</code> aus.
	 * @param arg0 Die Meldung.
	 * @param arg1 Der Fehler.
	 */
	public void warn(Object arg0, Throwable arg1) {
		if (this.isWarnEnabled()) {
			this.printMessage("warn", arg0, arg1);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>warn</code> aus.
	 * @param arg0 Die Meldung.
	 */
	public void warn(Object arg0) {
		if (this.isWarnEnabled()) {
			this.printMessage("warn", arg0);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>error</code> aus.
	 * @param arg0 Die Meldung.
	 * @param arg1 Der Fehler.
	 */
	public void error(Object arg0, Throwable arg1) {
		if (this.isErrorEnabled()) {
			this.printMessage("error", arg0, arg1);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>error</code> aus.
	 * @param arg0 Die Meldung.
	 */
	public void error(Object arg0) {
		if (this.isErrorEnabled()) {
			this.printMessage("error", arg0);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>fatal</code> aus.
	 * @param arg0 Die Meldung.
	 * @param arg1 Der Fehler.
	 */
	public void fatal(Object arg0, Throwable arg1) {
		if (this.isFatalEnabled()) {
			this.printMessage("fatal", arg0, arg1);
		}
	}
	
	/**
	 * Gibt eine Meldung des Log-Levels <code>fatal</code> aus.
	 * @param arg0 Die Meldung.
	 */
	public void fatal(Object arg0) {
		if (this.isFatalEnabled()) {
			this.printMessage("fatal", arg0);
		}
	}
	
	/**
	 * Gibt zurück, ob der Log-Level <code>trace</code> aktiv ist.
	 * @return Wenn <code>true</code> ist der Log-Level aktiv, ansonsten nicht.
	 */
	public boolean isTraceEnabled() {
		return (this.currentLogLevel >= LOG_LEVEL_TRACE); 
	}
	
	/**
	 * Gibt zurück, ob der Log-Level <code>debug</code> aktiv ist.
	 * @return Wenn <code>true</code> ist der Log-Level aktiv, ansonsten nicht.
	 */
	public boolean isDebugEnabled() {
		return (this.currentLogLevel >= LOG_LEVEL_DEBUG); 
	}
	
	/**
	 * Gibt zurück, ob der Log-Level <code>info</code> aktiv ist.
	 * @return Wenn <code>true</code> ist der Log-Level aktiv, ansonsten nicht.
	 */
	public boolean isInfoEnabled() {
		return (this.currentLogLevel >= LOG_LEVEL_INFO); 
	}
	
	/**
	 * Gibt zurück, ob der Log-Level <code>warn</code> aktiv ist.
	 * @return Wenn <code>true</code> ist der Log-Level aktiv, ansonsten nicht.
	 */
	public boolean isWarnEnabled() {
		return (this.currentLogLevel >= LOG_LEVEL_WARN); 
	}
	
	/**
	 * Gibt zurück, ob der Log-Level <code>error</code> aktiv ist.
	 * @return Wenn <code>true</code> ist der Log-Level aktiv, ansonsten nicht.
	 */
	public boolean isErrorEnabled() {
		return (this.currentLogLevel >= LOG_LEVEL_ERROR); 
	}
	
	/**
	 * Gibt zurück, ob der Log-Level <code>fatal</code> aktiv ist.
	 * @return Wenn <code>true</code> ist der Log-Level aktiv, ansonsten nicht.
	 */
	public boolean isFatalEnabled() {
		return (this.currentLogLevel >= LOG_LEVEL_FATAL); 
	}
}

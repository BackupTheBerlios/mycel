import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Diese Schnittstelle stellt eine Shell-Verbindung dar.
 * @author Gerrit Hohl, gerrit.hohl@freenet.de
 * @version <b>1.0</b>, 15.02.2004
 */
public interface ShellConnection {
	public static final int STANDARD_ECHO_CHAR = -1;
	
	/**
	 * Schließt die Verbindung.
	 * @throws IOException Wenn ein I/O Fehler auftritt.
	 */
	public void close() throws IOException;
	
	/**
	 * Gibt einen Stream zum Lesen aus der Verbindung zurück.
	 * @return Der Stream.
	 * @throws IOException Wenn ein I/O Fehler auftritt.
	 */
	public InputStream getInputStream() throws IOException;
	
	/**
	 * Gibt einen Stream zum Schreiben in die Verbindung zurück.
	 * @return Der Stream.
	 * @throws IOException Wenn ein I/O Fehler auftritt.
	 */
	public OutputStream getOutputStream() throws IOException;
	
	/**
	 * Gibt den Namen des Hosts zurück.
	 * @return Der Name.
	 */
	public String getHostName();
	
	/**
	 * Gibt die Adresse des Hosts zurück.
	 * @return Die Adresse.
	 */
	public String getHostAddress();
	
	/**
	 * Gibt den Port des Hosts zurück.
	 * @return Der Port.
	 */
	public int getHostPort();
	
	/**
	 * Gibt den eigenen Namen zurück.
	 * @return Der Name.
	 */
	public String getLocalName();
	
	/**
	 * Gibt die eigene Adresse zurück.
	 * @return Die Adresse.
	 */
	public String getLocalAddress();
	
	/**
	 * Gibt den eigenen Port zurück.
	 * @return Der Port.
	 */
	public int getLocalPort();
	
	/**
	 * Aktiviert oder deaktiviert das Echo.
	 * @param echo Wenn <code>true</code> wird das Echo aktiviert, ansonsten deaktiviert.
	 */
	public void setEcho(boolean echo);
	
	/**
	 * Gibt den Status des Echo zurück.
	 * @return Wenn <code>true</code> ist das Echo aktiviert, ansonsten deaktiviert.
	 */
	public boolean getEcho();
	
	/**
	 * Setzt das Echozeichen.
	 * Wird benützt um bei Passworteingaben das Zeichen zu
	 * verschleiern. Um das original Zeichen als Echo zu benutzen,
	 * gibt man die Konstante STANDARD_ECHO_CHAR an.
	 * @param echoChar Das Echozeichen.
	 */
	public void setEchoChar(int echoChar);
}

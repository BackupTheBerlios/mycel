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
	 * Schlie�t die Verbindung.
	 * @throws IOException Wenn ein I/O Fehler auftritt.
	 */
	public void close() throws IOException;
	
	/**
	 * Gibt einen Stream zum Lesen aus der Verbindung zur�ck.
	 * @return Der Stream.
	 * @throws IOException Wenn ein I/O Fehler auftritt.
	 */
	public InputStream getInputStream() throws IOException;
	
	/**
	 * Gibt einen Stream zum Schreiben in die Verbindung zur�ck.
	 * @return Der Stream.
	 * @throws IOException Wenn ein I/O Fehler auftritt.
	 */
	public OutputStream getOutputStream() throws IOException;
	
	/**
	 * Gibt den Namen des Hosts zur�ck.
	 * @return Der Name.
	 */
	public String getHostName();
	
	/**
	 * Gibt die Adresse des Hosts zur�ck.
	 * @return Die Adresse.
	 */
	public String getHostAddress();
	
	/**
	 * Gibt den Port des Hosts zur�ck.
	 * @return Der Port.
	 */
	public int getHostPort();
	
	/**
	 * Gibt den eigenen Namen zur�ck.
	 * @return Der Name.
	 */
	public String getLocalName();
	
	/**
	 * Gibt die eigene Adresse zur�ck.
	 * @return Die Adresse.
	 */
	public String getLocalAddress();
	
	/**
	 * Gibt den eigenen Port zur�ck.
	 * @return Der Port.
	 */
	public int getLocalPort();
	
	/**
	 * Aktiviert oder deaktiviert das Echo.
	 * @param echo Wenn <code>true</code> wird das Echo aktiviert, ansonsten deaktiviert.
	 */
	public void setEcho(boolean echo);
	
	/**
	 * Gibt den Status des Echo zur�ck.
	 * @return Wenn <code>true</code> ist das Echo aktiviert, ansonsten deaktiviert.
	 */
	public boolean getEcho();
	
	/**
	 * Setzt das Echozeichen.
	 * Wird ben�tzt um bei Passworteingaben das Zeichen zu
	 * verschleiern. Um das original Zeichen als Echo zu benutzen,
	 * gibt man die Konstante STANDARD_ECHO_CHAR an.
	 * @param echoChar Das Echozeichen.
	 */
	public void setEchoChar(int echoChar);
}

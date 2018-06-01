import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Connection implements Runnable
{
    private int port = 4444;
    private String host = "localhost";
    private Socket socket = null;
    private PrintWriter out = null;
    private InputStream in = null;
    private Console log;

    public Connection( Console log )
    {
        this.log = log;
    }

    @Override
    public void run()
    {
        connectToServer();
    }

    private void connectToServer()
    {
        boolean connected = false;

        log.println( "Uruchamianie klienta..." );
        do
        {
            try // Pierwsza próba połączenia z serwerem.
            {
                socket = new Socket( host, port );
                out = new PrintWriter( socket.getOutputStream(), true );
                in = socket.getInputStream();
                log.println( "Połączono z serwerem " + socket.toString() );
                connected = true;
                return;
            }
            catch( UnknownHostException e )
            {
                printError( "Nieznany host '" + host + "'" );
                System.exit( -1 );
            }
            catch( Exception e ) // Błąd. Ponowna próba połączenia za 3 sekundy.
            {
                printError( "Nie można połączyć z serwerem!" );
                connected = false;
                log.println("Próba połączenia...");
                try
                {
                    TimeUnit.SECONDS.sleep( 3 );
                }
                catch( InterruptedException ignored ){}
            }
        } while( !connected );
    }

    public void sendRequest( String request, Runnable reloadImage )
    {
        if( request.equals( "--q" ) )
            stop();
        out.println( request ); // Wysyłanie zapytania do serwera.

        // Po wysłaniu zapytania, wczytujemy odpowiedź od serwera.
        // Ta funkcja pobiera i zapisuje nowy obrazek z drzewem.
        readResponse( reloadImage );
    }

    // Każda odpowiedź może wyglądać następująco:
    // (1) header = "txt": wiadomość tekstowa od serwera
    // (2) header = "txt/jpg": wiadomość tekstowa, po której następuje obrazek JPG
    private void readResponse( Runnable reloadImage )
    {
        String header = Response.readText( in );
        // Ze schematu wynika, że wiadomość tekstowa od serwera jest zawsze
        String textResponse = Response.readText( in );
        log.println( textResponse );

        // Jeśli nagłówek to "txt/jpg", to znaczy, że po wiadomości tekstowej, będzie obrazek
        if( header.equals( "txt/jpg" ) )
        {
            //log.print( "Przeładowano obrazek" );
            Response.readImage( in, "Client\\client-tree.jpg" );
            reloadImage.run();
        }
    }

    private void stop()
    {
        log.println( "Wyłączanie klienta..." );
        System.exit( 0 );
    }

    private void printError( String errorMessage )
    {
        log.println( "[Wystąpił błąd]: " + errorMessage );
    }
}

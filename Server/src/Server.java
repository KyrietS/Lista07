import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Sebastian Fojcik
 */
@SuppressWarnings( "InfiniteLoopStatement" )                           // Wyłącza ostrzeżenia o nieskończoneych pętlach
public class Server
{
    private int port = 4444;
    private ServerSocket server = null;
    private Socket client = null;
    private BufferedReader in = null;
    private OutputStream out = null;
    private TreeManager treeManager = new TreeManager();               // Interfejs pomiędzy serwerem a strukturą BTree

    private Server()
    {
        try
        {
            System.out.println( "Uruchamianie serwera..." );
            server = new ServerSocket( port );
        }
        catch( IOException e )
        {
            printError( "Nie można uzyskać dostępu do portu " + port );
            stop();
        }
    }

    /** Oczekuje na przyłączenie klienta */
    private void waitForClient()
    {
        try
        {
            System.out.println( "Oczekiwanie na klienta..." );
            client = server.accept();
            in = new BufferedReader( new InputStreamReader( ( client.getInputStream() ) ) );
            out = client.getOutputStream();
            System.out.println( "Połączono z klientem " + client.toString() );
        }
        catch( IOException e )
        {
            printError( "Nieudana próba podłączenai klienta na porcie " + port );
            stop();
        }
    }

    /** Standardowe działanie serwera */
    private void start()
    {
        while( true )
        {
            waitForClient();
            listenSocket();
        }
    }

    /** Oczekuje na zapytanie od klienta i je wykonuje */
    private void listenSocket()
    {
        String request;                                             // Zapytanie otrzymane od klienta.
        while( true )
        {
            try
            {
                // Wczytaj zapytanie od klienta
                request = in.readLine();
                // Wykonaj odebrane zapytanie
                Response response = treeManager.execute( request );
                // Wyślij rezultat do klienta.
                sendResponse( response );

            }
            catch( Exception e )
            {
                printError( "Utracono połączenie z klientem" );
                return;
            }
        }
    }

    /** Wysyła odpowiedź do klienta */
    private void sendResponse( Response response) throws IOException
    {
        // header
        out.write( response.getHeaderSize() );
        out.write( response.getHeader() );
        // message
        out.write( response.getMessageSize() );
        out.write( response.getMessage() );
        // image
        if( response.getImageSize() != null )
        {
            out.write( response.getImageSize() );
            out.write( response.getImage() );
        }

        out.flush();
    }

    /** Bezpieczne wyłączenie serwera */
    private void stop()
    {
        try
        {
            System.out.println( "Wyłączanie serwera..." );
            out.close();
            in.close();
            server.close();
            client.close();

            System.exit( 0 );
        }
        catch( IOException e )
        {
            System.out.println( "Błąd przy wyłączaniu serwera!" );
            System.exit( -1 );
        }
    }

    /** Pomocnicza funkcja wypisująca błąd z komunikatem */
    private void printError( String errorMessage )
    {
        System.out.println( "[Wystąpił błąd]: " + errorMessage );
    }

    public static void main( String[] args )
    {
        Server server = new Server();
        server.start();
    }
}

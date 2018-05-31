import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server
{
    private int port = 4444;
    private ServerSocket server = null;
    private Socket client = null;
    private BufferedReader in = null;
    private OutputStream out = null;
    private String line = "";
    private TreeManager treeManager = new TreeManager();

    private Server()
    {
//        try
//        {
//            System.out.println( "Uruchamianie serwera..." );
//            server = new ServerSocket( port );
//        }
//        catch( IOException e )
//        {
//            printError( "Nie można uzyskać dostępu do portu " + port );
//            stop();
//        }

    }

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

    private void start()
    {
        while( true )
        {
            waitForClient();
            listenSocket();
        }
    }

    private void listenSocket()
    {
        try
        {
            while( true )
            {
                line = in.readLine();
                System.out.println( "Otrzymano zapytanie: " + line );
                String response = treeManager.execute( line );

                // Header
                sendResponse( "txt" );
                // Odpowiedź na zapytanie
                sendResponse( response );
            }
        }
        catch( Exception e )
        {
            printError( "Utracono połączenie z klientem" );
        }
    }

    private void sendResponse( String text ) throws IOException
    {
        byte[] textBytes = text.getBytes();
        byte[] textSizeBytes = ByteBuffer.allocate( 4 ).putInt( textBytes.length ).array();
        out.write( textSizeBytes );
        out.write( textBytes );
    }

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

    private void printError( String errorMessage )
    {
        System.out.println( "[Wystąpił błąd]: " + errorMessage );
    }

    public static void main( String[] args )
    {
        Server server = new Server();

        server.treeManager.generateGraphvizCode();

        //server.start();
    }
}

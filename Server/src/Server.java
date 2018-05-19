import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private int port = 4444;
    private ServerSocket server = null;
    private Socket client = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String line = "";
    private CmdInterpreter command = new CmdInterpreter();

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

    private void start()
    {
        System.out.println( server.toString() );
        do
        {
            try
            {
                System.out.println( "Oczekiwanie na klienta..." );
                client = server.accept();
                System.out.println( "Połączono z klientem " + client.toString() );
            }
            catch( IOException e )
            {
                printError( "Nieudana próba podłączenia klienta na porcie " + port );
                stop();
            }
            try
            {
                in = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
                out = new PrintWriter( client.getOutputStream(), true );
            }
            catch( IOException e )
            {
                printError( "Nieudana próba podłączenia klienta na porcie " + port );
                stop();
            }
        }while( listenSocket() );

        stop();
    }

    private boolean listenSocket()
    {
        try
        {
            while( !line.equals( "0" ) )
            {
                line = in.readLine();
                System.out.println( "Otrzymano zapytanie: " + line );
                line = command.execute( line );
                out.println( line );
            }
        }
        catch( Exception e )
        {
            printError( "Utracono połączenie z klientem" );
            return true;
        }

        return false;
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
        server.start();
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class Client
{
    private int port = 4444;
    private String host = "localhost";
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;


    private Client()
    {
        boolean connected = false;
        System.out.println( "Uruchamianie klienta..." );
        do
        {
            try
            {
                socket = new Socket( host, port );
                out = new PrintWriter( socket.getOutputStream(), true );
                in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
                System.out.println( "Połączono z serwerem " + socket.toString() );
                connected = true;
            }
            catch( UnknownHostException e )
            {
                printError( "Nieznany host '" + host + "'" );
                System.exit( -1 );
            }
            catch( Exception e )
            {
                printError( "Nie można połączyć z serwerem!" );
                connected = false;
                System.out.println("Próba połączenia...");
                try
                {
                    TimeUnit.SECONDS.sleep( 3 );
                }
                catch( InterruptedException ignored ){}
            }
        } while( !connected );
    }

    private void start()
    {
        String line;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while( true )
        {
            try
            {
                System.out.print( "Zapytanie: " );
                line = input.readLine();
                if( line.equals( "q" ) )
                    break;
                out.println( line );
            }
            catch( Exception e )
            {
                printError( "Problem z zapisem / odczytem ze strumienia" );
                continue;
            }
            try
            {
                line = in.readLine();
                System.out.println( "Odpowiedź: " + line );
            }
            catch( IOException e )
            {
                printError( "Nie można uzyskać odpowiedzi" );
            }
        }

        stop();
    }

    private void stop()
    {
        System.out.println( "Wyłączanie klienta..." );
        System.exit( 0 );
    }

    private void printError( String errorMessage )
    {
        System.out.println( "[Wystąpił błąd]: " + errorMessage );
    }

    public static void main( String[] args )
    {
        Client client = new Client();
        client.start();
    }
}

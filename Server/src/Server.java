import java.io.IOException;
import java.net.ServerSocket;

public class Server
{
    private ServerSocket server;

    private Server()
    {
        try
        {
            server = new ServerSocket( 4444 );
        }
        catch( IOException e )
        {
            System.out.println( "Błąd przy tworzeniu serwera!" );
        }

    }

    private void start()
    {
        System.out.println("Uruchomiono serwer");
        System.out.println( server.toString() );
        stop();
    }

    private void stop()
    {
        try
        {
            server.close();
            System.out.println( "Serwer skończył pracę" );
        }
        catch( IOException e )
        {
            System.out.println( "Błąd przy wyłączaniu serwera!" );
        }
    }

    public static void main( String[] args )
    {
        Server server = new Server();
        server.start();
    }
}

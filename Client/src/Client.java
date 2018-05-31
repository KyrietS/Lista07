import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class Client
{
    private int port = 4444;
    private String host = "localhost";
    private Socket socket = null;
    private PrintWriter out = null;
    private InputStream in = null;


    private Client()
    {
        System.out.println( "Uruchamianie klienta..." );
        connectToServer();
    }

    private void connectToServer()
    {
        boolean connected = false;

        do
        {
            try // Pierwsza próba połączenia z serwerem.
            {
                socket = new Socket( host, port );
                out = new PrintWriter( socket.getOutputStream(), true );
                in = socket.getInputStream();
                System.out.println( "Połączono z serwerem " + socket.toString() );
                connected = true;
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
        while( true )
        {
            // Wysyłanie zapytania do serwera
            sendRequest();
            // Odczytywanie odpowiedzi.
            readResponse();

        }
    }

    // Każda odpowiedź może wyglądać następująco:
    // (1) header = "txt": wiadomość tekstowa od serwera
    // (2) header = "txt/jpg": wiadomość tekstowa, po której następuje obrazek JPG
    private void readResponse()
    {
        String header = readTextResponse();
        // Ze schematu wynika, że wiadomość tekstowa od serwera jest zawsze
        String textResponse = readTextResponse();
        System.out.println( "Odpowiedź: " + textResponse );

        // Jeśli nagłówek to "txt/jpg", to znaczy, że po wiadomości tekstowej, będzie obrazek
        if( header.equals( "txt/jpg" ) )
            readImageResponse();
    }

    private void readImageResponse()
    {
        try
        {
            byte[] imgSizeBytes = new byte[ 4 ];
            in.read( imgSizeBytes );
            int imgSize = ByteBuffer.wrap( imgSizeBytes ).asIntBuffer().get();
            byte[] imgBytes = new byte[ imgSize ];
            in.read( imgBytes );

            BufferedImage image = ImageIO.read( new ByteArrayInputStream( imgBytes ) );
            ImageIO.write( image, "jpg", new File("C:\\Users\\Sebastian\\Desktop\\obrazek-z-serwera.jpg"));
            System.out.println("Zapisano obrazek na pulpicie");
        }
        catch( IOException e )
        {
            printError( "Błąd przy pobieraniu obrazka z serwera" );
            stop();
        }
        catch( Exception e )
        {
            printError( "Błąd przy zapisywaniu obrazka" );
        }
    }

    private String readTextResponse()
    {
        try
        {
            byte[] textSizeBytes = new byte[ 4 ];
            in.read( textSizeBytes );
            int textSize = ByteBuffer.wrap( textSizeBytes ).asIntBuffer().get();
            byte[] textBytes = new byte[ textSize ];
            in.read( textBytes );
            String text = new String( textBytes );
            return text;
        }
        catch( IOException e )
        {
            printError( "Błąd przy pobieraniu wiadomości tekstowej z serwera" );
            stop();
            return null;
        }
    }

    private void sendRequest()
    {
        try
        {
            String line;
            BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );
            System.out.print( "Zapytanie: " );
            line = input.readLine();
            if( line.equals( "--q" ) )
                stop();
            out.println( line ); // Wysyłanie zapytania do serwera.
        }
        catch( IOException e )
        {
            printError( "Błąd przy wysyłaniu zapytania do serwera" );
            stop();
        }

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

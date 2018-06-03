import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Zestaw funkcji służących do odczytywania odpowiedzi od serwera ze strumienia wejścia.
 * Odpowiedzi są przesyłane strumieniami bajtów. Zakłada się poprawność danych od serwera.
 *
 * @author Sebastian Fojcik
 */
class Response
{
    static String readText( InputStream in )
    {
        try
        {
            byte[] textSizeBytes = new byte[ 4 ];
            in.read( textSizeBytes );
            int textSize = ByteBuffer.wrap( textSizeBytes ).asIntBuffer().get();
            byte[] textBytes = new byte[ textSize ];
            in.read( textBytes );
            return new String( textBytes );
        }
        catch( IOException e )
        {
            System.out.println( "Błąd przy pobieraniu wiadomości tekstowej z serwera" );
            return null;
        }
    }

    @SuppressWarnings( "SameParameterValue" )           // Wyłącza ostrzeżenie, że 'path' ma jedną, stałą wartość.
    static void readImage( InputStream in, String path )
    {
        try
        {
            byte[] imgSizeBytes = new byte[ 4 ];
            in.read( imgSizeBytes );
            int imgSize = ByteBuffer.wrap( imgSizeBytes ).asIntBuffer().get();
            byte[] imgBytes = new byte[ imgSize ];
            in.readNBytes( imgBytes, 0, imgSize);

            BufferedImage image = ImageIO.read( new ByteArrayInputStream( imgBytes ) );
            ImageIO.write( image, "jpg", new File( path ));
        }
        catch( IOException e )
        {
            System.out.println( "Błąd przy pobieraniu obrazka z serwera" );
        }
        catch( Exception e )
        {
            System.out.println( "Błąd przy zapisywaniu obrazka" );
        }
    }
}

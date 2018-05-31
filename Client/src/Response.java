import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Response
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
            String text = new String( textBytes );
            return text;
        }
        catch( IOException e )
        {
            System.out.println( "Błąd przy pobieraniu wiadomości tekstowej z serwera" );
            return null;
        }
    }

    static boolean readImage( InputStream in, String path )
    {
        try
        {
            byte[] imgSizeBytes = new byte[ 4 ];
            in.read( imgSizeBytes );
            int imgSize = ByteBuffer.wrap( imgSizeBytes ).asIntBuffer().get();
            byte[] imgBytes = new byte[ imgSize ];
            in.read( imgBytes );

            BufferedImage image = ImageIO.read( new ByteArrayInputStream( imgBytes ) );
            ImageIO.write( image, "jpg", new File( path ));
            System.out.println("Zapisano obraz w " + path);
            return true;
        }
        catch( IOException e )
        {
            System.out.println( "Błąd przy pobieraniu obrazka z serwera" );
            return false;
        }
        catch( Exception e )
        {
            System.out.println( "Błąd przy zapisywaniu obrazka" );
            return false;
        }
    }
}

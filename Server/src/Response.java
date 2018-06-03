import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Odpowiedź wysyłana do użytkownika
 * @author Sebastian Fojcik
 */
class Response
{
    private byte[] headerSize;              // Rozmiar nagłówka (int) zapisany na 4 bajtach
    private byte[] header;                  // Nagłówek zapisany na [headerSize] bajtach

    private byte[] messageSize;             // Rozmiar wiadomości (int) zapisany na 4 bajtach
    private byte[] message;                 // Treść wiadomości

    private byte[] imageSize;               // Rozmiar obrazka (int) zapisany na 4 bajtach (max 2 GB)
    private byte[] image;                   // Obrazek JPG zapisany w postaci tablicy bajtów

    void setHeader( String header )
    {
        this.header = header.getBytes();
        this.headerSize = ByteBuffer.allocate( 4 ).putInt( this.header.length ).array();
    }

    void setMessage( String message )
    {
        this.message = message.getBytes();
        this.messageSize = ByteBuffer.allocate( 4 ).putInt( this.message.length ).array();
    }

    void setImage( BufferedImage image )
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write( image, "jpg", byteArrayOutputStream );
            this.image = byteArrayOutputStream.toByteArray();
            this.imageSize = ByteBuffer.allocate( 4 ).putInt( this.image.length ).array();
        }
        catch( Exception e )
        {
            System.out.println( "Błąd przy odczytywaniu bajtów z obrazu" );
        }
    }

    byte[] getHeaderSize()
    {
        return headerSize;
    }

    byte[] getHeader()
    {
        return header;
    }

    byte[] getMessageSize()
    {
        return messageSize;
    }

    byte[] getMessage()
    {
        return message;
    }

    byte[] getImageSize()
    {
        return imageSize;
    }

    byte[] getImage()
    {
        return image;
    }
}

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Response
{
    private byte[] headerSize;
    private byte[] header;

    private byte[] messageSize;
    private byte[] message;

    private byte[] imageSize;
    private byte[] image;

    public void setHeader( String header )
    {
        this.header = header.getBytes();
        this.headerSize = ByteBuffer.allocate( 4 ).putInt( this.header.length ).array();
    }

    public void setMessage( String message )
    {
        this.message = message.getBytes();
        this.messageSize = ByteBuffer.allocate( 4 ).putInt( this.message.length ).array();
    }

    public void setImage( BufferedImage image )
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

    public byte[] getHeaderSize()
    {
        return headerSize;
    }

    public byte[] getHeader()
    {
        return header;
    }

    public byte[] getMessageSize()
    {
        return messageSize;
    }

    public byte[] getMessage()
    {
        return message;
    }

    public byte[] getImageSize()
    {
        return imageSize;
    }

    public byte[] getImage()
    {
        return image;
    }
}

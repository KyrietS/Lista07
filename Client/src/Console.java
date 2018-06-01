import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Console
{
    private TextArea textArea;
    Console( TextArea textArea )
    {
        this.textArea = textArea;
    }

    public void print( String text )
    {
        Platform.runLater( () -> textArea.appendText( text ) );
    }
    public void println( String text )
    {
        Platform.runLater( () -> textArea.appendText( text + "\n" ) );
    }
    public void print( int n )
    {
        textArea.appendText( Integer.toString( n ) );
    }
    public void println( int n )
    {
        textArea.appendText( Integer.toString( n ) + "\n" );
    }
}

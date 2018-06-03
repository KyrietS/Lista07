import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Klasa obsługująca wypisywanie tekstu na ekran konsoli klienckiej.
 * @author Sebastian Fojcik
 */
class Console
{
    /** Referencja do TextArea, do której będzie wypisywany tekst */
    private TextArea textArea;

    Console( TextArea textArea )
    {
        this.textArea = textArea;
    }

    /** Wypisuje wiersz do konsoli */
    void println( String text )
    {
        Platform.runLater( () -> textArea.appendText( text + "\n" ) );
    }
}

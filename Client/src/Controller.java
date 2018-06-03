import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

/**
 * Główny kontroler GUI klienta.
 * @author Sebastian Fojcik
 */
public class Controller
{
    private Connection connection;
    private Console console;

    @FXML
    private TextArea textArea;
    @FXML
    private TextField input;
    @FXML
    private Button button;
    @FXML
    private ImageView image;

    @FXML
    private void initialize()
    {
        // Funkcja, która umożliwi klasie Connection blokować interfejs aplikacji.
        // Ponieważ connection działa w osobnym wątku, nie można tego inaczej zrobić.
        Connection.setInterfaceDisable = this::setInterfaceDisable;

        console = new Console( textArea );
        connection = new Connection( console );

        new File("Client/client-tree.jpg").delete();

        reloadImage();
        Thread thread = new Thread( connection );
        thread.setDaemon( true );
        thread.start();
    }

    @FXML
    private void execute()
    {
        try
        {
            String cmd = input.getText();
            console.println( "> " + cmd );
            Thread request = new Thread( () -> connection.sendRequest( cmd, this::reloadImage ) );
            request.setDaemon( true );
            request.start();
            input.clear();
        }
        catch( Exception e )
        {
            System.out.println( "Wyjatek: " + e.getMessage() );
            System.exit( -1 );
        }
    }

    private void reloadImage()
    {
        try
        {
            File imgFile = new File( "Client/client-tree.jpg");
            Image img = new Image(imgFile.toURI().toURL().toExternalForm(), true);
            Platform.runLater( () -> image.setImage( img ) );
        }
        catch( Exception e )
        {
            System.out.println( "Nie można wczytać obrazku" );
        }
    }

    private void setInterfaceDisable( boolean state )
    {
        input.setDisable( state );
        button.setDisable( state );
    }

}

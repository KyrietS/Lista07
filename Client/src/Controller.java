import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Controller
{
    Connection connection;
    Console console;

    @FXML
    private TextArea textArea;
    @FXML
    private TextField input;
    @FXML
    private ImageView image;

    @FXML
    private void initialize()
    {
        console = new Console( textArea );
        connection = new Connection( console );

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

}

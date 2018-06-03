import tree.BTree;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Klasa zarządzająca drzewem. Przetwarza zapytania i przygotowuje odpowiedź
 * @author Sebastian Fojcik
 */
@SuppressWarnings("unchecked")
class TreeManager
{
    private BTree tree = null;
    private enum TreeType{ NONE, INTEGER, DOUBLE, STRING }
    private TreeType treeType = TreeType.NONE;

    /** Wykonuje otrzymane polecenie i przygotowuje odpowiedź */
    Response execute( String cmd )
    {
        try
        {
            String[] args = cmd.split( " " );

            switch( args[ 0 ].toLowerCase() )
            {
                case "newtree": return newTree( args );
                case "insert":  return insert( args );
                case "search":  return search( args );
                case "delete":  return delete( args );
                case "?":
                case "help":
                case "pomoc":   return help();
                case "about":
                case "autor":   return about();
                default:        return syntaxErrorResponse();
            }
        }
        catch( Exception e ) // Nieznany błąd
        {
            System.out.println("Wystąpił błąd przy przetwarzaniu zapytania: " + cmd);
            return textResponse( "Wystąpił błąd przy przetwarzaniu zapytania" );
        }
    }

    /** Tworzy nowe drzewo */
    private Response newTree( String[] args )
    {
        if( args.length != 3 )
            return syntaxErrorResponse();

        int size;
        try { size = Integer.parseInt( args[ 2 ] ); }
        catch( Exception e ){ return syntaxErrorResponse(); }
        if( size < 3 ){ return errorResponse( "Nie można utworzyć drzewa mniejszego niż 3" ); }
        if( size % 2 == 0 ){ return errorResponse( "Rozmiar drzewa musi być liczbą nieparzystą!" ); }

        // Konwertowanie z jednostek dr Macyny, na jednostki stosowane w podręcznikach.
        size = (size + 1)/2;

        switch( args[ 1 ].toLowerCase() )
        {
            case "integer": tree = new BTree< Integer >( size ); treeType = TreeType.INTEGER; break;
            case "double":  tree = new BTree< Double >( size );  treeType = TreeType.DOUBLE; break;
            case "string":  tree = new BTree< String >( size );  treeType = TreeType.STRING; break;
            default: return errorResponse( "Nie można utworzyć drzewa zawierającego typ <" + args[ 1 ] + ">" );
        }

        return imageResponse( "Pomyślnie utworzono drzewo" );
    }

    /** Wstawia elementy do drzewa */
    private Response insert( String[] args )
    {
        if( args.length < 2 )
            return syntaxErrorResponse();
        if( treeType == TreeType.NONE )
            return errorResponse( "Musisz najpierw utworzyć drzewo" );
        Object[] values = readAllArguments( args );
        if( values == null )
            return syntaxErrorResponse();

        // Po sprarsowaniu argumentów możemy je umieścić w drzewie
        for( Object obj : values )
        {
            if( tree.search( (Comparable) obj ) == null )   // Nie dodajemy duplikatów
                tree.insert( ( Comparable ) obj );
        }

        return imageResponse( "Pomyślnie dodano element(y)" );
    }

    /** Usuwa elementy z drzewa */
    private Response delete( String[] args )
    {
        if( args.length < 2 )
            return syntaxErrorResponse();
        if( treeType == TreeType.NONE )
            return errorResponse( "Musisz najpierw utworzyć drzewo" );
        Object[] values = readAllArguments( args );
        if( values == null )
            return syntaxErrorResponse();

        // Po sparsowaniu argumentów usuwamy elementy z drzewa.
        for( Object obj : values )
                tree.remove( ( Comparable ) obj );

        return imageResponse( "Pomyślnie usunięto element(y)" );
    }

    /** Funkcja pomocnicza parsująca argumenty na typ drzewa */
    private Object[] readAllArguments( String[] args )
    {
        // Uwaga: args[ 0 ] zawiera nazwę polecenia, dlatego jest ten napis jest pomijany
        Object[] values = new Object[ args.length - 1 ];
        try
        {
            for( int i = 0; i < values.length; i++ )
            {
                switch( treeType )
                {
                    case INTEGER: values[ i ] = Integer.valueOf( args[ i + 1 ] );  break;
                    case DOUBLE:  values[ i ] = Double.valueOf( args[  i + 1 ] );  break;
                    case STRING:  values[ i ] = String.valueOf( args[  i + 1 ] );  break;
                }
            }
        }
        catch( Exception e ) // Jeśli conajmniej jedna konwersja się nie powiedzie
        {
            return null;
        }

        return values;
    }

    /** Szuka elementu w drzewie */
    private Response search( String[] args )
    {
        if( args.length != 2 )
            return syntaxErrorResponse();
        if( treeType == TreeType.NONE )
            return errorResponse( "Musisz najpierw utworzyć drzewo" );
        Object key = null;
        try
        {
            switch( treeType )
            {
                case INTEGER: key = Integer.valueOf( args[ 1 ] ); break;
                case DOUBLE:  key = Double.valueOf( args[ 1 ] );  break;
                case STRING:  key = String.valueOf( args[ 1 ] );  break;
            }
        }
        catch( Exception e )
        {
            return syntaxErrorResponse();
        }

        if( tree.search( (Comparable) key ) != null )
            return textResponse( "Podany klucz (" + key + ") znajduje się w drzewie" );
        else
            return textResponse( "Podany klucz (" + key + ") nie znajduje się w drzewie" );
    }

    /** Przygotowuje odpowiedź skłądającą się z wiadomości 'text' oraz obrazka */
    private Response imageResponse( String text )
    {
        Response response = new Response();
        response.setHeader( "txt/jpg" );
        response.setMessage( text );
        addPictureToResponse( response );
        return response;
    }

    /** Przygotowuje odpowiedź tekstową dla klienta */
    private Response textResponse( String text )
    {
        Response response = new Response();
        response.setHeader( "txt" );
        response.setMessage( text );
        return response;
    }

    /** Predefiniowana odpowiedź informująca o niepoprawnej składni polecenia */
    private Response syntaxErrorResponse()
    {
        return textResponse( "Niepoprawne polecenie. Wpisz \"?\" lub \"pomoc\", aby uzyskać więcej informacji" );
    }

    /** Przygotowuje odpowiedź z komunikatem błędu */
    private Response errorResponse( String text )
    {
        return textResponse( "[Błąd] " + text );
    }

    /** Funkcja pomocnicza, która generuje obraz drzewa i dodaje go do 'response' */
    private void addPictureToResponse( Response response )
    {

        // ------------- Generowanie kodu Graphviz -----------------
        StringBuilder code = new StringBuilder( "digraph g {\n" );
        code.append( "node [shape = record,height=.1];\n" );
        if( tree != null && tree.root != null )
            tree.root.generateGraphvizNode( 0, code );
        code.append( "}" );
        // --------------------------------------------------------


        // Zapisanie kodu do pliku
        try (PrintWriter out = new PrintWriter("Server\\server-tree")) {
            out.println( code.toString() );
        }
        catch( FileNotFoundException e )
        {
            System.out.println( "Błąd przy zapisie pliku" );
        }

        // Uruchamianie Graphviz i generowanie pliku JPG
        try
        {
            Process graphviz = new ProcessBuilder("D:\\2. PROGRAMY\\Graphviz\\bin\\dot.exe","-Tjpg","-O",
                    "-Gdpi=300", System.getProperty( "user.dir" )+"\\Server\\server-tree").start();
            graphviz.waitFor();
        }
        catch( Exception ignore )
        {
            System.out.println( "Wtrakcie działania Graphviz wystąpił błąd" );
            return;
        }

        // Wczytywanie JPG do programu
        try
        {
            BufferedImage image = ImageIO.read( new File( System.getProperty( "user.dir" ) + "\\Server\\server-tree.jpg" ) );
            response.setImage( image );
        }
        catch( IOException e )
        {
            System.out.println("Błąd odczytu pliku tree.jpg");
        }
    }

    /** Predefiniowana odpowiedź zawierająca pomoc */
    private Response help()
    {
        String helpText = "";
        helpText += "-----------------------------------------------------------------------------\n";
        helpText += "Lista dostępnych poleceń:\n";
        helpText += " - Nowe drzewo\n";
        helpText += "\tnewtree <Integer|Double|String> <size>\n";
        helpText += "\tsize - rozmiar drzewa (maksymalna ilość kluczy w wierzchołku)\n";
        helpText += " - Dodaj elementy\n\tinsert <arg1> <arg2> ...\n";
        helpText += " - Usuń elementy\n\tdelete <arg1> <arg2> ...\n";
        helpText += " - Znajdź element\n\tsearch <arg>\n";
        helpText += " - Wyświetl pomoc\n\tpomoc | help | ?\n";
        helpText += " - O autorze\n\tautor | about\n";
        helpText += "-----------------------------------------------------------------------------\n";
        return textResponse( helpText );
    }

    /** Predefiniowana odpowiedź zawierająca informacje o autorze */
    private Response about()
    {
        String aboutText = "";
        aboutText += "----------------------------------------------\n";
        aboutText += "       Program na Kurs Programowania\n\n";
        aboutText += "           Autor: Sebastian Fojcik\n\n";
        aboutText += " Niniejszy projekt jest realizacją listy nr 7\n";
        aboutText += "----------------------------------------------\n";

        return textResponse( aboutText );

    }
}

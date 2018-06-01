// Dostępne polecenia:
// newtree int | double | float | string
// insert value
// search value
// delete value
// draw
// draw+ (przy użyciu graphviz)
// Jeśli drzewo nie jest puste, to użycie newtree zmusi użyszkodnika to wpisania !newtree

import tree.BTree;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@SuppressWarnings("unchecked")
class TreeManager
{
    private BTree tree = null;
    private enum TreeType{ NONE, INTEGER, DOUBLE, STRING }
    private TreeType treeType = TreeType.NONE;

    TreeManager()
    {
        // Przykłdowe drzewo do testów
//        tree = new BTree< Integer >( 2 );
//        treeType = TreeType.INTEGER;
//        String[] args = {"insert", "7"};
//        insert( args );
//        //tree.insert( 7 );
//
//        addPictureToResponse( new Response() );

//        tree.insert( 5 );
//        tree.insert( 4 );
//        tree.insert( 3 );
//        tree.insert( 2 );
//        tree.insert( 1 );
//        tree.insert( 6 );
//        tree.insert( 0 );

    }

    Response execute( String cmd )
    {

        String[] args = cmd.split( " " );
        if( args.length < 2 )
            return syntaxErrorResponse();

        switch( args[ 0 ].toLowerCase() )
        {
            case "addtree": return addTree( args );
            case "insert": return insert( args );
            case "search": return search( args );
            case "delete": return delete( args );
            default: return syntaxErrorResponse();
        }
    }

    private Response addTree( String[] args )
    {
        // np. addtree Integer 3   ( addtree <Typ> <Rozmiar> )
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

        return textResponse( "Pomyślnie utworzono drzewo" );
    }

    private Response insert( String[] args )
    {
        if( args.length < 2 )
            return syntaxErrorResponse();
        if( treeType == TreeType.NONE )
            return errorResponse( "Musisz najpierw utworzyć drzewo" );
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
        catch( Exception e )
        {
            return syntaxErrorResponse();
        }

        // Po uzupełnieniu tablicy 'values' jesteśmy pewni, że dane były poprawne.
        // Umieszczamy je w drzewie. Nie ma znaczenia jaki to typ. Wiemy, że są porównywalne.
        for( Object c : values )
            tree.insert( (Comparable)c );

        return imageResponse( "Pomyślnie dodano element(y)" );
    }

    private Response search( String[] args )
    {
        return null;
    }

    private Response delete( String[] args )
    {
        return null;
    }

    private Response imageResponse( String text )
    {
        Response response = new Response();
        response.setHeader( "txt/jpg" );
        response.setMessage( text );
        addPictureToResponse( response );
        return response;
    }

    private Response textResponse( String text )
    {
        Response response = new Response();
        response.setHeader( "txt" );
        response.setMessage( text );
        return response;
    }

    private Response syntaxErrorResponse()
    {
        return textResponse( "Niepoprawne polecenie. Wpisz \"?\" lub \"pomoc\", aby uzyskać więcej informacji" );
    }

    private Response errorResponse( String text )
    {
        return textResponse( "[Błąd] " + text );
    }

    private void addPictureToResponse( Response response )
    {

        StringBuilder code = new StringBuilder( "digraph g {\n" );
        code.append( "node [shape = record,height=.1];\n" );

        tree.root.generateGraphvizNode( 0, code );

        code.append( "}" );

        try (PrintWriter out = new PrintWriter("Server\\server-tree")) {
            out.println( code.toString() );
        }
        catch( FileNotFoundException e )
        {
            System.out.println( "Błąd przy zapisie pliku" );
        }

        // Uruchamianie Graphviz i generowanie plik JPG
        try
        {
            Process graphviz = new ProcessBuilder("D:\\2. PROGRAMY\\Graphviz\\bin\\dot.exe","-Tjpg","-O",
                    "-Gdpi=300", System.getProperty( "user.dir" )+"\\Server\\server-tree").start();
            graphviz.waitFor();
        }
        catch( Exception ignore ){ System.out.println( "Wtrakcie działania Graphviz wystąpił błąd" );}

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

        //System.out.println("Graphviz code:\n" + code.toString() );
    }
}

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

    TreeManager()
    {
        // Przykłdowe drzewo do testów
        tree = new BTree< Integer >( 2 );
        tree.insert( 5 );
        tree.insert( 4 );
        tree.insert( 3 );
        tree.insert( 2 );
        tree.insert( 1 );
        tree.insert( 6 );
        tree.insert( 0 );

    }

    Response execute( String cmd )
    {
        Response response;
        response = new Response();
        response.setHeader( "txt/jpg" );
        response.setMessage( "Wykonano polecenie [" + cmd + "]" );
        generateGraphvizCode( response );

        return response;
    }

    void generateGraphvizCode( Response response )
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
            Process process = new ProcessBuilder("D:\\2. PROGRAMY\\Graphviz\\bin\\dot.exe","-Tjpg","-O",
                    System.getProperty( "user.dir" )+"\\Server\\server-tree").start();
        }
        catch( IOException ignore ){}

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

        System.out.println("Graphviz code:\n" + code.toString() );

//        System.out.println(tree.root.C[0].keys[0]);
//        System.out.println(tree.root.C[1].keys[0]);
    }
}

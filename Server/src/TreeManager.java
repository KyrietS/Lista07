// Dostępne polecenia:
// newtree int | double | float | string
// insert value
// search value
// delete value
// draw
// draw+ (przy użyciu graphviz)
// Jeśli drzewo nie jest puste, to użycie newtree zmusi użyszkodnika to wpisania !newtree

import tree.BTree;

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

    String execute( String cmd )
    {
        return "Wykonano polecenie [" + cmd + "]";
    }

    void generateGraphvizCode()
    {
        StringBuilder code = new StringBuilder( "digraph g {\n" );
        code.append( "node [shape = record,height=.1];\n" );

        tree.root.generateGraphvizNode( 0, code );

        code.append( "}" );
        System.out.println("Graphviz code:\n" + code.toString() );

//        System.out.println(tree.root.C[0].keys[0]);
//        System.out.println(tree.root.C[1].keys[0]);
    }
}

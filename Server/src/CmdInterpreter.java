// Dostępne polecenia:
// newtree int | double | float | string
// insert value
// search value
// delete value
// draw
// draw+ (przy użyciu graphviz)
// Jeśli drzewo nie jest puste, to użycie newtree zmusi użyszkodnika to wpisania !newtree

class CmdInterpreter
{
    private Tree tree;

    String execute( String cmd )
    {
        if( cmd.equals( "newtree" ) )
        {
            tree = new Tree<Integer>();
            tree.value = 5;
        }
        if( cmd.equals( "search" ) && tree != null )
            return tree.value.toString();
        return "--- " + cmd + " ---";
    }
}

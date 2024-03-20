public class Positions {
    char[] board;

    //ArrayList<> whitePieces;
    public Positions() {
        board = new char[] {'a','a','a','a','a','a','a','a','a','a',
                'a','a','a','a','a','a','a','a','a','a',
                'a','R','N','B','Q','K','B','N','R','a',
                'a','P','P','P','P','P','P','P','P','a',
                'a',' ',' ',' ',' ',' ',' ',' ',' ','a',
                'a',' ',' ',' ',' ',' ',' ',' ',' ','a',
                'a',' ',' ',' ',' ',' ',' ',' ',' ','a',
                'a',' ',' ',' ',' ',' ',' ',' ',' ','a',
                'a','p','p','p','p','p','p','p','p','a',
                'a','r','n','b','q','k','b','n','r','a',
                'a','a','a','a','a','a','a','a','a','a',
                'a','a','a','a','a','a','a','a','a','a'};
    }
    public void makeMove (int to, int from, byte special) {
        board[to] = board[from];
        board[from] = ' ';

    }
    public void unMakeMove (int to, int from, byte special, char fromPiece) {
        board[from] = board[to];
        board[to] = fromPiece;
    }
}

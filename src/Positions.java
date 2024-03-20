import java.util.*;
public class Positions {
    char[] board;
    byte boardState;

    ArrayList<ArrayList<Integer>> whitePieces;
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
        boardState = (byte) 240;
    }
    public void makeMove (int to, int from, byte special) {

        board[to] = board[from];
        board[from] = ' ';

        //special coding (4 bits)
        //promotion | capture | special1 | special0
        //promotion + capture + combo of specials(showing what type) = promotion capture
        //promotion + combo of specials = promotion
        //capture = capture
        //capture + special1 = en passant
        //special0 = double pawn push (set en passant)
        //special1 = kingside castle
        //special2 = queenside castle
    }
    public void unMakeMove (int to, int from, byte special, char fromPiece) {
        board[from] = board[to];
        board[to] = fromPiece;
    }
}

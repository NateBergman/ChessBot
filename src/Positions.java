import java.util.*;
public class Positions {
    byte[] board;
    byte boardState;

    ArrayList<ArrayList<Integer>> whitePieces;
    public Positions() {
        board = new byte[] {7,7,7,7,7,7,7,7,7,7,
                7,7,7,7,7,7,7,7,7,7,
                7,4,2,3,5,6,3,2,4,7,
                7,1,1,1,1,1,1,1,1,7,
                7,0,0,0,0,0,0,0,0,7,
                7,0,0,0,0,0,0,0,0,7,
                7,0,0,0,0,0,0,0,0,7,
                7,0,0,0,0,0,0,0,0,7,
                7,9,9,9,9,9,9,9,9,7,
                7,12,10,11,13,14,11,10,12,7,
                7,7,7,7,7,7,7,7,7,7,
                7,7,7,7,7,7,7,7,7,7};
        boardState = (byte) 0b11111000;
    }
    public void makeMove(int move) {
        int to = move & 0b1111111;
        int from = (move>>8) & 0b1111111;
        board[to] = board[from];

        if ((move>>19 & 1) == 1) { //promotion
            board[to] = (byte) (((byte) (move>>16) & 0b11) + 2);
        } else {
            board[to] = board[from];
        }
        board[from] = 0;

    }
    public void unMakeMove(int move) {
        int to = move & 0b1111111;
        int from = (move>>8) & 0b1111111;
        board[from] = board[to];
        //changes with ep board[to] = ;
    }
}

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
        int to = (move>>1) & 0b1111111;
        int from = (move>>8) & 0b1111111;
        boardState = (byte) (boardState & ((~move>>28)<<4)); //SUS updates castling rights and defaults to no en passantables unless added later by pawn push
        if ((move>>19 & 1) == 1) { //promotion
            board[to] = (byte) (((move>>16) & 0b11) + 2 + (8 * (move & 0b1)));
        } else {
            board[to] = board[from];
            if ((move>>16 & 0b101) == 0b101) { //en passant
                board[to - 10 + 20*(move & 0b1)] = 0; //btw (move & 0b1) gives you 0 if white move and 1 if black move
            } else if ((move>>17 & 1) == 1) { //castling
                if ((move>>16 & 1) == 1) { //long castle
                    board[to+1] = board[to-2];
                    board[to-2] = 0;
                } else { //short castle
                    board[to-1] = board[to+1];
                    board[to+1] = 0;
                }
            } else if ((move>>16 & 1) == 1) { //pawn pushing
                boardState += to%10;
            }
        }
        board[from] = 0;

    }
    public void unMakeMove(int move) {
        int to = (move>>1) & 0b1111111;
        int from = (move>>8) & 0b1111111;
        if ((move>>19 & 1) == 1) { //promotion
            board[from] = (byte)(1 + 8 * (move & 0b1)); //same color pawn as move
        } else {
            board[from] = board[to];
            if ((move>>16 & 0b101) == 0b101) { //en passant
                board[to - 10 + 20*(move & 0b1)] = (byte)(1 + 8 * (move ^ 0b1)); //opp color pawn
            } else if ((move>>17 & 1) == 1) { //castling
                if ((move>>16 & 1) == 1) { //long castle
                    board[to-2] = board[to+1];
                    board[to+1] = 0;
                } else { //short castle
                    board[to+1] = board[to-1];
                    board[to-1] = 0;
                }
            }
        }
        board[to] = (byte)(move>>20 & 0b1111); //captured piece
        boardState = (byte)(((boardState | ((move>>28)<<4)) & 0b11110000) + ((move>>24) & 0b1111));
    }
}

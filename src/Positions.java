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
        boardState = (byte) 0b11110000;
    }
    public void makeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        boardState = (byte) (boardState & 0b11110000); //resets en passantables
        if ((move>>19 & 1) == 1) { //if promotion, place correct piece
            board[to] = (byte) (((move>>16) & 0b11) + 2 + (8 * (move & 0b1))); //first part gets type, second half color
        } else {
            board[to] = board[from]; //otherwise piece in resulting square is same as initial
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
            } else if ((move>>16 & 1) == 1) { //pawn pushing makes this column en passantable
                boardState += to%10;
            }
        }
        board[from] = 0; //space piece is leaving is always empty
        if(from == 21 || from == 25) {//castling rights are lost if pieces move off 21,25,28,91,95,or98
            boardState = (byte)(boardState & 0b11011111);
        }
        if(from == 28 || from == 25) {
            boardState = (byte)(boardState & 0b11101111);
        }
        if(from == 91 || from == 95) {
            boardState = (byte)(boardState & 0b01111111);
        }
        if(from == 98 || from == 95) {
            boardState = (byte)(boardState & 0b10111111);
        }
    }
    public void unMakeMove(int move) {
        int to = (move>>1) & 0b1111111; //records to and from indexes
        int from = (move>>8) & 0b1111111;
        if ((move>>19 & 1) == 1) { //if promotion, need to unpromote
            board[from] = (byte)(1 + 8 * (move & 0b1)); //same color pawn as side moving
        } else {
            board[from] = board[to]; //otherwise just move the piece back
            if ((move>>16 & 0b101) == 0b101) { //en passant: also restore opp pawn
                board[to - 10 + 20*(move & 0b1)] = (byte)(1 + 8 * (move ^ 0b1)); //put opp color pawn in appropriate space
            } else if ((move>>17 & 1) == 1) { //undoing castling
                if ((move>>16 & 1) == 1) { //long castle
                    board[to-2] = board[to+1];
                    board[to+1] = 0;
                } else { //short castle
                    board[to+1] = board[to-1];
                    board[to-1] = 0;
                }
            }
        }
        board[to] = (byte)(move>>20 & 0b1111); //captured piece goes back on to
        boardState = (byte)(move>>24); //reverts board state (castle, ep) to previous
    }
    public int encodeMove(int to, int from) {
        int move = 0;
        move += (board[from]>>3) & 1;
        move += to<<1;
        move += from<<8;
        move += board[to]<<20;
        move += boardState<<24;
        if (board[to] != 0) {
            move += 1<<18;
        }
        if ((board[from] & 0b111) == 1) { //if a pawn (they have a lot of special moves)
            if (to > 90 || to < 30) { //promote
                move += 1<<19;
            } else if ((to > 50 && from < 40) || (to < 70 && from > 80)) { //push
                move += 1<<16;
            }
            else if ((boardState & 0b1111) + 60 - (10 * ((board[to]>>3) & 1)) == to) { //ep
                move += 0b101<<16;
            }
        } else if ((board[from] & 0b111) == 6 && from%10 == 5) { //castling
            if (to%10 == 3) {
                move += 0b11<<16;
            } else if (to%10 == 7) {
                move += 0b10<<16;
            }
        }
        return move;
    }
    public void printBoard(Map<Byte,Character> displayMap) {
        for (int y = 90; y > 19; y -= 10) {
            for (int x = 1; x < 9; x++) {
                System.out.print(displayMap.get(board[y + x]) + "|");
            }
            System.out.println("\n------------------------------------------");
        }
        System.out.println(boardState);
    }
}

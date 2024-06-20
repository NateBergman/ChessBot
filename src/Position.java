import java.util.*;
public class Position {
    //board representation
    byte[] board;
    byte boardState;
    Set<Integer>[] pieceLists;
    int[] kingPositions;

    //move gen constants
    boolean[] moveGenSlide;
    int[][] moveGenOffset;

    long[][] hashIndex;

    int[] moves;

    public Position() {
        moves = new int[128];
        board = new byte[]{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, //looks upside down in this view, 7s are borders
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7, //adding borders speeds up move generation because we don't have to check for "off the board"
                7, 4, 2, 3, 5, 6, 3, 2, 4, 7, //extra rows on top/bottom are for knight jumps
                7, 1, 1, 1, 1, 1, 1, 1, 1, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 0, 0, 0, 0, 0, 0, 0, 0, 7,
                7, 9, 9, 9, 9, 9, 9, 9, 9, 7,
                7, 12, 10, 11, 13, 14, 11, 10, 12, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
        boardState = (byte) 0b11110000;
        Collections.addAll(pieceLists[0],21, 22, 23, 24, 25, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38);
        Collections.addAll(pieceLists[1], 81, 82, 83, 84, 85, 86, 87, 88, 91, 92, 93, 94, 95, 96, 97, 98);

        //seeding hash index
        Random r = new Random(1); //each aspect of a position is given it's own 64-bit long; "randomly" generated but not really because the seed is always 1 at program start
        hashIndex = new long[13][64]; //1-12 are pieces on the board, 0 is for special stuff like side to move, castling, en passant (0 is side to move, 1-8 en passant, 9-23 castling rights)
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 64; j++) {
                hashIndex[i][j] = r.nextLong();
            }
        }
    }

    public long getHashIndex(boolean whiteMove) {
        long hash = 0; //to refer to a position, we just XOR each aspect of the position together
        for (int y = 2; y < 10; y++) { //isn't a perfect system, but with 64 bits the probability of having a conflict is really small
            for (int x = 1; x < 9; x++) {
                hash = hash ^ hashIndex[board[10*y+x] % 8 + (board[10*y+x]>>>3) * 6][x-1 + (y-2) * 8];
            }
        }
        if (!whiteMove) {
            hash = hashIndex[0][0] ^ hash;
        }
        if ((boardState & 0b1111) != 0) {
            hash = hash ^ hashIndex[0][boardState & 0b1111];
        }
        if ((boardState & 0b11110000) != 0) {
            hash = hash ^ hashIndex[0][8 + ((boardState & 0b11110000)>>>4)];
        }
        return hash;
    }
}

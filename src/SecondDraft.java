import java.util.*;
public class SecondDraft { //uses tapered piece-square eval, no/basic pruning, and mailbox with piece lists and offset move generation
    static byte[] board;
    static byte boardState;
    static Set<Integer> whitePieces = new TreeSet<>();
    static Set<Integer> blackPieces = new TreeSet<>();
    int whiteKing;
    int blackKing;

    static int phase;
    static int[] phaseCounts;

    static boolean[] slide = {false,false,true,true,true,false};
    static int[][] offset = {{},{-21, -19,-12, -8, 8, 12, 19, 21},{-11,  -9,  9, 11},{-10,  -1,  1, 10},{-11, -10, -9, -1, 1,  9, 10, 11},{-11, -10, -9, -1, 1,  9, 10, 11}};

    public static void main(String[] args) {
        board = new byte[]{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, //looks upside down in this view, 7s are borders
                7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
                7, 4, 2, 3, 5, 6, 3, 2, 4, 7,
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
        Collections.addAll(whitePieces, 21, 22, 23, 24, 25, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38);
        Collections.addAll(blackPieces, 81, 82, 83, 84, 85, 86, 87, 88, 91, 92, 93, 94, 95, 96, 97, 98);

        phase = 0;
        phaseCounts = new int[] {0,0,1,1,2,4,0,0,0,0,1,1,2,4,0};
    }
    public static ArrayList<Integer> getWhiteMoves() {
        ArrayList<Integer> moves = new ArrayList<>();
        for (int i : whitePieces) { //i = from coordinate, p = from piece, t = to coordinate, m = to offset, target = to piece
            int p = board[i] - 1;
            if (p == 0) { //pawn
                if (i/10 == 8) { //promotions

                }
                else {
                    if (i / 10 == 6) { //ep

                    }
                }
            } else {
               int[] o = offset[p];

            }
        }
        //add castling
        return moves;
    }
}

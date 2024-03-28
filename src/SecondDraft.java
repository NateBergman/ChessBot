import java.util.*;
public class SecondDraft { //uses tapered piece-square eval, no/basic pruning, and mailbox with piece lists
    static byte[] board;
    static byte boardState;
    static Set<Integer> whitePieces = new TreeSet<>();
    static Set<Integer> blackPieces = new TreeSet<>();
    static int[][] diagonalMoves;
    static int[][] horizontalMoves;
    static int[] knightMoves;
    static int[] kingMoves;
    int whiteKing;
    int blackKing;

    static int phase;
    static int[] phaseCounts;

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

        diagonalMoves = new int[][]{new int[]{11, 22, 33, 44, 55, 66, 77}, new int[]{9, 18, 27, 36, 45, 54, 63}, new int[]{-11, -22, -33, -44, -55, -66, -77}, new int[]{-9, -18, -27, -36, -45, -54, -63}};
        horizontalMoves = new int[][]{new int[]{10, 20, 30, 40, 50, 60, 70}, new int[]{1, 2, 3, 4, 5, 6, 7}, new int[]{-10, -20, -30, -40, -50, -60, -70}, new int[]{-1, -2, -3, -4, -5, -6, -7}};
        knightMoves = new int[] {21,19,12,8,-8,-12,-19,-21};
        kingMoves = new int[] {11,10,9,1,-1,-9,-10,-11};

        phase = 0;
        phaseCounts = new int[] {0,0,1,1,2,4,0,0,0,0,1,1,2,4,0};
    }
    public static ArrayList<Integer> getWhiteMoves() {
        ArrayList<Integer> moves = new ArrayList<>();
        for (int i : whitePieces) { //i = from coordinate, p = from piece, t = to coordinate, m = to offset, target = to piece
            byte p = board[i];
            if (p == 1) { //pawn
                if (i/10 == 8) { //promotions

                }
                else {
                    if (i / 10 == 6) { //ep

                    }
                }
            } else if (p == 2) { //knight
                for (int m : knightMoves) {
                    int t = i + m;
                    byte target = board[t];
                    if (target == 0 || target > 8) {
                        moves.add((boardState<<23) + (target<<19) + (i<<8) + (t<<1));
                    }
                }
            } else if (p == 5) { //king

            } else { //sliding
                if (p == 4 || p == 5) { //orthogonal

                }
                if (p == 3 || p == 5) { //diagonal

                }
            }
        }
        return moves;
    }
}

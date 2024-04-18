import java.util.*;
public class AttackMapMoveGenTest { //also using 0x88
    static byte[] board;
    static byte boardState;
    static Map<Integer,Long> whitePieceMap = new HashMap<>();
    static Map<Integer,Long> blackPieceMap = new HashMap<>();

    static boolean[] moveGenSlide = {false,false,true,true,true,false};
    static int[][] moveGenOffset = {{},{-21, -19,-12, -8, 8, 12, 19, 21},{-11,  -9,  9, 11},{-10,  -1,  1, 10},{-11, -10, -9, -1, 1,  9, 10, 11},{-11, -10, -9, -1, 1,  9, 10, 11}};

    public static void main (String[] args) {

    }
    public static long getAttackMap(int square) {
        long attackMap = 0;
        int fromPiece = board[square] - 1;
        if (fromPiece == 0) { //pawn

        } else {
            int[] offsets = moveGenOffset[fromPiece];
            boolean slide = moveGenSlide[fromPiece];
            for (int o : offsets) {
                int to = square + o;
                do {
                    byte toPiece = board[toCoordinate];
                    if (toPiece > 8) {
                        moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1) + (toPiece << 19));
                        break;
                    }
                    if (toPiece != 0) {
                        break;
                    }
                    moves.add((boardState << 23) + (fromCoordinate << 8) + (toCoordinate << 1));
                    toCoordinate += o;
                } while (slide);
            }
        }
    }
    public static long getMoves(int square) {
        return
    }
}
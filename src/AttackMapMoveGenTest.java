import java.util.*;
public class AttackMapMoveGenTest { //also using 0x88
    static byte[] board;
    static byte boardState;
    static Map<Integer,Long> whitePieceMap = new HashMap<>();
    static Map<Integer,Long> blackPieceMap = new HashMap<>();
    static long whitePieces;
    static long blackPieces;
    static long whiteAttacks;
    static long blackAttacks;

    static boolean[] attackSlide = {false, false,false,true,true,true,false, false,false, false,false,true,true,true,false};
    static int[][] attackOffset = {{}, {15,17},{-33, -31,-18, -14, 14, 18, 31, 33},{-17,  -15,  15, 17},{-16,  -1,  1, 16},{-17, -15, 15, 17, -16,  -1, 1, 16},{-17, -15, 15, 17, -16,  -1, 1, 16}, {},{},
            {-15,-17},{-33, -31,-18, -14, 14, 18, 31, 33},{-17,  -15,  15, 17},{-16,  -1,  1, 16},{-17, -15, 15, 17, -16,  -1, 1, 16},{-17, -15, 15, 17, -16,  -1, 1, 16}};

    public static void main (String[] args) {

    }
    public static long makePieceAttackMap(int square) {
        long attackMap = 0;
        int fromPiece = board[square];
        int[] offsets = attackOffset[fromPiece];
        boolean slide = attackSlide[fromPiece];
        for (int o : offsets) {
            int to = square + o;
                do {
                    if ((to & 0x88) != 0) {
                        break;
                    }
                    attackMap += 1 << to;
                    if (board[to] != 0) {
                        break;
                    }
                } while (slide);
            }
        return attackMap;
    }
    public static long makeSideAttackMap(boolean white) {
        long map = 0;
        Collection<Long> pieces;
        if (white) {
            pieces = whitePieceMap.values();
        } else {
            pieces = blackPieceMap.values();
        }
        for (long p : pieces) {
            map = map | p;
        }
        return map;
    }
    public static long makeSidePieceMap(boolean white) {
        long map = 0;
        Set<Integer> pieces;
        if (white) {
            pieces = whitePieceMap.keySet();
        } else {
            pieces = blackPieceMap.keySet();
        }
        for (int p : pieces) {
            map += 1 << p;
        }
        return map;
    }
    public static long getPseudoLegalMoves(int square) {
        byte piece = board[square];
        if (piece == 1) { //white pawn moves
            long moves = (whitePieceMap.get(square) & blackPieces);
        } else if (piece == 9) { //black pawn moves
            long moves = blackPieceMap.get(square) & whitePieces;
        } else {
            if (piece > 8) {
                return blackPieceMap.get(square) & (~blackPieces);
            } else {
                return whitePieceMap.get(square) & (~whitePieces);
            }
        }
    }
}
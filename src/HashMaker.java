import java.util.*;

public class HashMaker {
    int[][] hashIndex;
    Map<Integer,int[]> transpositionTable;
    public HashMaker () {
        Random r = new Random();
        hashIndex = new int[13][64]; //1-12 are pieces on the board, 0 is for special stuff like side to move, castling, en passant (0 is side to move, 1-8 en passant, 9-23 castling rights)
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 64; j++) {
                hashIndex[i][j] = r.nextInt(2147483647);
            }
        }
    }
    public int getHashIndex(int[] board, byte boardState, boolean blackMove) {
        int hash = 0;
        for (int y = 2; y < 10; y++) {
            for (int x = 1; x < 9; x++) {
                hash = hash ^ hashIndex[board[10*y+x] % 8 + (board[10*y+x]>>>3) * 6][x-1 + (y-2) * 8];
            }
        }
        if (blackMove) {
            hash = hashIndex[0][0] ^ hash;
        }
        if ((boardState & 0b1111) != 0) {
            hash = hash ^ hashIndex[0][boardState & 0b1111];
        }
        if ((boardState & 0b11110000) != 0) {
            hash = hash ^ hashIndex[0][8 + (boardState>>>4)];
        }
        return hash;
    }
    public void storeTranspositionTable(int hash, int bestMove, int depth) {
        if (!transpositionTable.containsKey(hash) || transpositionTable.get(hash)[1] < depth) {
            transpositionTable.put(hash, new int[] {bestMove,depth});
        }
    }
}

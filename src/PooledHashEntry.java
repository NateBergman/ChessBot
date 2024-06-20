import java.util.*;
public class PooledHashEntry {
    int[] moves;
    int depth;
    int score;
    boolean fullSearch;

    public PooledHashEntry() {
        depth = -9999; //no one should read unassigned entries because you never get to this low a depth
        score = 0;
        fullSearch = false;
        moves = new int[64];
    }

    public void assignEntry(int[] moves, int depth, int score, boolean fullSearch) {
        for (int i = 0; true; i++) { //assumes all positions have 64 or less moves
            int m = moves[i];
            if (m == 0) {
                break;
            }
            this.moves[i] = m;
        }
        this.depth = depth;
        this.score = score;
        this.fullSearch = fullSearch;
    }

    public boolean isFullSearch() {
        return fullSearch;
    }

    public int getScore() {
        return score;
    }

    public int getDepth() {
        return depth;
    }

    public boolean canUseScore(int depth, int alpha, int beta) {
        return (this.depth >= depth && (fullSearch || score < alpha || score > beta));
    }
}

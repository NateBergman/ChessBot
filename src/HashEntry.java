import java.util.*;
public class HashEntry {
    int bestMove;
    int depth;

    double score;
    boolean fullSearch;
    ArrayList<Integer> moves;
    public HashEntry(int bestMove,int depth,boolean fullSearch, double score) {
        this.bestMove = bestMove;
        this.depth = depth;
        this.score = score;
        this.fullSearch = fullSearch;
    }
    public int getBestMove() {
        return bestMove;
    }
    public int getDepth() {
        return depth;
    }
    public double getScore() {
        return score;
    }
    public boolean getFullSearch() {
        return fullSearch;
    }
}

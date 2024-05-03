import java.util.*;
public class HashEntry {
    int depth;

    double score;
    boolean fullSearch;
    ArrayList<Integer> moves;
    public HashEntry(ArrayList<Integer> moves, int depth, boolean fullSearch, double score) {
        this.moves = new ArrayList<>(moves);
        this.depth = depth;
        this.score = score;
        this.fullSearch = fullSearch;
    }
    public ArrayList<Integer> getMoves() {
        return moves;
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
    public boolean isQuiescence() {
        return depth < 1;
    }
}

import java.util.*;
public class HashEntry {
    int depth;

    double score;
    boolean fullSearch;
    ArrayList<Integer> moves;
    ArrayList<Integer> quiescenceMoves;
    public HashEntry(ArrayList<Integer> moves,int depth,boolean fullSearch,double score, ArrayList<Integer> quiescenceMoves) {
        this.moves = new ArrayList<>(moves);
        this.quiescenceMoves = new ArrayList<>(quiescenceMoves);
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
    public ArrayList<Integer> getQuiescenceMoves() {
        return quiescenceMoves;
    }
}

import java.util.*;
public class HashEntry {
    int depth;

    int score;
    boolean fullSearch;
    ArrayList<Integer> moves;
    int orderIn;
    public HashEntry(ArrayList<Integer> moves, int depth, boolean fullSearch, int score) {
        this.moves = new ArrayList<>(moves);
        this.depth = depth;
        this.score = score;
        this.fullSearch = fullSearch;
        orderIn = 0;
    }
    public HashEntry(ArrayList<Integer> moves, int depth, boolean fullSearch, int score, int orderIn) {
        this.moves = new ArrayList<>(moves);
        this.depth = depth;
        this.score = score;
        this.fullSearch = fullSearch;
        this.orderIn = orderIn;
    }
    public HashEntry(int depth, boolean fullSearch, int score, int orderIn) {
        this.depth = depth;
        this.score = score;
        this.fullSearch = fullSearch;
        this.orderIn = orderIn;
    }
    public ArrayList<Integer> getMoves() {
        return moves;
    }
    public int getDepth() {
        return depth;
    }
    public int getScore() {
        return score;
    }
    public boolean getFullSearch() {
        return fullSearch;
    }
    public boolean isQuiescence() {
        return depth < 1;
    }
    public boolean isCheckmate() {
        return (score > 9998 || score < -9998) && score > -99999 && score < 99999; //tests if it's a checkmate but not immediate king capture
    }
    public int getCheckmateScore(int currentDepth) { //incentivize earlier checkmates
        if (score < 0) {
            return score - currentDepth;
        }
        return score + currentDepth;
    }
    public int getOrderIn() {
        return orderIn;
    }

    public void setEntry (ArrayList<Integer> moves, int depth, boolean fullSearch, int score) {

    }
}

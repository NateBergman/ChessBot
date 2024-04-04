public class HashEntry {
    int bestMove;
    int depth;
    boolean fullSearch;
    double score;
    public HashEntry(int bestMove,int depth,boolean fullSearch,double score) {
        this.bestMove = bestMove;
        this.depth = depth;
        this.fullSearch = fullSearch;
        this.score = score;
    }
    public int getBestMove() {
        return bestMove;
    }
    public int getDepth() {
        return depth;
    }
    public boolean isFullSearch() {
        return fullSearch;
    }
    public double getScore() {
        return score;
    }
}

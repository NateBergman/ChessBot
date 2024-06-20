import java.util.*;
public class TranspositionTable {
    public HashMap<Long,PooledHashEntry> tt;
    int size;
    Queue<Long> indexQueue;
    public HashSet<Long> repetition;

    public TranspositionTable(int size) {
        tt = new HashMap<>();
        indexQueue = new LinkedList<>();

        for (long i = 0; i < size; i++) {
            PooledHashEntry h = new PooledHashEntry();
            tt.put(i,h);
            indexQueue.add(i);
        }
    }

    public boolean contains (long index) {
        return tt.containsKey(index);
    }
    public PooledHashEntry get (long index) {
        return tt.get(index);
    }
    public void assign (long index, int[] moves, int depth, int score, boolean fullSearch) {
        PooledHashEntry h = tt.remove(indexQueue.remove());
        h.assignEntry(moves,depth,score,fullSearch);
        indexQueue.add(index);
        tt.put(index,h);
    }

    public boolean isRepetition (long index) {
        return repetition.contains(index);
    }
    public void addRepetition (long index) {
        repetition.add(index);
    }
    public void removeRepetition(long index) {
        repetition.remove(index);
    }
}

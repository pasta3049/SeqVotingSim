package Model;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Map;

/**
 * Created by AriApar on 16/05/2016.
 */
public class MemEffVector implements IVector<Map<Integer, Integer>> {

    private Int2IntOpenHashMap scores;
    private int numCandidates;

    private int hashCode = 0;

    public MemEffVector(int numCandidates) {
        this.scores = new Int2IntOpenHashMap(numCandidates);
        scores.defaultReturnValue(0);
        this.numCandidates = numCandidates;
    }

    public MemEffVector(Int2IntOpenHashMap map, int numCandidates) {
        this.scores = (map != null) ? map : new Int2IntOpenHashMap(numCandidates);
        scores.defaultReturnValue(0);
        this.numCandidates = numCandidates;
    }

    public MemEffVector(int[] arr) {
        this.scores = new Int2IntOpenHashMap(arr.length);
        scores.defaultReturnValue(0);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) this.scores.put(i, arr[i]);
        }
        this.numCandidates = arr.length;
    }

    public MemEffVector add(IVector<Map<Integer, Integer>> voteVector) {
        if (voteVector.getLength() != getLength()) throw new AssertionError("vector sizes not equal on add");
        Int2IntOpenHashMap resMap = new Int2IntOpenHashMap(voteVector.getRepresentation());
        ObjectSet<Map.Entry<Integer, Integer>> entrySet = scores.entrySet();
        for (Map.Entry<Integer, Integer> entry : entrySet) {
            int key = entry.getKey();
            int value = entry.getValue();
            resMap.addTo(key, value);
            // if new value is 0 remove from map!
            if (resMap.get(key) == 0) resMap.remove(key);
        }
        return new MemEffVector(resMap, getLength());
    }

    public int getLength() {
        return numCandidates;
    }

    public int get(int i) {
        assert (i >= 0 && i < getLength());
        return scores.get(i);
    }

    public int getCandidate(int candidate) {
        assert candidate > 0 && candidate <= getLength();
        return get(candidate - 1);
    }

    public MemEffVector cloneAndSet(int index, int value) {
        assert (index >= 0 && index < getLength());
        Int2IntOpenHashMap resMap = new Int2IntOpenHashMap(scores);
        if (value == 0) resMap.remove(index);
        else resMap.put(index, value);
        return new MemEffVector(resMap, getLength());
    }

    public Int2IntOpenHashMap getRepresentation() {
        return scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemEffVector that = (MemEffVector) o;

        if (hashCode() != that.hashCode()) return false;
        if (scores.size() != that.scores.size()) return false;
        return scores.equals(that.scores);

    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 11;
            result = scores != null ? result * 31 + scores.hashCode() : result;
            hashCode = result;
        }
        return hashCode;
    }

    public MemEffVector cloneAndSetCandidate(int candidate, int value) {
        assert candidate > 0 && candidate <= getLength();
        return cloneAndSet(candidate - 1, value);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        for (int i = 0; i < getLength() - 1; i++) {
            str.append(scores.get(i) + ", ");
        }
        str.append(scores.get(getLength() - 1) + "]");
        return str.toString();
    }

    public int getSum() {
        int sum = 0;
        int[] values = scores.values().toIntArray();
        for (int i = 0; i < values.length; i++) sum += values[i];
        return sum;
    }
}

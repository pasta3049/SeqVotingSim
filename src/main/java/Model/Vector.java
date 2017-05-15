package Model;


import java.util.Arrays;

/**
 * Created by AriApar on 26/11/2015.
 *
 * Immutable Vector
 *
 */
public class Vector implements IVector<int[]> {
    private int[] scores;
    private int hashCode;

    public Vector(int numCandidates) {
        this.scores = new int[numCandidates];
    }

    public Vector(int[] scores) {
        this.scores = scores;
    }

    public Vector add(IVector<int[]> voteIVector) {
        assert (voteIVector.getLength() == scores.length);
        int[] resArr = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            resArr[i] = scores[i] + voteIVector.get(i);
        }
        return new Vector(resArr);
    }

    @Override
    public int getLength() {
        return scores.length;
    }

    @Override
    public int get(int i) {
        assert (i >= 0 && i < scores.length);
        return scores[i];
    }

    @Override
    public Vector cloneAndSet(int index, int value) {
        assert (index >= 0 && index < scores.length);
        int[] resArr = scores.clone();
        resArr[index] = value;
        return new Vector(resArr);
    }

    public int[] getRepresentation() {
        return scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector that = (Vector) o;
        if (hashCode() != that.hashCode()) return false;
        return Arrays.equals(scores, that.scores);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Arrays.hashCode(scores);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return Arrays.toString(scores);
    }

    public int getSum() {
        int sum = 0;
        for(int i = 0; i< scores.length; i++) sum += scores[i];
        return sum;
    }
}

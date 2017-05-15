package Model;

/**
 * Created by AriApar on 01/05/2016.
 */
public class ScoreVector extends Vector {
    //ScoreVector should be used as a vector representing the vote count of each candidate, and nothing else.
    //Its methods rely on this guarantee.
    public ScoreVector(int numCandidates) {
        super(numCandidates);
    }

    public ScoreVector(int[] scores) {
        super(scores);
    }

    public ScoreVector cloneAndSetCandidate(int candidate, int value) {
        assert candidate > 0 && candidate <= getLength();
        return cloneAndSet(candidate-1, value);
    }

    public int getCandidate(int candidate) {
        assert candidate > 0 && candidate <= getLength();
        return get(candidate-1);
    }

    public ScoreVector add(IVector voteIVector) {
        assert (voteIVector.getLength() == getLength());
        int[] resArr = new int[getLength()];
        for (int i = 0; i < getLength(); i++) {
            resArr[i] = get(i) + voteIVector.get(i);
        }
        return new ScoreVector(resArr);
    }

    @Override
    public ScoreVector cloneAndSet(int index, int value) {
        assert (index >= 0 && index < getLength());
        Vector rVector = super.cloneAndSet(index, value);
        return new ScoreVector(rVector.getRepresentation());
    }
}

package Model;

import java.util.Iterator;

/**
 * Created by AriApar on 25/11/2015.
 */
public class VotingOrder implements Iterable<Integer> {

    private int[] voteOrder;
    /**
     * Creates an Iterable Order that consists of {@code numVoters} voters, in increasing or decreasing voting order by voter ID's.
     * @param numVoters number of voters
     * @param increasing boolean representing whether the order should be increasing or (false case) decreasing.
     */
    public VotingOrder(int numVoters, boolean increasing) {
        voteOrder = new int[numVoters];
        if (increasing) {
            for (int i = 0; i < numVoters; i++) {
                voteOrder[i] = i + 1;
            }
        } else {
            for (int i = 0; i < numVoters; i++) {
                voteOrder[i] = numVoters - i;
            }
        }
    }

    public VotingOrder(int[] voteOrder) {
        this.voteOrder = voteOrder;
    }

    public Iterator<Integer> iterator() {
        Iterator<Integer> it = new Iterator<Integer>() {
            private int index = 0;

            @Override
            public boolean hasNext () {
                return index < voteOrder.length;
            }

            @Override
            public Integer next () {
                return voteOrder[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        return it;
    }

    public int indexOf(int voterId) {
        int index = 0;
        while (index < voteOrder.length && voteOrder[index] != voterId) index+=1;
        return index;
    }

    public boolean isLastVoter(int voterId) {
        return voteOrder[voteOrder.length -1] == voterId;
    }
}

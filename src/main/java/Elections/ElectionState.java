package Elections;

import Model.ScoreVector;
import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class ElectionState {
    private ScoreVector currentScores;
    private ArrayList<Integer> currentWinners;
    private ArrayList<Integer> currentVotes;

    public ElectionState(int numCandidates) {
        currentScores = new ScoreVector(numCandidates);
        currentWinners = new ArrayList<>();
        currentVotes = new ArrayList<>();
    }

    public ElectionState(ScoreVector currentScores, ArrayList<Integer> currentWinners,
                         ArrayList<Integer> currentVotes) {
        this.currentScores = currentScores;
        this.currentWinners = currentWinners;
        this.currentVotes = currentVotes;
    }

    public ScoreVector getCurrentScores() {
        return currentScores;
    }

    public ArrayList<Integer> getCurrentWinners() {
        return currentWinners;
    }

    public ArrayList<Integer> getCurrentVotes() { return currentVotes;}

    public int getLastVoteCast() {
        return currentVotes.get(currentVotes.size()-1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElectionState that = (ElectionState) o;

        if (getLastVoteCast() != that.getLastVoteCast()) return false;
        if (currentScores != null ? !currentScores.equals(that.currentScores) : that.currentScores != null)
            return false;
        if (currentWinners != null ? !currentWinners.equals(that.currentWinners) : that.currentWinners != null)
            return false;
        return currentVotes != null ? currentVotes.equals(that.currentVotes) : that.currentVotes == null;
    }

    @Override
    public int hashCode() {
        int result = currentScores != null ? currentScores.hashCode() : 0;
        result = 31 * result + (currentWinners != null ? currentWinners.hashCode() : 0);
        result = 31 * result + (currentVotes != null ? currentVotes.hashCode() : 0);
        return result;
    }
}

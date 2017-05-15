package Elections;

import Model.IVector;
import Model.ScoreVector;
import Model.VotingRule;

import java.util.ArrayList;

/**
 * Created by ug14mk using code from ElectionState by AriApar
 */
public class AltElectionState {
    private ScoreVector currentScores;
    private ArrayList<Integer> currentWinners;
    private ArrayList<IVector> currentVotes;
    private boolean abstentions;
    private VotingRule selectedVotingRule;

    public AltElectionState(int numCandidates, boolean abstentions, VotingRule selectedVotingRule) {
        currentScores = new ScoreVector(numCandidates);
        currentWinners = new ArrayList<>();
        currentVotes = new ArrayList<>();
        this.abstentions = abstentions;
        this.selectedVotingRule = selectedVotingRule;
    }

    public AltElectionState(ScoreVector currentScores, ArrayList<Integer> currentWinners,
                         ArrayList<IVector> currentVotes, boolean abstentions, VotingRule selectedVotingRule) {
        this.currentScores = currentScores;
        this.currentWinners = currentWinners;
        this.currentVotes = currentVotes;
        this.abstentions = abstentions;
        this.selectedVotingRule = selectedVotingRule;
    }

    public ScoreVector getCurrentScores() {
        return currentScores;
    }

    public ArrayList<Integer> getCurrentWinners() {
        return currentWinners;
    }

    public ArrayList<IVector> getCurrentVotes() { return currentVotes;}
    
    public ArrayList<String> getCurrentVoteDetails() {
    	ArrayList<String> strings = new ArrayList<String>();
    	for (int i = 0; i < currentVotes.size(); i++) {
    		strings.add(selectedVotingRule.getVoteDetailsAsString(currentVotes.get(i), currentScores.getLength(), abstentions));
    	}
    	return strings;
    }

    public IVector getLastVoteCast() {
        return currentVotes.get(currentVotes.size()-1);
    }
    
    public boolean getAbstentions() {
    	return abstentions;
    }
    
    public VotingRule getSelectedVotingRule() {
    	return selectedVotingRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AltElectionState that = (AltElectionState) o;

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

package Elections;

import Model.ScoreVector;
import Model.Voter;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AriApar on 25/11/2015.
 */
public abstract class Election {

    private ElectionParameters params;
    private ArrayList<Voter> voters;

    public void setElection(ElectionParameters params) {
        setParams(params);
        setVoters(params);
    }

    public ElectionParameters getParams() {
        return params;
    }

    public void setParams(ElectionParameters params) {
        this.params = params;
    }

    public ArrayList<Voter> getVoters() {
        return voters;
    }

    public void setVoters(ArrayList<Voter> voters) {
        this.voters = voters;
    }

    public void setVoters(ElectionParameters params) {
        ArrayList<Voter> voters = new ArrayList<>();
        for (int voterId : params.getOrder()) {
            voters.add(new Voter(voterId, params));
        }
        setVoters(voters);
    }

    public ArrayList<Integer> getWinnersOfScoreVector(ScoreVector scores) {
        return params.getRule().getWinnersOfScoreVector(scores, params);
    }

    public int getUniqueWinner(ScoreVector scores) {
        ArrayList<Integer> winners = getWinnersOfScoreVector(scores);
        if (winners.size() == 1) return winners.get(0);
        else {
            //tie-breaker via Random
            Random random = new Random();
            int winner = random.nextInt(winners.size());
            return winners.get(winner);
        }
    }

    public abstract ArrayList<ElectionState> findNE() throws Exception;
}

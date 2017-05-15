package Elections;

import Model.*;
import java.util.ArrayList;

/**
 * Created by AriApar on 25/11/2015.
 */
public class TruthfulElection extends Election {

    protected TruthfulElection(ElectionParameters params) {
        setElection(params);
    }

    @Override
    public ArrayList<ElectionState> findNE()  {
        ScoreVector scores = new ScoreVector(getParams().getPref().getNumCandidates());
        ArrayList<Integer> votes = new ArrayList<>();
        for(Voter v : getVoters()) {
            IVector voteIVector = v.vote();
            scores = scores.add(voteIVector);
            votes.add(getParams().getRule().getWinnersOfVoteVector(voteIVector, getParams()).get(0));
        }
        ArrayList<ElectionState> res = new ArrayList<>();
        res.add(new ElectionState(scores, getWinnersOfScoreVector(scores), votes));
        return res;
    }
}

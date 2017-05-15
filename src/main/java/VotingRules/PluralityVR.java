package VotingRules;

import Elections.ElectionParameters;
import Model.*;
import Model.Vector;

import java.util.*;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;


/**
 * Created by AriApar on 25/11/2015.
 * Modified by ug14mk on 01/02/2017 to include new method required by VotingRule interface
 *
 * This class models plurality voting with a coin flip for ties.
 */
public class PluralityVR implements VotingRule {

    private ArrayList<Integer> scoringVector;

    public PluralityVR(int candidates) {
        scoringVector = new ArrayList<>(candidates);
        for (int i = 0; i < candidates; i++) scoringVector.add(0);
        scoringVector.set(0,1);
    }

    @Override
    public IVector voteTruthful(Preferences pref) {
        return vote(pref.getNthPreference(1));
    }

    @Override
    public IVector vote(int candidate) {
        IVector res = new Model.Vector(scoringVector.size());
        res = res.cloneAndSet(candidate-1, 1);
        return res;
    }

    @Override
    public ArrayList<Integer> getWinnersOfVoteVector(IVector s, ElectionParameters params) {
        //Gets the winners if each preference got s(i) no of votes
        //preferences in s ordered lexicographically
        //if abstention is possible, there is an abstention vector at the end of scorevectors
        //no abstention
        if (!params.canAbstain()) {
            return calcWinnersOfPrefVectors(s, params.numberOfCandidates(), 0);
        }
        else {
            return calcWinnersOfPrefVectors(s, params.numberOfCandidates(), 1);
        }
    }

    @Override
    public ArrayList<Integer> getWinnersOfStateVector(IVector s, ElectionParameters params) {
        int length = !params.canAbstain() ? s.getLength() : s.getLength() -1;
        ArrayList<Integer> res = new ArrayList<>();
        int maxVotes = 0;
        for (int i=0; i< length; i++){
            if (s.get(i) > maxVotes) {
                maxVotes = s.get(i);
                res.clear();
                res.add(i+1);
            }
            else if (s.get(i) == maxVotes){
                res.add(i+1);
            }
        }
        return res;
    }

    @Override
    public IVector compilationFunction(IVector state, IVector vote, ElectionParameters params) {
        //preferences in vote ordered lexicographically
        //if abstention is possible, there is an abstention element at the end of scorevectors
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;
        IVector res = null;
        int block = (vote.getLength() - absCounter) / altCount;
        boolean done = false;
        //done should be true when we've seen the one, as plurality allows for only one vote (ie entry with value 1)
        //we only need to check the EVector positions for which we have a possibility of having one,
        //as described by the evector function.
        for (int cNo = 0; cNo < altCount && !done; cNo++) {
            int vectorIndex = cNo*block;
            if (vote.get(vectorIndex) == 1) {
                int oldValue = state.get(cNo);
                res = state.cloneAndSet(cNo, oldValue + 1);
                done = true;
            }
        }
        // if abstention, res will still be null, so need to deal with that
        if (abstain && !done) {
            int absIndex = state.getLength() - 1;
            int oldValue = state.get(absIndex);
            res = state.cloneAndSet(absIndex, oldValue + 1 );
        }
        return res;
    }

    @Override
    public List<IVector> generateStatesForLevel(int level, ElectionParameters params) {
        return generateUniqueScoresAtLevel(level, getCompilationStateSize(params));
    }

    @Override
    public int getCompilationStateSize(ElectionParameters params) {
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;
        return altCount + absCounter;
    }

    @Override
    public ArrayList<IVector> generateEVectors(ElectionParameters params) {
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;

        int eSize = factorial(altCount) + absCounter;
        int block = (eSize - absCounter) / altCount;

        ArrayList<IVector> res = new ArrayList<>();
        IVector zeroIVector = new Vector(eSize);

        for (int j = 0; j < eSize; j++) {
            //Only set e to 1 once for each voter (once per altCount elements)
            if (j % block == 0) {
                // if abstention is false, then this will return true once per each candidate
                // if true, then it will return true once per each cand.
                // and also once at the end, representing abstention
                IVector e = zeroIVector.cloneAndSet(j, 1);
                res.add(e);
            }
        }
        return res;
    }

    private ArrayList<Integer> calcWinnersOfPrefVectors(IVector s, int numAlternatives, int absCounter) {
        //only vectors passed in are vote vectors
        //so we know where to find the votes
        ArrayList<Integer> res = new ArrayList<>();
        int block = (s.getLength() - absCounter)/ numAlternatives;
        int maxVotes = 0;
        for (int i = 1; i <= numAlternatives; i++) {
            int cVotes = s.get(block*(i-1));
            if (cVotes > maxVotes) {
                res.clear();
                res.add(i);
                maxVotes = cVotes;
            } else if (cVotes != 0 && cVotes == maxVotes) {
                res.add(i);
            }
        }
        return res;
    }

    public ArrayList<Integer> getWinnersOfScoreVector(ScoreVector scores, ElectionParameters params) {
        ArrayList<Integer> winners = new ArrayList<>();
        int maxVotes = 0;
        for (int candidate = 1; candidate <= scores.getLength(); candidate++) {
            if (scores.getCandidate(candidate) > maxVotes) {
                winners.clear(); winners.add(candidate); maxVotes = scores.getCandidate(candidate);
            }
            else if (scores.getCandidate(candidate) == maxVotes) {
                winners.add(candidate);
            }
        }
        return winners;
    }

    private int factorial(int n) {
        int fact = 1;
        for (int j = 1; j <=n; j++) fact *= j;
        return fact;
    }

    private List<IVector> generateUniqueScoresAtLevel(int level, int size) {
        //DO NOT PUT ZEROES AS A MAPPING TO SCORE VECTORS
        assert (level >= 1);
        List<IVector> scores = new LinkedList<IVector>();
        //We will use Guava's ordered permutation methods for this
        //To do that we represent the problem as permutations of a string of level-1 1's and size-1 zeroes
        //and then splitting the arrays on zeroes
        //setting each candidate's vote count to the number of ones in its split.

        ImmutableList.Builder<Integer> builder = new ImmutableList.Builder<Integer>();
        for (int i = 0; i < level -1; i++) builder.add(1);
        for (int i = level -1; i < level + size -2; i++) builder.add(0);
        Collection<List<Integer>> perms = Collections2.orderedPermutations(builder.build());
        for (List<Integer> perm : perms) {
            int[] scoreCounts = new int[size];
            int index = 0; int count = 0; boolean done = false;
            for (int i=0; i< perm.size() && !done; i++) {
                if (perm.get(i) == 1) count += 1;
                else {
                    scoreCounts[index] = count;
                    index += 1;
                    count = 0;
                    if (index == size - 1) {
                        // rest is abstention, just write it down and set done
                        scoreCounts[index] = perm.size() - i -1;
                        done = true;
                    }
                }
            }
            scores.add(new Vector(scoreCounts));
        }
        return scores;
    }

	@Override
	public int getNumberOfVotesPerVoter(ElectionParameters params) {
		return 1;
	}

	@Override
	public String getVoteDetailsAsString(IVector s, int candidates, boolean abstentions) {
		int factAltCount = factorial(candidates);
		if (s.getLength() != factAltCount + (abstentions ? 1 : 0)) return "error";
		
		if (abstentions && s.get(s.getLength()-1) == 1) return "Abstention";
		
		int block = (factAltCount) / candidates;
		boolean done = false;
		String res = null;
		for (int i = 0; i < candidates && !done; i++) {
            int vectorIndex = i*block;
            if (s.get(vectorIndex) == 1) {
                res = "Candidate " + Integer.toString(i+1);
                done = true;
            }
        }
		return res;
	}
}

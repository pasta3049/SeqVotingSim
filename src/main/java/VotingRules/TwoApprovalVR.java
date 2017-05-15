package VotingRules;

import Elections.ElectionParameters;
import Model.*;
import Model.Vector;

import java.util.*;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;


/**
 * Created by Morgan King
 *
 * This class models 2 approval voting
 */
public class TwoApprovalVR implements VotingRule {

    private ArrayList<Integer> scoringVector;

    public TwoApprovalVR(int candidates) {
        scoringVector = new ArrayList<>(candidates);
        for (int i = 0; i < candidates; i++) {
        	scoringVector.add(0);
        }
        scoringVector.set(0,1);
        scoringVector.set(1, 1);
    }

    @Override
    public IVector voteTruthful(Preferences pref) {
    	IVector res = new Model.Vector(scoringVector.size());
    	res = res.cloneAndSet(pref.getNthPreference(1)-1, 1);
    	res = res.cloneAndSet(pref.getNthPreference(2)-1, 1);
    	return res;
        //return vote(pref.getNthPreference(1));
    }

    @Override
    public IVector vote(int candidate) { //does not make sense in 2 approval
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
        } else {
            return calcWinnersOfPrefVectors(s, params.numberOfCandidates(), 1);
        }
    }

    @Override
    public ArrayList<Integer> getWinnersOfStateVector(IVector s, ElectionParameters params) {
        int length = !params.canAbstain() ? s.getLength() : s.getLength() -1;
        ArrayList<Integer> res = new ArrayList<>();
        int maxVotes = 0;
        for (int i=0; i< length; i++) {
            if (s.get(i) > maxVotes) {
                maxVotes = s.get(i);
                res.clear();
                res.add(i+1);
            } else if (s.get(i) == maxVotes) {
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
        int numberOfOrderedPairs = altCount * (altCount-1);
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;
        IVector res = null;
        int block = (vote.getLength() - absCounter) / numberOfOrderedPairs; //changed by ug14mk
        boolean done = false;
        //done should be true when we've seen the one
        //we only need to check the EVector positions for which we have a possibility of having one,
        //as described by the evector function.
        
        
        for (int i = 0; i < altCount && !done; i++) {
        	for (int j = i; j < altCount - 1 && !done; j++) {
        		int vectorIndex = (i * (altCount - 1) + j) * block;
        		if (vote.get(vectorIndex) == 1) {
        			int oldValueI = state.get(i);
        			int oldValueJ = state.get(j+1); //j+1 used as there are no vote profiles beginning with the same candidate twice - so entries below this are off by 1
        			res = state.cloneAndSet(i, oldValueI+1);
        			res = res.cloneAndSet(j+1, oldValueJ+1);
        			done = true;
        		}
        	}
        }
        
        // if abstention, res will still be null, so need to deal with that
        if (abstain && !done) {
            int absIndex = state.getLength() - 1;
            int oldValue = state.get(absIndex);
            res = state.cloneAndSet(absIndex, oldValue + 2 );
        }
        return res;
    }

    @Override
    public List<IVector> generateStatesForLevel(int level, ElectionParameters params) {
        return generateUniqueScoresAtLevel(level, getCompilationStateSize(params), params.canAbstain()); //ug14mk
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
    	//ug14mk
        int altCount = params.numberOfCandidates();
        int numberOfOrderedPairs = altCount * (altCount - 1);//N.B. order is actually irrelevant - so the number of EVectors will be half of this
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;

        int eSize = factorial(altCount) + absCounter;
        int block = (eSize - absCounter) / numberOfOrderedPairs;

        ArrayList<IVector> res = new ArrayList<>();
        IVector zeroIVector = new Vector(eSize);

        /*for (int j = 0; j < eSize; j++) {
            //Only set e to 1 once for each voter (once per altCount elements)
            if (j % block == 0) {
                // if abstention is false, then this will return true once per each candidate
                // if true, then it will return true once per each cand.
                // and also once at the end, representing abstention
                IVector e = zeroIVector.cloneAndSet(j, 1);
                res.add(e);
            }
        }*/
        
        for (int i = 0; i < altCount; i++) {
        	//start at i so that votes beginning (2,1) for example are not used since they are equivalent to votes beginning (1,2)
        	for (int j = i; j < altCount-1; j++) {
        		IVector e = zeroIVector.cloneAndSet((i * (altCount-1) + j) * block, 1);
        		res.add(e);
        	}
        }
        
        if (abstain) {
        	IVector e = zeroIVector.cloneAndSet(eSize-1, 1);
        	res.add(e);
        }
        
        return res;
    }

    private ArrayList<Integer> calcWinnersOfPrefVectors(IVector s, int numAlternatives, int absCounter) {
        //only vectors passed in are vote vectors
        //so we know where to find the votes
        ArrayList<Integer> res = new ArrayList<>();
        int largeBlock = (s.getLength() - absCounter)/ numAlternatives;
        int smallBlock = (s.getLength() - absCounter)/ (numAlternatives * (numAlternatives - 1));
        int maxVotes = 0;
        for (int i = 1; i <= numAlternatives; i++) {
        	int zeroIndexedCNo = i-1;
        	int cVotes = 0;
        	for (int j = 0; j < zeroIndexedCNo; j++) {
        		cVotes+=s.get(j*largeBlock + (zeroIndexedCNo - 1) * smallBlock);
        	}
        	for (int j = zeroIndexedCNo; j < numAlternatives-1; j++) {
        		cVotes+=s.get(zeroIndexedCNo * largeBlock + j * smallBlock);
        	}
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

    private List<IVector> generateUniqueScoresAtLevel(int level, int size, boolean abstain) {
        //Levels less than one are NOT permitted, since these would represent negative numbers of voters.
        assert (level >= 1);
        List<IVector> scores = new LinkedList<IVector>();
        //We will use Guava's ordered permutation methods for this
        //To do that we represent the problem as permutations of a string of 2*(level-1) 1's and size-1 zeroes
        //and then splitting the arrays on zeroes
        //setting each candidate's vote count to the number of ones in its split
        //and rejecting entries where any candidate has more than (level-1) votes
        //since these are not possible as a voter cannot vote for the same candidate twice
        


        ImmutableList.Builder<Integer> builder = new ImmutableList.Builder<Integer>();
        for (int i = 0; i < 2* (level -1); i++) builder.add(1);
        for (int i = 2 * (level -1); i < 2 * (level -1) + size - 1; i++) builder.add(0);
        Collection<List<Integer>> perms = Collections2.orderedPermutations(builder.build());
        for (List<Integer> perm : perms) {
            int[] scoreCounts = new int[size];
            int index = 0; int count = 0; boolean done = false; boolean valid = true; int maxCount = 0;
            for (int i=0; i< perm.size() && !done && valid; i++) {
                if (perm.get(i) == 1) { 
                	count += 1;
                	if (count > (level-1)) valid = false;
                	if (count > maxCount) maxCount = count;
                } else {
                    scoreCounts[index] = count;
                    index += 1;
                    count = 0;
                    if (index == size - 1) {
                        // rest is abstention or last candidate, just write it down and set done
                        scoreCounts[index] = perm.size() - i -1;
                        if (!abstain && (scoreCounts[index] > level -1)) valid = false;
                        else if (abstain && (scoreCounts[index] % 2 == 1)) valid = false;
                        
                        if (abstain && maxCount > ((level-1) - (scoreCounts[index]/2))) valid=false;
                        done = true;
                    }
                }
            }
            if (valid) scores.add(new Vector(scoreCounts));
        }
        return scores;
    }

	@Override
	public int getNumberOfVotesPerVoter(ElectionParameters params) {
		return 2;
	}

	@Override
	public String getVoteDetailsAsString(IVector s, int candidates, boolean abstentions) {
		int factAltCount = factorial(candidates);
		if (s.getLength() != factAltCount + (abstentions ? 1 : 0)) return "error";
		
		if (abstentions && s.get(s.getLength()-1) == 1) return "Abstention";
		
		int block = (factAltCount) / (candidates*(candidates-1));
		boolean done = false;
		String res = null;
		
		for (int i = 0; i < candidates && !done; i++) {
        	for (int j = i; j < candidates - 1 && !done; j++) {
        		int vectorIndex = (i * (candidates - 1) + j) * block;
        		if (s.get(vectorIndex) == 1) {
        			res = "Candidates " + Integer.toString(i+1) + " and " + Integer.toString(j+1+1);
        			done = true;
        		}
        	}
        }
		
		return res;
	}
    
}

package VotingRules;

import Elections.ElectionParameters;
import Model.*;
import Model.Vector;

import java.util.*;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;


/**
 * Created by ug14mk on 02/12/2016.
 *
 * This class models borda count voting.
 */
public class BordaVR implements VotingRule {

    private ArrayList<Integer> scoringVector;
    private byte[][] points;

    public BordaVR(int candidates) {
        scoringVector = new ArrayList<>(candidates);
        for (int i = 0; i < candidates; i++) scoringVector.add(0);
        scoringVector.set(0,1);
        generatePointsForLexicographicList(candidates);
    }
    
    private void generatePointsForLexicographicList(int candidates) {
    	points = new byte[candidates][factorial(candidates)];
    	points[candidates-1][0] = 1;
    	for (byte cdts = 2; cdts <= candidates; cdts++) {
    		int block = factorial(cdts-1);
    		for (int k = 0; k < block; k++) {
    			points[candidates-cdts][k] = cdts;
    		}
    		for (int i = 1; i < cdts; i++) {
    			for (int j = 0; j < i; j++) {
    				for (int k = 0; k < block; k++) {
    					points[candidates-cdts+j][block*i+k] = points[candidates-cdts+j+1][k];
    				}
    			}
    			for (int k = 0; k < block; k++) {
    				points[candidates-cdts+i][block*i+k] = cdts;
    			}
    			for (int j = i+1; j < cdts; j++) {
    				for (int k = 0; k < block; k++) {
    					points[candidates-cdts+j][block*i+k] = points[candidates-cdts+j][k];
    				}
    			}
    		}
    	}
    }

    @Override
    public IVector voteTruthful(Preferences pref) { //ug14mk
        //return vote(pref.getNthPreference(1));
        int altCount = scoringVector.size();
        IVector res = new Model.Vector(scoringVector.size());
        for (int i = 1; i<=altCount; i++) {
        	//altCount points for first choice, altCount-1 for next choice etc.
            res=res.cloneAndSet(pref.getNthPreference(i)-1, altCount-i+1);
        }
        return res;
    }

    @Override
    public IVector vote(int candidate) { //does not make sense in Borda Count voting
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
    	int length = s.getLength();
    	if (length - (params.canAbstain() ? 1 : 0) == params.numberOfCandidates()) {
    		//s is a vector of truthful votes
    		return getWinnersOfStateVector(s, params);
    	}
    	else if (!params.canAbstain()) {
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
        //ug14mk
    	//preferences in vote ordered lexicographically
        //if abstention is possible, there is an abstention element at the end of scorevectors
        int altCount = params.numberOfCandidates();
        int factAltCount = factorial(altCount);
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;
        IVector res = null;
        boolean done = false;
        //done should be true when we've seen the one, as there should only be one entry with value 1
        //unfortunately a 1 may appear in any of the (O(n!)) postions
        for (int i = 0; i < factAltCount && !done; i++) {
            if (vote.get(i) == 1) {
            	res = state;
            	for (int j = 0; j < altCount; j++){
            		int oldValue = state.get(j);
            		res = res.cloneAndSet(j, oldValue + points[j][i]);
            	}
                done = true;
            }
        }
        // if abstention, res will still be null, so need to deal with that
        if (abstain && !done) {
            int absIndex = state.getLength() - 1;
            int oldValue = state.get(absIndex);
            res = state.cloneAndSet(absIndex, oldValue + ((altCount*(altCount+1))/2) );
        }
        return res;
    }

    @Override
    public List<IVector> generateStatesForLevel(int level, ElectionParameters params) {
        return generateUniqueScoresAtLevel(level, getCompilationStateSize(params), params.canAbstain());
    }

    @Override
    public int getCompilationStateSize(ElectionParameters params) {
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;
        return altCount + absCounter;
    }

    @Override
    public ArrayList<IVector> generateEVectors(ElectionParameters params) { //ug14mk
        int altCount = params.numberOfCandidates();
        boolean abstain = params.canAbstain();
        int absCounter = abstain ? 1 : 0;

        int eSize = factorial(altCount) + absCounter;
        ArrayList<IVector> res = new ArrayList<>();
        IVector zeroIVector = new Vector(eSize);

        //there are n! possible orderings of candidates that could be selected under Borda Count!
        for (int j = 0; j < eSize; j++) {
            IVector e = zeroIVector.cloneAndSet(j, 1);
            res.add(e);
        }
        return res;
    }

    private ArrayList<Integer> calcWinnersOfPrefVectors(IVector s, int numAlternatives, int absCounter) {
        //ug14mk
    	//only vectors passed in are vote vectors
        //so we know where to find the votes
        ArrayList<Integer> res = new ArrayList<>();
        int maxVotes = 0;
        int[] scores = new int[numAlternatives];
        for (int i = 0; i < factorial(numAlternatives); i++) {
        	if (s.get(i) != 0) {
        		for (int j = 0; j < numAlternatives; j++) {
        			scores[j] += s.get(i) * points[j][i];
        		}
        	}
        }
        for (int i = 1; i <= numAlternatives; i++) {
            int cVotes = scores[i-1];
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

    private List<IVector> generateUniqueScoresAtLevel(int level, int size, boolean abstentions) { //ug14mk
        //DO NOT PUT ZEROES AS A MAPPING TO SCORE VECTORS
        assert (level >= 1);
        List<IVector> scores = new LinkedList<IVector>();
        //We will use Guava's ordered permutation methods for this
        //To do that we represent the problem as permutations of a string of level-1 1's and size-1 zeroes
        //and then splitting the arrays on zeroes
        //setting each candidate's vote count to the number of ones in its split.
        
        //the number of voters is level-1, each has cast votes totalling (size*(size+1))/2
        //so ((level-1)*size*(size+1))/2 ones are required (assuming no abstentions)
        
        // If abstentions are permitted then we must consider each possible number of actual voters (i.e. from 0 to level-1)
        // also, size will be one greater than the number of candidates
        
        int numCandidates = abstentions ? (size-1) : size;
        int minVoters = abstentions ? 0 : (level-1);
        int maxVoters = level-1;
        for (int voters = minVoters; voters <= maxVoters; voters++) {
        	int rho = (voters*numCandidates*(numCandidates+1))/2;
        	ImmutableList.Builder<Integer> builder = new ImmutableList.Builder<Integer>();
        	for (int i = 0; i < rho; i++) builder.add(1);
        	for (int i = rho; i < rho + numCandidates -1; i++) builder.add(0);
        	Collection<List<Integer>> perms = Collections2.orderedPermutations(builder.build());
        	for (List<Integer> perm : perms) {
        		int[] scoreCounts = new int[size];
        		int index = 0; int count = 0; boolean done = false; boolean valid = true;
        		for (int i=0; i< perm.size() && !done && valid; i++) {
        			if (perm.get(i) == 1) {
        				count += 1;
        				if (count > numCandidates*voters) valid = false;
        			} else {
        				if (count < voters) valid = false;
        				scoreCounts[index] = count;
        				index += 1;
        				count = 0;
        				if (index == numCandidates - 1) {
        					// rest is last candidate, just write it down and set done
        					scoreCounts[index] = perm.size() - i -1;
        					if (scoreCounts[index] > numCandidates*voters) valid = false;
        					if (scoreCounts[index] < voters) valid = false;
        					done = true;
        					if (abstentions) {
        						//we must add put the value for the abstention as the end of the array
        						scoreCounts[size-1] = ((level-1) - voters) * (numCandidates * (numCandidates+1)) /2;
        					}
        				}
        			}
        		}
        		if (valid) scores.add(new Vector(scoreCounts));
        	}
        }
        return scores;
    }

	@Override
	public int getNumberOfVotesPerVoter(ElectionParameters params) {
		int c = params.numberOfCandidates();
		return c*(c+1)/2;
	}

	@Override
	public String getVoteDetailsAsString(IVector s, int candidates, boolean abstentions) {
		int factAltCount = factorial(candidates);
		if (s.getLength() != factAltCount + (abstentions ? 1 : 0)) return "error";
		if (candidates != points.length) return "incorrect borda rule for number of candidates";
		
		if (abstentions && s.get(s.getLength()-1) == 1) return "Abstention";
		
		String res = "";
		boolean done = false;
		for (int i = 0; i < factAltCount && !done; i++) {
            if (s.get(i) == 1) {
            	for (int j = 0; j < candidates; j++){
            		res = res + Integer.toString(points[j][i]);
            	}
                done = true;
            }
        }
		return res;
	}
}

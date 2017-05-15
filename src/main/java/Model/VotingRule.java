package Model;

import Elections.ElectionParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AriApar on 13/11/2015.
 * Modified by ug14mk on 01/02/2017
 */
public interface VotingRule {
    //Should return a vote vector
    IVector voteTruthful(Preferences pref);
    //Should return a vote vector
    IVector vote(int candidate);
    //Returns winner(s) of the given score vector
    ArrayList<Integer> getWinnersOfScoreVector(ScoreVector s, ElectionParameters params);
    //Returns winner(s) of a given vote vector
    ArrayList<Integer> getWinnersOfVoteVector(IVector s, ElectionParameters params);
    //Returns winner(s) of a state vector
    ArrayList<Integer> getWinnersOfStateVector(IVector s, ElectionParameters params);
    //Returns the new state once the new vote is applied to the old state
    IVector compilationFunction(IVector state, IVector vote, ElectionParameters params);
    //Returns a list of vectors representing the states for level i,
    //where level = count of votes cast + 1 (therefore 1-indexed).
    List<IVector> generateStatesForLevel(int level, ElectionParameters params);
    //Returns the length of the state vectors used
    int getCompilationStateSize(ElectionParameters params);
    //Returns the vectors that represent each possible vote a voter could cast
    ArrayList<IVector> generateEVectors(ElectionParameters params);
    //Returns the number of votes cast by each voter (e.g. 1 for plurality, 2 for 2-approval)
    int getNumberOfVotesPerVoter(ElectionParameters params);
    //Converts a vote vector to a string representing the vote cast
    String getVoteDetailsAsString(IVector s, int candidates, boolean abstentions);
}

package Elections;

import Model.*;
import Model.Vector;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by AriApar on 01/12/2015.
 * Modified by ug14mk
 */
public class DPElection extends Election{
    //Dynamic programming election that follows Algorithm 1 from Stackelberg paper.
    private boolean abstention;
    private ArrayList<Voter> voters;
    //private Map<Vector, List<DPInfo>>[] mapArr;

    private class Triple<X, Y, Z> {
        public final X first;
        public final Y second;
        public final Z third;
        public Triple(X x, Y y, Z z) {
            this.first = x;
            this.second = y;
            third = z;
        }
    }

    protected DPElection(ElectionParameters params) {
        setElection(params);
        abstention = params.canAbstain();
        voters = getVoters();
        //mapArr = (Map<Vector, List<DPInfo>>[]) Array.newInstance(Object2ObjectOpenHashMap.class, voters.size() +1);
    }

    private String getFileName(int stageNo) {
        return "tmp" + Thread.currentThread().getId() + "/map_Stage_" + stageNo + ".ser";
    }

    private boolean saveMapForStage(Map<IVector, List<DPInfo>> map, int stageNo) {
        //mapArr[stageNo] = map;
        try
        {
            File f = new File(getFileName(stageNo));
            f.getParentFile().mkdirs();
            BinIO.storeObject(map, f);
        }
        catch(IOException i)
        {
            i.printStackTrace();
            return false;
        }
        return true;
    }

    private Map<IVector, List<DPInfo>> getMapForStage(int stageNo) {
        //return mapArr[stageNo];
        try
        {
            File f = new File(getFileName(stageNo));
            Map<IVector, List<DPInfo>> map = (Map<IVector, List<DPInfo>>) BinIO.loadObject(f);
            return map;
        }
        catch(IOException i)
        {
            i.printStackTrace();
            return null;
        }
        catch(ClassNotFoundException c)
        {
            System.out.println("Map class not found");
            c.printStackTrace();
            return null;
        }
    }

    public ArrayList<ElectionState> findNE() throws Exception{
        int numVoters = voters.size();
        VotingRule votingRule = getParams().getRule();
        int numAlternatives = getParams().numberOfCandidates();
        //stars and bars calculation prep
        int numBoxes = abstention ? numAlternatives + 1 : numAlternatives; //only place this is used is in initializing the size of a hashMap so this value affects the efficiency but not the results of the program

        Object2ObjectOpenHashMap<IVector, List<DPInfo>> gMap =
                new Object2ObjectOpenHashMap<>(IntMath.binomial(numVoters + numBoxes - 2, numBoxes -1));

        final ArrayList<IVector> EIVector =  votingRule.generateEVectors(getParams());
        List<IVector> states;
        for (int j = numVoters +1; j >=1; j--) {
            states = votingRule.generateStatesForLevel(j, getParams());

            Object2ObjectOpenHashMap<IVector, List<DPInfo>> g =
                    new Object2ObjectOpenHashMap<>(IntMath.binomial(j + numBoxes - 2, numBoxes -1));
            if (j == numVoters + 1) {
                for (IVector s : states) {
                    getWinnersBaseCase(g, s);
                }
            }
            else {
                for (IVector s : states) {
                    getWinnersElseCase(g, gMap, EIVector, j, s);
                }
                writeToFile(gMap, j);
            }
            gMap = g;
            gMap.trim();
        }
        int stateSize = votingRule.getCompilationStateSize(getParams());
        return generateWinnerStates(gMap.get(generateZeroVector(stateSize)), numAlternatives, stateSize);
    }

    private Set<IVector> shrinkStatesBy1(Set<IVector> states) {
        Set<IVector> res = new ObjectOpenHashSet<>(states.size());
        for (IVector state : states) {
            for (int i = 0; i < state.getLength(); i++) {
                int value = state.get(i);
                if (value != 0) res.add(state.cloneAndSet(i, value - 1));
            }
        }
        return res;
    }

    private ArrayList<ElectionState> generateWinnerStates(List<DPInfo> dpInfos, int numAlternatives,
                                                          int stateSize) {
        Queue<Triple<ElectionState, DPInfo, IVector>> q = new ArrayDeque<>();
        ArrayList<ElectionState> res = new ArrayList<>();
        for (DPInfo item : dpInfos) {
            IVector key = generateZeroVector(stateSize);
            ElectionState initState = new ElectionState(numAlternatives);
            q.add(new Triple(initState, item, key));
        }
        Map<IVector, List<DPInfo>> currMap = new Object2ObjectOpenHashMap<>();
        int prevSum = -1; //level counter, so we don't reload the same level map
        //Initial queue done, now start processing it
        while (!q.isEmpty()) {
            Triple<ElectionState, DPInfo, IVector> tuple = q.remove();
            ElectionState state = tuple.first;
            DPInfo info = tuple.second;
            IVector key = tuple.third;
            int nvpv = getParams().getRule().getNumberOfVotesPerVoter(getParams());

            if (info.getE() != null) {
                IVector e = info.getE();
                ArrayList<Integer> candArray = getParams().getRule().getWinnersOfVoteVector(e, getParams());
                ElectionState newState = null;
                //FIXME: Correct this block of code
                if (candArray.size() == 0) {
                    //abstention
                    newState = prepNewState(state, 0);
                }
                else {
                    //int candidate = candArray.get(0);
                    //newState = prepNewState(state, candidate);
                    newState = prepNewState_revised(state, e);
                }
                key = getParams().getRule().compilationFunction(key, e, getParams());
                //optimisation
                if(key.getSum() != prevSum) {
                    int sum = key.getSum();
                    //currMap = getMapForStage(sum);
                    currMap = getMapForStage(sum/nvpv);
                    prevSum = sum;
                }
                List<DPInfo> newInfos = currMap.get(key);
                for (DPInfo item : newInfos) {
                    q.add(new Triple<>(newState, item, key));
                }
            }
            else {
                // key null, put the resulting state in the return array
                res.add(state);
            }
        }
        return res;
    }

    private void writeToFile(Map<IVector, List<DPInfo>> g, int j) {
        //Writes g to file and deletes it from the array
        saveMapForStage(g, j);
    }

    private ElectionState prepNewState(ElectionState state, int candidate) {
        //Get old electionstate values to generate new one
        ScoreVector vector = state.getCurrentScores();
        ArrayList<Integer> votes = state.getCurrentVotes();
        votes.add(candidate);

        if (candidate == 0) {
            //abstention
            return new ElectionState(vector, state.getCurrentWinners(), votes);
        }
        else {
            //add vote to scorevector
            vector = vector.cloneAndSetCandidate(candidate, vector.getCandidate(candidate) + 1);
            return new ElectionState(vector, getWinnersOfScoreVector(vector), votes);
        }
    }
    
    private ElectionState prepNewState_revised(ElectionState state, IVector e) {
    	ScoreVector vector = state.getCurrentScores();
    	ArrayList<Integer> votes = state.getCurrentVotes();
        ArrayList<Integer> candArray = getParams().getRule().getWinnersOfVoteVector(e, getParams());
        int candidate;
        if (candArray.size() > 00) candidate = candArray.get(0); else candidate = 0;
        votes.add(candidate); //TODO: change the definition of ElectionState to include full details of the votes
        
        if (candidate == 0) {
        	return new ElectionState(vector, state.getCurrentWinners(), votes);
        } else {
        	vector = (ScoreVector) getParams().getRule().compilationFunction(vector, e, getParams());
        	return new ElectionState(vector, getWinnersOfScoreVector(vector), votes);
        }
    }

    private void getWinnersElseCase(Map<IVector, List<DPInfo>> g,
                                    Map<IVector, List<DPInfo>> gLookup,
                                    ArrayList<IVector> EIVector,
                                    int j, IVector s) {
    	//System.err.println("REDOS");
        BigDecimal bestPref = BigDecimal.valueOf(Double.MIN_VALUE);
        BigDecimal bestUtil = BigDecimal.valueOf(Double.MIN_VALUE);
        ArrayList<IVector> optimum_e = new ArrayList<>();
        boolean hasCost = getParams().hasCost();

        for (IVector e : EIVector) {
            boolean abs = (abstention && e.get(e.getLength()-1) == 1);
            BigDecimal cost = (!abs && hasCost) ? Voter.COST_OF_VOTING : BigDecimal.ZERO;
            Voter v = voters.get(j-1);

            IVector gSum = getParams().getRule().compilationFunction(s, e, getParams());//TODO: Check if this is correct

            List<DPInfo> cStates = gLookup.get(gSum);
            ArrayList<Integer> cWinners = cStates.get(0).getWinners();
            //because we only need the rank of the current winners, getting only one is fine as
            //all winners will have same rank if they're all optimal

            int comparison = v.compareOverallUtilityForWinnersToCurrentBest(cWinners, cost, bestPref);

            if(comparison == 0) {
                // add this to current set of optimum e, if individual preference is also same
                int utilComparison = v.compareIndUtilityForVoteToCurrentBest(e, bestUtil);
                if (utilComparison == 0) { 
                	optimum_e.add(e);
                }
                // if not, if new indUtil more, make this the new optimum
                else if (utilComparison > 0) {
                    optimum_e.clear(); optimum_e.add(e); bestUtil = v.getIndUtilityForVote(e);
                }
            }
            else if (comparison > 0) {
                // new util more, trash old optimum, add this to new
                optimum_e.clear(); optimum_e.add(e);
                bestPref = v.getCombinedUtilityForCandidates(cWinners).subtract(cost);
                bestUtil = v.getIndUtilityForVote(e);
            }
        }
        updateMappingWithOptima(g, gLookup, s, optimum_e);
    }

    private int getNonAbstentionCount(IVector s) {
        int sum = 0;
        for (int i = 0; i < s.getLength() -1; i++) {
            sum += s.get(i);
        }
        return sum;
    }
    private void updateMappingWithOptima(Map<IVector, List<DPInfo>> g,
                                         Map<IVector, List<DPInfo>> gLookup,
                                         IVector s, ArrayList<IVector> optimum_e) {
        Set<IVector> seen = new ObjectOpenHashSet<>();
        for (IVector e : optimum_e) {
            IVector sPlusE = getParams().getRule().compilationFunction(s, e, getParams());
            if (!seen.contains(sPlusE)) {
                seen.add(sPlusE);
                List<DPInfo> g_of_sPlusE = gLookup.get(sPlusE);
                //prepare new DPInfo's
                g_of_sPlusE = prepNewInfos(g_of_sPlusE, e);
                if (g.containsKey(s)) {
                    g_of_sPlusE.addAll(g.get(s));
                    g.put(s, g_of_sPlusE);
                }
                else {
                    g.put(s, g_of_sPlusE);
                }
            }
        }
    }

    private List<DPInfo> prepNewInfos(List<DPInfo> g_of_sPlusE, IVector e) {
        //Same winners, new e for profiling later on
        List<DPInfo> res = new ArrayList<>();
        for (DPInfo item : g_of_sPlusE) {
            res.add(new DPInfo(item.getWinners(), e));
        }
        return res;
    }

    private void getWinnersBaseCase(Map<IVector, List<DPInfo>> g, IVector s) {
        List<DPInfo> res = new ArrayList<>();
        ArrayList<Integer> winners = getParams().getRule().getWinnersOfStateVector(s, getParams());
        res.add(new DPInfo(winners, null));
        g.put(s, res);
    }

    private ArrayList<IVector> generateEVectors(int size) {
        ArrayList<IVector> res = new ArrayList<>(size);
        IVector zeroIVector = generateZeroVector(size);
        for (int j = 0; j < size; j++) {
            IVector e = zeroIVector.cloneAndSet(j, 1);
            res.add(e);
        }
        return res;
    }

    private IVector generateZeroVector(int size) {
        return new Vector(size);
    }
}

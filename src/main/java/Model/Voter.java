package Model;

import Elections.ElectionParameters;
import Elections.ElectionState;
import util.Node;
import util.Tree;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by AriApar on 26/11/2015.
 */
public class Voter {

    private ElectionParameters params;
    private int voterId; //1..N
    public static final BigDecimal COST_OF_VOTING = BigDecimal.valueOf(0.0000000001D);

    public Voter(int voterId, ElectionParameters params) {
        this.voterId = voterId;
        this.params = params;
    }

    //Truthful vote
    public IVector vote() {
        return getRule().voteTruthful(getPrefList().getPreferencesForVoter(voterId));
    }

    public IVector vote(int candidate) {
        return getRule().vote(candidate);
    }

    public BigDecimal getCombinedUtilityForCandidates(ArrayList<Integer> candidates) {
        if (candidates.size() == 0) return BigDecimal.valueOf(Double.MIN_VALUE);
        int res = 0;
        for (Integer c : candidates) {
            res += getUtilityForCandidate(c);
        }
        BigDecimal result = BigDecimal.valueOf(res);
        result = result.divide(BigDecimal.valueOf(candidates.size()), 12, RoundingMode.HALF_UP);
        return result;
    }

    public Tree<ElectionState> chooseWhoToVote(Tree<ElectionState> root, int level) {
        ArrayList<Node<ElectionState>> currLevel = root.getNodesAtLevel(level);
        for (Node<ElectionState> currNode : currLevel) {
            ArrayList<Node<ElectionState>> toRemove = keepBestChild(currNode);
            currNode.removeChildren(toRemove);
        }
        return root;
    }

    public int compareOverallUtilityForWinnersToCurrentBest(ArrayList<Integer> winners,
                                                         BigDecimal cost, BigDecimal bestUtil)
    {
        BigDecimal cPref = getCombinedUtilityForCandidates(winners);
        cPref = cPref.subtract(cost);
        return cPref.compareTo(bestUtil);
    }

    public int compareIndUtilityForVoteToCurrentBest(IVector vote, BigDecimal bestUtil) {
        BigDecimal indUtil = getIndUtilityForVote(vote);
        return indUtil.compareTo(bestUtil);
    }

    public BigDecimal getIndUtilityForVote(IVector vote) {
        ArrayList<Integer> voteCast = params.getRule().getWinnersOfVoteVector(vote, params);
        return (voteCast.size() == 0) ? BigDecimal.ZERO :
                getCombinedUtilityForCandidates(voteCast);
    }

    private int getUtilityForCandidate(int candidate) {
        Preferences pref = getPrefList().getPreferencesForVoter(voterId);
        //laziness assumption: if abstaining gives you the same utility as voting, abstain.
        if (candidate == 0) throw new RuntimeException("Trying to get utility for cand 0, most likely" +
                "the costly voting is broken.");
        int rank = pref.getPreferenceOfCandidate(candidate);
        return (pref.length() - rank + 1) * 20;
    }

    private ArrayList<Node<ElectionState>> keepBestChild(Node<ElectionState> node) {
        ArrayList<Node<ElectionState>> children = node.getChildren();
        ArrayList<Node<ElectionState>> toRemove = new ArrayList<>();
        BigDecimal bestUtil = BigDecimal.valueOf(-1); boolean first = true;
        Node<ElectionState> bestChild = null;

        for (Node<ElectionState> child : children) {
            //check if this child is better than current best
            //if so, remove old best from children list, make this the best
            //otherwise, remove child from children list
            BigDecimal newUtil = calculateUtil(child);
            // isBetter returns the new utility if positive
            if (first ||  compare(newUtil, bestUtil) > 0) {
                //if we're first, set first to false
                //otherwise, add previous best to removal array
                if (!first)
                    toRemove.add(bestChild);
                else
                    first = false;
                //now set the new child to be best
                bestChild = child; bestUtil = newUtil;
            }
            else if (compare(newUtil, bestUtil) == 0) {
                bestChild = compareSameUtilChildren(toRemove, bestChild, child);
            }
            else {
                //worse, remove child
                toRemove.add(child);
            }
        }
        return toRemove;
    }

    private Node<ElectionState> compareSameUtilChildren(ArrayList<Node<ElectionState>> toRemove, Node<ElectionState> bestChild, Node<ElectionState> child) {
        //same rank child
        //keep the one which you like the most
        int cCand = child.getData().getLastVoteCast();
        int bCand = bestChild.getData().getLastVoteCast();
        //if the child is abstaining, gets no utility for truth-bias
        //otherwise if best is abstaining, or current cand is better than best so far
        //swap current and best
        //else, child is worse again, so remove it.
        if (cCand == 0) toRemove.add(child);
        else if (bCand == 0 || getUtilityForCandidate(cCand) > getUtilityForCandidate(bCand)) {
            toRemove.add(bestChild); bestChild = child;
        }
        else {
            toRemove.add(child);
        }
        return bestChild;
    }

    //returns 0 if same, the new best util if better, <0 if worse
    private BigDecimal calculateUtil(Node<ElectionState> candidate) {
        //get the vote cast, we need to use it for costly voting
        int voteCast = candidate.getData().getLastVoteCast();
        //go to the end of its branch to see what the result is
        ArrayList<Integer> cWinners = getWinnersOfBranch(candidate);
        // we calculate the avg utility we get from the winners.
        BigDecimal cSum = getCombinedUtilityForCandidates(cWinners);
        //cost of voting
        if (params.hasCost() && voteCast != 0) cSum = cSum.subtract(COST_OF_VOTING);
        return cSum;
    }

    private int compare(BigDecimal newUtil, BigDecimal currUtil) {
        return newUtil.compareTo(currUtil);
    }

    private ArrayList<Integer> getWinnersOfBranch(Node<ElectionState> candidate) {
        Node<ElectionState> cLeaf = candidate;
        while (cLeaf.hasChild()) {
            ArrayList<Node<ElectionState>> chList = cLeaf.getChildren();
            cLeaf = chList.get(0);
        }
        return cLeaf.getData().getCurrentWinners();
    }

    private VotingRule getRule() {
        return params.getRule();
    }

    private PreferenceList getPrefList() {
        return params.getPref();
    }

}

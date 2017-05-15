package Elections;

import Model.PreferenceList;
import Model.VotingOrder;
import Model.VotingRule;

/**
 * Created by AriApar on 12/01/2016.
 */
public class ElectionParameters {

    private PreferenceList pref;
    private VotingOrder order;
    private VotingRule rule;
    private ElectionType type;

    public ElectionParameters(PreferenceList pref, VotingOrder order, VotingRule rule, ElectionType type) {
        this.pref = pref;
        this.order = order;
        this.rule = rule;
        this.type = type;
    }

    public PreferenceList getPref() {
        return pref;
    }

    public void setPref(PreferenceList pref) {
        this.pref = pref;
    }

    public VotingOrder getOrder() {
        return order;
    }

    public void setOrder(VotingOrder order) {
        this.order = order;
    }

    public VotingRule getRule() {
        return rule;
    }

    public void setRule(VotingRule rule) {
        this.rule = rule;
    }

    public ElectionType getType() {
        return type;
    }

    public void setType(ElectionType type) {
        this.type = type;
    }

    public boolean canAbstain() {
        return (hasCost() || type == ElectionType.GAMETREEWITHABS || type == ElectionType.DPWITHABS);
    }

    public boolean hasCost() {
        return type == ElectionType.GAMETREEWITHCOSTLYABS || type == ElectionType.DPWITHCOSTLYABS;
    }

    public int numberOfCandidates() {
        return pref.getNumCandidates();
    }

    public int numberOfVoters() {
        return pref.getNumVoters();
    }
}

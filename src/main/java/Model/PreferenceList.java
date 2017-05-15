package Model;

import java.util.*;

/**
 * Created by AriApar on 13/11/2015.
 *
 * This class encapsulates preference lists of all voters.
 * Voter and Candidate ID's passed should start with index 1.
 *
 *
 */
public class PreferenceList {

    private int alternatives;
    private int voters;
    private List<Preferences> preferenceList;

    /**
     * Creates an empty PreferenceList with a given number of voters and alternatives.
     * @param voters        number of voters
     * @param alternatives  number of alternatives
     */
    public PreferenceList(int voters, int alternatives) {
        this.alternatives = alternatives;
        this.voters = voters;
        preferenceList = new ArrayList<>(voters);
    }

    public PreferenceList(int[][] prefList) {
        this.alternatives = prefList[0].length;
        this.voters = prefList.length;
        this.preferenceList = new ArrayList<>(voters);
        for (int i = 0; i<voters; i++) {
            preferenceList.add(new Preferences(prefList[i]));
        }
    }

    public Preferences getPreferencesForVoter(int voterId) {
        return preferenceList.get(voterId -1);
    }

    public int getNthPreferenceOfVoter(int n, int voterId){
        return getPreferencesForVoter(voterId).getNthPreference(n);
    }

    public int getNumCandidates() {
        return alternatives;
    }

    public int getNumVoters() {
        return voters;
    }
}

package Testers;

import Elections.*;
import Model.PreferenceList;
import Model.VotingOrder;
import Model.VotingRule;
import VotingRules.PluralityVR;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by AriApar on 25/11/2015.
 */
public class TruthfulVotingTester extends AbstractTester{
    public static void main(String[] args) throws Exception{
        try {
            Scanner in = new Scanner(getFile("SmallPListSample"));
            int voters = in.nextInt();
            int candidates = in.nextInt();
            int[][] prefList = new int[voters][candidates];
            for (int i = 0; i<voters; i++){
                for (int j= 0; j<candidates; j++) {
                    prefList[i][j] = in.nextInt();
                }
            }

            PreferenceList pref = new PreferenceList(prefList);
            VotingOrder order = new VotingOrder(voters, true);
            VotingRule rule = new PluralityVR(candidates);

            ElectionParameters params = new ElectionParameters(pref, order, rule, ElectionType.TRUTHFUL);
            TruthfulElection e = (TruthfulElection) ElectionFactory.create(params);

            ArrayList<ElectionState> winners = e.findNE();

            printResults(order, winners);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

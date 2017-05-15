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
 * Created by AriApar on 02/12/2015.
 */
public class DPVotingTester extends AbstractTester {

    public static void main(String[] args) throws Exception{
        try {
            Scanner in = new Scanner(getFile("9x4Sample"));
            int voters = in.nextInt();
            int candidates = in.nextInt();
            int[][] prefList = new int[voters][candidates];
            for (int i = 0; i < voters; i++) {
                for (int j = 0; j < candidates; j++) {
                    prefList[i][j] = in.nextInt();
                }
            }
            ElectionType type = ElectionType.DP;
            if (args.length > 0) {
                if (args[0].equals("-a")) type = ElectionType.DPWITHABS;
                else if (args[0].equals("-ac")) {
                    type = ElectionType.DPWITHCOSTLYABS;
                }
            }

            PreferenceList pref = new PreferenceList(prefList);
            VotingOrder order = new VotingOrder(voters, true);
            VotingRule rule = new PluralityVR(candidates);

            ElectionParameters params = new ElectionParameters(pref, order, rule, type);
            DPElection e = (DPElection) ElectionFactory.create(params);

            long startTime = System.nanoTime();
            ArrayList<ElectionState> winners = e.findNE();
            long endTime = System.nanoTime();
            printResults(order, winners);
            System.out.println("Time elapsed on calculation: " + (endTime - startTime) + " nanoseconds");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

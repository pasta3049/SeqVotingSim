package Testers;

import Elections.*;
import Model.PreferenceList;
import Model.VotingOrder;
import Model.VotingRule;
import VotingRules.PluralityVR;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Created by AriApar on 21/04/2016.
 */
public class DPVotingTestSuite extends AbstractTester {

    public static void main(String[] args) {
        try {
            File[] files = new File(Paths.get("res", "PListSamples").toString()).listFiles();

            Arrays.sort( files, new Comparator<File>() {
                public int compare( File a, File b ) {
                    return Long.valueOf(a.lastModified()).compareTo(b.lastModified());
                }
            });

            long lStartTime = System.currentTimeMillis();
            for (File f: files) {
                Path filePath = f.toPath();
                if (Files.isRegularFile(filePath) && !filePath.getFileName().toString().equals(".DS_Store")
                        && filePath.getFileName().toString().equals("2x150Sample38")) {
                    runSampleFromPath(filePath, args);
                    System.out.println("File " + filePath.getFileName().toString() + " done.");
                }
            }
            long lEndTime = System.currentTimeMillis();
            long difference = lEndTime - lStartTime;
            System.out.println("Elapsed milliseconds: " + difference);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runSampleFromPath(Path p, String[] args) {
        try {
            Scanner in = new Scanner(p);
            int voters = in.nextInt();
            int candidates = in.nextInt();
            int[][] prefList = new int[voters][candidates];
            for (int i = 0; i < voters; i++) {
                for (int j = 0; j < candidates; j++) {
                    prefList[i][j] = in.nextInt();
                }
            }
            in.close();
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
            saveResultsToFile(order, winners, (endTime - startTime), p);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(p.toString());
        }
    }
}

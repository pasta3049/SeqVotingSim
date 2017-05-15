package me.ariapar.Processor;
//created by ug14mk
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;

import Elections.AltElection;
import Elections.AltElectionFactory;
import Elections.AltElectionState;
import Elections.ElectionParameters;
import Elections.ElectionType;
import Model.IVector;
import Model.PreferenceList;
import Model.VotingOrder;
import Model.VotingRule;
import VotingRules.BordaVR;
import VotingRules.PluralityVR;
import VotingRules.TwoApprovalVR;

public class AltSampleFileProcessor implements Runnable {
	private Path p;
    private String[] args;
    private String ruleString;

    public AltSampleFileProcessor(Path p, String[] args) {
        this.p = p;
        this.args = args;
        this.ruleString = "";
    }
    
    public AltSampleFileProcessor(Path p, String[] args, String rule) {
    	this.p = p;
    	this.args = args;
    	this.ruleString = rule;
    }

    @Override
    public void run() {
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
                else if (args[0].equals("-act")) type = ElectionType.GAMETREEWITHCOSTLYABS;
                else if (args[0].equals("-t")) type = ElectionType.TRUTHFUL;
            }

            PreferenceList pref = new PreferenceList(prefList);
            VotingOrder order = new VotingOrder(voters, true);
            VotingRule rule;
            if (ruleString.toUpperCase(Locale.ROOT) == "2APPROVAL") {
            	rule = new TwoApprovalVR(candidates);
            } else if (ruleString.toUpperCase(Locale.ROOT) == "BORDA") {
            	rule = new BordaVR(candidates);
            } else {
            	rule = new PluralityVR(candidates);
            }

            ElectionParameters params = new ElectionParameters(pref, order, rule, type);
            AltElection e = AltElectionFactory.create(params);

            long startTime = System.nanoTime();
            ArrayList<AltElectionState> winners = e.findNE();
            long endTime = System.nanoTime();
            saveResultsToFile(order, winners, (endTime - startTime), p, type, params);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(p.toString());
        }
    }

    public void saveResultsToFile(VotingOrder order, ArrayList<AltElectionState> winners,
                                         long time, Path path, ElectionType type, ElectionParameters params){
        //make the directory if needed
        String directoryPath;
        try {
            switch (type){
                case GAMETREEWITHCOSTLYABS: directoryPath = "tree/results/";
                    break;
                case TRUTHFUL: directoryPath = "truthful/results/";
                    break;
                case DPWITHCOSTLYABS: directoryPath = "results";
                    break;
                case DPWITHABS: directoryPath = "lazy_wo_cost/results";
                    break;
                default: throw new RuntimeException("invalid election type for saving results");
            }
            File f = new File(directoryPath);
            f.mkdirs();
            //get file path
            Path p = Paths.get(directoryPath, path.getFileName().toString());
            BufferedWriter writer = null;
            try {
                writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8);
                writer.write("This election has " + winners.size() +
                        " Nash equilibria!");
                writer.newLine();
                Iterator<AltElectionState> it = winners.iterator();
                for (int i = 1; i<= winners.size(); i++) {
                    writer.write("Nash Equilibrium " + i + ":");
                    writer.newLine();
                    writer.write("The winner is candidate(s) ");
                    AltElectionState wins = it.next();
                    //Print winners
                    ArrayList<Integer> elected = wins.getCurrentWinners();
                    for (int j = 0; j < elected.size() - 1; j++) writer.write(elected.get(j) + ", ");
                    writer.write(elected.get(elected.size() -1).toString());
                    writer.newLine();
                    //Print vote distribution
                    writer.write("Vote Distribution: " + wins.getCurrentScores().toString());
                    writer.newLine();
                    ArrayList<IVector> votes = wins.getCurrentVotes();
                    writer.write("Abstentions: " +  (votes.size() * wins.getSelectedVotingRule().getNumberOfVotesPerVoter(params) - wins.getCurrentScores().getSum()));
                    writer.newLine();
                    //Print votes cast by each voter
                    writer.write("Votes Cast (in order): ");
                    writer.newLine();
                    Iterator<String> iter = wins.getCurrentVoteDetails().iterator();
                    for (Integer v : order) {
                        writer.write("Voter " + v + ": " +  iter.next());
                        writer.newLine();
                    }
                }
                writer.write("Time taken: " + time + " nanoseconds");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    writer.close();
                    System.out.println("File " + path.getFileName().toString() + " done, " + args[0]);
                } catch (IOException ex) {
                    // Log error writing file and exit.
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

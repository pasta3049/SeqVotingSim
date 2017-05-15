package Testers;

import Elections.ElectionState;
import Model.VotingOrder;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Created by AriApar on 13/01/2016.
 */
public class AbstractTester {

    public static File getFile(String fileName) {
        //Get file from resources folder
        File file = new File("res/PListExamples/" + fileName);
        return file;
    }

    public static void printResults(VotingOrder order, ArrayList<ElectionState> winners) {
        System.out.println("This election has " + winners.size() +
                " Nash equilibria!");
        Iterator<ElectionState> it = winners.iterator();
        for (int i = 1; i<= winners.size(); i++) {
            System.out.println("Nash Equilibrium " + i + ":");
            System.out.print("The winner is candidate(s) ");
            ElectionState wins = it.next();
            //Print winners
            ArrayList<Integer> elected = wins.getCurrentWinners();
            for (int j = 0; j < elected.size() - 1; j++) System.out.print(elected.get(j) + ", ");
            System.out.println(elected.get(elected.size() -1));
            //Print vote distribution
            System.out.println("Vote Distribution: " + wins.getCurrentScores().toString());
            //Print votes cast by each voter
            System.out.println("Votes Cast (in order): ");
            Iterator<Integer> iter = wins.getCurrentVotes().iterator();
            for (Integer v : order) {
                System.out.println("Voter " + v + ": Candidate " +  iter.next());
            }
        }
    }

    public static void saveResultsToFile(VotingOrder order, ArrayList<ElectionState> winners,
                                         long time, Path path) {
        //make the directory if needed
        String directoryPath = path.getParent().getParent().toString() + "/results/";
        File f = new File(directoryPath);
        f.mkdirs();
        //get file path
        Path p = Paths.get(directoryPath, path.getFileName().toString());
        try {
            BufferedWriter writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8);
            writer.write("This election has " + winners.size() +
                    " Nash equilibria!");
            writer.newLine();
            Iterator<ElectionState> it = winners.iterator();
            for (int i = 1; i<= winners.size(); i++) {
                writer.write("Nash Equilibrium " + i + ":");
                writer.newLine();
                writer.write("The winner is candidate(s) ");
                ElectionState wins = it.next();
                //Print winners
                ArrayList<Integer> elected = wins.getCurrentWinners();

                for (int j = 0; j < elected.size() - 1; j++) writer.write(elected.get(j) + ", ");
                writer.write(elected.get(elected.size() -1).toString());
                writer.newLine();
                //Print vote distribution
                writer.write("Vote Distribution: " + wins.getCurrentScores().toString());
                writer.newLine();
                ArrayList<Integer> votes = wins.getCurrentVotes();
                writer.write("Abstentions: " +  (votes.size() - wins.getCurrentScores().getSum()));
                writer.newLine();
                //Print votes cast by each voter
                writer.write("Votes Cast (in order): ");
                writer.newLine();
                Iterator<Integer> iter = wins.getCurrentVotes().iterator();
                for (Integer v : order) {
                    writer.write("Voter " + v + ": Candidate " +  iter.next());
                    writer.newLine();
                }

            }
            writer.write("Time taken: " + time + " nanoseconds");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

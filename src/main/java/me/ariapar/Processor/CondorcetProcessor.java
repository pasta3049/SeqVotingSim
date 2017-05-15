package me.ariapar.Processor;
//created by ug14mk
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CondorcetProcessor implements Runnable {

	private static final String outputPath = "condorcetResults";
	
	private Path p;
	
	public CondorcetProcessor(Path p) {
		this.p = p;
	}
	
	@Override
	public void run() {
		try {
			int condorcetWinner = 0;
			
			Scanner in;
			in = new Scanner(p);
	        int voters = in.nextInt();
	        int candidates = in.nextInt();
	        int[][] prefList = new int[voters][candidates];
	        for (int i = 0; i < voters; i++) {
	            for (int j = 0; j < candidates; j++) {
	                prefList[i][j] = in.nextInt();
	            }
	        }
	        in.close();
	        
	        int[][] pairwiseWinners = new int[candidates][candidates];
	        for (int i = 0; i < candidates; i++) {
	        	for (int j = i; j < candidates; j++) { //start at i as both elections are filled in by the else clause below
	        		if (i == j) {
	        			pairwiseWinners[i][j] = i; //a candidate will clearly win an election against herself
	        		} else {
	        			int winnerIndex = twoPersonElectionWinner(i, j, prefList);
	        			pairwiseWinners[i][j] = winnerIndex;
	        			pairwiseWinners[j][i] = winnerIndex; //both elections are filled in so the election does not need to be run twice with the same two candidates
	        		}
	        	}
	        }
	        
	        //check if there a candidate who has won all pairwise elections involving herself
	        for (int i = 0; i < candidates; i++) {
	        	boolean winsAllElections = true;
	        	for (int j = 0; j < candidates && winsAllElections; j++) {
	        		if (pairwiseWinners[i][j] != i) winsAllElections = false;
	        	}
	        	if (winsAllElections) {
	        		condorcetWinner = i+1; //+1 since array is indexed from 0 and candidate numbers are indexed from 1
	        		break;
	        	}
	        }
	        File f = new File(outputPath);
            f.mkdirs();
            //get file path
            Path outputFile = Paths.get(outputPath, p.getFileName().toString());
            
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
    				new FileOutputStream(outputFile.toString()), "utf-8"))) {
            	if (condorcetWinner == 0) {
            		writer.write(String.format("This election has no Condorcet winner%n", 0));
            	} else {
            		writer.write(String.format("The Condorcet winner is candidate %d%n", condorcetWinner));
            	}
            }
            finally {
            	System.out.println("File " + p.getFileName().toString() + " done, ");
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static int twoPersonElectionWinner(int firstCandIndex, int secondCandIndex, int[][] prefList) {
		//note that firstCandIndex+1 is the candidate number of the first candidate
        int firstCandCount = 0;
        int secondCandCount = 0;
        for (int i = 0; i < prefList.length; i++) {
            for (int j = 0; j < prefList[0].length; j++) {
                if (prefList[i][j] == firstCandIndex+1 || prefList[i][j] == secondCandIndex+1) {
                    //this is for a two person election, so only votes for the two selected candidates are relevant
                    if (prefList[i][j] == firstCandIndex+1) firstCandCount +=1;
                    else secondCandCount +=1;
                    break; //break the inner (j) for loop so that the preference for the other candidate is not counted
                }
            }
        }
        if (firstCandCount > secondCandCount) return firstCandIndex;
        else if (secondCandCount > firstCandCount) return secondCandIndex;
        else return -1; //tie
    }
}

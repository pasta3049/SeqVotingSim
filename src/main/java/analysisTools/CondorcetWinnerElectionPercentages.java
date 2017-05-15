package analysisTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CondorcetWinnerElectionPercentages {

	private static final String dataFolderPath = "condorcetGroupedResults";
	private static final int numberOfCandidates = 4;
	private static final int numberOfVoters = 15;
	
	
	private enum ComparisonResult {
		EQUAL, PARTIALMATCH, NOMATCH
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> condorcetWinners = new ArrayList<Integer>();
		ArrayList<Set<Integer>> pluralityTruthWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> pluralityNEWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> twoApprovalTruthWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> twoApprovalNEWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> bordaTruthWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> bordaNEWinners = new ArrayList<Set<Integer>>();
		
		boolean bordaCountAvailable = false;
		int pluralityTruth = 0;
		int pluralityTruthTie = 0;
		int pluralityNash = 0;
		int pluralityNashTie = 0;
		int twoApprovalTruth = 0;
		int twoApprovalTruthTie = 0;
		int twoApprovalNash = 0;
		int twoApprovalNashTie = 0;
		int bordaTruth = 0;
		int bordaTruthTie = 0;
		int bordaNash = 0;
		int bordaNashTie = 0;		
		
		File dataFile = new File(Paths.get(dataFolderPath,(numberOfCandidates + "x" + numberOfVoters)).toString());
		try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
			String line;
			int lineCounter = 0;
			
			while ((line = br.readLine()) != null) {
				lineCounter++;
				if (lineCounter == 1) {
					if (!line.equals("Candidates: " + numberOfCandidates)) {
						System.err.println("Error: Incorrect number of candidates");
						break;
					}
				}
				else if (lineCounter == 2) {
					if (!line.equals("Voters: " + numberOfVoters)) {
						System.err.println("Error: Incorrect number of voters");
						break;
					}
				}
				else if (lineCounter == 3) {
					if (line.equals("Borda count available: false")) {
						bordaCountAvailable = false;
					} else if (line.equals("Borda count available: true")) {
						bordaCountAvailable = true;
					} else {
						System.err.println("Error: Invalid file");
						break;
					}
				}
				else if (lineCounter == 4) {
					if (bordaCountAvailable) {
						if (!line.equalsIgnoreCase("filename; condorcet; pluralityTruth; pluralityNE; 2apTruth; 2apNE; bordaTruth; bordaNE")) {
							System.err.println("Error: Invalid file");
							break;
						}
					} else {
						if (!line.equals("filename; condorcet; pluralityTruth; pluralityNE; 2apTruth; 2apNE")) {
							System.err.println("Error: Invalid file");
							break;
						}
					}
				}
				else { //if lineCounter >= 5
					String[] winnerStrings = line.split("; ?");
					if (!bordaCountAvailable && winnerStrings.length != 5) {
						System.err.println("Error: Invalid file, invalid entry at line " + lineCounter);
						break;
					}
					
					if (!addCondorcetWinner(winnerStrings, condorcetWinners, lineCounter)) break;
					
					//plurality truthful
					if (!addWinnersToArrayList(winnerStrings, 2, pluralityTruthWinners, lineCounter)) break;
					
					//plurality nash equilibrium
					if (!addWinnersToArrayList(winnerStrings, 3, pluralityNEWinners, lineCounter)) break;
					
					//2 approval truthful
					if (!addWinnersToArrayList(winnerStrings, 4, twoApprovalTruthWinners, lineCounter)) break;
					
					//2 approval nash equilibrium
					if (!addWinnersToArrayList(winnerStrings, 5, twoApprovalNEWinners, lineCounter)) break;
					
					if (bordaCountAvailable) {
						//borda truthful
						if (!addWinnersToArrayList(winnerStrings, 6, bordaTruthWinners, lineCounter)) break;
						
						//borda nash equilibrium
						if (!addWinnersToArrayList(winnerStrings, 7, bordaNEWinners, lineCounter)) break;
					}
				}
			}
		}
		// TODO Auto-generated method stub
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < condorcetWinners.size(); i++) {
			switch (checkCondorcetWinner(condorcetWinners.get(i), pluralityTruthWinners.get(i))) {
			case EQUAL:
				pluralityTruth++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				pluralityTruthTie++;
				break;
			}
			
			switch (checkCondorcetWinner(condorcetWinners.get(i), pluralityNEWinners.get(i))) {
			case EQUAL:
				pluralityNash++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				pluralityNashTie++;
				break;
			}
			
			switch (checkCondorcetWinner(condorcetWinners.get(i), twoApprovalTruthWinners.get(i))) {
			case EQUAL:
				twoApprovalTruth++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				twoApprovalTruthTie++;
				break;
			}
			
			switch (checkCondorcetWinner(condorcetWinners.get(i), twoApprovalNEWinners.get(i))) {
			case EQUAL:
				twoApprovalNash++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				twoApprovalNashTie++;
				break;
			}
			
			if (bordaCountAvailable) {
				switch (checkCondorcetWinner(condorcetWinners.get(i), bordaTruthWinners.get(i))) {
				case EQUAL:
					bordaTruth++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					bordaTruthTie++;
					break;
				}
				
				switch (checkCondorcetWinner(condorcetWinners.get(i), bordaNEWinners.get(i))) {
				case EQUAL:
					bordaNash++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					bordaNashTie++;
					break;
				}
			}
		}
		
		System.out.println(String.format("Plurality Truthful: Condorcet Winner elected: %d (%d%%), Tie involving Condorcet Winner: %d (%d%%)", pluralityTruth, 100*pluralityTruth/condorcetWinners.size(), pluralityTruthTie, 100*pluralityTruthTie/condorcetWinners.size()));
		System.out.println(String.format("Plurality Nash: Condorcet Winner elected: %d (%d%%), Tie involving Condorcet Winner: %d (%d%%)", pluralityNash, 100*pluralityNash/condorcetWinners.size(), pluralityNashTie, 100*pluralityNashTie/condorcetWinners.size()));
		System.out.println(String.format("2-approval Truthful: Condorcet Winner elected: %d (%d%%), Tie involving Condorcet Winner: %d (%d%%)", twoApprovalTruth, 100*twoApprovalTruth/condorcetWinners.size(), twoApprovalTruthTie, 100*twoApprovalTruthTie/condorcetWinners.size()));
		System.out.println(String.format("2-approval Nash: Condorcet Winner elected: %d (%d%%), Tie involving Condorcet Winner: %d (%d%%)", twoApprovalNash, 100*twoApprovalNash/condorcetWinners.size(), twoApprovalNashTie, 100*twoApprovalNashTie/condorcetWinners.size()));
		if (bordaCountAvailable) {
			System.out.println(String.format("Borda Truthful: Condorcet Winner elected: %d (%d%%), Tie involving Condorcet Winner: %d (%d%%)", bordaTruth, 100*bordaTruth/condorcetWinners.size(), bordaTruthTie, 100*bordaTruthTie/condorcetWinners.size()));
			System.out.println(String.format("Borda Nash: Condorcet Winner elected: %d (%d%%), Tie involving Condorcet Winner: %d (%d%%)", bordaNash, 100*bordaNash/condorcetWinners.size(), bordaNashTie, 100*bordaNashTie/condorcetWinners.size()));
		}
	}
	
	private static boolean addCondorcetWinner(String[] winnerStrings, ArrayList<Integer> condorcetWinners, int lineCounter) {
		int winner;
		try {
			winner = Integer.parseInt(winnerStrings[1]);
			condorcetWinners.add(winner);
			return true;
		} catch (NumberFormatException e) {
			System.err.println("Error: Invalid file, invalid entry at line " + lineCounter);
			return false;
		}
		
	}
	
	private static boolean addWinnersToArrayList(String[] winnerStrings, int arrayPosition, ArrayList<Set<Integer>> arrayList, int lineCounter) {
		if (winnerStrings[arrayPosition].charAt(0) != '{' || winnerStrings[arrayPosition].charAt(winnerStrings[arrayPosition].length()-1) != '}') {
			System.err.println("Error: Invalid file, invalid entry at line " + lineCounter);
			return false;
		} else {
			String[] winners = winnerStrings[arrayPosition].split("\\{|\\}|(, ?)");
			Set<Integer> winnerSet = new HashSet<Integer>();
			for (int i = 1; i < winners.length; i++) { //start at 1 since empty string at beginning of array
				winnerSet.add(Integer.parseInt(winners[i]));
			}
			arrayList.add(winnerSet);
			return true;
		}
	}
	
	private static ComparisonResult checkCondorcetWinner(int condorcetWinner, Set<Integer> w1) {
		if (!w1.contains(condorcetWinner)) return ComparisonResult.NOMATCH;
		else if (w1.size() == 1) return ComparisonResult.EQUAL;
		else return ComparisonResult.PARTIALMATCH;
	}
}

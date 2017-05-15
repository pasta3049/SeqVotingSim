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

public class GetMatchPercentages {
//created by ug14mk
	private static final String dataFolderPath = "groupedResults";
	private static final int numberOfCandidates = 4;
	private static final int numberOfVoters = 15;
	
	private enum ComparisonResult {
		EQUAL, PARTIALMATCH, NOMATCH
	}
	
	public static void main(String[] args) {
		
		ArrayList<Set<Integer>> pluralityTruthWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> pluralityNEWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> twoApprovalTruthWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> twoApprovalNEWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> bordaTruthWinners = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> bordaNEWinners = new ArrayList<Set<Integer>>();
		
		boolean bordaCountAvailable = false;
		int truthMatches = 0;
		int nashMatches = 0;
		int pluralityMatches = 0;
		int twoApprovalMatches = 0;
		int truthPartial = 0;
		int nashPartial = 0;
		int pluralityPartial = 0;
		int twoApprovalPartial = 0;
		
		int bordaMatches = 0;
		int bordaPartial = 0;
		int truthBorPluMatches = 0;
		int truthBorPluPartial = 0;
		int truthBorTwoMatches = 0;
		int truthBorTwoPartial = 0;
		int nashBorPluMatches = 0;
		int nashBorPluPartial = 0;
		int nashBorTwoMatches = 0;
		int nashBorTwoPartial = 0;
		int truthAllMatches = 0;
		int truthAllPartial = 0;
		int nashAllMatches = 0;
		int nashAllPartial = 0;
		
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
						if (!line.equalsIgnoreCase("filename; pluralityTruth; pluralityNE; 2apTruth; 2apNE; bordaTruth; bordaNE")) {
							System.err.println("Error: Invalid file");
							break;
						}
					} else {
						if (!line.equals("filename; pluralityTruth; pluralityNE; 2apTruth; 2apNE")) {
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
					//TODO: Implement code for parsing files which include Borda Count data
					
					//plurality truthful
					if (!addWinnersToArrayList(winnerStrings, 1, pluralityTruthWinners, lineCounter)) break;
					
					//plurality nash equilibrium
					if (!addWinnersToArrayList(winnerStrings, 2, pluralityNEWinners, lineCounter)) break;
					
					//2 approval truthful
					if (!addWinnersToArrayList(winnerStrings, 3, twoApprovalTruthWinners, lineCounter)) break;
					
					//2 approval nash equilibrium
					if (!addWinnersToArrayList(winnerStrings, 4, twoApprovalNEWinners, lineCounter)) break;
					
					if (bordaCountAvailable) {
						//borda truthful
						if (!addWinnersToArrayList(winnerStrings, 5, bordaTruthWinners, lineCounter)) break;
						
						//borda nash equilibrium
						if (!addWinnersToArrayList(winnerStrings, 6, bordaNEWinners, lineCounter)) break;
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
		
		for (int i = 0; i < pluralityTruthWinners.size(); i++) {
			switch (compareWinners(pluralityTruthWinners.get(i), pluralityNEWinners.get(i))) {
			case EQUAL:
				pluralityMatches++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				pluralityPartial++;
				break;
			}
			
			switch (compareWinners(twoApprovalTruthWinners.get(i), twoApprovalNEWinners.get(i))) {
			case EQUAL:
				twoApprovalMatches++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				twoApprovalPartial++;
				break;
			}
			
			switch (compareWinners(pluralityTruthWinners.get(i), twoApprovalTruthWinners.get(i))) {
			case EQUAL:
				truthMatches++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				truthPartial++;
				break;
			}
			
			switch (compareWinners(pluralityNEWinners.get(i), twoApprovalNEWinners.get(i))) {
			case EQUAL:
				nashMatches++;
				break;
			case NOMATCH:
				break;
			case PARTIALMATCH:
				nashPartial++;
				break;
			}
			
			if (bordaCountAvailable) {
				switch (compareWinners(bordaTruthWinners.get(i), bordaNEWinners.get(i))) {
				case EQUAL:
					bordaMatches++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					bordaPartial++;
					break;
				}
				
				switch (compareWinners(bordaTruthWinners.get(i), pluralityTruthWinners.get(i))) {
				case EQUAL:
					truthBorPluMatches++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					truthBorPluPartial++;
					break;
				}
				
				switch (compareWinners(bordaTruthWinners.get(i), twoApprovalTruthWinners.get(i))) {
				case EQUAL:
					truthBorTwoMatches++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					truthBorTwoPartial++;
					break;
				}
				
				switch (compareWinners(bordaNEWinners.get(i), pluralityNEWinners.get(i))) {
				case EQUAL:
					nashBorPluMatches++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					nashBorPluPartial++;
					break;
				}
				
				switch (compareWinners(bordaNEWinners.get(i), twoApprovalNEWinners.get(i))) {
				case EQUAL:
					nashBorTwoMatches++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					nashBorTwoPartial++;
					break;
				}
				
				switch (tripleCompareWinners(bordaTruthWinners.get(i), pluralityTruthWinners.get(i), twoApprovalTruthWinners.get(i))) {
				case EQUAL:
					truthAllMatches++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					truthAllPartial++;
					break;
				}
				
				switch (tripleCompareWinners(bordaNEWinners.get(i), pluralityNEWinners.get(i), twoApprovalNEWinners.get(i))) {
				case EQUAL:
					nashAllMatches++;
					break;
				case NOMATCH:
					break;
				case PARTIALMATCH:
					nashAllPartial++;
					break;
				}
			}
		}
		
		System.out.println(String.format("Plurality Nash and Truthful: Matches: %d, Partial Matches %d", pluralityMatches, pluralityPartial));
		System.out.println(String.format("2-approval Nash and Truthful: Matches: %d, Partial Matches %d", twoApprovalMatches, twoApprovalPartial));
		if (bordaCountAvailable) System.out.println(String.format("Borda Nash and Truthful: Matches: %d, Partial Matches %d", bordaMatches, bordaPartial));
		System.out.println(String.format("Truthful Plurality and 2-approval: Matches: %d, Partial Matches %d", truthMatches, truthPartial));
		if (bordaCountAvailable) System.out.println(String.format("Truthful Plurality and Borda: Matches: %d, Partial Matches %d", truthBorPluMatches, truthBorPluPartial));
		if (bordaCountAvailable) System.out.println(String.format("Truthful 2-approval and Borda: Matches: %d, Partial Matches %d", truthBorTwoMatches, truthBorTwoPartial));
		if (bordaCountAvailable) System.out.println(String.format("Truthful all: Matches: %d, Partial Matches %d", truthAllMatches, truthAllPartial));
		System.out.println(String.format("Nash Plurality and 2-approval: Matches: %d, Partial Matches %d", nashMatches, nashPartial));
		if (bordaCountAvailable) System.out.println(String.format("Nash Plurality and Borda: Matches: %d, Partial Matches %d", nashBorPluMatches, nashBorPluPartial));
		if (bordaCountAvailable) System.out.println(String.format("Nash 2-approval and Borda: Matches: %d, Partial Matches %d", nashBorTwoMatches, nashBorTwoPartial));
		if (bordaCountAvailable) System.out.println(String.format("Nash all: Matches: %d, Partial Matches %d", nashAllMatches, nashAllPartial));
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
	
	private static ComparisonResult compareWinners(Set<Integer> w1, Set<Integer> w2) {
		if (w1.equals(w2)) {
			return ComparisonResult.EQUAL;
		} else if (w1.size() == 1 && w2.size() == 1) {
			return ComparisonResult.NOMATCH; //no point checking for partial match as there is only one winner in each set
		} else {
			for (int winner : w1) {
				if (w2.contains(winner)) {
					return ComparisonResult.PARTIALMATCH;
				}
			}
			return ComparisonResult.NOMATCH;
		}
	}
	
	private static ComparisonResult tripleCompareWinners(Set<Integer> w1, Set<Integer> w2, Set<Integer> w3) {
		if (w1.equals(w2) && w1.equals(w3)) {
			return ComparisonResult.EQUAL;
		} else if (w1.size() == 1 && w2.size() == 1 && w3.size() == 1) {
			return ComparisonResult.NOMATCH;
		} else {
			for (int winner : w1) {
				if (w2.contains(winner) && w3.contains(winner)) {
					return ComparisonResult.PARTIALMATCH;
				}
			}
			return ComparisonResult.NOMATCH;
		}
	}

}

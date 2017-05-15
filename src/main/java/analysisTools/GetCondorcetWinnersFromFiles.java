package analysisTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class GetCondorcetWinnersFromFiles {
	
	private static final String condorcetPath = "condorcetResults";
	private static final String pluralityTruthPath = "generatedResults/pluralityTruth";
	private static final String pluralityNashPath = "generatedResults/pluralityNE";
	private static final String twoApprovalTruthPath = "generatedResults/2approvalTruth";
	private static final String twoApprovalNashPath = "generatedResults/2approvalNE";
	private static final String bordaTruthPath = "";
	private static final String bordaNashPath = "";
	private static final boolean bordaAvailable = false;
	private static final int numberOfCandidates = 3;
	private static final int numberOfVoters = 10;
	private static final String numberOfCandidatesAndVoters = Integer.toString(numberOfCandidates) + "x" + Integer.toString(numberOfVoters);
	private static final String outputPath = "groupedResults/" + numberOfCandidatesAndVoters;

	private static boolean errors = false;
	private static int numberOfCondorcetWinners = 0;
	
	public static void main(String[] args) {
		File[] files = new File(Paths.get(condorcetPath).toString()).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.contains(numberOfCandidatesAndVoters + "Sample"));
            }
        });
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputPath), "utf-8"))) {
			writer.write(String.format("Candidates: %d%n", numberOfCandidates));
			writer.write(String.format("Voters: %d%n", numberOfVoters));
			if (bordaAvailable) {
				writer.write(String.format("Borda count available: true%n"));
				writer.write(String.format("%s; %s; %s; %s; %s; %s; %s; %s%n", "filename", "condorcet", "pluralityTruth", "pluralityNE", "2apTruth", "2apNE", "bordaTruth", "bordaNE"));
			} else {
				writer.write(String.format("Borda count available: false%n"));
				writer.write(String.format("%s; %s; %s; %s; %s; %s%n", "filename", "condorcet", "pluralityTruth", "pluralityNE", "2apTruth", "2apNE"));
			}
			
			for (File cFile: files) {
				String name = cFile.getName();
				File ptFile = new File(Paths.get(pluralityTruthPath, name).toString());
				File pnFile = new File(Paths.get(pluralityNashPath, name).toString());
				File ttFile = new File(Paths.get(twoApprovalTruthPath, name).toString());
				File tnFile = new File(Paths.get(twoApprovalNashPath, name).toString());
				File btFile = bordaAvailable ? new File(Paths.get(bordaTruthPath, name).toString()) : null;
				File bnFile = bordaAvailable ? new File(Paths.get(bordaNashPath, name).toString()) : null;
				int condorcetResult = getCondorcetWinnerFromFile(cFile);
				if (condorcetResult != -1) {
					numberOfCondorcetWinners++;
					String ptResult = getWinnersFromFile(ptFile);
					String pnResult = getWinnersFromFile(pnFile);
					String ttResult = getWinnersFromFile(ttFile);
					String tnResult = getWinnersFromFile(tnFile);
					String btResult = bordaAvailable ? getWinnersFromFile(btFile) : null;
					String bnResult = bordaAvailable ? getWinnersFromFile(bnFile) : null;
					if (bordaAvailable) {
						writer.write(String.format("%s; %d; %s; %s; %s; %s; %s; %s%n", name, condorcetResult, ptResult, pnResult, ttResult, tnResult, btResult, bnResult));
					} else {
						writer.write(String.format("%s; %d; %s; %s; %s; %s%n", name, condorcetResult, ptResult, pnResult, ttResult, tnResult));
					}
				}
			}
			writer.close();
			System.out.println(numberOfCondorcetWinners);
			System.err.println(errors ? "Errors" : "No errors");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	private static int getCondorcetWinnerFromFile(File f) {
		try {
			String p;
			Scanner scanner = new Scanner(f);
			p = scanner.findInLine("The Condorcet winner is candidate \\d+");
			scanner.close();
			if (p == null) {
				return -1;
			} else {
				String winnerString = p.substring("The Condorcet winner is candidate ".length());
				int winner = Integer.parseInt(winnerString);
				return winner;
			}
		} catch (Exception e) {
			return -1;
		}
	}
	
	private static String getWinnersFromFile(File f) {
		try {
			String p;
			Scanner scanner = new Scanner(f);
			p = scanner.findInLine("This election has (\\d+) Nash equilibria!");
			MatchResult result = scanner.match();
			if (result.groupCount() != 1 || p.charAt(18) != '1' || p.charAt(19) != ' ') {
				errors = true;
				scanner.close();
				return "error";
			} else {
				p = scanner.findWithinHorizon("The winner is candidate\\(s\\) ([ ,\\d]*)", 0);
			}
			scanner.close();
			return("{" + p.substring(27) + "}");
		} catch (IOException e) {
			errors = true;
			return "error";
		}
	}
}

package sampleGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AriApar on 21/04/2016.
 */
public class SampleFileWriter {
    // Writes a sample file to a given directory
    private int numVoters;
    private int numCandidates;
    private String directoryPath;
    private PermutationSampler sampler;

    public SampleFileWriter(int numVoters, int numCandidates, String directoryPath) {
        this.numVoters = numVoters;
        this.numCandidates = numCandidates;
        this.directoryPath = directoryPath;
        this.sampler = new PermutationSampler(numCandidates);
    }

    public void writeSampleFile(int fileIndexSuffix) {
        //make the directory if needed
        File f = new File(directoryPath);
        f.mkdirs();
        //get file path
        Path p = Paths.get(directoryPath, getFileName(fileIndexSuffix));
        try {
            BufferedWriter writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8);
            writer.write(numVoters + " " + numCandidates);
            writer.newLine();
            for (int i=0; i< numVoters-1; i++) {
                writer.write(getNextPermutationString());
                writer.newLine();
            }
            writer.write(getNextPermutationString());
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getNextPermutationString() {
        List<Integer> perm = sampler.getNextPermutation();
        return perm.stream()
                .map(number -> String.valueOf(number))
                .collect(Collectors.joining(" "));
    }

    private String getFileName(int suffix) {
        return numCandidates + "x" + numVoters + "Sample" + suffix;
    }
}

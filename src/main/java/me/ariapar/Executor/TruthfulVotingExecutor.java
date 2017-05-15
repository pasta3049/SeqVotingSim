package me.ariapar.Executor;

import me.ariapar.Processor.SampleFileProcessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by AriApar on 05/05/2016.
 */
public class TruthfulVotingExecutor {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        File resDirectory = new File(Paths.get("truthful/results").toString());
        resDirectory.mkdirs();
        File[] alreadyTestedFiles = resDirectory.listFiles();

        ArrayList<String> fileNames = new ArrayList<>();
        if (alreadyTestedFiles != null) {
            for (File testedFile : alreadyTestedFiles) {
                fileNames.add(testedFile.getName());
            }
        }

        Collections.sort(fileNames);

        /*File[] files = new File(Paths.get("res", "PListSamples").toString())
                .listFiles();*/
        File[] files = new File(Paths.get("/auto/users/ug14mk/scratch/samples").toString())
        		.listFiles();

        Arrays.sort( files, new Comparator<File>() {
            public int compare( File a, File b ) {
                return a.getName().compareTo(b.getName());
            }
        });

        for (File file : files) {
        		Path filePath = file.toPath();
        		if (Files.isRegularFile(filePath))
        			threadPool.submit(new SampleFileProcessor(filePath, new String[]{"-t"}, "BORDA"));
        }
        // shutdown the pool once you've submitted your last job
        long lStartTime = System.currentTimeMillis();
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(7L, TimeUnit.DAYS);
            long lEndTime = System.currentTimeMillis();
            long difference = lEndTime - lStartTime;
            System.out.println("Elapsed milliseconds: " + difference);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

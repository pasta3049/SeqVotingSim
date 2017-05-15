package me.ariapar.Executor;

import me.ariapar.Processor.SampleFileProcessor;

import java.io.File;
import java.io.FilenameFilter;
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
 * Created by AriApar on 25/04/2016.
 */
public class BackInductionVotingExecutor {

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        File resDirectory = new File(Paths.get("tree/results").toString());
        resDirectory.mkdirs();
        File[] alreadyTestedFiles = resDirectory.listFiles();

        ArrayList<String> fileNames = new ArrayList<>();
        if (alreadyTestedFiles != null) {
            for (File testedFile : alreadyTestedFiles) {
                fileNames.add(testedFile.getName());
            }
        }

        Collections.sort(fileNames);

        //File[] files = new File(Paths.get("res", "PListSamples").toString())
        File[] files = new File(Paths.get("/auto/users/ug14mk/scratch/samples").toString())
                .listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        //int index = Collections.binarySearch(fileNames, name);
                        //int size = fileNames.size();

                        return name.contains("3x10S") || name.contains("2x10S");
                    }
                });

        Arrays.sort( files, new Comparator<File>() {
            public int compare( File a, File b ) {
                return Long.valueOf(a.lastModified()).compareTo(b.lastModified());
            }
        });

        for (File file : files) {
            Path filePath = file.toPath();
            if (Files.isRegularFile(filePath))
                threadPool.submit(new SampleFileProcessor(filePath, args));
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

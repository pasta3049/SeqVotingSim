package me.ariapar.Executor;

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

import me.ariapar.Processor.CondorcetProcessor;


public class CondorcetVotingExecutor {

	public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(8);
        
        //File[] files = new File(Paths.get("res", "PListSamples").toString())
        File[] files = new File(Paths.get("/auto/users/ug14mk/scratch/samples").toString())
                .listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {

                return (name.contains("2x") ||
                        name.contains("3x"));

            }
        });

        Arrays.sort( files, new Comparator<File>() {
            public int compare( File a, File b ) {
                return a.getName().compareTo(b.getName());
            }
        });

        boolean debugTest = true;
        for (File file : files) {
        	if (debugTest){
        		Path filePath = file.toPath();
        		if (Files.isRegularFile(filePath)) {
        			threadPool.submit(new CondorcetProcessor(filePath));
        		}
        		debugTest = false;
        	}
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

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
 * Created by AriApar on 22/04/2016.
 */
public class DPVotingExecutor {

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(8);
        File resDirectory = new File(Paths.get("lazy_wo_cost/results").toString());
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
                int index = Collections.binarySearch(fileNames, name);
                int size = fileNames.size();

                return (name.contains("2x") ||
                        name.contains("3x") ||
                        name.contains("4x10S") || name.contains("4x15S") || name.contains("4x20S") || name.contains("4x25S") ||
                        name.contains("4x50S") || name.contains("4x75S") || name.contains("4x100S") || //name.contains("4x120S") ||
                        name.contains("5x10S") || name.contains("5x15S") || name.contains("5x20S") || name.contains("5x25S") ||
                        name.contains("6x10S") || name.contains("6x15S") || name.contains("6x20S") || name.contains("6x25S") ||
                            name.contains("7x10S") || name.contains("7x15S") || name.contains("7x20S") || name.contains("7x25S") ||
                                name.contains("8x10S") || name.contains("8x15S") || name.contains("8x20S"))// || name.contains("8x25S"))

                        && !(index >= 0 && index < size && fileNames.get(index).equals(name)) ;
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
        			threadPool.submit(new SampleFileProcessor(filePath, new String[]{"-ac"},"2APPROVAL"));
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

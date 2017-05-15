package Testers;

import sampleGenerator.SampleFileWriter;

/**
 * Created by AriApar on 21/04/2016.
 */
public class SampleFileTester extends AbstractTester{
    public static void main(String[] args) {
        SampleFileWriter writer = new SampleFileWriter(10,3,"res/PListSamples/");
        writer.writeSampleFile(0);
    }
}

package sampleGenerator;

import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by AriApar on 21/04/2016.
 */
public class PermutationSampler {

    private int upperBound;
    private ArrayList<List<Integer>> perms = null;
    private Random rand;

    //Generates random samples of permutations from 1 .. n
    public PermutationSampler(int n) {
        this.upperBound = n;
        this.rand = new Random();
    }

    public List<Integer> getNextPermutation() {
        if (perms == null) initializePerms();
        return perms.get(rand.nextInt(perms.size()));
    }

    private void initializePerms() {
        //prepare the original list of 1..n
        List<Integer> intList = new ArrayList<>();
        for (int i = 1; i <= upperBound; i++) {
            intList.add(i);
        }
        // generate the permutations
        perms = new ArrayList(Collections2.orderedPermutations(intList));
    }
}

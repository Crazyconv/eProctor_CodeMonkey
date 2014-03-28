package utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class NumberIterator {
    private static final Random random = new Random();
    public static Iterator<Integer> generate(Integer result,Integer range){
        Set<Integer> set = new HashSet<Integer>();
        while(set.size()<result){
            set.add(random.nextInt(range));
        }
        return set.iterator();
    }
}

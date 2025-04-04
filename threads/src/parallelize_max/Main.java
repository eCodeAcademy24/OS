package parallelize_max;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Searcher extends Thread {

    int start;

    int end;

    int max;

    public Searcher(int start, int end) {
        this.start = start;
        this.end = end;
        this.max = Main.ARRAY[start];
    }

    @Override
    public void run() {
        for (int i = start + 1; i < end; i++) {
            if (Main.ARRAY[i] > max) {
                max = Main.ARRAY[i];
            }
        }
    }

    public int getMax() {
        return max;
    }
}

public class Main {
    static int SIZE = 1000000;
    static int SEARCHER = 1000;

    static Random RANDOM = new Random();

    static int ARRAY[] = new int[SIZE];

    public static void main(String[] args) {

        for (int i = 0; i < SIZE; i++) {
            ARRAY[i] = RANDOM.nextInt(1000000); //0-999999
        }

        List<Searcher> threads = new ArrayList<>();

        int step = SIZE / SEARCHER; // 1000000 / 1000 = 1000
        for (int start = 0; start < SIZE; start += step) {
            int end = start + step; // 0-1000, 1000-2000 ...
            threads.add(new Searcher(start, end));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(threads
                .stream()
                .mapToInt(Searcher::getMax)
                .max()
                .getAsInt());


//        if you want to do it without streams
//        int max = threads.getFirst().getMax();
//        for(Searcher s : threads) {
//            if (s.getMax() > max) {
//                max = s.getMax();
//            }
//        }
//
//        System.out.println(max);
    }
}

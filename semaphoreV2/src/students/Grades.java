package students;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Grades {

    static double average = 0;

    static final BoundedRandomGenerator random = new BoundedRandomGenerator();

    private static final int ARRAY_LENGTH = 10000000;

    private static final int NUM_THREADS = 10;

    static long totalSum = 0;

    static Semaphore sumSemaphore;
    static Semaphore doneSemaphore;

    static void init() {
        sumSemaphore = new Semaphore(1);
        doneSemaphore = new Semaphore(0);
    }

    // DO NOT CHANGE
    public static int[] getSubArray(int[] array, int start, int end) {
        return Arrays.copyOfRange(array, start, end);
    }

    public static void main(String[] args) {

        init();

        int[] arr = ArrayGenerator.generate(ARRAY_LENGTH);
        int chunkSize = ARRAY_LENGTH / NUM_THREADS;

        CalculateThread[] threads = new CalculateThread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == NUM_THREADS - 1) ? ARRAY_LENGTH : start + chunkSize;
            threads[i] = new CalculateThread(arr, start, end);
            threads[i].start();
        }

        try {
            doneSemaphore.acquire(NUM_THREADS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        average = (double) totalSum / ARRAY_LENGTH;

        // DO NOT CHANGE
        System.out.println("Your calculated average grade is: " + average);
        System.out.println("The actual average grade is: " + ArrayGenerator.actualAvg);

        SynchronizationChecker.checkResult();
    }

    static class CalculateThread extends Thread {

        private final int[] arr;
        private final int startSearch;
        private final int endSearch;

        public CalculateThread(int[] arr, int startSearch, int endSearch) {
            this.arr = arr;
            this.startSearch = startSearch;
            this.endSearch = endSearch;
        }

        @Override
        public void run() {
            long localSum = 0;
            for (int i = startSearch; i < endSearch; i++) {
                localSum += arr[i];
            }

            try {
                sumSemaphore.acquire();
                totalSum += localSum;
                sumSemaphore.release();

                doneSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public Double calculateAverageGrade() {
            return Arrays.stream(arr).average().getAsDouble();
        }
    }

    /******************************************************
     // DO NOT CHANGE THE CODE BELOW TO THE END OF THE FILE
     *******************************************************/

    static class BoundedRandomGenerator {
        static final Random random = new Random();
        static final int RANDOM_BOUND_UPPER = 10;
        static final int RANDOM_BOUND_LOWER = 6;

        public int nextInt() {
            return random.nextInt(RANDOM_BOUND_UPPER - RANDOM_BOUND_LOWER) + RANDOM_BOUND_LOWER;
        }

    }

    static class ArrayGenerator {

        private static double actualAvg = 0;

        static int[] generate(int length) {
            int[] array = new int[length];

            for (int i = 0; i < length; i++) {
                int grade = Grades.random.nextInt();
                actualAvg += grade;
                array[i] = grade;
            }

            actualAvg /= array.length;

            return array;
        }
    }

    static class SynchronizationChecker {
        public static void checkResult() {
            if (ArrayGenerator.actualAvg != average) {
                throw new RuntimeException("The calculated result is not equal to the actual average grade!");
            }
        }
    }
}

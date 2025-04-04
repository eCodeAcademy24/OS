package sushibar;

import defaultClasses.ProblemExecution;
import defaultClasses.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class SushiBarSolution {


    //TODO: definiraj semafori


    //TODO: iniciijaziraj semafori
    public static void init() {

    }

    public static class Customer extends TemplateThread {

        public Customer(int numRuns) {
            super(numRuns);
        }

        //TODO: implementacija na execute()
        @Override
        public void execute() throws InterruptedException {

        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    static SushiBarState state = new SushiBarState();

    public static void run() {
        try {
            int numRuns = 1;
            int numIterations = 1200;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Customer c = new Customer(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
            // System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
package shared_counter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Counter extends Thread {
    public static int COUNTER = 0;

    public static Semaphore s = new Semaphore(1);

    public Counter() {

    }

    @Override
    public void run() {
        try {
            increment();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void increment() throws InterruptedException {
        for(int i = 1; i <= 10000; i++) {
            s.acquire();
//            incrementOnes();
            COUNTER++;
            s.release();
        }
    }

//    private static synchronized void incrementOnes() {
//        COUNTER++;
//    }
}

public class Main {
    public static void main(String[] args) {
        List<Counter> threads = new ArrayList<>();

        for(int i = 1; i <= 1000; i++) {
            threads.add(new Counter());
        }

        for(Counter c : threads) {
            c.start();
        }

        for(Counter c : threads) {
            try {
                c.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(Counter.COUNTER);
    }
}

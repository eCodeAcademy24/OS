package intro;

import java.util.ArrayList;
import java.util.List;

class Printer extends Thread {

    int number;

    public Printer(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(number * 10L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(number);
    }
}

public class Main {
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        int n = 100;
        for (int i = 1; i <= n; i++) {
            threads.add(new Printer(i));
        }

        //start all the threads in the list
        for(Thread t : threads) {
            t.start();
        }

        //waits for all the threads in the list to finish
        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
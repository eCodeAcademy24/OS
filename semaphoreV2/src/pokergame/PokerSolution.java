package pokergame;

import defaultClasses.ProblemExecution;
import defaultClasses.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class PokerSolution {

    //TODO: definiraj semafori
    static Semaphore canSit;

    static Semaphore canDeal;

    static int playerCount;

    static  Semaphore lock;

    //TODO: inicijaziraj semafori
    public static void init() {
        canSit = new Semaphore(6);
        lock = new Semaphore(1);
        canDeal = new Semaphore(0);
    }

    public static class Player extends TemplateThread {

        public Player(int numRuns) {
            super(numRuns);
        }

        //TODO: implementiraj execute() kaj Player
        @Override
        public void execute() throws InterruptedException {
            canSit.acquire();
            lock.acquire();
            state.playerSeat();
            playerCount++;
            if (playerCount == 6) {
                state.dealCards();
                canDeal.release(5);
            }
            lock.release();

            canDeal.acquire();
            state.play();

            lock.acquire();
            playerCount--;
            if (playerCount == 0) {
                state.endRound();
                canSit.release(6);
            }
            lock.release();

            // dokolku treba site da povikaat endRound
//            lock.acquire();
//            state.endRound();
//            canSit.release(6);
//            playerCount = 0;
//            lock.release();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    static PokerState state = new PokerState();

    public static void run() {
        try {
            int numRuns = 1;
            int numIterations = 1200;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Player c = new Player(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
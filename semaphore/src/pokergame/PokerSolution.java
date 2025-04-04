package pokergame;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import defaultClasses.ProblemExecution;
import defaultClasses.TemplateThread;

public class PokerSolution {

    //TODO: definiraj semafori
    static Semaphore canSit;
    static Semaphore lock;
    static Semaphore canDeal;
    static int playerCount;

    //TODO: inicijaziraj semafori
    public static void init() {
        playerCount = 0;
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
            state.playerSeat();
            lock.acquire();
            playerCount++;
            if(playerCount == 6){
                state.dealCards();
                canDeal.release(6);
            }
            lock.release();

            canDeal.acquire();
            state.play();

            lock.acquire();
            state.endRound();
            canSit.release(6);
            playerCount = 0;
            lock.release();

//            lock.acquire(); ova reshenie e za dokolku se bara igracite da ja napustaat masata eden po eden
//            playerCount--;
//            if(playerCount == 0){
//                state.endRound();
//                canSit.release(6);
//            }
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
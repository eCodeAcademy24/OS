package baboonCrossing;

import defaultClasses.ProblemExecution;
import defaultClasses.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;


public class BaboonCrossingSolution {
    //TODO: definiraj semafori
    public static Semaphore controlRope;

    public static Semaphore baboonLeftLock;

    public static Semaphore baboonRightLock;

    public static Semaphore baboonsOnRope;

    public static int leftCount;

    public static int rightCount;

    //TODO: inicijaliziraj semafori so pomos na init()
    public static void init() {
        controlRope = new Semaphore(1);
        baboonLeftLock = new Semaphore(1);
        baboonRightLock = new Semaphore(1);
        baboonsOnRope = new Semaphore(5);
        leftCount = 0;
        rightCount = 0;
    }

    public static class BaboonLeft extends TemplateThread {

        public BaboonLeft(int numRuns) {
            super(numRuns);
        }

        //TODO: definiraj execute() na BaboonLeft
        @Override
        public void execute() throws InterruptedException {
            /*  somnitelno scenario, kade moze da se sluchi pri vlez na edna strana vo jazeto, vekje od drugata
                da ima vlezeno babun, pa zatoa obavezno se pravi taa proverka
            */
            baboonLeftLock.acquire();
            if (leftCount == 0) {
                controlRope.acquire();
            }
            state.enter(this);
            leftCount++;
            //otkako vlegol prviot, se naznacuva deka momentalno kje go koristi jazeto od soodvetnata strana
            if (leftCount == 1) {
                state.leftPassing();
            }
            baboonLeftLock.release();

            //istovremeno mozat da vlezat 5 babuni na jazeto
            baboonsOnRope.acquire();
            state.cross(this); // nivna implementacija, zamislete deka samo pecatime tuka
            baboonsOnRope.release();

            baboonLeftLock.acquire();
            state.leave(this);
            leftCount--;
            if (leftCount == 0) { //koga posledniot kje premine od drugata strana, dava signal deka jazeto e slobodno
                controlRope.release();
            }
            baboonLeftLock.release();
        }
    }

    public static class BaboonRight extends TemplateThread {

        public BaboonRight(int numRuns) {
            super(numRuns);
        }

        //TODO: definiraj execute() na BaboonRight
        @Override
        public void execute() throws InterruptedException {
             /*  somnitelno scenario, kade moze da se sluchi pri vlez na edna strana vo jazeto, vekje od drugata
                da ima vlezeno babun, pa zatoa obavezno se pravi taa proverka
            */
            baboonRightLock.acquire();
            if (rightCount == 0) {
                controlRope.acquire();
            }
            state.enter(this);
            rightCount++;
            //otkako vlegol prviot, se naznacuva deka momentalno kje go koristi jazeto soodvetnata strana
            if (rightCount == 1) {
                state.rightPassing();
            }
            baboonRightLock.release();

            //istovremeno mozat da vlezat 5 babuni na jazeto
            baboonsOnRope.acquire();
            state.cross(this);
            baboonsOnRope.release();

            baboonRightLock.acquire();
            state.leave(this);
            rightCount--;
            if (rightCount == 0) { //koga posledniot kje premine od drugata strana, dava signal deka jazeto e slobodno
                controlRope.release();
            }
            baboonRightLock.release();
        }
    }

    static BaboonCrossingState state = new BaboonCrossingState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 500;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                BaboonLeft l = new BaboonLeft(numRuns);
                BaboonRight r = new BaboonRight(numRuns);
                threads.add(l);
                threads.add(r);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package vinegar;

import java.util.HashSet;
import java.util.concurrent.Semaphore;


public class Vinegar {

    static Semaphore c = new Semaphore(2);

    static Semaphore o = new Semaphore(2);

    static Semaphore h = new Semaphore(4);

    static Semaphore hHere = new Semaphore(0);

    static Semaphore oHere = new Semaphore(0);

    static Semaphore lock = new Semaphore(1);

    static int cCounter = 0;

    static Semaphore canBond = new Semaphore(0);

    static Semaphore bondSuccess = new Semaphore(0);


    public static void main(String[] args) {
        HashSet<Thread> threads = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            threads.add(new C());

            threads.add(new H());

            threads.add(new H());

            threads.add(new O());
        }

        for(Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        boolean deadlock = false;
        for (Thread t : threads) {
            if (t.isAlive()) {
                deadlock = true;
                System.out.println("Possible deadlock!");
                t.interrupt();
            }
        }

        if(!deadlock) {
            System.out.println("Process finished");
        }
    }

    static class C extends Thread{

        public void execute() throws InterruptedException {
            c.acquire();
            lock.acquire();
            cCounter++;
            if (cCounter == 2) {
                cCounter = 0;
                System.out.println("C here");
                lock.release();
                hHere.acquire(4);
                oHere.acquire(2);
                canBond.release(7);
                lock.acquire();
                System.out.println("Molecule bonding");
                System.out.println("C done");
                lock.release();
                bondSuccess.acquire(7);
                System.out.println("Molecule created");
                c.release(2);
                o.release(2);
                h.release(4);
            } else {
                System.out.println("C here");
                lock.release();
                canBond.acquire();
                lock.acquire();
                System.out.println("Molecule bonding");
                System.out.println("C done");
                lock.release();
                bondSuccess.release();
            }
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class H extends Thread{

        public void execute() throws InterruptedException {
            h.acquire();
            lock.acquire();
            hHere.release();
            System.out.println("H here");
            lock.release();
            canBond.acquire();
            lock.acquire();
            System.out.println("Molecule bonding");
            System.out.println("H done");
            lock.release();
            bondSuccess.release();
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    static class O extends Thread {


        public void execute() throws InterruptedException {
            o.acquire();
            lock.acquire();
            oHere.release();
            System.out.println("O here");
            lock.release();
            canBond.acquire();
            lock.acquire();
            System.out.println("Molecule Bonding");
            System.out.println("O done");
            lock.release();
            bondSuccess.release();
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}


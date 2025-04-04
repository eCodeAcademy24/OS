package vinegar;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

//Во една фабрика потребно е производство на оцет - C2H4O2.
//        Во процесот на производство треба да се присутни 2 јаглеродни (C) атоми, 4 водородни (H) и 2 кислородни (О) атоми.
//        Молекулите на C2H4O2 се формираат една по една.
//        Секој од атомите е претставен преку соодветна класа, во која execute() методот треба да се извршува во позадина.
//        Во execute методот, треба да овозможите да се извршуваат паралелно максимум 2 јаглеродни (C) атоми, 4 водородни (H)
//        и 2 кислородни (О) атоми. По влегувањето на секој од атомите треба да се испечати порака дека е присутен. Потоа,
//        атомите треба да чекаат додека сите потребни атоми за молекулата пристигнат, по што се печати Molecule bonding.
//        од страна на сите атоми. Откако ќе заврши спојувањето, секој од методите печати дека е завршен. На крајот треба само
//        еден атом да испечати Molecule created. и да овозможи креирање на нова молекула.
//        Вашата задача е во main методот да стартувате 20 јаглеродни, 40 водородни и 20 кислородни атоми, кои ќе се извршуваат
//        во позадина. Потоа треба да почекате 2 секунди за да завршат сите. Они кои не завршиле, треба да ги прекинете и да
//        испечатите Possible deadlock!. Ако сите завршиле без да ги прекинете, испечатете Process finished..
//        Вашата задача е да го дополните дадениот код според барањата на задачата, при што треба да внимавате не настане Race
//        Condition и Deadlock.

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

        // run all threads in background
        for (Thread t : threads) {
            t.start();
        }

        // after all of them are started, wait each of them to finish for maximum 2_000 ms
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // for each thread, terminate it if it is not finished
        boolean deadlock = false;
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                deadlock = true;
                System.out.println("Possible deadlock!");
                thread.interrupt();
            }
        }
        if(!deadlock) System.out.println("Process finished.");


    }


    static class C extends Thread implements Runnable {

        public void execute() throws InterruptedException {

            // at most 2 atoms should print this in parallel
            c.acquire();
            lock.acquire();
            cCounter++;
            if (cCounter == 2) {
                cCounter = 0;
                System.out.println("C here.");
                lock.release();
                hHere.acquire(4); // 4 -> 3, 3 -> 2, 2 -> 1, 1 -> 0
                oHere.acquire(2); // 2 -> 1 , 1 -> 0
                canBond.release(7);
                lock.acquire();
                System.out.println("Molecule bonding.");
                System.out.println("C done.");
                lock.release();
                bondSuccess.acquire(7);
                System.out.println("Molecule created.");
                c.release(2);
                o.release(2);
                h.release(4);
            } else {
                System.out.println("C here.");
                lock.release();
                canBond.acquire();
                lock.acquire();
                System.out.println("Molecule bonding.");
                System.out.println("C done.");
                lock.release();
                bondSuccess.release();
            }

            // after all atoms are present, they should start with the bonding process together

            // this represent the bonding process

            // only one atom should print the next line, representing that the molecule is created


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


    static class H extends Thread implements Runnable {


        public void execute() throws InterruptedException {
            h.acquire();
            lock.acquire();
            hHere.release();
            System.out.println("H here.");
            lock.release();
            canBond.acquire();
            lock.acquire();
            System.out.println("Molecule bonding");
            System.out.println("H done.");
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


    static class O extends Thread implements Runnable {


        public void execute() throws InterruptedException {
            o.acquire();
            lock.acquire();
            oHere.release();
            System.out.println("O here.");
            lock.release();
            canBond.acquire();
            lock.acquire();
            System.out.println("Molecule bonding");
            System.out.println("O done.");
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


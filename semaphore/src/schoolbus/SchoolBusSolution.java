package schoolbus;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import defaultClasses.ProblemExecution;
import defaultClasses.TemplateThread;


public class SchoolBusSolution {

    //TODO: definiraj semafori


    static int students;

    //TODO: iniciijaziraj semafori
    public static void init() {

    }

    public static class Driver extends TemplateThread {

        public Driver(int numRuns) {
            super(numRuns);
        }

        //TODO: implementacija na execute() kaj Driver
        @Override
        public void execute() throws InterruptedException {

        }
    }

    public static class Student extends TemplateThread {

        public Student(int numRuns) {
            super(numRuns);
        }

        //TODO: implementacija na execute() kaj Student
        @Override
        public void execute() throws InterruptedException {

        }
    }
    static SchoolBusState state = new SchoolBusState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 1000;
            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                Student p = new Student(numRuns);
                threads.add(p);
                if (i % 50 == 0) {
                    Driver c = new Driver(numRuns);
                    threads.add(c);
                }
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
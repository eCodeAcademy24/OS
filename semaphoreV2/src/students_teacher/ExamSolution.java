package students_teacher;

import defaultClasses.ProblemExecution;
import defaultClasses.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class ExamSolution {

    // TODO: definiraj gi potrebnite semafori
    static Semaphore teacherInside;

    static Semaphore studentsEnter;

    static Semaphore studentsHere;

    static Semaphore studentsCanLeave;

    static Semaphore teacherLeave;

    static int studentsCount;

    static Semaphore lock;

    // TODO: inicijaliziraj nivnata vrednost soodvetno
    public static void init() {
        teacherInside = new Semaphore(1);
        studentsEnter = new Semaphore(0);
        studentsHere = new Semaphore(0);
        studentsCanLeave = new Semaphore(0);
        studentsCount = 0;
        lock = new Semaphore(1);
    }

    public static class Teacher extends TemplateThread {

        public Teacher(int numRuns) {
            super(numRuns);
        }


        // TODO: implementiraj ja execute metodata kaj Teacher
        @Override
        public void execute() throws InterruptedException {
            teacherInside.acquire();
            state.teacherEnter();
            studentsEnter.release(50);

            studentsHere.acquire();
            state.distributeTests();
            state.examEnd();
            studentsCanLeave.release(50);

            teacherLeave.acquire();
            state.teacherLeave();
            teacherInside.release();
        }
    }

    public static class Student extends TemplateThread {

        public Student(int numRuns) {
            super(numRuns);
        }

        // TODO: implementiraj ja execute metodata kaj Student
        @Override
        public void execute() throws InterruptedException {
            studentsEnter.acquire();
            lock.acquire();
            state.studentEnter();
            studentsCount++;
            if (studentsCount == 50) {
                studentsHere.release(50);
            }
            lock.release();

            studentsCanLeave.acquire();
            lock.acquire();
            studentsCount--;
            state.studentLeave();
            if (studentsCount == 0) {
                teacherLeave.release();
            }
            lock.release();
        }
    }

    static ExamState state = new ExamState();

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
                    Teacher c = new Teacher(numRuns);
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
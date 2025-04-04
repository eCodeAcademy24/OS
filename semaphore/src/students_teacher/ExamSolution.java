package students_teacher;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import defaultClasses.ProblemExecution;
import defaultClasses.TemplateThread;

public class ExamSolution {

    static Semaphore teacherInside;
    static Semaphore lock;
    static Semaphore studentsEnter;
    static Semaphore studentsHere;
    static Semaphore studentsCanLeave;
    static Semaphore teacherLeave;
    static int students;
    public static void init() {
        teacherInside = new Semaphore(1);
        studentsEnter = new Semaphore(0);
        lock = new Semaphore(1);
        studentsHere = new Semaphore(0);
        studentsCanLeave = new Semaphore(0);
        students = 0;
    }

    public static class Teacher extends TemplateThread {

        public Teacher(int numRuns) {
            super(numRuns);
        }

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

        @Override
        public void execute() throws InterruptedException {
            studentsEnter.acquire();
            state.studentEnter();
            lock.acquire();
            students++;
            state.studentEnter();
            if(students == 50){
                studentsHere.release();
            }
            lock.release();

            studentsCanLeave.acquire();
            lock.acquire();
            students--;
            state.studentLeave();
            if(students == 0){
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
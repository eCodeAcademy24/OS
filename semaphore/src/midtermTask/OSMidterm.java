package midtermTask;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Semaphore;

//Сценариото кој овој таск го покрива е изминување на матрица, која содржи карактери и бројки на рандом позиции кои
// не ги знаеме. При изминувањето на матрицата по редици потребно е да се проверува дали елементот е број или карактер
// и ако е карактер истиот да се конкатанира со претходно изминатите карактери за на крајот да се добие целосен стринг
// од сите карактери по редослед на изминувањето.
//
//        Дадениот почетен код е со имплементирани функционалности за изработка на задачата без паралелизам.
//        Ваша задача е овој код да го транспортирате во код кој ќе работи со паралелни нишки така што ќе ја изминувате
//        секоја редица од матрицата како посебна нишка.
//
//        Информации за секоја од класите и функционалностите во нив:
//
//        DataMatrix - класа за имплементација на матрицата
//        Податоци:
//        m-редици n-колони data-податоците во матрицата
//        Функции:
//        getM() -> број редици
//        getN() -> број колони
//        getEl(int i, int j) -> елементот на позиција (i,j)
//        getRow(int pos) -> податоците од редицата на индекс pos
//        getColumn(int pos) -> податоците од колоната на индекс pos
//        isString(int i, int j) -> проверка дали елементот на позиција (i,j) е стринг
//
//        StatisticsResource - класа каде се чува стрингот кој ќе го конкатанирате
//        Податоци:
//        concatenatedString -> стрингот кој ќе го споите
//        Функции:
//        concatenateString(String new_character) -> се спојува новиот карактер со целиот стринг
//        printString() -> се принта стрингот
//
//        Concatenation - класа во која се функционалностите за изминување на матрицата и конкатанирање на стрингот
//        ОВАА КЛАСА ВИЕ ТРЕБА ДА ЈА МОДИФИЦИРАТЕ ВО НИТКА
//        Податоци:
//        matrix -> матрицата
//        statisticsResource -> стрингот кој ќе го конкатанирате
//        Функции:
//        concatenate() -> ги спојува карактерите во statisticsResource стрингот со изминување на целата матрица,
//        oваа функција е само да видите како треба да ви изгледа крајниот резултат од задачата истата не ја користите
//        во имплементација на нитките
//        concatenate_by_row() -> ВИЕ ЈА ИМПЛЕМЕНТИРАТЕ ОВАА ФУНКЦИЈА КОЈА: изминува eдна редица од матрицата и
//        ги спојува карактерите во стрингот. Ако ви е потребно може да додадете аргументи во функцијата
//        execute() -> ВИЕ ЈА ИМПЛЕМЕНТИРАТЕ ОВАА ФУНКЦИЈА КОЈА: има повик до concatenate_by_row()
//
//        Методите во класата StatisticsResource и DataMatrix треба да останат непроменети.
//
//        Бројот на нитки треба да одговара на бројот на редици на матрицата.
//
//        При синхронизација на работата на нитките, дозволено ви е користење на произволен број на семафори.
//        Потребно е да ги идентификувате сите критични региони кои може да произлезат при имплементација на новото решение.
//        Испечатените резултати кои ќе се добијат при извршување на програмата, треба да бидат идентични со оригиналните.
//
//        Решението прикачете го во форма на .JAVA фајл.
class OSMidterm {

    //TODO: Initialize the semaphores you need
    static Semaphore lock = new Semaphore(1);
    static int counter = 0;

    public static void main(String[] args) {

        //STARTING CODE, DON'T MAKE CHANGES
        //-----------------------------------------------------------------------------------------
        String final_text="Bravo!!! Ja resi zadacata :)";
        int m=10, n=100;
        Object[][] data = new Object[m][n];
        Random rand = new Random();
        int k=0;
        for (int i=0;i<m;i++) {
            for (int j=0;j<n;j++) {
                int random = rand.nextInt(100);
                if(random%2==0 & k<final_text.length()) {
                    data[i][j] = final_text.charAt(k);
                    k++;
                } else {
                    data[i][j] = rand.nextInt(100);
                }
            }
        }

        DataMatrix matrix = new DataMatrix(m,n, data);
        StatisticsResource statisticsResource = new StatisticsResource();
        //-----------------------------------------------------------------------------------------

        //ONLY TESTING CODE, SO YOU CAN TAKE A LOOK OF THE OUTPUT YOU NEED TO GET AT THE END
        //YOU CAN COMMENT OR DELETE IT AFTER YOU WRITE THE CODE USING THREADS
        //-----------------------------------------------------------------------------------------
        HashSet<Concatenation> threads = new HashSet<Concatenation>();

        for(int i = 0; i < 10; i++){
            Concatenation c = new Concatenation(matrix, statisticsResource);
            threads.add(c);
        }

        for(Thread t : threads){
            t.start();
        }

        for(Thread t : threads){
            try {
                t.join();
                Thread.sleep(100); // за да 10 секунди треба да биде на 10 000
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        statisticsResource.printString();

        for(Concatenation t : threads){ // може и Thread t : threads
            if(t.isAlive()){
                System.out.println("Possible deadlock!");
                t.interrupt();
            }
        }


        //-----------------------------------------------------------------------------------------

        //TODO: Run the threads from the Concatenation class

        //TODO: Wait 10seconds for all threads to finish

        //TODO: Print the string you get, call function printString()

        //TODO: Check if some thread is still alive, if so kill it and print "Possible deadlock"

    }

    // TODO: Make the Concatenation Class  a Thread Class
    static class Concatenation extends Thread {

        private DataMatrix matrix;
        private StatisticsResource statisticsResource;

        public Concatenation(DataMatrix matrix, StatisticsResource statisticsResource) {
            this.matrix = matrix;
            this.statisticsResource = statisticsResource;
        }
        //concatenation function implemented on the whole matrix, so you can take a look of the task's logic
        public void concatenate() {
            for (int i=0;i<this.matrix.getM();i++) {
                for (int j=0;j<this.matrix.getN();j++) {
                    if (this.matrix.isString(i,j)) {
                        this.statisticsResource.concatenateString(this.matrix.getEl(i,j).toString());
                    }
                }
            }
        }

        public void concatenate_by_row() throws InterruptedException {
            //TODO: Implement this function
            // add  arguments in the function if needed
            lock.acquire();
            counter++;
            for (int i=0;i<this.matrix.getN();i++) {
                if(this.matrix.isString(counter - 1, i)){
                    this.statisticsResource.concatenateString(this.matrix.getEl(counter - 1, i).toString());
                }
            }
            lock.release();
        }
        public void execute(){
            try {
                concatenate_by_row();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            execute();
        }
    }

    //-------------------------------------------------------------------------
    //YOU ARE NOT CHANGING THE CODE BELOW
    static class DataMatrix {

        private int m,n;
        private Object[][] data;

        public DataMatrix(int m, int n, Object[][] data) {
            this.m = m;
            this.n = n;
            this.data = data;
        }

        public int getM() {
            return m;
        }

        public int getN() {
            return n;
        }

        public Object[][] getData() {
            return data;
        }

        public Object getEl(int i, int j) {
            return data[i][j];
        }

        public Object[] getRow(int pos) {
            return this.data[pos];
        }

        public Object[] getColumn(int pos) {
            Object[] result = new Object[m];
            for (int i=0;i<m;i++) {
                result[i]=data[i][pos];
            }
            return result;
        }

        public boolean isString(int i, int j) {
            return this.data[i][j] instanceof Character;
        }


    }

    static class StatisticsResource {

        private String concatenatedString;

        public StatisticsResource() {
            this.concatenatedString = "";
        }

        //function for String concatenation
        public void concatenateString(String new_character) {
            concatenatedString+=new_character;
        }

        //function for printing the concatenated string
        public void printString() {
            System.out.println("Here is the phrase from the matrix: " + concatenatedString);
        }

    }

}


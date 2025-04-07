package ca3n2;

public class Ca3N2 {

    public static int NUM_RUN = 50;
    // TODO: definiraj semafori

    // TODO: inicijaliziraj semafori
    public void init() {

    }

    public static class Ca extends Thread {

        public void bond() {
            System.out.println("Ca is bonding now.");
        }

        public void validate() {
            System.out.println("Success in bonding");
        }

        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // TODO: implemetiraj execute() kaj Ca
        public void execute() throws InterruptedException {

        }

    }

    public static class N extends Thread {

        // TODO: implemetiraj execute() kaj N
        public void execute() throws InterruptedException {

        }

        public void bond() {
            System.out.println("N is bonding now.");
        }

        public void validate() {
            System.out.println("Success in bonding");
        }


        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


import java.io.*;
import java.net.*;
import java.util.*;

class Logger {
    private static final Object lock = new Object();

    public static void logToFile(String message) {
        synchronized (lock) {
            try {
                FileWriter fw = new FileWriter("chatlog.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                out.println(message);
                out.flush();
            } catch (IOException e) {
                System.out.println("Грешка при запишување во датотека: " + e.getMessage());
            }
        }
    }
}


class ClientSend extends Thread {
    private final PrintWriter out;
    private final Queue<String> queue = new LinkedList<>();

    public ClientSend(Socket socket) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message;
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        queue.wait();
                    }
                    message = queue.poll();
                }

                out.println(message);
                Logger.logToFile(message);
                System.out.println("Испратено: " + message);
            }
        } catch (InterruptedException e) {
            System.out.println("Прекината нишка за праќање.");
        }
    }

    public void addMessage(String message) {
        synchronized (queue) {
            queue.add(message);
            queue.notify();
        }
    }
}

class ClientReceive extends Thread {
    private final BufferedReader in;
    private final ClientSend sender;
    private final String INDEX = "123456";

    public ClientReceive(Socket socket, ClientSend sender) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Примено: " + line);
                Logger.logToFile(line);

                if (line.contains("Succesfully logged on to server")) {
                    sender.addMessage("hello:" + INDEX);
                }
            }
        } catch (IOException e) {
            System.out.println("Примањето е прекинато: " + e.getMessage());
        }
    }
}


public class ChatClient {
    public static final String ADDRESS = "194.149.135.49";
    public static final int PORT = 9753;
    public static final String INDEX = "123456";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(ADDRESS, PORT);
            ClientSend sender = new ClientSend(socket);
            ClientReceive receiver = new ClientReceive(socket, sender);

            sender.start();
            receiver.start();

            sender.addMessage("login:" + INDEX);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    socket.close();
                    System.exit(0);
                }

                sender.addMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Грешка при поврзување или комуникација: " + e.getMessage());
        }
    }
}

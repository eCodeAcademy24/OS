package tcp;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    private final int PORT = 8765;
    private final File outputFile;

    public Server(String fileOutputPath) {
        this.outputFile = new File(fileOutputPath);
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Mail Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new Worker(clientSocket, outputFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java tcp.MailServer <path_to_output_file>");
            return;
        }

        Server server = new Server(args[0]);
        server.start();
    }
}

package tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Worker implements Runnable {
    private final Socket clientSocket;
    private final File fileOutput;

    public Worker(Socket clientSocket, File fileOutput) {
        this.clientSocket = clientSocket;
        this.fileOutput = fileOutput;
    }

    @Override
    public void run() {
        String clientIp = clientSocket.getInetAddress().getHostAddress();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("HELLO " + clientIp);
            out.flush();

            String helloResponse = in.readLine();
            if (helloResponse == null || !helloResponse.startsWith("HELLO ")) {
                throw new IOException("Invalid HELLO message from the client: " + helloResponse);
            }

            out.println("SEND DAILY DATA");
            out.flush();

            String dataLine = in.readLine();
            if (!isValidDataLine(dataLine)) {
                throw new IOException("Invalid data: " + dataLine);
            }

            appendToFile(dataLine);

            out.println("OK");
            out.flush();

            String quit = in.readLine();
            if (!"QUIT".equals(quit)) {
                throw new IOException("Expected QUIT, got: " + quit);
            }

            System.out.println("Client " + clientIp + " finished the session.");
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error with client " + clientIp + ": " + e.getMessage());
        }
    }

    private boolean isValidDataLine(String line) {
        if (line == null) return false;
        String[] parts = line.split(",");
        if (parts.length != 4) return false;

        try {
            Integer.parseInt(parts[1].trim());
            Integer.parseInt(parts[2].trim());
            Integer.parseInt(parts[3].trim());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private synchronized void appendToFile(String line) throws IOException {
        boolean writeHeader = !fileOutput.exists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileOutput, true))) {
            if (writeHeader) {
                writer.println(
                        "date," +
                                "No. of vaccinated with Pfizer," +
                                "No. of vaccinated with Sinovac," +
                                "No. of vaccinated with AstraZeneca"
                );
            }
            writer.println(line);
        }
    }
}

public class CovidServer extends Thread {
    private final int PORT = 5555;
    private final File outputFile;

    public CovidServer(String fileOutputPath) {
        this.outputFile = new File(fileOutputPath);
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

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
            System.err.println("Usage: java tcp.CovidServer <path_to_csv_data>");
            return;
        }

        CovidServer server = new CovidServer(args[0]);
        server.start();
    }
}

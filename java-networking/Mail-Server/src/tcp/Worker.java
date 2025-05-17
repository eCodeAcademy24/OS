package tcp;

import java.io.*;
import java.net.Socket;

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
            out.println("START " + clientIp);
            log("START " + clientIp);

            String mailToLine = in.readLine();
            if (!mailToLine.startsWith("MAIL TO: ")) throw new IOException("Invalid MAIL_TO line");
            String mailTo = mailToLine.substring(9);
            log(mailToLine);
            validateEmail(mailTo);
            out.println("TNX");

            String mailFromLine = in.readLine();
            if (!mailFromLine.startsWith("MAIL FROM: ")) throw new IOException("Invalid MAIL FROM line");
            String mailFrom = mailFromLine.substring(11);
            log(mailFromLine);
            validateEmail(mailFrom);
            out.println("200");

            String mailCCLine = in.readLine();
            if (!mailCCLine.startsWith("MAIL CC: ")) throw new IOException("Invalid MAIL_CC line");
            String mailCC = mailCCLine.substring(9);
            log(mailCCLine);
            out.println("RECEIVERS: " + mailTo + ", " + mailCC);

            int linesCount = 0;
            String dataLine;
            while ((dataLine = in.readLine()) != null) {
                if (dataLine.equals("?")) break;
                log(dataLine);
                linesCount++;
            }

            out.println("RECEIVED " + linesCount + " lines");

            String exitLine = in.readLine();
            if (!"EXIT".equals(exitLine)) throw new IOException("Expected EXIT");
            log("EXIT");

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error with client " + clientIp + ": " + e.getMessage());
        }
    }

    private void validateEmail(String email) throws IOException {
        if (email == null || !email.contains("@")) {
            throw new IOException("Invalid email address: " + email);
        }
    }

    private synchronized void log(String line) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileOutput, true))) {
            writer.println(line);
        }
    }
}
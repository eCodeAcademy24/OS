package tcp;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Worker implements Runnable {
    private final Socket clientSocket;

    public Worker(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        String clientIP = clientSocket.getInetAddress().getHostAddress();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String handshake = in.readLine();
            if (!"HANDSHAKE".equals(handshake)) {
                clientSocket.close();
                return;
            }

            out.write("Logged In " + clientIP + "\n");
            out.flush();

            String input;
            while ((input = in.readLine()) != null) {
                if ("STOP".equalsIgnoreCase(input)) {
                    out.write(Server.knownWords.size() + "\n");
                    out.write("LOGGED OUT\n");
                    out.flush();
                    clientSocket.close();
                    break;
                }

                boolean known;
                synchronized (Server.knownWords) {
                    known = Server.knownWords.contains(input);
                    if (!known) {
                        Server.knownWords.add(input);
                        logWord(input, clientIP);
                    }
                }


                out.write(input + " " + (known ? "IMA" : "NEMA") + "\n");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logWord(String word, String clientIP) {
        String filePath = "D:\\ecode\\Kurs\\OS\\Zadaci\\java-networking\\Last-Word\\src\\known-words.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            bw.write(word + " | " + timestamp + " | " + clientIP + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
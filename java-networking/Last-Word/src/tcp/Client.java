package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    private final int serverPort;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), serverPort);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String[] sampleWords = {
                    "apple", "banana", "cherry", "date", "elderberry",
                    "fig", "grape", "apple", "banana", "stop"
            };

            bw.write("HANDSHAKE\n");
            bw.flush();

            System.out.println("Client received: " + br.readLine());

            for (String word : sampleWords) {
                bw.write(word + "\n");
                bw.flush();

                String reply = br.readLine();
                System.out.println("Client received: " + reply);

                if ("STOP".equalsIgnoreCase(word)) {
                    String count = br.readLine();
                    System.out.println("Client received: " + count);
                    String logout = br.readLine();
                    System.out.println("Client received: " + logout);
                    break;
                }

                Thread.sleep(200);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client(7391).start();
    }
}

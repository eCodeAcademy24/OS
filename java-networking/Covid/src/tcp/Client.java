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
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                System.out.println("Server: " + line);

                if (line.startsWith("HELLO ")) {
                    int localPort = socket.getLocalPort();
                    String helloMsg = "HELLO " + localPort;
                    out.write(helloMsg + "\n");
                    out.flush();
                    System.out.println("Client: " + helloMsg);
                } else if (line.equals("SEND DAILY DATA")) {
                    String dailyData = "12/02/2026, 10, 10000, 1500";
                    out.write(dailyData + "\n");
                    out.flush();
                    System.out.println("Client: " + dailyData);
                } else if (line.equals("OK")) {
                    String quitMsg = "QUIT";
                    out.write(quitMsg + "\n");
                    out.flush();
                    System.out.println("Client: " + quitMsg);
                    break;
                } else {
                    System.out.println("Unexpected line: " + line);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client(5555);
        client.start();
    }
}

package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {

    private final int serverPort;

    private final String emailFrom = "sender@example.com";
    private final String emailCC = "cc@example.com";

    private final String[] messageLines = {
            "Hello, this is the first line of the email.",
            "This is the second line.",
            "Regards,",
            "Client"
    };

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Server: " + line);

                if (line.startsWith("START ")) {
                    String emailTo = "receiver@example.com";
                    String mailToMsg = "MAIL TO: " + emailTo;
                    out.write(mailToMsg + "\n");
                    out.flush();
                    System.out.println("Client: " + mailToMsg);

                } else if (line.equals("TNX")) {
                    String mailFromMsg = "MAIL FROM: " + emailFrom;
                    out.write(mailFromMsg + "\n");
                    out.flush();
                    System.out.println("Client: " + mailFromMsg);

                } else if (line.equals("200")) {
                    String mailCCMsg = "MAIL CC: " + emailCC;
                    out.write(mailCCMsg + "\n");
                    out.flush();
                    System.out.println("Client: " + mailCCMsg);

                } else if (line.startsWith("RECEIVERS: ")) {
                    for (String msgLine : messageLines) {
                        out.write(msgLine + "\n");
                    }
                    out.write("?\n");
                    out.flush();
                    System.out.println("Client: Sent all message lines and '?'");

                } else if (line.startsWith("RECEIVED ")) {
                    out.write("EXIT\n");
                    out.flush();
                    System.out.println("Client: EXIT");
                    break;

                } else {
                    System.out.println("Unexpected message: " + line);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client(8765);
        client.start();
    }
}

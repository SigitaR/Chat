import java.io.*;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by Sigute on 10/8/2017.
 */
public class ClientThread extends Thread {
    private String clientName = null;
    private BufferedReader is = null;
    public PrintWriter os = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private Vector usernames = new Vector();

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
    }

    public void run() {

        ClientThread[] threads = this.threads;

        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);


      // New client.

            synchronized (this) {
                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        Server s = new Server();
                        clientName = s.getClientName();
                        break;
                    }
                }

                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].os.println("-- User " + clientName
                                + " entered the chat room");
                        Server s = new Server();
                        usernames = s.getUsernames();
                        threads[i].os.println("Users online: " +
                                usernames.toString().substring(1, usernames.toString().length() - 1));

                    }
                }
            }
      //Broadcasting onversations.
            while (true) {
                String line = is.readLine();
                if (line.length() > 255) {
                    line = line.substring(5, 255);
                    os.println("J_ER 413: Message length should not exceed 250 characters.");
                    continue;
                }

                if (line.startsWith("QUIT")) {
                    break;
                }

                if (line.startsWith("DATA " + clientName + ":")) {
                    synchronized (this) {
                        for (int i = 0; i < threads.length; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].os.println(line.substring(5));
                            }
                        }
                    }
                } else {
                    os.println("J_ER 400: Syntax error.");
                    continue;
                }

            }
            synchronized (this) {
                this.clientName = clientName;

                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null) {
                        threads[i].os.println("-- " + clientName
                                + " is leaving the chat room");
                        usernames.remove(clientName);
                        threads[i].os.println("Active users: " +
                                usernames.toString().substring(1, usernames.toString().length() - 1));

                    }
                }
            }
            os.println("Quiting: " + clientName + " just left the chat");

      // Clean up

            synchronized (this) {
                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}



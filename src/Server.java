import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by Sigute on 10/4/2017.
 */
public class Server {

        private static ServerSocket serverSocket = null;
        private static Socket clientSocket = null;
        public static String clientName = null;
        private static BufferedReader is = null;
        private static PrintWriter os = null;
        private static final ClientThread[] threads = new ClientThread[20];
        private static final Vector usernames = new Vector();

        public static String getClientName() {
            return clientName;
        }

        public static Vector getUsernames() {
            return usernames;
        }

    public static void main(String args[]) {

            int portNumber = 4545;
                System.out.println("Chat server is running on Port: " + portNumber);

            try {
                serverSocket = new ServerSocket(portNumber);
            } catch (IOException e) {
                System.out.println("J_ER 503: Cannot connect to server " + e);
            }
    //Client socket created and passed to Client thread

            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    os = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                    String username;
                    while (true) {
                        os.println("Enter your username: ");
                        Scanner sc = new Scanner(clientSocket.getInputStream());
                        username = sc.nextLine();

                        UserNameValidator check = new UserNameValidator();
                        if (!check.validate(username)) {
                            os.println("J_ER 406: Invalid username. Special characters are not allowed.");
                            continue;

//                       }
//                         if (check.checkDuplicates(username)) {
//                            os.println("J_ER 406: Username exists. Try again.");
//                            continue;

                     } else {

                            usernames.add(username);
                            clientName = username;
                            os.println("J_OK \nTo leave enter QUIT in a new line.\n" +
                                    "To write a message enter \"DATA\" username: ");

                            Client c = new Client();
                            String host = c.getHost();
                            int port = c.getPort();

                            System.out.println("JOIN " + username + ", " + host + ":" + port);
                            break;

                        }
                    }

                    int i = 0;
                    for (i = 0; i < threads.length; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new ClientThread(clientSocket, threads)).start();
                            os.println("Users online: " +
                                    usernames.toString().substring(1, usernames.toString().length() - 1));
                            break;
                        }
                    }

                } catch (IOException e) {
                    System.out.println("J_ER 400: Cannot create client");
                }
            }
        }

    }




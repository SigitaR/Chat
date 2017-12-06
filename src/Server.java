import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Sigute on 12/3/2017.
 */
public class Server {

    private static final int PORT = 4545;
    private static volatile Set<ServerThread> threadList;
    private static PrintWriter output;

    public static Set<ServerThread> getThreadList() {
        return threadList;
    }

    public static void main(String[] args) throws IOException {
        new Server().runServer();
    }

    private void runServer() throws IOException {
        System.out.println("Chat server is running on Port: " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);
        Socket socket = null;
        threadList = new HashSet<>();
        while (true) {
            String join = "";
            String joinRequest = "";
            while (!(checkUsername(join)).equals("J_OK")) {
                socket = serverSocket.accept();
                output = new PrintWriter(socket.getOutputStream(), true);
                Scanner input = new Scanner(socket.getInputStream());
                joinRequest = input.nextLine();
                if (!"JOIN".equals(joinRequest.substring(0, 4))) {
                    output.println("J_ER 503: Cannot connect to server");
                    System.out.println("J_ER 416: Invalid command: " + joinRequest);
                }
                int index = joinRequest.indexOf(',');
                join = joinRequest.substring(5, index);
                output.println(checkUsername(join));

            }

            System.out.println(joinRequest);

            ServerThread t1 = new ServerThread(socket, join);
            threadList.add(t1);
            t1.start();

            for (ServerThread t : threadList) {
                PrintWriter msg = new PrintWriter(t.getSocket().getOutputStream(), true);
                msg.println("LIST " + threadList.toString().substring(1, (threadList.toString()).length() - 1));

            }
        }
    }

    private String checkUsername(String join) {
        String result;
        UserNameValidator check = new UserNameValidator();

        if (!check.validate(join)) {
            result = "J_ER 406: Invalid username. Special characters are not allowed.";
            return result;

        } else if (!threadList.isEmpty() && !check.unique(join)) {
            result = "J_ER 406: This username already exists.";
            return result;

        } else {
            result = "J_OK";
            return result;
        }
    }
    class ServerThread extends Thread {

        private Socket socket;
        private String username;
        private Scanner scanner;
        private PrintWriter output;

        public Socket getSocket() {
            return this.socket;
        }

        public String getUsername() {
            return this.username;
        }

        ServerThread(Socket socket, String username) {

            this.socket = socket;
            this.username = username;
            try {
                scanner = new Scanner(socket.getInputStream());
            } catch (IOException e) {

            }
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof ServerThread) {
                String username = ((ServerThread) o).getUsername();
                if (username != null && username.equals(this.getUsername())) {
                    return true;
                }
            }
            return false;
        }

        //converts to hashCOde
        @Override
        public int hashCode() {

            return this.username.hashCode();
        }

        @Override
        public void run() {
            String message;
            Set<ServerThread> threadList = Server.getThreadList();

            do {
                int length = 4 + username.length();
                message = scanner.nextLine();
                if (message.contains("IMAV")) {
                    System.out.println(message);
                } else {
                    String count = message.substring(length);
                    if (!(count.length() > 255)) {

                        System.out.println(message);
                        for (ServerThread t : threadList) {
                            try {
                                output = new PrintWriter(t.getSocket().getOutputStream(), true);
                                output.println(message);
                            } catch (IOException ioEx) {
                            }

                        }
                    } else {
                        output.println("J_ER 413: Message length should not exceed 250 characters.");
                    }
                }
            } while (!message.contains("QUIT"));
            ServerThread thread = new ServerThread(socket, username);

            try {
                System.out.println(username + " has left the chat");
                output.println("Quiting: " + username + " has left the chat");
                threadList.remove(thread);
                for (ServerThread t : threadList) {

                    PrintWriter msg = new PrintWriter(t.getSocket().getOutputStream(), true);
                    msg.println("LIST " + threadList.toString().substring(1, (threadList.toString()).length() - 1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return username;
        }
    }
}

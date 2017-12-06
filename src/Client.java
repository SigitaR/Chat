import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sigute on 10/4/2017.
 */
public class Client {

    private static String host = "127.0.0.1";
    private static int port = 4545;
    private static String username;
    private static Socket clientSocket;
    private static LocalDateTime latestIMAV = LocalDateTime.now();// IMAV

    public Client(String host, int port) {
    }

    public static void main(String[] args) throws IOException {

        new Client("127.0.0.1", 4545).start();
    }
    public static void start() {
        String answer = "";

        while (!answer.contains("J_OK")) {
            try {
                clientSocket = new Socket(host, port);

                Scanner sc = new Scanner(System.in);
                System.out.println("Enter your username: ");
                username = sc.nextLine();
                Scanner dataIn = new Scanner(clientSocket.getInputStream());
                PrintWriter dataOut = new PrintWriter(clientSocket.getOutputStream(), true);

                String join = "JOIN " + username + ", " + host + ":" + port;
                dataOut.println(join);
                answer = dataIn.nextLine();
                System.out.println(answer);

                //if username exists
                if (answer.contains("J_ER")) {
                    continue;

                }

                System.out.println("To leave enter QUIT in a new line. ");

                Thread t = new Thread(() -> {
                    while (true) {

                        String input = sc.nextLine();

                        String msg = "DATA " + username + ": " + input;

                        if (input.length() < 255) {

                            dataOut.println(msg);
                        } else {
                            System.out.println("J_ER 413: Message length should not exceed 250 characters.");
                        }

                    }
                });
                t.start();

                //IMAV thread
                Thread t2 = new Thread(() -> {
                    while (true) {
                        String message = dataIn.nextLine();
                        if (message.startsWith("DATA")) {
                            message = message.substring(5);
                        }
                        System.out.println(message);
                    }
                });
                t2.start();

                Timer timer = new Timer(true);

                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        latestIMAV = LocalDateTime.now();
                        dataOut.println("IMAV " + username);
                    }
                }, 10000, 03 * 1000);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}

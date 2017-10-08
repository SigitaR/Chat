import java.io.*;
import java.net.Socket;

/**
 * Created by Sigute on 10/4/2017.
 */
public class Client implements Runnable {

        private static Socket clientSocket = null;
        private static PrintWriter os = null;
        private static BufferedReader is = null;
        private static BufferedReader inputLine = null;
        private static boolean closed = false;

        private static String host = "127.0.0.1";
        private static int port = 4545;

        public static String getHost() {
            return host;
        }

        public static int getPort() {
            return port;
        }

        public static void main(String[] args) {

            if (args.length < 2) {
                System.out.println("Chat server is using host " + host + ", Port Number = " + port);

            } else {
                host = args[0];
                port = Integer.valueOf(args[1]).intValue();
            }

            try {
                clientSocket = new Socket(host, port);
                inputLine = new BufferedReader(new InputStreamReader(System.in));
                os = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            } catch (IOException e) {
                System.err.println("J_ER 502: Cannot open socket "
                        + host);
            }


            if (clientSocket != null && os != null && is != null) {
                try {

        //Thread that reads from the server.
                    new Thread(new Client()).start();
                    while (!closed) {
                        os.println(inputLine.readLine().trim());
                    }

                    os.close();
                    is.close();
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("J_ER 504:  " + e);
                }
            }
        }


        public void run() {

            String responseLine;
            try {
                while ((responseLine = is.readLine()) != null) {
                    System.out.println(responseLine);
                    if (responseLine.indexOf("Quiting: ") != -1)
                        break;
                }
                closed = true;
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }


}

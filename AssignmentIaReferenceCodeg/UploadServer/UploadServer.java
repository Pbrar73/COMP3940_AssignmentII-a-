import java.net.*;
import java.io.*;
import java.util.Arrays;

public class UploadServer {


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {
            // specify port number for connection
            serverSocket = new ServerSocket(8082);
            System.out.println("Server is running on port 8082...");
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8082.");
            System.exit(-1);
        }
        while (true) {
            // while connection, call UploadServerThread
            new UploadServerThread(serverSocket.accept()).start();
        }
    }
}

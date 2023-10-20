import java.io.*;
import java.net.*;

public class UploadClient {
    public UploadClient() {
    }

    public String uploadFile() {
        String listing = "";
        try {
            // connect to the server on localhost at that port
            Socket socket = new Socket("localhost", 8082);
            System.out.println("entered client side connection");

            // Prepare to read response from the server
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Prepare to write data to the server
            OutputStream out = socket.getOutputStream();

            // Read file into byte array
            FileInputStream fis = new FileInputStream("AndroidLogo.png");
            byte[] bytes = fis.readAllBytes();

            // Send file bytes to server
            out.write(bytes);
            out.flush(); // Ensure all data is sent
            socket.shutdownOutput(); // Indicate that we've finished sending
            fis.close();

            System.out.println("Came this far\n");

            // Receive server response
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                listing += serverMessage + System.lineSeparator(); // Collect server response
                System.out.println(serverMessage); // Print each line received from the server
            }

            // Close input stream indicating we've finished reading the server's response
            socket.shutdownInput();

            // Clean up resources
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listing;
    }

}
import java.net.*;
import java.io.*;
import java.time.Clock;
import java.util.stream.Stream;

public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      super("DirServerThread");
      this.socket = socket;
   }

   public void run() {
      try {
         BufferedReader check = new BufferedReader(
               new InputStreamReader(socket.getInputStream()));
         InputStream in = socket.getInputStream();
         HttpServletRequest req = new HttpServletRequest(in);
         OutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos);
         HttpServlet httpServlet = new UploadServlet(socket);
         String inputLine;
         if ((inputLine = check.readLine()) != null) {
            if (inputLine.startsWith("GET / HTTP/1.1")) {
               System.out.println("calling doGet method");
               httpServlet.doGet(req, res);
            } else if (inputLine.startsWith("POST /upload HTTP/1.1")) {
               System.out.println("calling doPost method");
               httpServlet.doPost(req, res);
            }
         }
         OutputStream out = socket.getOutputStream();
         out.write(((ByteArrayOutputStream) baos).toByteArray());
         socket.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}

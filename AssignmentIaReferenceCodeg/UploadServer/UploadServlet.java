import java.io.*;
import java.net.Socket;
import java.time.Clock;
import java.util.Arrays;

public class UploadServlet extends HttpServlet {
   private Socket socket; // Member variable to hold the Socket

   public UploadServlet(Socket socket) {
      this.socket = socket;
   }

   private static final String UPLOAD_DIR = "uploads"; // Directory to save uploaded files

   protected void doGet(HttpServletRequest request, HttpServletResponse response) {
      System.out.println("entered doGet");

      PrintWriter out = new PrintWriter(response.getOutputStream(), true);

      // Writing the headers
      out.println("HTTP/1.1 200 OK");
      out.println("Content-Type: text/html");
      out.println(""); // This is the separation between headers and content

      // Writing the content (HTML)
      out.println("<html><body>");
      out.println("<h1>File Upload Form</h1>");
      out.println("<form method='POST' action='/upload' enctype='multipart/form-data'>");
      out.println("<input type='file' name='file' /><br />"); // File uploader element
      out.println("<input type='text' name='caption' placeholder='Caption' /><br />");
      out.println("<input type='text' name='date' placeholder='Date' /><br />");
      out.println("<input type='submit' value='Upload' />");
      out.println("</form>");
      out.println("</body></html>");
      out.flush(); // Make sure everything is written to the stream
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      System.out.println("entered doPost");

      // Ensure the upload directory exists
      File uploadDir = new File(UPLOAD_DIR);
      if (!uploadDir.exists()) {
         boolean dirCreated = uploadDir.mkdir();
         System.out.println("Upload directory created: " + dirCreated); // DEBUG statement
      }

      try {
         BufferedReader check = new BufferedReader(
               new InputStreamReader(socket.getInputStream()));
         PrintWriter out = new PrintWriter(response.getOutputStream(), true);

         String line;
         String boundary = null;
         String caption = null;
         String date = null;
         String originalFilename = null;
         ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();

         // Get the boundary from the Content-Type header
         // while ((line = check.readLine()) != null && !line.isEmpty()) {
         // System.out.println("Header Line: " + line); // DEBUG statement
         // if (line.startsWith("Content-Type: multipart/form-data")) {
         // boundary = line.split("boundary=")[1];
         // System.out.println("Parsed boundary: " + boundary); // DEBUG statement
         // }
         // }

         while (!(line = check.readLine()).isEmpty()) { // Headers end with an empty line
            System.out.println("Header Line: " + line); // DEBUG statement
            if (line.startsWith("Content-Type: multipart/form-data")) {
               // Extract the boundary from the Content-Type header
               String[] fields = line.split(";");
               for (String field : fields) {
                  if (field.trim().startsWith("boundary=")) {
                     boundary = field.split("=")[1];
                     break;
                  }
               }
            }
         }

         if (boundary == null) {
            System.err.println("Boundary was not found"); // DEBUG statement
            out.println("HTTP/1.1 400 Bad Request");
            return;
         }

         boolean isFileContent = false;

         // Read form data
         while ((line = check.readLine()) != null && !line.equals("--" + boundary + "--")) {
            System.out.println("Read line: " + line); // DEBUG statement

            if (line.contains("Content-Disposition: form-data; name=\"file\"")) {
               originalFilename = extractFilename(line);
               System.out.println("Original Filename: " + originalFilename); // DEBUG statement
               check.readLine(); // Skip the Content-Type line (if it exists)
               check.readLine(); // empty line before file content
               isFileContent = true;
               continue; // Continue to the next iteration to avoid writing this line to file
            } else if (line.contains("Content-Disposition: form-data; name=\"caption\"")) {
               check.readLine(); // empty line before caption
               caption = check.readLine();
               System.out.println("Caption: " + caption); // DEBUG statement
            } else if (line.contains("Content-Disposition: form-data; name=\"date\"")) {
               check.readLine(); // empty line before date
               date = check.readLine();
               System.out.println("Date: " + date); // DEBUG statement
            }

            if (isFileContent && !line.equals("--" + boundary)) {
               System.out.println("Writing to file output stream: " + line); // DEBUG statement
               fileOutputStream.write(line.getBytes());
               fileOutputStream.write('\n');
            }
            if (line.equals("--" + boundary)) {
               System.out.println("End of file content"); // DEBUG statement
               isFileContent = false;
            }
         }

         if (caption == null || date == null || originalFilename == null) {
            System.err.println("One or more form fields were missing"); // DEBUG statement
            out.println("HTTP/1.1 400 Bad Request");
            return;
         }

         // Save the file
         // Format the date for use in the file path (replace slashes with underscores)
         String formattedDate = date.replaceAll("/", "-");
         String filename = UPLOAD_DIR + "/" + caption + "_" + formattedDate + "_" + originalFilename;

         System.out.println("Saving file: " + filename); // DEBUG statement
         try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(fileOutputStream.toByteArray());
         }
         System.out.println("File upload successful"); // DEBUG statement

         // Modify the server to send the sorted list of files in the response
         File[] files = new File(UPLOAD_DIR).listFiles();
         Arrays.sort(files); // Sort the files alphabetically
         StringBuilder responseBuilder = new StringBuilder();
         responseBuilder.append("HTTP/1.1 200 OK\r\n");
         responseBuilder.append("Content-Type: text/html\r\n");
         responseBuilder.append("\r\n");
         responseBuilder.append("<html><body>\n");
         responseBuilder.append("<h1>List of Files:</h1>\n<ul>\n");
         for (File file : files) {
            responseBuilder.append("<li>").append(file.getName()).append("</li>\n");
         }
         responseBuilder.append("</ul>\n</body></html>");

         // Send the sorted list back to the client
         out.println(responseBuilder.toString());
      } catch (Exception ex) {
         System.err.println("An error occurred during file upload"); // DEBUG statement
         ex.printStackTrace(); // This will print the stack trace of the exception to the system error stream
      }
   }

   private static String extractFilename(String contentDisposition) {
      String[] parts = contentDisposition.split(";");
      for (String part : parts) {
         if (part.trim().startsWith("filename")) {
            String filename = part.split("=")[1].replace("\"", "");
            return filename;
         }
      }
      return null;
   }
}
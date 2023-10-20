import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UploadAsyncTask extends AsyncTask {
    protected void onPostExecute(String result) {
        System.out.println(result);
    }

    protected String doInBackground() {
        try {
            String BOUNDARY = "---------------------------" + UUID.randomUUID().toString();

            URL url = new URL("http://localhost:8081/upload"); // Adjust the URL accordingly
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            connection.setDoOutput(true);

            // Construct the request body with multipart form data
            String formData = "--" + BOUNDARY + "\r\n" +
                    "Content-Disposition: form-data; name=\"caption\"\r\n\r\nYour Caption\r\n" +
                    "--" + BOUNDARY + "\r\n" +
                    "Content-Disposition: form-data; name=\"date\"\r\n\r\n2023-10-16\r\n";

            // Add file data here if you have it
            // formData += "Binary file data";

            // Close the multipart form data
            // formData += "\r\n--" + BOUNDARY + "--\r\n";

            // Write the data to the connection
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(formData);
            out.flush();
            out.close();

            // Get the response from the server
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return "File uploaded successfully. Response: " + response.toString();
            } else {
                return "Error uploading the file. Status code: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
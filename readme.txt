# COMP3940_AssignmentII-a-

Group Participation:
Upload Server Folder: Gursidh, Simrat + Noor
Console App folder: Jaskaran + Pavanpreet

What works: 
1 mark] Expand the code in the “UploadServer” folder inside the attached AssignmentIIaReferenceCode.zip file such that if a user accesses this server from the browser by typing http://localhost:8082/ the user shall see a file upload form.  The upload server thus should be able to handle an HTTP GET Request from the browser by sending a web page back to the browser in its HTTP Response.  This web page shall be composed of an HTML Form element containing an HTML File Uploader element.  Like your previous assignments, this HTML Form could have fields such as caption and date in addition to the file uploader element. 
 
[2 marks] Upon user selecting a file and pressing the submit button on the form, the upload server shall be able to handle this incoming HTTP Post Request from the browser.  The upload server shall parse this POST Request which now contains the multi-part form data, extract the multiple parts of the form e.g. caption, date and filename as well as the file itself, and save the file in a folder of its file system with the caption and date added to its filename.  The upload server shall be able to handle upload of as text files.  It can support receiving and saving of text files uploaded from the browser.
[1.5 mark]  Expand the code in the ConsoleApp folder in the attached AssignmentIIaReferenceCode.zip such that it is able to communicate with the Tomcat’s webapp “upload” that was given to you as a reference code during the initial weeks of the course.  The ConsoleApp shall construct an HTTP Post request containing the multipart form data composed of fields such as the caption and date.  When the ConsoleApp connects to tomcat at localhost and port 8081 and then transmits this POST request, Tomcat would think that the request has actually come from the browser and will pass on the POST request to the UploadServlet in its upload webapp.

Console app connects to tomcat and can do at least a simple post or at least is able to get tomcat to invoke the doPost method of the servlet, even if the post does not contain multipart file upload data, but it can print the default first and last name from the upload webapp to commandline after running ConsoleApp(Activity.java) 

What does not work:
Binary Images,  and using our Upload Client to connect to local host 8082 (did get it to call post and get though)
Parsing the multipart form data from upload webapp as before and save the extracted file in the images folder.


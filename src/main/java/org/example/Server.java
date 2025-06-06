package org.example;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;

import java.io.*;
import java.lang.String;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private final FileToMap fileToMap;
    private final MapToDatabase mapToDatabase;
    private final DatabaseManager databaseManager;
    private final Registration registration;
    private final Login login;
    private final  UserProject userProject;
    private final Translator translator;
    private final FormatFileWriter formatFileWriter;





    public Server(FileToMap fileToMap, MapToDatabase mapToDatabase, DatabaseManager databaseManager, Registration registration, Login login, UserProject userProject, Translator translator, FormatFileWriter formatFileWriter) {
        this.fileToMap = fileToMap;
        this.mapToDatabase = mapToDatabase;
        this.databaseManager = databaseManager;
        this.registration = registration;
        this.login = login;
        this.userProject = userProject;
        this.translator = translator;
        this.formatFileWriter = formatFileWriter;
    }

    public static String loadHtml(String fileName) {
        try {
            return new String(Files.readAllBytes(Path.of("src/main/resources/public/" + fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        Javalin app = Javalin.create(config -> {
        }).start(7070);

        app.get("/", ctx -> ctx.html(loadHtml("index.html")));

        app.get("/registration", ctx -> ctx.html(loadHtml("registration.html")));
        app.post("/registration", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            int flag = registration.registrationUser(username, password);
            if(flag > 0){
                ctx.html("User has already exist");
            } else {
                ctx.redirect("/login");
            }
        });

        app.get("/login", ctx -> ctx.html(loadHtml("login.html")));
        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            int id = login.loginUser(username, password);
            if(id == 0){
                ctx.html("Email or password is not valid");
            } else {
                ctx.redirect("/profile");
                ctx.sessionAttribute("id", id);
            }

        });

        app.get("/profile", ctx -> {
            List<String> projectNames = userProject.getProjects(ctx.sessionAttribute("id"));
            Map<String, Integer> map;

            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<!DOCTYPE html>");
            htmlBuilder.append("<html lang=\"en\">");
            htmlBuilder.append("<head>");
            htmlBuilder.append("<meta charset=\"UTF-8\">");
            htmlBuilder.append("<title>List of projects</title>");
            htmlBuilder.append("<style>");
            htmlBuilder.append("body {");
            htmlBuilder.append("    font-family: Arial, sans-serif;");
            htmlBuilder.append("    background-color: #f4f4f4;");
            htmlBuilder.append("    margin: 0;");
            htmlBuilder.append("    padding: 0;");
            htmlBuilder.append("    display: flex;");
            htmlBuilder.append("    flex-direction: column;");
            htmlBuilder.append("    min-height: 100vh;");
            htmlBuilder.append("}");
            htmlBuilder.append("main {");
            htmlBuilder.append("    flex: 1;");
            htmlBuilder.append("    text-align: center;");
            htmlBuilder.append("}");
            htmlBuilder.append("button {");
            htmlBuilder.append("    padding: 5px 10px;");
            htmlBuilder.append("    border: none;");
            htmlBuilder.append("    border-radius: 5px;");
            htmlBuilder.append("    background-color: #007bff;");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    cursor: pointer;");
            htmlBuilder.append("    margin-bottom: 10px;");
            htmlBuilder.append("}");
            htmlBuilder.append("header {");
            htmlBuilder.append("    display: flex;");
            htmlBuilder.append("    justify-content: space-between;");
            htmlBuilder.append("    align-items: center;");
            htmlBuilder.append("    padding: 10px 20px;");
            htmlBuilder.append("    background-color: #333;");
            htmlBuilder.append("    color: white;");
            htmlBuilder.append("}");
            htmlBuilder.append("nav {");
            htmlBuilder.append("    margin-right: 1600px;");
            htmlBuilder.append("}");
            htmlBuilder.append("nav a {");
            htmlBuilder.append("    color: white;");
            htmlBuilder.append("    text-decoration: none;");
            htmlBuilder.append("    margin-right: 20px;");
            htmlBuilder.append("}");
            htmlBuilder.append("footer {");
            htmlBuilder.append("    margin-top: auto;");
            htmlBuilder.append("    background-color: #343a40;");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    padding: 20px 0;");
            htmlBuilder.append("    text-align: center;");
            htmlBuilder.append("}");
            htmlBuilder.append("footer a {");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    text-decoration: none;");
            htmlBuilder.append("    margin: 0 10px;");
            htmlBuilder.append("}");
            htmlBuilder.append(".project-container {");
            htmlBuilder.append("    margin-bottom: 20px;");
            htmlBuilder.append("}");
            htmlBuilder.append(".project-name {");
            htmlBuilder.append("    display: block;");
            htmlBuilder.append("    margin-bottom: 10px;");
            htmlBuilder.append("    font-size: 1.2em;");
            htmlBuilder.append("    text-decoration: none;");
            htmlBuilder.append("    color: #007bff;");
            htmlBuilder.append("}");
            htmlBuilder.append("table {");
            htmlBuilder.append("    margin: 0 auto 20px;");
            htmlBuilder.append("    border-collapse: collapse;");
            htmlBuilder.append("    width: 80%;");
            htmlBuilder.append("}");
            htmlBuilder.append("td, th {");
            htmlBuilder.append("    border: 1px solid #ddd;");
            htmlBuilder.append("    padding: 8px;");
            htmlBuilder.append("}");
            htmlBuilder.append("tr:nth-child(even) {");
            htmlBuilder.append("    background-color: #f2f2f2;");
            htmlBuilder.append("}");
            htmlBuilder.append("th {");
            htmlBuilder.append("    padding-top: 12px;");
            htmlBuilder.append("    padding-bottom: 12px;");
            htmlBuilder.append("    text-align: left;");
            htmlBuilder.append("    background-color: #2f4f4f;");
            htmlBuilder.append("    color: white;");
            htmlBuilder.append("}");
            htmlBuilder.append(".button-container {");
            htmlBuilder.append("    display: flex;");
            htmlBuilder.append("    justify-content: center;");
            htmlBuilder.append("    gap: 10px;");
            htmlBuilder.append("}");
            htmlBuilder.append("</style>");
            htmlBuilder.append("</head>");
            htmlBuilder.append("<body>");
            htmlBuilder.append("<header>");
            htmlBuilder.append("<img src=\"images.png\" alt=\"Icon Image\" style=\"height: 50px; width: 50px;\">");
            htmlBuilder.append("<nav>");
            htmlBuilder.append("    <a href=\"about.html\">About Us</a>");
            htmlBuilder.append("    <a href=\"contact.html\">Contact Us</a>");
            htmlBuilder.append("</nav>");
            htmlBuilder.append("</header>");
            htmlBuilder.append("<main>");
            htmlBuilder.append("<h2>List of projects</h2>");

            for (String projectName : projectNames) {
                map = userProject.getProjectInfo(ctx.sessionAttribute("id"), projectName);

                htmlBuilder.append("<div class=\"project-container\">");
                htmlBuilder.append("<a class=\"project-name\" href=\"/translate?projectName=").append(projectName).append("\">").append(projectName).append("</a>");

                htmlBuilder.append("<table>");
                htmlBuilder.append("<tr><th>Language</th><th>Count</th></tr>");
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    // Додавання рядка для кожного ключа-значення
                    htmlBuilder.append("<tr>");
                    htmlBuilder.append("<td>").append(key).append("</td>");
                    htmlBuilder.append("<td>").append(value).append("</td>");
                    htmlBuilder.append("</tr>");
                }
                htmlBuilder.append("</table>");

                // Додавання кнопок "Delete" та "Update"
                htmlBuilder.append("<div class=\"button-container\">");
                htmlBuilder.append("<form action=\"/deleteProject\" method=\"post\" style=\"display:inline-block;\">");
                htmlBuilder.append("<input type=\"hidden\" name=\"projectName\" value=\"").append(projectName).append("\">");
                htmlBuilder.append("<button type=\"submit\">Delete</button>");
                htmlBuilder.append("</form>");

                htmlBuilder.append("<form action=\"/updateProject\" method=\"get\" style=\"display:inline-block;\">");
                htmlBuilder.append("<input type=\"hidden\" name=\"projectName\" value=\"").append(projectName).append("\">");
                htmlBuilder.append("<button type=\"submit\">Update</button>");
                htmlBuilder.append("</form>");
                htmlBuilder.append("</div>");
                htmlBuilder.append("</div>");
            }

            htmlBuilder.append("<form action=\"/newProject\" method=\"get\">");
            htmlBuilder.append("<button type=\"submit\" name=\"newProjectName\" value=\"Create new project\">Create new project</button>");
            htmlBuilder.append("</form>");

            htmlBuilder.append("</main>");
            htmlBuilder.append("<footer>");
            htmlBuilder.append("    <div>&copy; 2024 Sumy State University, Valerij Diahovets </div>");
            htmlBuilder.append("</footer>");
            htmlBuilder.append("</body>");
            htmlBuilder.append("</html>");

            ctx.html(htmlBuilder.toString());
        });


//////////////
        app.get("/updateProject", ctx -> {
            String projectName = ctx.queryParam("projectName");
            ctx.sessionAttribute("projectName", projectName);
            List<String> languages = userProject.getLanguage(ctx.sessionAttribute("id"), ctx.sessionAttribute("projectName"));

            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<!DOCTYPE html>");
            htmlBuilder.append("<html lang=\"en\">");
            htmlBuilder.append("<head>");
            htmlBuilder.append("<meta charset=\"UTF-8\">");
            htmlBuilder.append("<title>Update project</title>");
            htmlBuilder.append("<style>");
            htmlBuilder.append("body {");
            htmlBuilder.append("    font-family: Arial, sans-serif;");
            htmlBuilder.append("    background-color: #f4f4f4;");
            htmlBuilder.append("    margin: 0;");
            htmlBuilder.append("    padding: 0;");
            htmlBuilder.append("    display: flex;");
            htmlBuilder.append("    flex-direction: column;");
            htmlBuilder.append("    min-height: 100vh;");
            htmlBuilder.append("}");
            htmlBuilder.append("main {");
            htmlBuilder.append("    flex: 1;");
            htmlBuilder.append("    text-align: center;");
            htmlBuilder.append("}");
            htmlBuilder.append("h2 {");
            htmlBuilder.append("     margin-top: 250px; /* відступ від верху для заголовку */");
            htmlBuilder.append("}");
            htmlBuilder.append("form {");
            htmlBuilder.append("     margin: 20px auto;");
            htmlBuilder.append("     padding: 20px;");
            htmlBuilder.append("    background-color: #fff;");
            htmlBuilder.append("    border-radius: 10px;");
            htmlBuilder.append("    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);");
            htmlBuilder.append("    display: inline-block;");
            htmlBuilder.append("}");
            htmlBuilder.append("label {");
            htmlBuilder.append("     display: block;");
            htmlBuilder.append("    margin-bottom: 10px;");
            htmlBuilder.append("}");
            htmlBuilder.append("select,");
            htmlBuilder.append("input[type='file'] {");
            htmlBuilder.append("    width: 100%;");
            htmlBuilder.append("    padding: 10px;");
            htmlBuilder.append("    margin-bottom: 15px;");
            htmlBuilder.append("    border: 1px solid #ccc;");
            htmlBuilder.append("    border-radius: 5px;");
            htmlBuilder.append("    box-sizing: border-box;");
            htmlBuilder.append("}");
            htmlBuilder.append("input[type='submit'] {");
            htmlBuilder.append("     width: 100%;");
            htmlBuilder.append("    padding: 10px;");
            htmlBuilder.append("     border: none;");
            htmlBuilder.append("    border-radius: 5px;");
            htmlBuilder.append("     background-color: #007bff;");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("     cursor: pointer;");
            htmlBuilder.append("}");
            htmlBuilder.append("header {");
            htmlBuilder.append("    display: flex;");
            htmlBuilder.append("    justify-content: space-between;");
            htmlBuilder.append("    align-items: center;");
            htmlBuilder.append("    padding: 10px 20px;");
            htmlBuilder.append("    background-color: #333;");
            htmlBuilder.append("    color: white;");
            htmlBuilder.append("}");
            htmlBuilder.append("nav {");
            htmlBuilder.append("    margin-right: 1600px;");
            htmlBuilder.append("}");
            htmlBuilder.append("nav a {");
            htmlBuilder.append("    color: white;");
            htmlBuilder.append("    text-decoration: none;");
            htmlBuilder.append("    margin-right: 20px;");
            htmlBuilder.append("}");
            htmlBuilder.append("footer {");
            htmlBuilder.append("    margin-top: auto;");
            htmlBuilder.append("    background-color: #343a40;");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    padding: 20px 0;");
            htmlBuilder.append("    text-align: center;");
            htmlBuilder.append("}");
            htmlBuilder.append("footer a {");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    text-decoration: none;");
            htmlBuilder.append("    margin: 0 10px;");
            htmlBuilder.append("}");
            htmlBuilder.append("</style>");
            htmlBuilder.append("</head>");
            htmlBuilder.append("<body>");
            htmlBuilder.append("<header>");
            htmlBuilder.append("<img src=\"images.png\" alt=\"Icon Image\" style=\"height: 50px; width: 50px;\">");
            htmlBuilder.append("<nav>");
            htmlBuilder.append("    <a href=\"about.html\">About Us</a>");
            htmlBuilder.append("    <a href=\"contact.html\">Contact Us</a>");
            htmlBuilder.append("</nav>");
            htmlBuilder.append("</header>");
            htmlBuilder.append("<main>");
            htmlBuilder.append("<h2>Update project</h2>");
            htmlBuilder.append("<form action=\"/updateProject\" method=\"post\" enctype=\"multipart/form-data\">");
            htmlBuilder.append("<label for=\"language\">Language</label>");
            htmlBuilder.append("<select name=\"language\">");

            for (String language : languages) {
                htmlBuilder.append("<option value=\"").append(language).append("\">").append(language).append("</option>");
            }
            htmlBuilder.append("</select>");
            htmlBuilder.append("<br>");
            htmlBuilder.append("<input type=\"file\" name=\"file\" id=\"file\">");
            htmlBuilder.append("<br>");
            htmlBuilder.append("<input type=\"submit\" value=\"Confirm\">");

            htmlBuilder.append("</form>");
            htmlBuilder.append("</main>");
            htmlBuilder.append("<footer>");
            htmlBuilder.append("    <div>&copy; 2024 Sumy State University, Valerij Diahovets </div>");
            htmlBuilder.append("</footer>");
            htmlBuilder.append("</body>");
            htmlBuilder.append("</html>");
            ctx.html(htmlBuilder.toString());

        });
        app.post("/updateProject", ctx -> {
            try {
                String projectName = ctx.sessionAttribute("projectName");
                String language = ctx.formParam("language");
                ctx.sessionAttribute("sourceLang", language);

                UploadedFile uplFile = ctx.uploadedFile("file");

                if (uplFile != null) {
                    // Якщо файл був завантажений, обробіть його
                    File file = new File(uplFile.filename());
                    copyInputStreamToFile(uplFile.content(), file);

                    if (uplFile.filename().endsWith(".xml")) {
                        fileToMap.fileXML(file, language, ctx.sessionAttribute("id"), projectName);
                    } else if (uplFile.filename().endsWith(".yaml") || uplFile.filename().endsWith(".json")) {
                        fileToMap.fileYAMLandJSON(file, language, ctx.sessionAttribute("id"), projectName);
                    } else {
                        ctx.html("Error: Unsupported file format.");
                        return;
                    }
                }
                ctx.redirect("/translate");
            } catch (Exception e) {
                ctx.redirect("/translate");
            }
        });

        app.get("/newProject", ctx -> ctx.html(loadHtml("newProject.html")));
        app.post("/newProject", ctx -> {
            try {
                String projectName = ctx.formParam("projectName");
                String language = ctx.formParam("language");
                ctx.sessionAttribute("sourceLang", language);
                ctx.sessionAttribute("projectName", projectName);


                UploadedFile uplFile = ctx.uploadedFile("file");

                File file = new File(uplFile.filename());
                copyInputStreamToFile(uplFile.content(), file);

                if(uplFile.filename().endsWith(".xml")){
                    fileToMap.fileXML(file, language, ctx.sessionAttribute("id"), projectName);
                }else if(uplFile.filename().endsWith(".yaml") | uplFile.filename().endsWith(".json")){
                    fileToMap.fileYAMLandJSON(file, language, ctx.sessionAttribute("id"), projectName);
                } else {
                    ctx.html("Error: Unsupported file format.");
                    return;
                }

                ctx.redirect("/translate");
            } catch (Exception e){
                ctx.html("Error: No file uploaded or there is no project name");
            }


        });

        //////////////////////

        app.get("/translate", ctx -> {
            String projectName = ctx.sessionAttribute("projectName");
            if(projectName == null){
                projectName = ctx.queryParam("projectName");
                ctx.sessionAttribute("projectName", projectName);
            }
            ctx.html(loadHtml("choice.html"));
        });
        app.post("/translate", ctx -> {

            String sourceLang = ctx.sessionAttribute("sourceLang");
            if (sourceLang == null){
                List<String> languages = userProject.getLanguage(ctx.sessionAttribute("id"), ctx.sessionAttribute("projectName"));
                for (String language : languages) {
                    sourceLang = language;
                }
                ctx.sessionAttribute("sourceLang", sourceLang);
            }


            String fileName = ctx.formParam("fileName");
            if(fileName.isEmpty()){
                ctx.html("Enter file name");
                return;
            }

            String targetLang = ctx.formParam("language");
            String format = ctx.formParam("format");
            ctx.sessionAttribute("fileName", fileName);
            ctx.sessionAttribute("targetLang", targetLang);
            ctx.sessionAttribute("format", format);

            Map<String, String> map;
            Map<String, String> newMap = new HashMap<>();
            map = mapToDatabase.getMap(ctx.sessionAttribute("id"), ctx.sessionAttribute("projectName"), ctx.sessionAttribute("sourceLang"));

            for(Map.Entry<String, String> entry : map.entrySet()){
                String translation = mapToDatabase.getUserTranslation(ctx.sessionAttribute("id"), ctx.sessionAttribute("projectName"), ctx.sessionAttribute("targetLang"), entry.getKey());
                if (translation == null){
                    translation = databaseManager.getTranslation(ctx.sessionAttribute("targetLang"), ctx.sessionAttribute("sourceLang"), entry.getValue());
                }
                if (translation == null) {
                    translation = translator.translate(entry.getValue(), ctx.sessionAttribute("sourceLang"), ctx.sessionAttribute("targetLang"));
                    databaseManager.saveTranslation(ctx.sessionAttribute("sourceLang"), entry.getValue(), ctx.sessionAttribute("targetLang"), translation);
                }
                newMap.put(entry.getKey(), translation);
            }

            ctx.sessionAttribute("map", newMap);
            ctx.redirect("/translate/confirm");
        });

        app.get("/translate/confirm", ctx -> {
            Map<String, String> map = ctx.sessionAttribute("map");

            // Створення рядка HTML для відображення мапи у формі
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<!DOCTYPE html>");
            htmlBuilder.append("<html lang=\"en\">");
            htmlBuilder.append("<head>");
            htmlBuilder.append("<meta charset=\"UTF-8\">");
            htmlBuilder.append("<title>Translation editing</title>");
            htmlBuilder.append("<style>");
            htmlBuilder.append("body {");
            htmlBuilder.append("    font-family: Arial, sans-serif;");
            htmlBuilder.append("    background-color: #f4f4f4;");
            htmlBuilder.append("    margin: 0;");
            htmlBuilder.append("    padding: 0;");
            htmlBuilder.append("    display: flex;");
            htmlBuilder.append("    flex-direction: column;");
            htmlBuilder.append("    min-height: 100vh;");
            htmlBuilder.append("}");
            htmlBuilder.append("main {");
            htmlBuilder.append("    flex: 1;");
            htmlBuilder.append("    text-align: center;");
            htmlBuilder.append("}");
            htmlBuilder.append("table {");
            htmlBuilder.append("    width: 50%;");
            htmlBuilder.append("    margin: 20px auto;");
            htmlBuilder.append("    border-collapse: collapse;");
            htmlBuilder.append("}");
            htmlBuilder.append("th, td {");
            htmlBuilder.append("    padding: 10px;");
            htmlBuilder.append("    border: 1px solid #ddd;");
            htmlBuilder.append("}");
            htmlBuilder.append("th {");
            htmlBuilder.append("    background-color: #f2f2f2;");
            htmlBuilder.append("}");
            htmlBuilder.append("input[type='text'] {");
            htmlBuilder.append("    padding: 5px;");
            htmlBuilder.append("    width: 100%;");
            htmlBuilder.append("}");
            htmlBuilder.append("button {");
            htmlBuilder.append("    padding: 10px 20px;");
            htmlBuilder.append("    border: none;");
            htmlBuilder.append("    border-radius: 5px;");
            htmlBuilder.append("    background-color: #007bff;");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    cursor: pointer;");
            htmlBuilder.append("}");
            htmlBuilder.append("header {");
            htmlBuilder.append("    display: flex;");
            htmlBuilder.append("    justify-content: space-between;");
            htmlBuilder.append("    align-items: center;");
            htmlBuilder.append("    padding: 10px 20px;");
            htmlBuilder.append("    background-color: #333;");
            htmlBuilder.append("    color: white;");
            htmlBuilder.append("}");
            htmlBuilder.append("nav {");
            htmlBuilder.append("    margin-right: 1600px;");
            htmlBuilder.append("}");
            htmlBuilder.append("nav a {");
            htmlBuilder.append("    color: white;");
            htmlBuilder.append("    text-decoration: none;");
            htmlBuilder.append("    margin-right: 20px;");
            htmlBuilder.append("}");
            htmlBuilder.append("footer {");
            htmlBuilder.append("    margin-top: auto;");
            htmlBuilder.append("    background-color: #343a40;");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    padding: 20px 0;");
            htmlBuilder.append("    text-align: center;");
            htmlBuilder.append("}");
            htmlBuilder.append("footer a {");
            htmlBuilder.append("    color: #fff;");
            htmlBuilder.append("    text-decoration: none;");
            htmlBuilder.append("    margin: 0 10px;");
            htmlBuilder.append("}");
            htmlBuilder.append("</style>");
            htmlBuilder.append("</head>");
            htmlBuilder.append("<body>");
            htmlBuilder.append("<header>");
            htmlBuilder.append("<img src=\"images.png\" alt=\"Icon Image\" style=\"height: 50px; width: 50px;\">");
            htmlBuilder.append("<nav>");
            htmlBuilder.append("    <a href=\"about.html\">About Us</a>");
            htmlBuilder.append("    <a href=\"contact.html\">Contact Us</a>");
            htmlBuilder.append("</nav>");
            htmlBuilder.append("</header>");
            htmlBuilder.append("<main>");
            htmlBuilder.append("<h2>Translation editing</h2>");
            htmlBuilder.append("<form action=\"/translate/confirm\" method=\"post\">");
            htmlBuilder.append("<table>");

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                // Додавання рядка для кожного ключа-значення
                htmlBuilder.append("<tr>");
                htmlBuilder.append("<td>").append(key).append("</td>");
                htmlBuilder.append("<td><input type=\"text\" name=\"").append(key).append("\" value=\"").append(value).append("\"></td>");
                htmlBuilder.append("</tr>");
            }

            htmlBuilder.append("</table>");
            htmlBuilder.append("<button type=\"submit\">Save Changes</button>");
            htmlBuilder.append("</form>");
            htmlBuilder.append("</main>");
            htmlBuilder.append("<footer>");
            htmlBuilder.append("    <div>&copy; 2024 Sumy State University, Valerij Diahovets </div>");
            htmlBuilder.append("</footer>");
            htmlBuilder.append("</body>");
            htmlBuilder.append("</html>");

            // Відображення HTML-коду на сторінці
            ctx.html(htmlBuilder.toString());
        });
        app.post("/translate/confirm", ctx -> {

            // Отримання мапи з сесії
            Map<String, String> map = ctx.sessionAttribute("map");

            // Отримання нових значень ключів з POST-запиту і оновлення мапи
            for (String key : map.keySet()) {
                String newValue = ctx.formParam(key);
                // Оновлення значення ключа у мапі
                map.put(key, newValue);
            }

            // Оновлена мапа тепер зберігається у сесії
            ctx.sessionAttribute("map", map);

            mapToDatabase.saveUserTranslations(ctx.sessionAttribute("id"), ctx.sessionAttribute("projectName"), ctx.sessionAttribute("targetLang"), map);
            String filename = ctx.sessionAttribute("fileName");

            String format = ctx.sessionAttribute("format");

            if(format.equals("xml")){

                filename = filename + ".xml";

                File file = new File(filename);
                formatFileWriter.exportToXML(map, file.getName());
            }
            if(format.equals("yaml")){

                filename = filename + ".yaml";
                File file = new File(filename);
                formatFileWriter.exportToYAML(map, file.getName());
            }
            if(format.equals("json")){

                filename = filename + ".json";
                File file = new File(filename);
                formatFileWriter.exportToJSON(map, file.getName());
            }

            ctx.sessionAttribute("downloadLink", filename);
            ctx.sessionAttribute("fileName", filename);

            ctx.html(
                    "<html><head><meta charset='UTF-8'><title>Translation results</title>" +
                            "<style>" +
                            "body {" +
                            "    font-family: Arial, sans-serif;" +
                            "    background-color: #f4f4f4;" +
                            "    padding: 20px;" +
                            "    text-align: center;" +
                            "}" +
                            "h2 {" +
                            "    margin-top: 20px;" +
                            "}" +
                            "form {" +
                            "    margin-top: 20px;" +
                            "}" +
                            "p {" +
                            "    margin-bottom: 20px;" +
                            "}" +
                            "button {" +
                            "    padding: 10px 20px;" +
                            "    border: none;" +
                            "    border-radius: 5px;" +
                            "    background-color: #007bff;" +
                            "    color: #fff;" +
                            "    cursor: pointer;" +
                            "}" +
                            "</style>" +
                            "</head><body>" +
                            "<h2>Translation results</h2>" +
                      //      "<p>Ви перекладаєте з мови " + ctx.sessionAttribute("sourceLang") + " на мову " + ctx.sessionAttribute("targetLang") + "</p>" +
                            "<form method='get' action='/download'>" +
                            "    <input type='hidden' id='filename' name='filename' value='" + ctx.sessionAttribute("downloadLink") + "'></input>" +
                            "    <button type='submit'>Download File</button>" +
                            "</form>" +
                            "</body></html>");
        });



        app.get("/download", ctx -> {
            String filename = ctx.sessionAttribute("fileName");
            File file = new File(filename);
            if (file.exists()) {
                ctx.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                ctx.header("Content-Length", String.valueOf(file.length()));
                ctx.header("Content-Type", "application/octet-stream");
                ctx.result(new FileInputStream(file));
            } else {
                ctx.html("Error: File not found.");
            }
        });

    }

    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    public static void main(String[] args) {
        Translator translator = new Translator();
        DatabaseManager databaseManager = new DatabaseManager(translator);
        MapToDatabase mapToDatabase = new MapToDatabase();
        FileToMap fileToMap = new FileToMap(mapToDatabase);
        Registration registration = new Registration();
        UserProject userProject = new UserProject();
        Login login = new Login();
        FormatFileWriter formatFileWriter = new FormatFileWriter();

        Server server = new Server(fileToMap, mapToDatabase, databaseManager, registration, login, userProject, translator, formatFileWriter);
        server.start();
    }
}
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class communicationTest {

    // 用于存储用户验证身份的token，这里我直接默认存储了第一个用户的token。
    private static String authToken = "820e62e0c6881fc3f0ce7975f47279ab41e21247f82f82398656cefac1570e0f";
    private static String userID = "e4188c7b";
    public static String urlsuffix = "http://localhost:8000/NotepadServer/";

    public static void sendPOST_register() throws IOException {
        URI uri = null;
        try {
            uri = new URI(urlsuffix + "register");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
    
        String jsonInputString = "{\"username\": \"test\", \"password\": \"123456\"}";
    
        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);           
        }
    
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                userID = jsonResponse.getString("userID");
            }
        } else {
            System.out.println("POST request not worked");
        }
    }

    public static void sendPOST_changePassword(String userID, String oldPassword, String newPassword) throws IOException {
        URI uri = null;
        try {
            uri = new URI(urlsuffix + "changePassword");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);

        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("userID", userID);
        jsonInputString.put("oldPassword", oldPassword);
        jsonInputString.put("newPassword", newPassword);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.toString().getBytes("utf-8");
            os.write(input, 0, input.length);           
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }

    // 请在这里加上不允许输入为空的检测
    public static void sendPOST_chatGLM(String message) throws IOException {
        URI uri = null;
        try {
            uri = new URI(urlsuffix + "chatGLM");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);

        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("message", message);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.toString().getBytes("utf-8");
            os.write(input, 0, input.length);           
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                String answer = jsonResponse.getString("answer");
                System.out.println(answer);
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }

    public static void sendPOST_login(String userID, String password) throws IOException {
        URI uri = null;
        try {
            uri = new URI(urlsuffix + "login");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("userID", userID);
        jsonInputString.put("password", password);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.toString().getBytes("utf-8");
            os.write(input, 0, input.length);           
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                authToken = jsonResponse.getString("token");
                String username = jsonResponse.getString("username");
                // 检查并获取personalSignature
                String personalSignature;
                if (jsonResponse.isNull("personalSignature")) {
                    personalSignature = "";  // 或者其他默认值
                } else {
                    personalSignature = jsonResponse.getString("personalSignature");
                }

                // 检查并获取noteList
                JSONArray noteList;
                if (jsonResponse.isNull("noteList")) {
                    noteList = new JSONArray();  // 或者其他默认值
                } else {
                    noteList = jsonResponse.getJSONArray("noteList");
                }

                System.out.println("Note List: " + noteList.toString());
                System.out.println("Token: " + authToken);
                System.out.println("Username: " + username);
                System.out.println("Personal Signature: " + personalSignature);
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }

    public static void sendPOST_uploadNote(String userID, String title, String type, File parentDirectory) throws IOException {

        if (!parentDirectory.exists() || !parentDirectory.isDirectory() || parentDirectory.listFiles().length == 0){
            System.out.println("Parent directory does not exist");
            return;
        }

        URI uri = null;
        try {
            uri = new URI(urlsuffix + "createNote");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);

        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("userID", userID);
        jsonInputString.put("title", title);
        jsonInputString.put("type", type);
        jsonInputString.put("parentDirectory", parentDirectory.getPath());
        String uploadFileListJson = "[{\"content\": \"天上太阳哈呀哈基米诶\", \"type\": \"text\"}, {\"content\":\"./userData/1.jpg\", \"type\": \"image\"}, {\"content\":\"./userData/2.jpg\", \"type\": \"image\"}, {\"content\": \"./userData/3.mp4\", \"type\": \"audio\"}]";
        JSONArray jsonArray = new JSONArray(uploadFileListJson);
        jsonInputString.put("uploadFileListJson", jsonArray);
        String boundary = Long.toHexString(System.currentTimeMillis()); 
        conn.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + boundary);
        
        try (OutputStream output = conn.getOutputStream(); PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
            // Send JSON data.
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"json\"").append("\r\n");
            writer.append("Content-Type: application/json; charset=UTF-8").append("\r\n");
            writer.append("\r\n");
            writer.append(jsonInputString.toString()).append("\r\n").flush();
        
            // Send binary file.
            for (File uploadFile: parentDirectory.listFiles()) {
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + uploadFile.getName() + "\"").append("\r\n");
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(uploadFile.getName())).append("\r\n");
                writer.append("Content-Transfer-Encoding: binary").append("\r\n");
                writer.append("\r\n").flush();
                Files.copy(uploadFile.toPath(), output);
                output.flush(); // Important before continuing with writer!
                writer.append("\r\n").flush(); // CRLF is important! It indicates end of binary boundary.
            }
        
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append("\r\n").flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }

    public static void sendPOST_deleteNote(String userID, String demosticId) throws IOException {
        URI uri = null;
        try {
            uri = new URI(urlsuffix + "deleteNote");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);

        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("userID", userID);
        jsonInputString.put("demosticId", demosticId);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.toString().getBytes("utf-8");
            os.write(input, 0, input.length);           
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }
    /* 
    public static void sendPOST_modifyNote(String userID, String demosticId, String title, String type) {
        URI uri = null;
        try {
            uri = new URI("http://localhost:8000/NotepadServer/modifyNote");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);

        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("userID", userID);
        jsonInputString.put("title", title);
        jsonInputString.put("type", type);
        jsonInputString.put("demosticId", demosticId);
        
        String boundary = Long.toHexString(System.currentTimeMillis()); 
        conn.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + boundary);
        
        try (OutputStream output = conn.getOutputStream(); PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
            // Send JSON data.
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"json\"").append("\r\n");
            writer.append("Content-Type: application/json; charset=UTF-8").append("\r\n");
            writer.append("\r\n");
            writer.append(jsonInputString.toString()).append("\r\n").flush();
        
            // Send binary file.
            //////////////////////////////////////////请注意！！！！////////////////////////////////////////////
            // 这里需要你完成很多事情。
            // 1. 获取文件的路径
            // 2. 判断文件是否应该上传
            // 3. 上传对应的文件
            for (File uploadFile: parentDirectory.listFiles()) {
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + uploadFile.getName() + "\"").append("\r\n");
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(uploadFile.getName())).append("\r\n");
                writer.append("Content-Transfer-Encoding: binary").append("\r\n");
                writer.append("\r\n").flush();
                Files.copy(uploadFile.toPath(), output);
                output.flush(); // Important before continuing with writer!
                writer.append("\r\n").flush(); // CRLF is important! It indicates end of binary boundary.
            }
        
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append("\r\n").flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }*/

    //这段代码完成的功能是从服务器下载文件并放到对应的文件路径上面
    public static void sendPOST_syncDownload(String userID, int demosticId,String filename) throws IOException {
        URI uri = null;
        try {
            uri = new URI(urlsuffix + "syncDownload");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);

        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("userID", userID);
        jsonInputString.put("demosticId", demosticId);
        jsonInputString.put("filename", filename);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.toString().getBytes("utf-8");
            os.write(input, 0, input.length);           
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {             
                String filePath = "userData/" + userID + "/" + demosticId + "/" + filename;
                File file = new File(filePath);                
                file.getParentFile().mkdirs();
                FileOutputStream output = new FileOutputStream(file);
                System.out.println("./userData/" + userID + "/" + demosticId + filename);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = conn.getInputStream().read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }

    public static void sendPOST_changeAvatar(String userID, String filePath) throws IOException{
        File newAvatar = new File(filePath);
        if (!newAvatar.exists()){
            System.out.println("Avatar does not exist");
            return;
        }
    
        URI uri = null;
        try {
            uri = new URI(urlsuffix + "changeAvatar");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);
    
        JSONObject jsonInputString = new JSONObject();
        jsonInputString.put("userID", userID);
        String boundary = Long.toHexString(System.currentTimeMillis()); 
        conn.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + boundary);
        
        try (OutputStream output = conn.getOutputStream(); PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
            // Send JSON data.
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"json\"").append("\r\n");
            writer.append("Content-Type: application/json; charset=UTF-8").append("\r\n");
            writer.append("\r\n");
            writer.append(jsonInputString.toString()).append("\r\n").flush();
        
            // Send binary file.
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"newAvatar\"; filename=\"" + newAvatar.getName() + "\"").append("\r\n");
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(newAvatar.getName())).append("\r\n");
            writer.append("Content-Transfer-Encoding: binary").append("\r\n");
            writer.append("\r\n").flush();
            Files.copy(newAvatar.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append("\r\n").flush(); // CRLF is important! It indicates end of binary boundary.
        
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append("\r\n").flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } else {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Error: " + response.toString());
            }
        }
    }

    public static void main(String[] args) {
        try {
            int functionNumber = Integer.parseInt(args[0]);
            switch (functionNumber) {
                case 1:
                    sendPOST_register();
                    break;
                case 2:
                    sendPOST_changePassword(userID, "123456", "654321");
                    break;
                case 3:
                    sendPOST_chatGLM("请你为我做一下心理疏导。");
                    break;
                case 4:
                    sendPOST_login(userID, "123456");
                    break;
                case 5:
                    File parentDirectory = new File("userData", "3");
                    sendPOST_uploadNote(userID, "美好的生活", "dairy", parentDirectory);
                    break;
                case 6:
                    sendPOST_deleteNote(userID, "2");
                    break;
                /*case 7:
                    sendPOST_modifyNote(userID, "2", "美好的生活", "dairy");
                    break;*/
                case 8:
                    sendPOST_syncDownload(userID, 1,"1.txt");
                    break;
                case 9:
                    sendPOST_changeAvatar(userID, "./userData/avatar/1.jpg");
                    break;
                default:
                    System.out.println("Invalid function number");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Argument must be an integer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
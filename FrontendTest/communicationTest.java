import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONObject;

public class communicationTest {

    // 用于存储用户验证身份的token，这里我直接默认存储了第一个用户的token。
    private static String authToken = "03f6ac911ce046e407721c8d4a93a3788ac09b66716f3070bb1af239a768b831";

    // 这个函数向服务器发送一个 GET 请求以获取 CSRF 令牌    
    public static String getCSRFToken() throws IOException {
        URI uri = null;
        try {
            uri = new URI("http://localhost:8000/NotepadServer/get_csrf_token");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
    
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                String csrfToken = jsonResponse.getString("csrf_token");
                System.out.println("CSRF Token: " + csrfToken);
                return csrfToken;
            }
        } else {
            throw new IOException("Failed to get CSRF token: HTTP error code : " + responseCode);
        }
    }

    public static void sendPOST_register() throws IOException {
        URI uri = null;
        try {
            uri = new URI("http://localhost:8000/NotepadServer/register");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");

        String csrfToken = getCSRFToken();
        conn.setRequestProperty("X-CSRFToken", csrfToken);
        conn.setRequestProperty("Cookie", "csrftoken=" + csrfToken);
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
                JSONObject json = new JSONObject(response.toString());
                authToken = json.getString("token");
                System.out.println("Auth Token: " + authToken);
            }
        } else {
            System.out.println("POST request not worked");
        }
    }

    public static void sendPOST_changePassword(String userID, String oldPassword, String newPassword) throws IOException {
        URI uri = null;
        try {
            uri = new URI("http://localhost:8000/NotepadServer/changePassword");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        
        String csrfToken = getCSRFToken();
        conn.setRequestProperty("X-CSRFToken", csrfToken);
        conn.setRequestProperty("Cookie", "csrftoken=" + csrfToken);
        conn.setDoOutput(true);
        System.out.println(authToken);
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

    public static void main(String[] args) {
        try {
            int functionNumber = Integer.parseInt(args[0]);
            switch (functionNumber) {
                case 1:
                    sendPOST_register();
                    break;
                case 2:
                    sendPOST_changePassword("8374be20", "123456", "654321");
                    break;
                // 添加更多的 case 语句来调用其他函数
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
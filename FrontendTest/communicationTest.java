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

    // 保存 token 的变量
    private static String token = null;

    public static void sendPOST() throws IOException {
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
                // 解析响应体，获取 token
                JSONObject jsonResponse = new JSONObject(response.toString());
                token = jsonResponse.getString("token");
                System.out.println("Token: " + token);
            }
        } else {
            System.out.println("POST request not worked");
        }
    }

    // 获取 token 的方法
    public static String getToken() {
        return token;
    }

    public static void main(String[] args) {
        try {
            sendPOST();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {

    private static final String API_URL = "https://api-inference.huggingface.co/models/TinyLlama/TinyLlama-1.1B-Chat-v1.0";
    private static final String TOKEN = "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"; // Replace with your token

    public static void main(String[] args) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
            conn.setDoOutput(true);

            String jsonInputString = "{\"inputs\": \"Please generate a python hello world example. \"}";

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8")) {
                while(scanner.hasNextLine()) {
                    System.out.println(scanner.nextLine());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.mishlen.telegram_bot_elastic.service;
import com.mishlen.telegram_bot_elastic.model.FileModel;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileService {
    public static String searchLog(String message, FileModel model) throws IOException {
        String get_url = "http://localhost:8080/api/search";
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(get_url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            message = message.replace("\"", "\\"+"\"");
            message = message.replace("\n", "");
            
            String body = "{\n" +
                    "\"json\": " + "\"" + message + "\"" +
                    "}";

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

        } catch (Exception e) {
            response.append(e.getMessage());
            e.printStackTrace();
        }
        return response.toString();
    }
}

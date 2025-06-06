package org.example;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class Translator {
    private final String apiKey = Keys.loadProperty("DEEPL.AUTH_KEY");
    private final OkHttpClient httpClient;

    public Translator() {
        this.httpClient = new OkHttpClient();
    }

    public String translate(String text, String sourceLanguage, String targetLanguage) throws IOException {
        // Створення запиту до API Deepl
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api-free.deepl.com/v2/translate").newBuilder();
        urlBuilder.addQueryParameter("auth_key", apiKey);
        urlBuilder.addQueryParameter("text", text);
        urlBuilder.addQueryParameter("source_lang", sourceLanguage);
        urlBuilder.addQueryParameter("target_lang", targetLanguage);

        // Виконання HTTP запиту
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Отримання результату перекладу з відповіді API
            JSONObject jsonResponse = new JSONObject(response.body().string());
            String answer = jsonResponse.getJSONArray("translations").getJSONObject(0).getString("text");
            if(answer.endsWith(".")){
                answer = answer.substring(0, answer.length() - 1);
            }
            return answer;
        }
    }
}
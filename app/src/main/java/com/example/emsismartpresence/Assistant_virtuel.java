package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Assistant_virtuel extends AppCompatActivity {
    private EditText editMessage;
    private RecyclerView recyclerView;
    private Button btnSend;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private final String API_KEY = "AIzaSyDlKUuqChaM_8BQIvjFBw56-oQzaGIDCqo";
    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_virtuel);

        // Configurer la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(Assistant_virtuel.this, Home.class);
            startActivity(intent);
            finish();
        });

        // Initialiser les vues
        editMessage = findViewById(R.id.prompt);
        recyclerView = findViewById(R.id.messages_recycler_view);
        btnSend = findViewById(R.id.btnSend);

        // Configurer le RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Faire défiler vers le bas automatiquement
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                recyclerView.post(() -> recyclerView.smoothScrollToPosition(messageAdapter.getItemCount()));
            }
        });

        // Gérer l'envoi du message
        btnSend.setOnClickListener(v -> {
            String userMessage = editMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Ajouter le message de l'utilisateur
                messageList.add(new Message(userMessage, Message.TYPE_USER, LocalDateTime.now()));
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
                editMessage.setText("");

                // Envoyer au modèle
                sendMessageToGemini(userMessage);
            }
        });
    }

    private void sendMessageToGemini(String message) {
        JSONObject json = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", message);
            JSONObject contentItem = new JSONObject();
            contentItem.put("parts", new JSONArray().put(part));
            contents.put(contentItem);
            json.put("contents", contents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    messageList.add(new Message("Erreur : " + e.getMessage(), Message.TYPE_ASSISTANT, LocalDateTime.now()));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.smoothScrollToPosition(messageList.size() - 1);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray candidates = jsonResponse.getJSONArray("candidates");
                        String text = candidates.getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() -> {
                            messageList.add(new Message(text, Message.TYPE_ASSISTANT, LocalDateTime.now()));
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.smoothScrollToPosition(messageList.size() - 1);
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            messageList.add(new Message("Erreur de traitement : " + e.getMessage(), Message.TYPE_ASSISTANT, LocalDateTime.now()));
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.smoothScrollToPosition(messageList.size() - 1);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        messageList.add(new Message("Erreur : " + response.message(), Message.TYPE_ASSISTANT, LocalDateTime.now()));
                        messageAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                    });
                }
            }
        });
    }
}
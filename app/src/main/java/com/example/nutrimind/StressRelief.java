package com.example.nutrimind;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.*;

public class StressRelief extends AppCompatActivity {

    ImageView btnMic, btnPlus, btnSend;
    EditText etText;
    TextView txtResult;

    String url = "https://erandika-nutrimind-backend.hf.space/transcribe";

    static final int PICK_AUDIO = 101;
    static final int RECORD_AUDIO = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress_relief);

        btnMic = findViewById(R.id.btnMic);
        btnPlus = findViewById(R.id.btnPlus);
        btnSend = findViewById(R.id.btnSendText);
        etText = findViewById(R.id.etFeeling);
        txtResult = findViewById(R.id.txtAIResponse);

        // TEXT SEND
        btnSend.setOnClickListener(v -> sendText());

        // MIC BUTTON → RECORD
        btnMic.setOnClickListener(v -> {
            Intent i = new Intent(this, RecordActivity.class);
            startActivityForResult(i, RECORD_AUDIO);
        });

        // PLUS BUTTON → PICK FILE
        btnPlus.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("audio/*");
            startActivityForResult(i, PICK_AUDIO);
        });
    }

    // ---------------- TEXT ----------------
    void sendText() {
        String msg = etText.getText().toString().trim();
        if (msg.isEmpty()) return;

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("text", msg)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {}

            public void onResponse(@NonNull Call call, @NonNull Response response) throws java.io.IOException {
                String res = response.body().string();
                runOnUiThread(() -> handle(res));
            }
        });
    }

    // ---------------- AUDIO RESULT ----------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == PICK_AUDIO) {
            Uri uri = data.getData();
            if (uri != null) {
                File file = copyFile(uri);
                if (file != null) uploadAudio(file);
            }

        } else if (requestCode == RECORD_AUDIO) {
            String path = data.getStringExtra("audioPath");
            if (path != null) {
                uploadAudio(new File(path));
            }
        }
    }

    // ---------------- UPLOAD AUDIO ----------------
    void uploadAudio(File file) {

        OkHttpClient client = new OkHttpClient();

        RequestBody fileBody = RequestBody.create(
                file,
                MediaType.parse("application/octet-stream")
        );

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {}

            public void onResponse(@NonNull Call call, @NonNull Response response) throws java.io.IOException {
                String res = response.body().string();
                runOnUiThread(() -> handle(res));
            }
        });
    }

    // ---------------- PARSE ----------------
    void handle(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject r = obj.getJSONArray("results").getJSONObject(0);

            String mood = r.getString("mood");
            String response = r.getString("response");

            txtResult.setText(response);

            getSharedPreferences("NutriMindPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("latestMood", mood)
                    .apply();

        } catch (Exception e) {
            txtResult.setText("Error");
        }
    }

    // ---------------- FILE COPY ----------------
    File copyFile(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "audio.wav");

            FileOutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();

            return file;

        } catch (Exception e) {
            return null;
        }
    }
}
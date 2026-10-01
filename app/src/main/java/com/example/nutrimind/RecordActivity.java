package com.example.nutrimind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private Button btnRecord;
    private MediaRecorder recorder;
    private boolean isRecording = false;
    private String audioFilePath;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        btnRecord = findViewById(R.id.btnRecord);

        audioFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                .getAbsolutePath() + "/recorded_audio.m4a";

        btnRecord.setOnClickListener(v -> {

            if (checkPermission()) {

                if (!isRecording) {
                    startRecording();
                    btnRecord.setText("⏹ Stop");
                } else {
                    stopRecording();
                }

                isRecording = !isRecording;

            } else {
                requestPermission();
            }
        });
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(audioFilePath);

            recorder.prepare();
            recorder.start();

            Toast.makeText(this, "Recording started 🎤", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;

            Toast.makeText(this, "Recording finished", Toast.LENGTH_SHORT).show();

            // Return file path back
            Intent resultIntent = new Intent();
            resultIntent.putExtra("audioPath", audioFilePath);
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error stopping", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }
}

package com.example.multilanguagetranslator;

import android.Manifest;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Map<String, Button> recordButtons;
    private Map<String, Button> playButtons;
    private Map<String, MediaRecorder> mediaRecorders;
    private Map<String, MediaPlayer> mediaPlayers;
    private Map<String, Boolean> isRecordingMap;
    private Map<String, Boolean> isPlayingMap;
    private String query;
    private String[] languages = {"English", "French", "Spanish", "Portuguese", "German", "Japanese", "Korean", "Chinese"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        recordButtons = new HashMap<>();
        playButtons = new HashMap<>();
        mediaRecorders = new HashMap<>();
        mediaPlayers = new HashMap<>();
        isRecordingMap = new HashMap<>();
        isPlayingMap = new HashMap<>();

        query = getIntent().getStringExtra("query");

        LinearLayout buttonContainer = findViewById(R.id.buttonContainer);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO_PERMISSION);

        for (String language : languages) {
            LinearLayout horizontalLayout = new LinearLayout(this);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            horizontalLayoutParams.setMargins(8, 8, 8, 8);
            horizontalLayout.setLayoutParams(horizontalLayoutParams);
            horizontalLayout.setGravity(Gravity.CENTER);

            Button recordButton = new Button(this);
            recordButton.setText(language);
            recordButton.setBackgroundResource(R.drawable.button_normal);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            buttonParams.setMargins(8, 8, 16, 8);
            recordButton.setLayoutParams(buttonParams);
            recordButton.setTextSize(12);
            recordButton.setTextColor(Color.WHITE);
            recordButton.setPadding(20, 10, 20, 10);
            recordButton.setOnClickListener(v -> {
                if (isRecordingMap.get(language)) {
                    stopRecording(language);
                } else {
                    startRecording(language);
                }
            });
            horizontalLayout.addView(recordButton);

            Button playButton = new Button(this);
            playButton.setText("Play");
            playButton.setTextColor(Color.WHITE);
            playButton.setBackgroundResource(R.drawable.play_button_normal);
            playButton.setLayoutParams(buttonParams);
            playButton.setTextSize(12);
            playButton.setPadding(20, 10, 20, 10);
            playButton.setOnClickListener(v -> {
                if (isPlayingMap.get(language)) {
                    stopAudio(language);
                } else {
                    playAudio(language);
                }
            });
            horizontalLayout.addView(playButton);

            buttonContainer.addView(horizontalLayout);

            recordButtons.put(language, recordButton);
            playButtons.put(language, playButton);
            isRecordingMap.put(language, false);
            isPlayingMap.put(language, false);
        }
    }

    private void startRecording(String language) {
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getFileName(language));
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            mediaRecorders.put(language, mediaRecorder);
            isRecordingMap.put(language, true);
            recordButtons.get(language).setBackgroundResource(R.drawable.button_pressed);
            Toast.makeText(this, "I'm recording in " + language + "...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording(String language) {
        MediaRecorder mediaRecorder = mediaRecorders.get(language);
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorders.remove(language);
            isRecordingMap.put(language, false);
            recordButtons.get(language).setText(language);
            recordButtons.get(language).setBackgroundResource(R.drawable.button_normal);
            Toast.makeText(this, "Audio saved: " + getFileName(language), Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio(String language) {
        if (mediaPlayers.get(language) == null) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayers.put(language, mediaPlayer);
        } else {
            mediaPlayers.get(language).reset();
        }

        MediaPlayer mediaPlayer = mediaPlayers.get(language);

        try {
            mediaPlayer.setDataSource(getFileName(language));
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlayingMap.put(language, true);
            playButtons.get(language).setBackgroundResource(R.drawable.play_button_pressed);
            mediaPlayer.setOnCompletionListener(mp -> {
                stopAudio(language);
                resetPlayButton(language);
            });
            Toast.makeText(this, "I'm playing: " + language, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudio(String language) {
        MediaPlayer mediaPlayer = mediaPlayers.get(language);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            isPlayingMap.put(language, false);
            resetPlayButton(language);
            Toast.makeText(this, "Playback interrupted: " + language, Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPlayButton(String language) {
        playButtons.get(language).setText("Play");
        playButtons.get(language).setBackgroundResource(R.drawable.play_button_normal);
    }

    private String getFileName(String language) {
        return getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/" + query + "_" + language + ".3gp";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaRecorder mediaRecorder : mediaRecorders.values()) {
            if (mediaRecorder != null) {
                mediaRecorder.release();
            }
        }
        for (MediaPlayer mediaPlayer : mediaPlayers.values()) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
    }
}
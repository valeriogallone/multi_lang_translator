package com.example.multilanguagetranslator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MultiLanguage extends AppCompatActivity {
    private SearchView searchView;
    private TextView resultTextView;
    private Button
            engButton, fraButton, spaButton, porButton,
            gerButton, japButton, korButton,chiButton, viewHistoryButton;
    private HistoryManager historyManager;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        resultTextView = findViewById(R.id.resultTextView);
        engButton = findViewById(R.id.engButton);
        fraButton = findViewById(R.id.fraButton);
        spaButton = findViewById(R.id.spaButton);
        porButton = findViewById(R.id.porButton);
        gerButton = findViewById(R.id.gerButton);
        japButton = findViewById(R.id.japButton);
        korButton = findViewById(R.id.korButton);
        chiButton = findViewById(R.id.chiButton);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);

        historyManager = new HistoryManager(this);


        engButton.setOnClickListener(v -> translateText(TranslateLanguage.ENGLISH));
        fraButton.setOnClickListener(v -> translateText(TranslateLanguage.FRENCH));
        spaButton.setOnClickListener(v -> translateText(TranslateLanguage.SPANISH));
        porButton.setOnClickListener(v -> translateText(TranslateLanguage.PORTUGUESE));
        gerButton.setOnClickListener(v -> translateText(TranslateLanguage.GERMAN));
        japButton.setOnClickListener(v -> translateText(TranslateLanguage.JAPANESE));
        korButton.setOnClickListener(v -> translateText(TranslateLanguage.KOREAN));
        chiButton.setOnClickListener(v -> translateText(TranslateLanguage.CHINESE));

        viewHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MultiLanguage.this, HistoryActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    Log.d("MultiLanguage", "Query submitted: " + query);
                    historyManager.addToSearchHistory(query);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void translateText(String targetLanguage) {
        String inputText = searchView.getQuery().toString();

        if (!inputText.isEmpty()) {
            TranslatorOptions options = new TranslatorOptions.Builder()
                    //Switch to another language if you need, i'm Italian so i use italian for researches :)
                    .setSourceLanguage(TranslateLanguage.ITALIAN)
                    .setTargetLanguage(targetLanguage)
                    .build();
            Translator translator = (Translator) Translation.getClient(options);

            translator.downloadModelIfNeeded().addOnSuccessListener(unused -> {
                translator.translate(inputText).addOnSuccessListener(translatedText ->
                        resultTextView.setText(translatedText)
                ).addOnFailureListener(exception ->
                        resultTextView.setText("Error during translation: " + exception.getMessage())
                );
            }).addOnFailureListener(exception ->
                    resultTextView.setText("Error during module download: " + exception.getMessage())
            );
        } else {
            resultTextView.setText("Insert and translate.");
        }
    }
}

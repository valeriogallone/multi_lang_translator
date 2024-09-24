package com.example.multilanguagetranslator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private LinearLayout historyContainer;
    private TextView tvHistoryTitle;
    private HistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.historyContainer);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);

        historyManager = new HistoryManager(this);
        displaySearchHistory();

        }

    private void displaySearchHistory() {
        ArrayList<String> searchHistory = historyManager.getSearchHistory();
        StringBuilder historyBuilder = new StringBuilder();
        for (String query : searchHistory) {
            historyBuilder.append(query).append("\n");
            TextView textView = new TextView(this);
            textView.setText(query);
            textView.setPadding(0, 30, 0, 30);
            textView.setOnClickListener(v -> {
                Intent intent = new Intent(HistoryActivity.this, RecordActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
            });
            historyContainer.addView(textView);
        }
        tvHistoryTitle.setText("Search history:\n");

    }

}


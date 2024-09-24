package com.example.multilanguagetranslator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryManager {

    private static final String PREFS_NAME = "search_prefs";
    private static final String HISTORY_KEY = "search_history";
    private SharedPreferences sharedPreferences;
    private ArrayList<String> searchHistory;

    public HistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSearchHistory();
    }

    public void addToSearchHistory(String query) {
        if (!searchHistory.contains(query)) {
            searchHistory.add(query);
            Log.d("HistoryManager", "Added to history: " + query);
            saveSearchHistory();
        }
    }

    private void loadSearchHistory() {
        String json = sharedPreferences.getString(HISTORY_KEY, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        searchHistory = new Gson().fromJson(json, type);

        if (searchHistory == null) {
            Log.d("HistoryManager", "History loaded: " + searchHistory);
            searchHistory = new ArrayList<>();
        }
    }

    private void saveSearchHistory() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(searchHistory);
        editor.putString(HISTORY_KEY, json);
        editor.apply();
    }

    public ArrayList<String> getSearchHistory() {

        return searchHistory;
    }
}

package com.melodify.music.fragment;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.melodify.music.R;

import java.util.Locale;

public class ChangeLanguageFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_language, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Find language selection buttons or spinner and set click listeners
        Button englishButton = view.findViewById(R.id.button_english);
        Button vietnameseButton = view.findViewById(R.id.button_vietnamese);
        Button frenchButton = view.findViewById(R.id.button_french);
        Button spanishButton = view.findViewById(R.id.button_spanish);
        Button chineseButton = view.findViewById(R.id.button_chinese);
        Button koreanButton = view.findViewById(R.id.button_korean);
        Button japaneseButton = view.findViewById(R.id.button_japanese);

        englishButton.setOnClickListener(this);
        vietnameseButton.setOnClickListener(this);
        frenchButton.setOnClickListener(this);
        spanishButton.setOnClickListener(this);
        chineseButton.setOnClickListener(this);
        koreanButton.setOnClickListener(this);
        japaneseButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        String language = "";

        switch (v.getId()) {
            case R.id.button_english:
                language = "en";
                break;
            case R.id.button_vietnamese:
                language = "vi";
                break;
            case R.id.button_french:
                language = "fr";
                break;
            case R.id.button_spanish:
                language = "es";
                break;
            case R.id.button_chinese:
                language = "zh";
                break;
            case R.id.button_korean:
                language = "ko";
                break;
            case R.id.button_japanese:
                language = "ja";
                break;
            // Add more cases for other languages if needed
        }

        setLocale(language);
        saveLanguagePreference(language);
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        Resources resources = getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Restart the activity to apply the language changes
        requireActivity().recreate();
    }

    private void saveLanguagePreference(String language) {
        preferences.edit().putString("language", language).apply();
    }
}


package com.example.testingapp.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testingapp.R;
import com.example.testingapp.model.AccountModel;

public class ThemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    private void applyTheme() {
        // Use AccountModel to check for user instead of Firebase to avoid NPEs
        String email = AccountModel.getInstance().getEmail1();

        String prefName;
        if (email == null || email.isEmpty()) {
            prefName = "settings_default";
        } else {
            prefName = "settings_" + email;
        }
        // Read theme from global settings
        SharedPreferences userPref = getSharedPreferences(prefName, MODE_PRIVATE);
        int theme = userPref.getInt("theme", 0);

        switch (theme) {
        case 1:
            setTheme(R.style.Theme_App_Dark);
            break;
        case 2:
            setTheme(R.style.Theme_App_Blue);
            break;
        case 3:
            setTheme(R.style.Theme_App_Green);
            break;
        default:
            setTheme(R.style.Theme_App_Starting);
        }
    }
}
package com.example.testingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.testingapp.BR;
import com.example.testingapp.R;
import com.example.testingapp.databinding.ActivityMainBinding;
import com.example.testingapp.viewmodel.MainViewModel;

public class MainActivity extends ThemeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        // Use Data Binding to inflate the layout
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Create ViewModel
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Bind ViewModel
        binding.setVariable(BR.viewModel, viewModel);

        binding.setLifecycleOwner(this);

        // Find Start button by ID
        Button startButton = findViewById(R.id.start_button);

        // Find Quit button by ID
        Button quitButton = findViewById(R.id.quit_button);

        // Set onclick Listener for button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to move to login screen
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        // Set onclick Listener for button
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quit the app if this button is pressed
                finishAffinity();
            }
        });
    }
}
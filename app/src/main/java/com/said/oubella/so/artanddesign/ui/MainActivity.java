package com.said.oubella.so.artanddesign.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.said.oubella.so.artanddesign.ArtAndDesignApp;
import com.said.oubella.so.artanddesign.R;
import com.said.oubella.so.artanddesign.databinding.ActivityMainBinding;

import java.util.Objects;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        final NavController navController = ((NavHostFragment) Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.main_host_fragment)))
                .getNavController();

        final InputMethodManager ime = ((ArtAndDesignApp) getApplication()).container().inputMethodManager();

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() != R.id.searchFragment) {
                ime.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });
    }
}

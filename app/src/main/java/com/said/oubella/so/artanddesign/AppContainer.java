package com.said.oubella.so.artanddesign;

import android.app.Application;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.room.Room;

import com.said.oubella.so.artanddesign.cache.ArticlesDao;
import com.said.oubella.so.artanddesign.cache.ArticlesDatabase;
import com.said.oubella.so.artanddesign.connectivity.Connectivity;
import com.said.oubella.so.artanddesign.connectivity.ConnectivityImpl;
import com.said.oubella.so.artanddesign.preferences.AppPreferences;
import com.said.oubella.so.artanddesign.preferences.AppPreferencesImpl;
import com.said.oubella.so.artanddesign.repository.ArticlesRepository;
import com.said.oubella.so.artanddesign.repository.ArticlesRepositoryImpl;

public final class AppContainer {

    private final ArticlesRepository repository;
    private final Connectivity connectivity;
    private final AppPreferences preferences;
    private final InputMethodManager inputMethodManager;

    AppContainer(Application context) {

        final AppExecutors executors = new AppExecutors();

        final ArticlesDao articlesDao = Room.databaseBuilder(
                context, ArticlesDatabase.class, "articles.db"
        ).fallbackToDestructiveMigrationFrom().build().articlesDao();

        this.inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.connectivity = new ConnectivityImpl(context, executors);
        this.repository = new ArticlesRepositoryImpl(articlesDao, connectivity);
        this.preferences = new AppPreferencesImpl(context);
    }

    public InputMethodManager inputMethodManager() {
        return inputMethodManager;
    }

    public ArticlesRepository articlesRepository() {
        return repository;
    }

    public Connectivity connectivity() {
        return connectivity;
    }

    public AppPreferences preferences() {
        return preferences;
    }
}

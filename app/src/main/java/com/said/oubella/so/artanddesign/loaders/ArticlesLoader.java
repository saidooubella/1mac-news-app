package com.said.oubella.so.artanddesign.loaders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import com.said.oubella.so.artanddesign.repository.ArticlesRepository;
import com.said.oubella.so.artanddesign.state.FetchingResult;

public final class ArticlesLoader extends AsyncTaskLoader<FetchingResult> {

    private final ArticlesRepository repository;

    public ArticlesLoader(Context context, ArticlesRepository repository) {
        super(context);
        this.repository = repository;
    }

    public ArticlesLoader init() {
        forceLoad();
        return this;
    }

    @NonNull
    @Override
    public FetchingResult loadInBackground() {
        return repository.refreshData();
    }
}

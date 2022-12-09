package com.said.oubella.so.artanddesign.loaders;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.said.oubella.so.artanddesign.repository.ArticlesRepository;
import com.said.oubella.so.artanddesign.state.SearchResult;

public class ArticlesSearchLoader extends AsyncTaskLoader<SearchResult> {

    private final ArticlesRepository repository;
    private final String requestUrl;

    public ArticlesSearchLoader(Context context, String requestUrl, ArticlesRepository repository) {
        super(context);
        this.requestUrl = requestUrl;
        this.repository = repository;
    }

    @Nullable
    @Override
    public SearchResult loadInBackground() {
        return repository.searchFor(requestUrl);
    }

    public ArticlesSearchLoader init() {
        forceLoad();
        return this;
    }
}

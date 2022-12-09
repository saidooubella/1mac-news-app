package com.said.oubella.so.artanddesign.repository;

import androidx.lifecycle.LiveData;

import com.said.oubella.so.artanddesign.models.Article;
import com.said.oubella.so.artanddesign.state.FetchingResult;
import com.said.oubella.so.artanddesign.state.SearchResult;

import java.util.List;

public interface ArticlesRepository {

    FetchingResult refreshData();

    SearchResult searchFor(String requestUrl);

    LiveData<List<Article>> articles();
}

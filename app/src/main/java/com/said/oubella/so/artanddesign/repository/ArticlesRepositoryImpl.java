package com.said.oubella.so.artanddesign.repository;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.said.oubella.so.artanddesign.cache.ArticlesDao;
import com.said.oubella.so.artanddesign.connectivity.Connectivity;
import com.said.oubella.so.artanddesign.models.Article;
import com.said.oubella.so.artanddesign.network.ApiRequest;
import com.said.oubella.so.artanddesign.network.ApiResult;
import com.said.oubella.so.artanddesign.network.RequestUrlBuilder;
import com.said.oubella.so.artanddesign.state.FetchingResult;
import com.said.oubella.so.artanddesign.state.SearchResult;

import java.util.List;

public final class ArticlesRepositoryImpl implements ArticlesRepository {

    private final ArticlesDao articlesDao;
    private final Connectivity connectivity;

    public ArticlesRepositoryImpl(ArticlesDao articlesDao, Connectivity connectivity) {
        this.articlesDao = articlesDao;
        this.connectivity = connectivity;
    }

    @Override
    public FetchingResult refreshData() {
        final ApiResult response = ApiRequest.requestData(new RequestUrlBuilder().build(), connectivity);
        if (response instanceof ApiResult.Success) {
            articlesDao.clear();
            articlesDao.insertAll(((ApiResult.Success) response).getResult());
            return FetchingResult.OK;
        } else if (response instanceof ApiResult.Failure) {
            if (response == ApiResult.Failure.SERVER_BAD_RESPONSE) {
                return FetchingResult.BAD_SERVER_RESPONSE;
            } else if (response == ApiResult.Failure.LAKE_OF_SERVICE) {
                return FetchingResult.NO_INTERNET;
            }
        }
        throw new IllegalStateException("Unexpected value: " + response);
    }

    @Override
    public SearchResult searchFor(String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) return SearchResult.Other.NONE;
        ApiResult result = ApiRequest.requestData(requestUrl, connectivity);
        if (result instanceof ApiResult.Success) {
            final ApiResult.Success success = (ApiResult.Success) result;
            if (success.getResult().isEmpty()) return SearchResult.Other.NO_RESULTS;
            return new SearchResult.Result(success.getResult());
        } else if (result instanceof ApiResult.Failure) {
            if (result == ApiResult.Failure.SERVER_BAD_RESPONSE) {
                return SearchResult.Other.BAD_SERVER_RESPONSE;
            } else if (result == ApiResult.Failure.LAKE_OF_SERVICE) {
                return SearchResult.Other.NO_INTERNET;
            }
        }
        throw new IllegalStateException("Unexpected value: " + result);
    }

    @Override
    public LiveData<List<Article>> articles() {
        return articlesDao.getArticles();
    }
}

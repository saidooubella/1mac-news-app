package com.said.oubella.so.artanddesign.network;

import com.said.oubella.so.artanddesign.models.Article;

import java.util.List;

public interface ApiResult {

    enum Failure implements ApiResult {
        SERVER_BAD_RESPONSE,
        LAKE_OF_SERVICE
    }

    final class Success implements ApiResult {

        private final List<Article> result;

        Success(List<Article> result) {
            this.result = result;
        }

        public List<Article> getResult() {
            return result;
        }
    }
}

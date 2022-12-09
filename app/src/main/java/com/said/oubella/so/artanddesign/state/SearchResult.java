package com.said.oubella.so.artanddesign.state;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.said.oubella.so.artanddesign.R;
import com.said.oubella.so.artanddesign.models.Article;

import java.util.List;

public interface SearchResult {

    enum Other implements SearchResult {

        NO_INTERNET {
            @Override
            public int icon() {
                return R.drawable.ic_no_internet;
            }

            @Override
            public int message() {
                return R.string.no_internet_description;
            }
        },

        BAD_SERVER_RESPONSE {
            @Override
            public int icon() {
                return R.drawable.ic_server_error;
            }

            @Override
            public int message() {
                return R.string.server_error_description;
            }
        },

        NO_RESULTS {
            @Override
            public int icon() {
                return R.drawable.ic_clear;
            }

            @Override
            public int message() {
                return R.string.no_results_description;
            }
        },

        NONE {
            @Override
            public int icon() {
                return R.drawable.ic_search;
            }

            @Override
            public int message() {
                return R.string.enter_query_description;
            }
        };

        @DrawableRes
        public abstract int icon();

        @StringRes
        public abstract int message();
    }

    final class Result implements SearchResult {

        @NonNull
        private final List<Article> articles;

        public Result(@NonNull List<Article> articles) {
            this.articles = articles;
        }

        @NonNull
        public List<Article> articles() {
            return articles;
        }
    }
}

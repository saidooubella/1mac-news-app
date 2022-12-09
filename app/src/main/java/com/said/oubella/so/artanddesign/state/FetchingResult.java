package com.said.oubella.so.artanddesign.state;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.said.oubella.so.artanddesign.R;

public enum FetchingResult {

    OK {
        @Override
        public int icon() {
            return -1;
        }

        @Override
        public int message() {
            return -1;
        }
    },

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
    };

    @DrawableRes
    public abstract int icon();

    @StringRes
    public abstract int message();
}
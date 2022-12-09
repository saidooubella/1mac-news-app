package com.said.oubella.so.artanddesign.network;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class RequestUrlBuilder {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private static final Uri BASE_URI = Uri.parse("https://content.guardianapis.com/search");

    private static final String API_KEY = "[INSERT-API-KEY]";
    private static final String SHOW_FIELDS = "trailText,thumbnail";
    private static final String TAG = "artanddesign/artanddesign";
    private static final String SHOW_TAGS = "contributor";
    private static final String PAGE_SIZE = "50";
    private static final String QUERY = "street";
    private static final String FORMAT = "json";

    private static final String SHOW_FIELDS_PARAM = "show-fields";
    private static final String FROM_DATE_PARAM = "from-date";
    private static final String SHOW_TAGS_PARAM = "show-tags";
    private static final String PAGE_SIZE_PARAM = "page-size";
    private static final String ORDER_BY_PARAM = "order-by";
    private static final String TO_DATE_PARAM = "to-date";
    private static final String API_KEY_PARAM = "api-key";
    private static final String FORMAT_PARAM = "format";
    private static final String QUERY_PARAM = "q";
    private static final String TAG_PARAM = "tag";

    private String query = QUERY;
    private Order orderBy = Order.RELEVANCE;

    private boolean nullify = false;
    private String fromDate = null;
    private String toDate = null;

    public RequestUrlBuilder setQuery(@NonNull String query) {
        this.query = query.replaceAll("\\s+", "%20");
        return this;
    }

    public RequestUrlBuilder setFromDate(@Nullable Date fromDate) {
        this.fromDate = fromDate != null ? DATE_FORMAT.format(fromDate) : null;
        return this;
    }

    public RequestUrlBuilder setToDate(@Nullable Date toDate) {
        this.toDate = toDate != null ? DATE_FORMAT.format(toDate) : null;
        return this;
    }

    public RequestUrlBuilder setOrderBy(@Nullable Order orderBy) {
        this.orderBy = orderBy == null ? Order.RELEVANCE : orderBy;
        return this;
    }

    /**
     * Useful to check whether a search query provided for SearchFragment state management
     * */
    public RequestUrlBuilder nullifyWhenQueryIsEmpty() {
        this.nullify = true;
        return this;
    }

    public String build() {

        if (nullify && TextUtils.isEmpty(query)) return null;

        final Uri.Builder uriBuilder = BASE_URI.buildUpon();

        if (!TextUtils.isEmpty(fromDate)) {
            uriBuilder.appendQueryParameter(FROM_DATE_PARAM, fromDate);
        }

        if (!TextUtils.isEmpty(toDate)) {
            uriBuilder.appendQueryParameter(TO_DATE_PARAM, toDate);
        }

        uriBuilder.appendQueryParameter(QUERY_PARAM, query);
        uriBuilder.appendQueryParameter(ORDER_BY_PARAM, orderBy.getOrder());
        uriBuilder.appendQueryParameter(FORMAT_PARAM, FORMAT);
        uriBuilder.appendQueryParameter(TAG_PARAM, TAG);
        uriBuilder.appendQueryParameter(SHOW_TAGS_PARAM, SHOW_TAGS);
        uriBuilder.appendQueryParameter(PAGE_SIZE_PARAM, PAGE_SIZE);
        uriBuilder.appendQueryParameter(SHOW_FIELDS_PARAM, SHOW_FIELDS);
        uriBuilder.appendQueryParameter(API_KEY_PARAM, API_KEY);

        return uriBuilder.toString();
    }
}

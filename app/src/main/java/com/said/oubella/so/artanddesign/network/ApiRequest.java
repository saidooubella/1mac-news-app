package com.said.oubella.so.artanddesign.network;

import androidx.annotation.NonNull;

import com.said.oubella.so.artanddesign.connectivity.Connectivity;
import com.said.oubella.so.artanddesign.models.Article;
import com.said.oubella.so.artanddesign.models.Fields;
import com.said.oubella.so.artanddesign.models.Tags;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ApiRequest {

    private static final String WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String BYLINE_IMAGE_URL = "bylineImageUrl";
    private static final String SECTION_NAME = "sectionName";
    private static final String TRAIL_TEXT = "trailText";
    private static final String THUMBNAIL = "thumbnail";
    private static final String WEB_TITLE = "webTitle";
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEB_URL = "webUrl";
    private static final String FIELDS = "fields";
    private static final String TAGS = "tags";
    private static final String ID = "id";

    private ApiRequest() {
    }

    public static ApiResult requestData(String requestURL, Connectivity connectivity) {
        return readResultFromUrl(wrapRequestUrl(requestURL), connectivity);
    }

    private static URL wrapRequestUrl(String requestUrl) {
        try {
            return new URL(requestUrl);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @NonNull
    private static ApiResult readResultFromUrl(final URL url, final Connectivity connectivity) {

        if (!connectivity.isAvailable()) return ApiResult.Failure.LAKE_OF_SERVICE;

        HttpURLConnection urlConnection = null;

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = urlConnection.getInputStream()) {
                    final String json = readResultFromStream(inputStream);
                    final List<Article> response = parseJSONResult(json);
                    if (response != Collections.<Article>emptyList()) {
                        return new ApiResult.Success(response);
                    }
                }
            }

        } catch (final JSONException | IOException ignored) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return ApiResult.Failure.SERVER_BAD_RESPONSE;
    }

    private static String readResultFromStream(final InputStream inputStream) throws IOException {
        final StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = reader.readLine()) != null; ) builder.append(line);
        }
        return builder.toString();
    }

    @NonNull
    private static List<Article> parseJSONResult(final String json) throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);
        return readResponseFrom(jsonObject.optJSONObject(RESPONSE));
    }

    @NonNull
    private static List<Article> readResponseFrom(JSONObject jsonObject) {

        if (jsonObject == null) return Collections.emptyList();

        final List<Article> articles = new ArrayList<>();

        if (jsonObject.has(RESULTS)) {
            final JSONArray results = jsonObject.optJSONArray(RESULTS);
            for (int i = 0; i < (results != null ? results.length() : 0); i++) {
                final Article article = readArticleFrom(results.optJSONObject(i));
                if (article != Article.EMPTY) articles.add(article);
            }
        }

        return articles;
    }

    @NonNull
    private static Article readArticleFrom(JSONObject jsonObject) {

        if (jsonObject == null) return Article.EMPTY;

        final String id = jsonObject.optString(ID);
        final String sectionName = jsonObject.optString(SECTION_NAME);
        final String publicationDate = jsonObject.optString(WEB_PUBLICATION_DATE);
        final String formattedDate = Helper.convertSourceDateIntoFormatted(publicationDate);
        final String webUrl = jsonObject.optString(WEB_URL);
        final String webTitle = jsonObject.optString(WEB_TITLE);
        final Fields fields = readFieldsFrom(jsonObject.optJSONObject(FIELDS));
        final Tags tags = readTagsFrom(jsonObject.optJSONArray(TAGS));

        return new Article(id, sectionName, formattedDate, webUrl, webTitle, fields, tags);
    }

    @NonNull
    private static Fields readFieldsFrom(JSONObject jsonObject) {

        if (jsonObject == null) return Fields.EMPTY;

        final String trailText = jsonObject.optString(TRAIL_TEXT);
        final String thumbnail = jsonObject.optString(THUMBNAIL);
        final String cleanedTrailText = Helper.cleanFromHtmlTags(trailText);

        return new Fields(cleanedTrailText, thumbnail);
    }

    @NonNull
    private static Tags readTagsFrom(JSONArray jsonArray) {

        if (jsonArray == null || jsonArray.length() == 0) return Tags.EMPTY;

        final JSONObject jsonObject = jsonArray.optJSONObject(0);
        final String webTitle = jsonObject.optString(WEB_TITLE);
        final String bylineImageUrl = jsonObject.optString(BYLINE_IMAGE_URL);

        return new Tags(webTitle, bylineImageUrl);
    }
}

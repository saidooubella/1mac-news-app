package com.said.oubella.so.artanddesign.network;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class Helper {

    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    private Helper() {
    }

    static String convertSourceDateIntoFormatted(String source) {
        try {
            final Date date = INPUT_DATE_FORMAT.parse(source);
            if (date != null) return OUTPUT_DATE_FORMAT.format(date);
        } catch (ParseException ignored) {
        }
        return "00/00/0000 00:00";
    }

    @NonNull
    static String cleanFromHtmlTags(String source) {
        return source.replaceAll("<.*?>", "");
    }
}

package com.said.oubella.so.artanddesign.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

public final class Tags {

    @Ignore
    public static final Tags EMPTY = new Tags();

    @NonNull
    @ColumnInfo(name = "author_name")
    private String authorName;

    @NonNull
    @ColumnInfo(name = "author_picture")
    private String authorPicture;

    public Tags() {
        this("Unknown", "");
    }

    @Ignore
    public Tags(@NonNull String authorName, @NonNull String authorPicture) {
        this.authorName = authorName.isEmpty() ? "Unknown" : authorName;
        this.authorPicture = authorPicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tags tags = (Tags) o;
        return authorName.equals(tags.authorName) &&
                authorPicture.equals(tags.authorPicture);
    }

    @NonNull
    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(@NonNull String authorName) {
        this.authorName = authorName;
    }

    @NonNull
    public String getAuthorPicture() {
        return authorPicture;
    }

    public void setAuthorPicture(@NonNull String authorPicture) {
        this.authorPicture = authorPicture;
    }
}

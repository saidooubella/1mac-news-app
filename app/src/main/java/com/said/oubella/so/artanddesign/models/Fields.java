package com.said.oubella.so.artanddesign.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

public final class Fields {

    @Ignore
    public static final Fields EMPTY = new Fields();

    @NonNull
    @ColumnInfo(name = "article_trail_text")
    private String postTrailText;

    @NonNull
    @ColumnInfo(name = "article_thumbnail")
    private String postThumbnail;

    public Fields() {
        this("", "");
    }

    @Ignore
    public Fields(@NonNull String postTrailText, @NonNull String thumbnail) {
        this.postTrailText = postTrailText;
        this.postThumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fields fields = (Fields) o;
        return postTrailText.equals(fields.postTrailText) &&
                postThumbnail.equals(fields.postThumbnail);
    }

    @NonNull
    public String getPostTrailText() {
        return postTrailText;
    }

    public void setPostTrailText(@NonNull String postTrailText) {
        this.postTrailText = postTrailText;
    }

    @NonNull
    public String getPostThumbnail() {
        return postThumbnail;
    }

    public void setPostThumbnail(@NonNull String postThumbnail) {
        this.postThumbnail = postThumbnail;
    }
}

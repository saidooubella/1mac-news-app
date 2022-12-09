package com.said.oubella.so.artanddesign.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "articles")
public final class Article {

    @Ignore
    public static final Article EMPTY = new Article();

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "article_id")
    private String postId;

    @NonNull
    @ColumnInfo(name = "article_section")
    private String postSectionName;

    @NonNull
    @ColumnInfo(name = "article_publish_date")
    private String postPublicationDate;

    @NonNull
    @ColumnInfo(name = "article_url")
    private String postUrl;

    @NonNull
    @ColumnInfo(name = "article_title")
    private String postTitle;

    @NonNull
    @Embedded
    private Fields postFields;

    @NonNull
    @Embedded
    private Tags postTags;

    public Article() {
        this("", "", "", "", "", Fields.EMPTY, Tags.EMPTY);
    }

    @Ignore
    public Article(@NonNull String postId, @NonNull String postSectionName, @NonNull String postPublicationDate, @NonNull String postUrl, @NonNull String postTitle, @NonNull Fields postFields, @NonNull Tags postTags) {
        this.postId = postId;
        this.postSectionName = postSectionName;
        this.postPublicationDate = postPublicationDate;
        this.postUrl = postUrl;
        this.postTitle = postTitle;
        this.postFields = postFields;
        this.postTags = postTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return postId.equals(article.postId) &&
                postSectionName.equals(article.postSectionName) &&
                postPublicationDate.equals(article.postPublicationDate) &&
                postUrl.equals(article.postUrl) &&
                postTitle.equals(article.postTitle) &&
                Objects.equals(postFields, article.postFields) &&
                Objects.equals(postTags, article.postTags);
    }

    @NonNull
    public String getPostId() {
        return postId;
    }

    public void setPostId(@NonNull String postId) {
        this.postId = postId;
    }

    @NonNull
    public String getPostSectionName() {
        return postSectionName;
    }

    public void setPostSectionName(@NonNull String postSectionName) {
        this.postSectionName = postSectionName;
    }

    @NonNull
    public String getPostPublicationDate() {
        return postPublicationDate;
    }

    public void setPostPublicationDate(@NonNull String postPublicationDate) {
        this.postPublicationDate = postPublicationDate;
    }

    @NonNull
    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(@NonNull String postUrl) {
        this.postUrl = postUrl;
    }

    @NonNull
    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(@NonNull String postTitle) {
        this.postTitle = postTitle;
    }

    @NonNull
    public Fields getPostFields() {
        return postFields;
    }

    public void setPostFields(@NonNull Fields postFields) {
        this.postFields = postFields;
    }

    @NonNull
    public Tags getPostTags() {
        return postTags;
    }

    public void setPostTags(@NonNull Tags postTags) {
        this.postTags = postTags;
    }
}

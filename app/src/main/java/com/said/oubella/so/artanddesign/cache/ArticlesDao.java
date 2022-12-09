package com.said.oubella.so.artanddesign.cache;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.said.oubella.so.artanddesign.models.Article;

import java.util.List;

@Dao
public interface ArticlesDao {

    @Insert
    void insertAll(List<Article> articles);

    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getArticles();

    @Query("DELETE FROM articles")
    void clear();
}

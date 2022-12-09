package com.said.oubella.so.artanddesign.cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.said.oubella.so.artanddesign.models.Article;

@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class ArticlesDatabase extends RoomDatabase {
    public abstract ArticlesDao articlesDao();
}

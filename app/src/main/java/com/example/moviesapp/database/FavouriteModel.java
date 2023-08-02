package com.example.moviesapp.database;

import android.provider.BaseColumns;

public class FavouriteModel {

    public static final class FavouriteEntry implements BaseColumns{
        public static final String TABLE_NAME = "favourite";
        public static final String COLUMN_MOVIEID = "movieid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_USERRATING = "userrating";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_PLOT_SYNOPSIS = "overview";
    }
}

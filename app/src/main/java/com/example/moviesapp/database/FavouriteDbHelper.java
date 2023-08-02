package com.example.moviesapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.moviesapp.models.MovieModel;

import java.util.ArrayList;
import java.util.List;

public class FavouriteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favourite.db";
    private static final int DATABASE_VERSION = 1;

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    public FavouriteDbHelper( Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void open(){
        db = dbHelper.getWritableDatabase();
        Log.d("Main","Database Opened");
    }
    public void close(){
        Log.d("Main","Database Closed");
        dbHelper.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String createTableStatement = "CREATE TABLE "+FavouriteModel.FavouriteEntry.TABLE_NAME+" ("+
                FavouriteModel.FavouriteEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FavouriteModel.FavouriteEntry.COLUMN_MOVIEID+" INTEGER, "+
                FavouriteModel.FavouriteEntry.COLUMN_PLOT_SYNOPSIS+" TEXT NOT NULL, "+
                FavouriteModel.FavouriteEntry.COLUMN_POSTER_PATH+" TEXT NOT NULL, "+
                FavouriteModel.FavouriteEntry.COLUMN_TITLE+" TEXT NOT NULL, "+
                FavouriteModel.FavouriteEntry.COLUMN_USERRATING+" REAL NOT NULL )";
        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FavouriteModel.FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addToFavouriteDatabase(MovieModel movieModel){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavouriteModel.FavouriteEntry.COLUMN_MOVIEID,movieModel.getMovie_id());
        values.put(FavouriteModel.FavouriteEntry.COLUMN_TITLE,movieModel.getTitle());
        values.put(FavouriteModel.FavouriteEntry.COLUMN_PLOT_SYNOPSIS,movieModel.getOverview());
        values.put(FavouriteModel.FavouriteEntry.COLUMN_POSTER_PATH,movieModel.getPoster_path());
        values.put(FavouriteModel.FavouriteEntry.COLUMN_USERRATING,movieModel.getVote_average());
        long success = db.insert(FavouriteModel.FavouriteEntry.TABLE_NAME, null, values);
        if (success==1){
            System.out.println("Added to the database");
        }else {
            System.out.println("Couldn't be able to add the data to the database");
        }
        db.close();
    }
    public void deleteFromFavouriteDatabase(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavouriteModel.FavouriteEntry.TABLE_NAME,FavouriteModel.FavouriteEntry.COLUMN_MOVIEID+"="+id,null);
    }
    @SuppressLint("Range")
    public List<MovieModel> getAllFavouriteMovies(){
        String [] columns = {
                FavouriteModel.FavouriteEntry._ID,
                FavouriteModel.FavouriteEntry.COLUMN_MOVIEID,
                FavouriteModel.FavouriteEntry.COLUMN_TITLE,
                FavouriteModel.FavouriteEntry.COLUMN_PLOT_SYNOPSIS,
                FavouriteModel.FavouriteEntry.COLUMN_POSTER_PATH,
                FavouriteModel.FavouriteEntry.COLUMN_USERRATING
        };
        String sortOrder = FavouriteModel.FavouriteEntry._ID+" ASC";
        List<MovieModel> favouriteList= new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.query(FavouriteModel.FavouriteEntry.TABLE_NAME,columns,null,null,null,null,sortOrder);
        if (cursor.moveToFirst()){
            do {
                MovieModel model = new MovieModel();
                model.setMovie_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavouriteModel.FavouriteEntry.COLUMN_MOVIEID))));
                model.setTitle(cursor.getString(cursor.getColumnIndex(FavouriteModel.FavouriteEntry.COLUMN_TITLE)));
                model.setOverview(cursor.getString(cursor.getColumnIndex(FavouriteModel.FavouriteEntry.COLUMN_PLOT_SYNOPSIS)));
                model.setPoster_path(cursor.getString(cursor.getColumnIndex(FavouriteModel.FavouriteEntry.COLUMN_POSTER_PATH)));
                model.setVote_average(Float.parseFloat(cursor.getString(cursor.getColumnIndex(FavouriteModel.FavouriteEntry.COLUMN_USERRATING))));
                favouriteList.add(model);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favouriteList;
    }
    public boolean isExist(String searchItem){
        SQLiteDatabase database = this.getReadableDatabase();
        String[] projection = {
                FavouriteModel.FavouriteEntry._ID,
                FavouriteModel.FavouriteEntry.COLUMN_MOVIEID,
                FavouriteModel.FavouriteEntry.COLUMN_TITLE,
                FavouriteModel.FavouriteEntry.COLUMN_PLOT_SYNOPSIS,
                FavouriteModel.FavouriteEntry.COLUMN_POSTER_PATH,
                FavouriteModel.FavouriteEntry.COLUMN_USERRATING
        };
        String selection = FavouriteModel.FavouriteEntry.COLUMN_TITLE+" =?";
        String[] selectionArgs = {searchItem};
        String limit = "1";

        Cursor cursor = database.query(FavouriteModel.FavouriteEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,null,limit);
        boolean exist = (cursor.getCount()>0);
        cursor.close();
        return exist;
    }
}

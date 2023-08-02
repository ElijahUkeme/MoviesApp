package com.example.moviesapp.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesapp.api.MovieApiClient;
import com.example.moviesapp.models.MovieModel;
import com.example.moviesapp.models.Trailer;

import java.util.List;

public class MovieRepository {

    private static MovieRepository instance;
    private MovieApiClient movieApiClient;
    private String nextQuery;
    private int nextPageNumber;


    public static MovieRepository getInstance(){
        if (instance==null){
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository(){

        movieApiClient = MovieApiClient.getInstance();
    }
    public LiveData<List<MovieModel>> getMovies(){
        return movieApiClient.getMovies();
    }
    public LiveData<List<MovieModel>> getPopularMovies() {
        return movieApiClient.getPopularMovies();
    }
    public LiveData<List<Trailer>> getMoviesTrailer(){
        return movieApiClient.getTrailerMovies();
    }

    //calling the method from the movieapiclient
    public void searchMovies(String query,int pageNumber){
        nextQuery = query;
        nextPageNumber = pageNumber;
        movieApiClient.searchMoviesApi(query, pageNumber);
    }

    public void searchPopularMovies(int pageNumber){
        nextPageNumber = pageNumber;
        movieApiClient.getPopularMovies(pageNumber);
    }
    public void searchMovieTrailerId(int movie_id){
        movieApiClient.getTrailerMovie(movie_id);

    }
    public void searchNextPage(){
        searchMovies(nextQuery,nextPageNumber+1);
    }
    public void searchNextPopularPage(){
        searchPopularMovies(nextPageNumber+1);
    }

}

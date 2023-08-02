package com.example.moviesapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviesapp.api.MovieApiClient;
import com.example.moviesapp.models.MovieModel;
import com.example.moviesapp.models.Trailer;
import com.example.moviesapp.repository.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {

    private MovieRepository movieRepository;

    public MovieListViewModel() {
        movieRepository = MovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return movieRepository.getMovies();
    }
    public LiveData<List<Trailer>> getTrailerMovies(){
        return movieRepository.getMoviesTrailer();
    }
    public LiveData<List<MovieModel>> getPopularMovies(){
        MovieApiClient.type = "popular";
        return movieRepository.getPopularMovies();
    }
    public LiveData<List<MovieModel>> getTopRatedMovies(){
        MovieApiClient.type = "top rated";
        return movieRepository.getPopularMovies();
    }
    public LiveData<List<MovieModel>> getUpcomingMovies(){
        MovieApiClient.type = "upcoming";
        return movieRepository.getPopularMovies();
    }
    public LiveData<List<MovieModel>> getNowPlayingMovies(){
        MovieApiClient.type = "now playing";
        return movieRepository.getPopularMovies();
    }

    public void searchMovies(String query,int pageNumber){
        movieRepository.searchMovies(query, pageNumber);
    }

    public void searchMoviesPage(int pageNumber){
        movieRepository.searchPopularMovies(pageNumber);
    }
    public void searchMovieTrailer(int movie_id){
        movieRepository.searchMovieTrailerId(movie_id);
    }
    public void searchNextPage(){
        movieRepository.searchNextPage();
    }
    public void searchNextMoviePage(){
        movieRepository.searchNextPopularPage();
    }
}

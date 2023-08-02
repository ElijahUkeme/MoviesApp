package com.example.moviesapp.interfaces;

import com.example.moviesapp.models.MovieModel;
import com.example.moviesapp.models.MovieSearchResponse;
import com.example.moviesapp.models.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {

    @GET("/3/search/movie")
    Call<MovieSearchResponse> searchForMovies(
            @Query("api_key") String api_key,
            @Query("query") String query,
            @Query("page") int page
            );

    @GET("/3/movie/{movie_id}?")
    Call<MovieModel> getMoviesById(@Path("movie_id") int movie_id,
                                   @Query("api_key") String api_key);

    @GET("/3/movie/popular")
    Call<MovieSearchResponse> getPopularMovies(@Query("api_key") String api_key,
                                               @Query("page")int page);

    @GET("/3/movie/now_playing")
    Call<MovieSearchResponse> getNowPlayingMovies(@Query("api_key") String api_key,
                                               @Query("page")int page);

    @GET("/3/movie/top_rated")
    Call<MovieSearchResponse> getTopRatedMovies(@Query("api_key") String api_key,
                                               @Query("page")int page);

    @GET("/3/movie/upcoming")
    Call<MovieSearchResponse> getUpComingMovies(@Query("api_key") String api_key,
                                               @Query("page")int page);

    @GET("/3/movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id")int movie_id, @Query("api_key")String api_key);
}

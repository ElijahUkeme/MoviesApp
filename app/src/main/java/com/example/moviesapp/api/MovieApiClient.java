package com.example.moviesapp.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviesapp.executor.AppExecutor;
import com.example.moviesapp.models.MovieModel;
import com.example.moviesapp.models.MovieSearchResponse;
import com.example.moviesapp.models.Trailer;
import com.example.moviesapp.models.TrailerResponse;
import com.example.moviesapp.utils.Credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {
    public static String type;


    //This is for search movies
    private MutableLiveData<List<MovieModel>> mMovies;

    //This is for other movies
    private MutableLiveData<List<MovieModel>> popularMovies;
    private MutableLiveData<List<Trailer>> trailerMovies;

    private static MovieApiClient instance;

    //Making A Global Runnable Request

    private RetrieveMoviesRunnable retrieveMoviesRunnable;

    private RetrieveMoviesRunnablePopular retrieveMoviesRunnablePopular;
    private RetrieveMoviesRunnableTrailer retrieveMoviesRunnableTrailer;

    public static MovieApiClient getInstance(){
        if (instance==null){
            instance = new MovieApiClient();
        }
        return instance;
    }
    private MovieApiClient(){

        mMovies = new MutableLiveData<>();
        popularMovies = new MutableLiveData<>();
        trailerMovies = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return mMovies;
    }
    public LiveData<List<MovieModel>> getPopularMovies(){

        return popularMovies;
    }
    public LiveData<List<Trailer>> getTrailerMovies(){
        return trailerMovies;
    }

    public void searchMoviesApi(String query, int pageNumber){
        if (retrieveMoviesRunnable !=null){
            retrieveMoviesRunnable = null;
        }

        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query, pageNumber);
        final Future myHandler = AppExecutor.getInstance().netWorkIO().submit(retrieveMoviesRunnable);


        AppExecutor.getInstance().netWorkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // This is for cancelling of Request
                myHandler.cancel(true);
            }
        },5000, TimeUnit.MILLISECONDS);
    }

    public void getPopularMovies(int pageNumber){
        if (retrieveMoviesRunnablePopular !=null){
            retrieveMoviesRunnablePopular = null;
        }

        retrieveMoviesRunnablePopular = new RetrieveMoviesRunnablePopular(pageNumber);
        final Future myHandlerPopular = AppExecutor.getInstance().netWorkIO().submit(retrieveMoviesRunnablePopular);

        AppExecutor.getInstance().netWorkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // This is for cancelling of Request
                myHandlerPopular.cancel(true);
            }
        },5000, TimeUnit.MILLISECONDS);
    }

    public void getTrailerMovie(int movie_id){
        if (retrieveMoviesRunnableTrailer !=null){
            retrieveMoviesRunnableTrailer = null;
        }
        retrieveMoviesRunnableTrailer = new RetrieveMoviesRunnableTrailer(movie_id);
        final Future myRetrieveHandler = AppExecutor.getInstance().netWorkIO().submit(retrieveMoviesRunnableTrailer);

        AppExecutor.getInstance().netWorkIO().schedule(new Runnable() {
            @Override
            public void run() {
                myRetrieveHandler.cancel(true);
            }
        },5000,TimeUnit.MILLISECONDS);
    }

    class RetrieveMoviesRunnable implements Runnable{

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            //Getting the response object
            try {
                Response response = getMovies(query,pageNumber).execute();

                if (cancelRequest){
                    return;
                }
                if (response.code()==200){
                    List<MovieModel> list = new ArrayList<>(((MovieSearchResponse)response.body()).getMovies());
                    if (pageNumber==1){
                        //sending data to live data
                        //postvalue used for background thread
                        //setvalue not used for background thread
                        mMovies.postValue(list);
                    }else {
                        List<MovieModel> currentList = mMovies.getValue();
                        currentList.addAll(list);
                        mMovies.postValue(currentList);
                    }
                }else {
                    Log.d("Main","Error "+response.errorBody().string());
                    mMovies.postValue(null);
                }
            }

            catch (IOException e) {
                e.printStackTrace();
                mMovies.postValue(null);
            }

        }
        private Call<MovieSearchResponse> getMovies(String query, int pageNumber){
            return ServerClass.getMovieApi().searchForMovies(
                    Credentials.API_KEY,query,pageNumber
            );
        }
        public void cancelRequest(){
            Log.d("Main","Cancelling search request");
            cancelRequest = true;
        }
    }

    class RetrieveMoviesRunnablePopular implements Runnable{

        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnablePopular(int pageNumber) {
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            //Getting the response object based on the passed parameters
            try {
                Response response = null;
                if (MovieApiClient.type.equalsIgnoreCase("popular")){
                    response = getPopular(pageNumber).execute();
                }else if (MovieApiClient.type.equalsIgnoreCase("now playing")){
                    response = getNowPlaying(pageNumber).execute();
                }else if (MovieApiClient.type.equalsIgnoreCase("top rated")){
                    response = getTopRated(pageNumber).execute();
                }else if (MovieApiClient.type.equalsIgnoreCase("upcoming")){
                    response = getUpcoming(pageNumber).execute();
                }else {
                    response = null;
                }
                Log.d("Main","the type is: "+MovieApiClient.type);
                //Response responsePopular = getPopular(pageNumber).execute();

                if (cancelRequest){
                    return;
                }
                if (response.code()==200){
                    List<MovieModel> list = new ArrayList<>(((MovieSearchResponse)response.body()).getMovies());
                    if (pageNumber==1){
                        //sending data to live data
                        //postvalue used for background thread
                        //setvalue not used for background thread
                        popularMovies.postValue(list);
                    }else {
                        List<MovieModel> currentList = popularMovies.getValue();
                        currentList.addAll(list);
                        popularMovies.postValue(currentList);
                    }
                }else {
                    Log.d("Main","Error "+response.errorBody().string());
                    popularMovies.postValue(null);
                }
            }

            catch (IOException e) {
                e.printStackTrace();
                popularMovies.postValue(null);
            }

        }

        private Call<MovieSearchResponse> getPopular(int pageNumber){
            return ServerClass.getMovieApi().getPopularMovies(
                    Credentials.API_KEY,pageNumber
            );
        }
        private Call<MovieSearchResponse> getNowPlaying(int pageNumber){
            return ServerClass.getMovieApi().getNowPlayingMovies(
                    Credentials.API_KEY,pageNumber
            );
        }
        private Call<MovieSearchResponse> getUpcoming(int pageNumber){
            return ServerClass.getMovieApi().getUpComingMovies(
                    Credentials.API_KEY,pageNumber
            );
        }
        private Call<MovieSearchResponse> getTopRated(int pageNumber){
            return ServerClass.getMovieApi().getTopRatedMovies(
                    Credentials.API_KEY,pageNumber
            );
        }
        public void cancelRequest(){
            Log.d("Main","Cancelling search request");
            cancelRequest = true;
        }
    }

    class RetrieveMoviesRunnableTrailer implements Runnable {
        private int movie_id;
        boolean cancelRequest;

        public RetrieveMoviesRunnableTrailer(int movie_id) {
            this.movie_id = movie_id;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {

                Response response = getTrailer(movie_id).execute();
                if (cancelRequest) {
                    return;
                }
                if (response.code() == 200) {
                    List<Trailer> trailerResponseList = new ArrayList<>(((TrailerResponse)response.body()).getTrailers());
                    trailerMovies.postValue(trailerResponseList);
                }else {
                    Log.d("Main",response.errorBody().string());
                    trailerMovies.postValue(null);
                }

            } catch (Exception e) {
                e.printStackTrace();
                trailerMovies.postValue(null);
            }
        }

        private Call<TrailerResponse> getTrailer(int movie_id) {
            return ServerClass.getMovieApi().getMovieTrailer(movie_id, Credentials.API_KEY);
        }
    }

}




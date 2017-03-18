package com.github.filipelipan.popularmovies.moviedb;

import com.github.filipelipan.popularmovies.key.ApiKey;
import com.github.filipelipan.popularmovies.model.Result;
import com.github.filipelipan.popularmovies.model.Reviews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lispa on 08/01/2017.
 */

public interface MoviesDbService {

    String BASE_URL = "http://api.themoviedb.org/";
    String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185";
    String BASE_POSTER_URL_HD = "http://image.tmdb.org/t/p/w500";

    //TODO: INSERT YOUR API_KEY HERE
    //
    String API_KEY = ApiKey.API_KEY;

    @GET("/3/movie/popular")
    Call<Result> getPopularMovies(@Query("api_key") String apiKey);

    @GET("/3/movie/top_rated")
    Call<Result> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("/3/movie/{id}/trailers")
    Call<Result> getTrailers(@Path("id") int id,@Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    Call<Reviews> getReviews(@Path("id") int id, @Query("api_key") String apiKey);

}

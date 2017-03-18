package com.github.filipelipan.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.github.filipelipan.popularmovies.R;
import com.github.filipelipan.popularmovies.data.MovieContract;
import com.github.filipelipan.popularmovies.model.Movie;
import com.github.filipelipan.popularmovies.model.Result;
import com.github.filipelipan.popularmovies.moviedb.MoviesDbService;
import com.github.filipelipan.popularmovies.util.OperationType;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lispa on 20/02/2017.
 */

public class MovieTasks {

    public static final String TAG = MovieTasks.class.getSimpleName();

    public static final String ACTION_DOWNLOAD_TOP_RATED_MOVIES = "download-top-rated-movies";
    public static final String ACTION_DOWNLOAD_MOST_POPULAR_MOVIES = "download-most-popular-movies";

    public static void excuteTask(Context context, String action){
        if(ACTION_DOWNLOAD_TOP_RATED_MOVIES.equals(action)){
            downloadMovies(context, OperationType.TOP_RATED);
        }else if(ACTION_DOWNLOAD_MOST_POPULAR_MOVIES.equals(action)){
            downloadMovies(context , OperationType.MOST_POPULAR);
        }
    }


    private static void downloadMovies(final Context context , final int option){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesDbService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesDbService service = retrofit.create(MoviesDbService.class);
        Call<Result> makeMovieRequest;
        if (option == OperationType.TOP_RATED) {
            makeMovieRequest = service.getTopRatedMovies(MoviesDbService.API_KEY);
        } else {
            makeMovieRequest = service.getPopularMovies(MoviesDbService.API_KEY);
        }

        makeMovieRequest.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.isSuccessful() || response == null) {
                    Log.e(TAG, context.getString(R.string.retrofitError));

                } else {
                    if (response.body() != null) {
                        Result result = response.body();

                        Uri deleteUri = MovieContract.MovieEntry.CONTENT_URI_MOVIES_BY_CLASSIFICATION;
                        deleteUri = deleteUri.buildUpon().appendEncodedPath(option + "").build();
                        context.getContentResolver().delete(deleteUri, null, null);

                        //insert movies into the content provider
                        Uri movieBulkInsertUri = MovieContract.MovieEntry.CONTENT_URI;
                        ContentValues[] contentValues = buildContentValuesArrayFromMovies(result.results, option);
                        context.getContentResolver().bulkInsert(movieBulkInsertUri, contentValues);
                    }
                }

            }


            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, "Something went wrong on retrofit, coming from retrofit on failure");
            }
        });
    }

    private static ContentValues[] buildContentValuesArrayFromMovies(ArrayList<Movie> movies, int option) {
        ContentValues[] contentValues = new ContentValues[movies.size()];

        for(int i = 0; i < movies.size() ; i++){
            ContentValues contentValues1 = new ContentValues();
            contentValues1.put(MovieContract.MovieEntry.COLUMN_ID_MOVIEDB, movies.get(i).getId());
            contentValues1.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movies.get(i).getOriginalTitle());
            contentValues1.put(MovieContract.MovieEntry.COLUM_POSTER_PATH, movies.get(i).getPosterPath());
            contentValues1.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movies.get(i).getOverview());
            contentValues1.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movies.get(i).getVoteAverage());
            contentValues1.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movies.get(i).getReleaseDate());
            contentValues1.put(MovieContract.MovieEntry.COLUMN_FOREIGN_KEY_CLASSIFICATION, option);
            contentValues[i] = contentValues1;
        }

        return contentValues;
    }
}

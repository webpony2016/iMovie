package com.icareu.imovie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.icareu.imovie.beans.Movie;
import com.icareu.imovie.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static String RECIEVE_DATA = "RECIEVE_DATA";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The movie content this fragment is presenting.
     */
    private Movie mMovie;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        // Get the movie content from Intent.
        mMovie = (Movie)activity.getIntent().getSerializableExtra(RECIEVE_DATA);

        if (mMovie != null) {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mMovie.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        // Show the movie content .
        if (mMovie != null) {
            showMovieContent(rootView, mMovie.getId());
        }

        return rootView;
    }

    public class FetchMoiveTask extends AsyncTask<HttpURLConnection,Void, Movie> {

        private final String LOG_TAG = FetchMoiveTask.class.getSimpleName();

        @Override
        protected Movie doInBackground(HttpURLConnection... httpURLConnections) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // open the connection
                urlConnection = httpURLConnections[0];
                urlConnection.connect();

                Log.v(LOG_TAG, "Get data from server");
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                return getMovieDataFromJson(buffer.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Movie  movie) {
            if(movie != null){
                Activity activity = getActivity();
                ((TextView) activity.findViewById(R.id.tvRuntime)).setText(String.valueOf(movie.getRunTime() + "min"));
            }
        }

        /**
         * Take the String representing the movies data in JSON Format and
         * pull out the data we need to construct the MovieList needed.
         * @param jsonString String representing the movies data in JSON Format
         * @return Movie ArrayList
         */
        private Movie getMovieDataFromJson(String jsonString) {
            Movie result = null;
            try {
                JSONObject jsonMovieObject = new JSONObject(jsonString);

                    Movie.Builder movieBuilder = Movie.newBuilder(
                            Integer.parseInt(jsonMovieObject.getString(Utility.TMDB_ID)),
                            jsonMovieObject.getString(Utility.TMDB_TITLE))
                            .setBackdropPath(jsonMovieObject.getString(Utility.TMDB_BACKDROP_PATH))
                            .setOriginalTitle(jsonMovieObject.getString(Utility.TMDB_ORIGINAL_TITLE))
                            .setPopularity(jsonMovieObject.getString(Utility.TMDB_POPULARITY))
                            .setPosterPath(jsonMovieObject.getString(Utility.TMDB_POSTER_PATH))
                            .setOverview(jsonMovieObject.getString(Utility.TMDB_OVERVIEW))
                            .setReleaseDate(jsonMovieObject.getString(Utility.TMDB_RELEASE_DATE))
                            .setVoteAverage(jsonMovieObject.getDouble(Utility.TMDB_VOTE_AVERAGE))
                            .setRunTime(jsonMovieObject.getInt(Utility.TMDB_RUNTIME));
                result = movieBuilder.build();

            } catch (JSONException e) {
                System.err.println(e);
                Log.d(LOG_TAG, "Error parsing JSON. String was: " + jsonString);
            }
            return result;
        }
    }


    /**
     * Retrieve the movie information from server and update the UI.
     * @throws MalformedURLException
     */
    private void showMovieContent(View rootView, int movieId)  {
        // For showing the movie content quickly,
        // some data are fetched from the intent object directly instead of being retrieved from server
        ((TextView) rootView.findViewById(R.id.tvMovieOverview)).setText(mMovie.getOverview());
        ((TextView) rootView.findViewById(R.id.tvYear)).setText(mMovie.getReleaseDate());
        ((TextView) rootView.findViewById(R.id.tvVoteAverage)).setText(String.valueOf(mMovie.getVoteAverage() + "/10"));

        BitmapImageViewTarget biv = new BitmapImageViewTarget((ImageView) rootView.findViewById(R.id.ivMovieThumbnail)) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCornerRadius(25);
                view.setImageDrawable(circularBitmapDrawable);
            }
        };
        Glide.with(getActivity()).load(mMovie.getPosterPath()).asBitmap().fitCenter()
                .into(biv);
        try {
            // Construct the URL for the query
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(String.valueOf(movieId))
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

            Log.i(LOG_TAG, uriBuilder.build().toString());

            URL url = new URL(uriBuilder.build().toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setDoInput(true);
            new FetchMoiveTask().execute(urlConnection);
        }catch ( IOException e ){
            Log.e(LOG_TAG,"error", e);
        }
    }
}

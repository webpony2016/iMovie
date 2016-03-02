package com.icareu.imovie;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.icareu.imovie.adapters.MovieGridAdapter;
import com.icareu.imovie.beans.Movie;
import com.icareu.imovie.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * An activity representing a mList of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a mList of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the mList of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieListActivity.class.getSimpleName();
    private final String SHARED_PREF = "SP";
    private final String PREF_LIST_TYPE = "MOVIE_LIST_TYPE";

    private MovieGridAdapter mMovieAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mListState;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Show the movie list which default is popular list.
        SharedPreferences sp = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        int typeInt = sp.getInt(PREF_LIST_TYPE, Utility.MovieListTypeEnum.POPULAR.value());
        Utility.MovieListTypeEnum type = Utility.MovieListTypeEnum.valueOf(typeInt);
        showMovies(type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Utility.MovieListTypeEnum movieListTypeEnum;
        switch (id) {
            case R.id.action_now_playing:
                movieListTypeEnum = Utility.MovieListTypeEnum.NOW_PLAYING;
                break;
            case R.id.action_popular:
                movieListTypeEnum = Utility.MovieListTypeEnum.POPULAR;
                break;
            case R.id.action_top_rated:
                movieListTypeEnum = Utility.MovieListTypeEnum.TOP_RATED;
                break;
            case R.id.action_upcoming:
                movieListTypeEnum = Utility.MovieListTypeEnum.UPCOMING;
                break;
            default:
                movieListTypeEnum = Utility.MovieListTypeEnum.POPULAR;
        };

        // Save the shared preference of movie list type.
        SharedPreferences sp = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_LIST_TYPE, movieListTypeEnum.value());
        editor.apply();

        showMovies(movieListTypeEnum);

        return super.onOptionsItemSelected(item);
    }

    public class FetchMoivesTask extends AsyncTask<HttpURLConnection,Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoivesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(HttpURLConnection... httpURLConnections) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // open the connection
                urlConnection = httpURLConnections[0];
                urlConnection.connect();

                Log.v(LOG_TAG,"Get data from server");
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

                return getMoviesDataFromJson(buffer.toString());
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
        protected void onPostExecute(ArrayList<Movie> movies) {
            if(movies != null){
                mMovieAdapter = new MovieGridAdapter(MovieListActivity.this, movies);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list);
                assert recyclerView != null;
//                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                recyclerView.setLayoutManager(new GridLayoutManager(MovieListActivity.this,2));
                recyclerView.setAdapter(mMovieAdapter);
            }
        }

        /**
         * Take the String representing the movies data in JSON Format and
         * pull out the data we need to construct the MovieList needed.
         * @param jsonString String representing the movies data in JSON Format
         * @return Movie ArrayList
         */
        private ArrayList<Movie> getMoviesDataFromJson(String jsonString) {
            ArrayList<Movie> results = new ArrayList<Movie>();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray array = (JSONArray) jsonObject.get(Utility.TMDB_RESULTS);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonMovieObject = array.getJSONObject(i);
                    Movie.Builder movieBuilder = Movie.newBuilder(
                            Integer.parseInt(jsonMovieObject.getString(Utility.TMDB_ID)),
                            jsonMovieObject.getString(Utility.TMDB_TITLE))
                            .setBackdropPath(jsonMovieObject.getString(Utility.TMDB_BACKDROP_PATH))
                            .setOriginalTitle(jsonMovieObject.getString(Utility.TMDB_ORIGINAL_TITLE))
                            .setPopularity(jsonMovieObject.getString(Utility.TMDB_POPULARITY))
                            .setPosterPath(jsonMovieObject.getString(Utility.TMDB_POSTER_PATH))
                            .setOverview(jsonMovieObject.getString(Utility.TMDB_OVERVIEW))
                            .setReleaseDate(jsonMovieObject.getString(Utility.TMDB_RELEASE_DATE))
                            .setVoteAverage(jsonMovieObject.getDouble(Utility.TMDB_VOTE_AVERAGE));
                    results.add(movieBuilder.build());
                }
            } catch (JSONException e) {
                System.err.println(e);
                Log.d(LOG_TAG, "Error parsing JSON. String was: " + jsonString);
            }
            return results;
        }
    }


    /**
     * Retrieve the movies information from server and update the UI.
     * @throws MalformedURLException
     */
    private void showMovies(Utility.MovieListTypeEnum movieListEnum)  {
        try {
            // Set the title to show current type of movie list
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            getSupportActionBar().setTitle(getTitle() + "   " + movieListEnum.toString().toUpperCase());

            // Construct the URL for the query
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieListEnum.toString())
                    .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY);

            Log.i(LOG_TAG, uriBuilder.build().toString());

            URL url = new URL(uriBuilder.build().toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setDoInput(true);
            new FetchMoivesTask().execute(urlConnection);
        }catch ( IOException e ){
            Log.e(LOG_TAG,"error", e);
        }
    }
}

package io.mattsommer.networking;

import android.net.Uri;
import android.util.Log;
import io.mattsommer.popularmovies.BuildConfig;
import io.mattsommer.ui.movie.MovieFragment.FetchMoviesTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by matt on 10/12/17.
 */

public class FetchMovies {

  private static final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

  public static String Fetch(String sortPreference) {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    String JsonResponseStr = null;
    Uri builtUri = null;

    final String APPID_PARAM = "api_key";

    try {
      final String MDB_BASE_URL =
          "http://api.themoviedb.org/3/movie";
      final String MDB_POPULAR = "popular";
      final String MDB_TOP_RATED = "top_rated";

      //TODO: Reimplement preference option
      //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
      String sortValue = sortPreference; //preferences.getString("sort", "");

      if (sortValue.equalsIgnoreCase("popularity")) {
        builtUri = Uri.parse(MDB_BASE_URL).buildUpon()
            .appendPath(MDB_POPULAR)
            .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
            .build();
      } else if (sortValue.equalsIgnoreCase("rating")) {
        builtUri = Uri.parse(MDB_BASE_URL).buildUpon()
            .appendPath(MDB_TOP_RATED)
            .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
            .build();
      } else {
        Log.e("Error ", "Sort preference not set properly");
        return null;
      }

      URL url = new URL(builtUri.toString());

      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      InputStream inputStream = urlConnection.getInputStream();
      StringBuffer buffer = new StringBuffer();
      if (inputStream == null) {
        return null;
      }
      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line + "\n");
      }

      if (buffer.length() == 0) {
        return null;
      }
      JsonResponseStr = buffer.toString();
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
    return JsonResponseStr;
  }
}
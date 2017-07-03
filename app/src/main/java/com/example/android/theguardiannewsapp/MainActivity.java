package com.example.android.theguardiannewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static android.R.attr.x;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    //TODO change/delete URL
    // URL for article data from The Guardian
    private static final String articlesRequestURL = "https://content.guardianapis.com/search?q=";

    // Constant value for the article loader ID.
    private static final int ARTICLE_LOADER_ID = 1;

     // Adapter for the list of articles
    private ArticleAdapter mAdapter;

     // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        //Find and set the empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);


        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

/*
        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);
*/

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with the news.
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // If there is a network connection, fetch data
        if (hasInternet()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }


  @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
      SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

      String selection = sharedPrefs.getString(
              getString(R.string.settings_section_key),
              getString(R.string.settings_sections_default)
      );

      Uri baseUri = Uri.parse(articlesRequestURL);
      Uri.Builder uriBuilder = baseUri.buildUpon();

      Calendar c = Calendar.getInstance();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String formattedDate = df.format(c.getTime());

      uriBuilder.appendQueryParameter("section",selection);
      uriBuilder.appendQueryParameter("from-date",formattedDate);
      uriBuilder.appendQueryParameter("api-key","test");

      return new ArticleLoader(this, "https://content.guardianapis.com/search?q=&section=business&from-date=2014-01-01&api-key=test");
              //Once it works, substitute the input with: uriBuilder.toString());

/*

      //TODO remove comments/code
      //String minMagnitude = sharedPrefs.getString(
      Set<String> selections = sharedPrefs.getStringSet(
              getString(R.string.settings_section_key),
              null);
      Uri baseUri = Uri.parse(articlesRequestURL);
      Uri.Builder uriBuilder = baseUri.buildUpon();
      String[] selected = selections.toArray(new String[0]);

        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Build the URL query:
*//*        Set<String> selections = sharedPrefs.getStringSet(getString(R.string.settings_section_key), null);
        String[] selected = selections.toArray(new String[]{});
        Uri baseUri = Uri.parse(articlesRequestURL);
        Uri.Builder uriBuilder = baseUri.buildUpon();*//*

      String bufferString = "";
      for (int j = 1, count = 0; j < selected.length ; j++) {
            if (count == 0) {
                bufferString = selected[j];
                count++;
            } else {
                bufferString = bufferString + "%20OR%20" + selected[j].toLowerCase();
            }
        }

        uriBuilder.appendQueryParameter("section",bufferString);
      uriBuilder.appendQueryParameter("from-date",);
      uriBuilder.appendQueryParameter("api-key","test");

      // Create a new loader for the given URL
      return new ArticleLoader(this, "https://content.guardianapis.com/search?q=&tag=politics/politics&from-date=2014-01-01&api-key=test");
                //https://content.guardianapis.com/search?q=&section=business&from-date=2014-01-01&api-key=test*/
    }

    // Method to check if it is connected
    public boolean hasInternet(){
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No articles found."
        mEmptyStateTextView.setText(R.string.no_articles);

        // Clear the adapter of previous article data
        mAdapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();


    }

    //Menu overrides:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_main, menu);
        return true;
    }

    //Menu overrides:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.android.theguardiannewsapp;

import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import static android.R.attr.format;


// Helper methods related to requesting and receiving article data from The Guardian.
public final class QueryMethods {

    //Tag for the log messages
    private static final String LOG_TAG = QueryMethods.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryMethods} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryMethods (and an object instance of QueryMethods is not needed).
     */
    private QueryMethods() {
    }

    // Query The Guardian dataset and return a list of {@link Article} objects.
    public static List<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Article}s
        return articles;
    }

    // Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */

    // To extract data from JSON:
    private static List<Article> extractFeatureFromJson(String articleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            // Extract the JSONArray
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray articleArray = responseObject.getJSONArray("results");

            // For each article in the articleArray, create an {@link Article} object
            for (int i = 0; i < articleArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentArticle = articleArray.getJSONObject(i);

                //TODO Do the extraction correctly

                // Extract the value for the key called "webTitle" and put it in "title"
                String title = currentArticle.getString("webTitle");

                // region (Region)Extract the value for the key called "sectionName" and put it in "section"
                String section = "";
                if (currentArticle.has("sectionName")) {
                    section = currentArticle.getString("sectionName");
                } else {
                    section = "";
                }
                //endregion

                //region  (Region)Extract the value for the key called "webUrl" and put it in "url"
                String url = "";
                if (currentArticle.has("webUrl")) {
                    url = currentArticle.getString("webUrl");
                } else {
                    url = "";
                }
                //endregion

                //region (Region)Extract the value for the key called "webPublicationDate" and put it in "dateString"
                String date = "";
                if (currentArticle.has("webPublicationDate")) {
                    String dateString = currentArticle.getString("webPublicationDate");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    Date d = new Date();
                    try {
                        d = format.parse(dateString);
                    } catch (ParseException e) {
                        Log.e(LOG_TAG, "Problem parsing the date.", e);
                    }

                    DateFormat format2 = DateFormat.getDateInstance(DateFormat.MEDIUM);
                    date = format2.format(d);
                }
                //endregion

/* TODO Use authors?
                JSONArray authors = new JSONArray();
                // Extract the array of authors. If the authors don't exist, put a placeholder text:
                if (currentArticle.has("authors")) {
                    authors = currentArticle.getJSONArray("authors");
                } else {
                    authors.put("Author N/A");
                }
                //String that will be used to store all the authors:
                String authorString = "";
                //Boolean to be used in the "for" loop:
                boolean first = true;

                // For each author in the authorArray, extract and combine them
                for (int j = 0; j < authors.length(); j++) {
                    //Set "first" to false if there is more than one author, so that a comma will be added before:
                    if (j > 0) {
                        first = false;
                    }
                    //Add the authors
                    if (first) {
                        authorString = authors.getString(j);
                    } else {
                        authorString = authorString + ", " + authors.getString(j);
                    }
                }*/
                // Use one constructor or  the other depending on the field date existing:
                if (currentArticle.has("webPublicationDate")) {
                    // Create a new {@link Article} object with the section, title url and date of the article
                    Article article = new Article(title, section, url, date);
                    // Add the new {@link Article} to the list of articles.
                    articles.add(article);
                } else {
                    // Create a new {@link Article} object with the section, title and urlof the article
                    Article article = new Article(title, section, url);
                    // Add the new {@link Article} to the list of articles.
                    articles.add(article);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryMethods", "Problem parsing the article JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

}
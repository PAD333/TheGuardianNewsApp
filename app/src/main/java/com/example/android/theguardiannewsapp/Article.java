package com.example.android.theguardiannewsapp;

import java.util.Date;

/**
 * Created by Pablo on 24/06/2017.
 */

public class Article {


    //Title of the article
    private String mTitle;

    //Section of the article
    private String mSection;

    // URL of the article
    private String mUrl;

/* TODO Use authors?   // Author of the article (Optional)
    private String mAuthor;*/

    // Date published (Optional)
    private String date;


    //region Constructors (region). Since there is one parameter, I created one constructor for each combination.
    public Article(String mTitle, String mSection, String mUrl) {
        this.mTitle = mTitle;
        this.mSection = mSection;
        this.mUrl = mUrl;
    }

    public Article(String mTitle, String mSection, String mUrl, String date) {
        this.mTitle = mTitle;
        this.mSection = mSection;
        this.mUrl = mUrl;
        this.date = date;
    }

    /* TODO Use authors?
    public Article(String mTitle, String mSection, String mAuthor, String date) {
        this.mTitle = mTitle;
        this.mSection = mSection;
        this.mAuthor = mAuthor;
        this.date = date;
    }

    public Article(String mTitle, String mSection, String mAuthor) {
        this.mTitle = mTitle;
        this.mSection = mSection;
        this.mAuthor = mAuthor;
    }*/

    //endregion

/* TODO Use authors?   // Author Getter
    public String getAuthor() {
        return mAuthor;
    }*/

    // Title Getter
    public String getTitle() {
        return mTitle;
    }

    //Section Getter
    public String getSection() {
        return mSection;
    }

    // URL Getter
    public String getUrl () {
        return mUrl;
    }

    // Date Getter
    public String getDate() {
        return date;
    }
}

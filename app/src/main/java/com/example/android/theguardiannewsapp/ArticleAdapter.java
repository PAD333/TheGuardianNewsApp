package com.example.android.theguardiannewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pablo on 25/06/2017.
 */

public class ArticleAdapter extends ArrayAdapter<Article>  {
    public ArticleAdapter(Context context, List<Article> articles ) {
        super(context, 0, articles);
    }

    //Modificar
    /**
     * Returns a list item view that displays information about the article at the given position
     * in the list of articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        //region Region: Set the text on the TextViews
        // Find the article at the given position in the list of articles
        Article currentArticle = getItem(position);

        // Get the title string from the Article object
        String title = currentArticle.getTitle();
        // Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title_view);
        // Display the title of the current article in that TextView
        titleView.setText(title);

        // Get the section string from the Article object
        String section = currentArticle.getSection();
        // Find the TextView with view ID section
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section_view);
        // Display the section of the current article in that TextView
        sectionView.setText(section);

        // Get the section string from the Article object
        String date = currentArticle.getDate();
        TextView dateView = (TextView) listItemView.findViewById(R.id.date_view);
        if (date==null){
            // Find the TextView with view ID title
            dateView.setVisibility(View.GONE);
        }else {
            // Display the date of the current article in that TextView
            dateView.setText(date);
        }

/* TODO Use authors?       // Get the author string from the Article object
        String author = currentArticle.getAuthor();
        // Find the TextView with view ID author
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        // Display the author of the current article in that TextView
        authorView.setText(author);*/

        //endregion

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
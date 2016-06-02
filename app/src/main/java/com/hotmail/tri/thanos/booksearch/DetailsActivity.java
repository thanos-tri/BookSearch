package com.hotmail.tri.thanos.booksearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private ImageView detailsCoverImage;
    private TextView detailsTitleText;
    private TextView detailsAuthorsText;
    private TextView detailsPublisherText;
    private TextView detailsPagesText;

    private ImageButton detailsShareButton;

    private void initComponents(){
        // Init ImageView component
        detailsCoverImage = (ImageView) findViewById(R.id.detailsCoverImage);

        // Init TextView components
        detailsTitleText = (TextView) findViewById(R.id.detailsTitleText);
        detailsAuthorsText = (TextView) findViewById(R.id.detailsAuthorsText);
        detailsPublisherText = (TextView) findViewById(R.id.detailsPublisherText);
        detailsPagesText = (TextView) findViewById(R.id.detailsPagesText);

        // Init ImageButton component
        detailsShareButton = (ImageButton) findViewById(R.id.detailsShareButton);

    }

    private void renderUI(){
        final String title = getIntent().getStringExtra("fullTitle");
        String authors = getIntent().getStringExtra("authors");
        String pages = getIntent().getStringExtra("pages");
        String publishers = getIntent().getStringExtra("publishers");
        Bitmap bmp = getIntent().getParcelableExtra("cover");
        final String url = getIntent().getStringExtra("url");

        if(bmp != null)
            detailsCoverImage.setImageBitmap(bmp);
        else
            detailsCoverImage.setImageResource(R.drawable.no_cover);

        detailsTitleText.setText(title);
        detailsAuthorsText.setText(authors);
        StringBuffer buffer = new StringBuffer();
        buffer.append("Published by:\n").append(publishers);
        detailsPublisherText.setText(buffer.toString());
        buffer = new StringBuffer();
        if(Integer.parseInt(pages) > 0){
            buffer.append(pages).append(" pages");
            detailsPagesText.setText(buffer.toString());
        }
        else{
            buffer.append("E-book");
            detailsPagesText.setText(buffer);
        }

        // Add OnClickListener to ImageButton for sharing

        detailsShareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_SUBJECT, title);
                share.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(share, "Share link!"));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initComponents();
        renderUI();
    }
}

package com.hotmail.tri.thanos.booksearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

    private List<BookTitle> books;
    private ImageButton searchButton;
    private EditText searchText;
    private ProgressBar searchLoadingSpinner;
    private RecyclerView bookTitleList;
    private TextView noResultsText;

    private ListManager manager;

    private void initComponents(){
        // Init Book list to empty list
        books = new ArrayList<>();

        // Init EditText components
        searchText = (EditText) findViewById(R.id.searchText);
        assert searchText != null;
        searchText.setFocusableInTouchMode(true);
        searchText.requestFocus();

        // Init ProgressBar components
        searchLoadingSpinner = (ProgressBar) findViewById(R.id.searchLoadingSpinner);
        assert searchLoadingSpinner != null;
        searchLoadingSpinner.setVisibility(View.GONE);

        // Init ImageButton components
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        assert searchButton != null;
        searchButton.setImageResource(android.R.drawable.ic_menu_search);

        // Init RecyclerView components
        bookTitleList = (RecyclerView) findViewById(R.id.searchRecyclerView);
        assert bookTitleList != null;
        bookTitleList.setHasFixedSize(true);
        bookTitleList.setItemAnimator(new DefaultItemAnimator());
        BookTitleAdapter adapter = new BookTitleAdapter(books);
        bookTitleList.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        bookTitleList.setLayoutManager( manager );

        // Init TextView component
        noResultsText = (TextView) findViewById(R.id.noResultsText);
        assert noResultsText != null;
        noResultsText.setVisibility(View.GONE);
    }

    private void addListenersToComponents(){
        // Add enter button listener to EditText
        searchText.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    startSearch();
                    return true;
                }
                return false;
            }
        });

        // Add an OnTouchListener to RecyclerView to catch clicks on Views
        final GestureDetector detector = new GestureDetector(SearchActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        bookTitleList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if( child != null && detector.onTouchEvent(motionEvent)){
                    // Passing editionKeys, authors, title and cover bitmap (to avoid extra download)
                    BookTitle current = books.get(recyclerView.getChildAdapterPosition(child));

                    Intent intent = new Intent(SearchActivity.this, EditionsActivity.class);

                    intent.putExtra("fullTitle", current.getFullTitle());
                    intent.putExtra("allAuthors", current.getAuthorsString());
                    intent.putExtra("coverBitmap", current.getCoverBitmap());
                    intent.putStringArrayListExtra("editionKeys", current.getEditionKeys());

                    startActivity(intent);

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
        });

        // Add OnClickListener on Button to initiate the search when clicked
        searchButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        startSearch();
                    }
                }
        );
    }

    private Boolean isNetworkAvailable(){
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectionManager.getActiveNetworkInfo();
        if(activeNetworkInfo == null)
            return false;
        else if(!activeNetworkInfo.isConnected())
            return false;
        return true;
    }

    private void startSearch(){
        if(manager.getStatus() == AsyncTask.Status.RUNNING){
            // New search initiated. Cancel task
            manager.cancel(true);
        }
        // Get user's input
        String input = searchText.getText().toString();
        if(input.isEmpty()){
            Toast.makeText(SearchActivity.this, "Nothing to search for!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isNetworkAvailable()) {
            manager = new ListManager();
            manager.execute(input);
        }
        else{
            Toast.makeText(SearchActivity.this, "Not connected to the internet", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initComponents();
        addListenersToComponents();
        manager = new ListManager();

    }

    private class ListManager extends AsyncTask<String, Void, Void> {

        private void populateBookTitleList(JSONArray docs){
            for(int i=0; i < docs.length(); i++){
                try {
                    JSONObject obj = docs.getJSONObject(i);
                    BookTitle b = new BookTitle();
                    b.setTitle(obj.getString("title"));
                    if(obj.has("subtitle")) {
                        b.setSubtitle(obj.getString("subtitle"));
                    }
                    if(obj.has("author_name")) {
                        JSONArray authors = obj.getJSONArray("author_name");
                        for (int j = 0; j < authors.length(); j++) {
                            b.addAuthor(authors.getString(j));
                        }
                    }
                    if(obj.has("edition_key")) {
                        JSONArray editionKeys = obj.getJSONArray("edition_key");
                        for (int j = 0; j < editionKeys.length(); j++) {
                            b.addEditionKey(editionKeys.getString(j));
                        }
                    }
                    if(obj.has("cover_i")){
                        b.setCoverID( obj.getString("cover_i") );
                        b.buildCoverURL(BookTitle.MEDIUM);
                    }
                    else if(obj.has("cover_edition_key")){
                        b.setCoverOLID( obj.getString("cover_edition_key"));
                        b.buildCoverURL(BookTitle.MEDIUM);
                    }
                    if(b.hasCoverId() || b.hasCoverOLID()){
                        Bitmap bmp = Glide.with(SearchActivity.this.getApplicationContext()).load(b.getCoverURL()).asBitmap().into(-1, -1).get();
                        b.setCoverBitmap(bmp);
                    }
                    books.add(b);
                } catch (JSONException e){
                    Log.e("JSON", "Could not retrieve data for JSONObject in position " + i);
                    e.printStackTrace();
                } catch (ExecutionException e){
                    Log.e("JSON", "Execution error, download aborted");
                    e.printStackTrace();
                } catch (InterruptedException e){
                    Log.e("JSON", "Image download interrupted");
                    e.printStackTrace();
                }
            }
        }

        private void emptyBookTitleList(){
            books.clear();
            System.gc();
        }

        private String buildQueryString(String input){
            StringBuilder builder = new StringBuilder();

            for(String s : input.split(" ")){
                builder.append(s);
                builder.append("+");
            }
            builder.deleteCharAt(builder.length() - 1);

            return (builder.toString());
        }

        private String buildSearchUrlString(String input){
            StringBuffer buffer = new StringBuffer();
            String domain = "https://openlibrary.org/";
            String action = "search";
            String type = ".json";
            String searchType = "title";
            String query = buildQueryString(input);

            buffer  .append(domain)
                    .append(action)
                    .append(type)
                    .append("?")
                    .append(searchType)
                    .append("=")
                    .append(query);

            return buffer.toString();
        }

        private JSONArray concatArray(JSONArray first, JSONArray last){
            for(int i = 0; i < last.length(); i++){
                try {
                    first.put(last.get(i));
                }catch (JSONException e){
                    Log.e("JSON", "Could not concatenate JSONArrays");
                    e.printStackTrace();
                }
            }
            return first;
        }

        @Override
        protected void onPreExecute() {
            emptyBookTitleList();
            searchLoadingSpinner.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... params) {
            // Check Internet connection

            JSONObject obj;
            JSONArray docs;
            String searchUrl = buildSearchUrlString(params[0]);

            // Create new URL to connect to, initially no page
            DownloadJSON downloader = new DownloadJSON(searchUrl);

            // Download json and create a new object for it
            obj = downloader.downloadJSONObject();

            // Find max num of pages
            int resultsPerPage = 100;
            int numFound;
            try{
                numFound = Integer.parseInt( obj.getString("numFound") );
            } catch (JSONException e){
                Log.e("JSON", "Error in fetching data for key \"numFound\"");
                e.printStackTrace();
                return null;
            }
            int pages = 1 + numFound / resultsPerPage;

            // Create a new JSONArray to fill with book docs
            docs = downloader.getJSONArrayFromJSONObject(obj, "docs");

            // Foreach page, fetch json and add its' docs JSONArray to existing docs
            for(int i = 1; i < pages; i ++){
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder  .append(searchUrl)
                            .append("&page=" + (i+1));
                downloader.setUrl(urlBuilder.toString());
                obj = downloader.downloadJSONObject();
                docs = concatArray(docs, downloader.getJSONArrayFromJSONObject(obj, "docs"));
            }

            populateBookTitleList(docs);
            return null;
        }

        @Override
        protected void onPostExecute(Void voidElement) {
            searchLoadingSpinner.setVisibility(View.GONE);
            if(books.isEmpty())
                noResultsText.setVisibility(View.VISIBLE);

            // TODO implement items per page!! See Endless RecyclerView Scroll Listener
        }

        @Override
        protected void onCancelled(Void voidElement) {
            emptyBookTitleList();
        }
    }
}

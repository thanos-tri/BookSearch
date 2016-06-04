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
    private List<BookTitle> lessBooks;

    private ImageButton searchButton;
    private EditText searchText;
    private ProgressBar searchLoadingSpinner;
    private RecyclerView bookTitleList;
    private TextView noResultsText;

    private ListManager listManager;
    private BookTitleAdapter adapter;

    private Boolean loading;
    private int maxPages;
    private int currentPage;
    private String searchTextString;

    private void initComponents(){
        // Init Book lists to empty lists
        books = new ArrayList<>();
        lessBooks = new ArrayList<>();

        // Init listManager object (avoid NullPointerExceptions)
        int firstPage = 1;
        listManager = new ListManager(firstPage);

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
        adapter = new BookTitleAdapter(lessBooks);
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
                    initSearch();
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
        bookTitleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
                    final LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = manager.getItemCount();
                    int lastVisibleItem = 1 + manager.findLastVisibleItemPosition();
                    if(!loading && totalItemCount <= lastVisibleItem){
                        if(lessBooks.size() >= books.size()){
                            if(currentPage < maxPages) {
                                Log.w("ASDFG", "lessBooks size " + lessBooks.size() + " vs books size " + books.size());
                                Log.w("ASDFG", "Current page " + currentPage + " vs max pages " + maxPages + " so we need to download more data");
                                loading = true;
                                // Download more books
                                currentPage++;
                                new ListManager(currentPage).execute(searchTextString);
                            }
                        }
                        else{
                            // Load more from books list
                            loading = true;
                            int start = lessBooks.size();
                            int bookNum = Math.min(25, books.size() - start);
                            showMoreBooks(bookNum);
                            adapter.notifyDataSetChanged();
                            new DownloadManager().execute(start, start + bookNum);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.notifyItemRemoved(lessBooks.size() - 1);
//                                    lessBooks.remove(lessBooks.size() - 1);
//
//                                    int bookNum = Math.min(20, books.size() - lessBooks.size());
//                                    showMoreBooks(bookNum);
//                                    adapter.notifyDataSetChanged();
//                                    loading = false;
//                                }
//                            }, 2000);
                        }
                    }
                }
            }
        });

        // Add OnClickListener on Button to initiate the search when clicked
        searchButton.setOnClickListener(
            new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // TODO fix focus to close soft keyboard
                    initSearch();
                }
            }
        );
    }

    private void initSearch(){
        // Clear focus off EditText
        searchText.clearFocus();

        // If the user has no internet connection method returns
        if(!isNetworkAvailable()) {
            Toast.makeText(SearchActivity.this, "Not connected to the internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If another search is underway, cancel it and start over the new
        if(listManager.getStatus() == AsyncTask.Status.RUNNING){
            // New search initiated. Cancel task
            Toast.makeText(SearchActivity.this, "Canceling previous search...", Toast.LENGTH_SHORT).show();
            listManager.cancel(true);
        }

        // Get user's input
        searchTextString = searchText.getText().toString();
        if(searchTextString.isEmpty()){
            Toast.makeText(SearchActivity.this, "Nothing to search for.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-init everything used from previous sessions:
        // Empty previous session's lists
        books.clear();
        lessBooks.clear();
        System.gc();

        // Notify adapter
        adapter.notifyDataSetChanged();

        // Init maxPages num to 0 (unknown), currentPage to 1
        currentPage = 1;
        maxPages = 0;

        // Loading = false
        loading = false;

        listManager = new ListManager(currentPage);
        listManager.execute(searchTextString);
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

    private void showMoreBooks(int itemsPerPage){
        int start = lessBooks.size();
        int end = Math.min(lessBooks.size() + itemsPerPage, books.size());
        for(int i=start; i < end; i++){
            lessBooks.add(books.get(i));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initComponents();
        addListenersToComponents();
    }

    private class ListManager extends AsyncTask<String, Integer, Void> {
        private int pageToDownload;

        public ListManager(int pageNum){
            this.pageToDownload = pageNum;
        }

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
                    books.add(b);
                } catch (JSONException e){
                    Log.e("JSON", "Could not retrieve data for JSONObject in position " + i);
                    e.printStackTrace();
                }
            }
        }

        private void emptyBookTitleList(){
            lessBooks.clear();
            books.clear();
            System.gc();
        }

        private String buildQueryString(String input){
            StringBuilder builder = new StringBuilder();

            // For the search to complete properly, we need to turn everything to lowercase
            for(String s : input.split(" ")){
                builder.append(s.toLowerCase());
                builder.append("+");
            }
            builder.deleteCharAt(builder.length() - 1);

            return (builder.toString());
        }

        private String buildSearchUrlString(String input, int page){
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
                    .append(query)
                    .append("&page=")
                    .append(page);

            return buffer.toString();
        }

        @Override
        protected Void doInBackground(String... input) {
            JSONObject obj;
            JSONArray docs;
            String searchUrl;

            searchUrl = buildSearchUrlString(input[0], pageToDownload);

            // Create new URL to connect to, initially page 1
            DownloadJSON downloader = new DownloadJSON(searchUrl);

            // Download json and create a new object for it
            obj = downloader.downloadJSONObject();

            if(maxPages == 0) {
                // Find max num of pages
                int resultsPerPage = 100;
                int numFound;
                try {
                    numFound = Integer.parseInt(obj.getString("numFound"));
                } catch (JSONException e) {
                    Log.e("JSON", "Error in fetching data for key \"numFound\"");
                    e.printStackTrace();
                    return null;
                }
                maxPages = 1 + numFound / resultsPerPage;
            }

            // Create a new JSONArray to fill with book docs
            docs = downloader.getJSONArrayFromJSONObject(obj, "docs");

            populateBookTitleList(docs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            loading = true;
            if(lessBooks.isEmpty()) {
                searchLoadingSpinner.setVisibility(View.VISIBLE);
                noResultsText.setVisibility(View.GONE);
            }
            else{
                lessBooks.add(null);
                //adapter.notifyDataSetChanged();
                // TODO this or the one below??
                adapter.notifyItemInserted(lessBooks.size()-1);
            }
        }

        @Override
        protected void onCancelled(Void aVoid) {
            loading = false;
            emptyBookTitleList();
        }

        @Override
        protected void onPostExecute(Void voidElement) {
            if(lessBooks.isEmpty()) {
                searchLoadingSpinner.setVisibility(View.GONE);
                if (books.isEmpty())
                    noResultsText.setVisibility(View.VISIBLE);
            }
            else{
                adapter.notifyItemRemoved(lessBooks.size() - 1);
                lessBooks.remove(lessBooks.size() - 1);
            }

            int start = lessBooks.size();
            int bookNum = Math.min(25, books.size() - lessBooks.size() );
            showMoreBooks(bookNum);
            new DownloadManager().execute(start, lessBooks.size());
            adapter.notifyDataSetChanged();
            loading = false;
        }
    }

    private class DownloadManager extends AsyncTask<Integer, Integer, Void>{
        @Override
        protected Void doInBackground(Integer ... position) {
            int start = position[0];
            int end = position[1];
            for(int i=start; i < end; i++){
                BookTitle b = lessBooks.get(i);
                if(!b.hasCoverBitmap() && (b.hasCoverId() || b.hasCoverOLID())) {
                    try {
                        Bitmap bmp = Glide.with(SearchActivity.this.getApplicationContext()).load(b.getCoverURL()).asBitmap().into(-1, -1).get();
                        b.setCoverBitmap(bmp);
                        publishProgress(i);
                    } catch (ExecutionException e) {
                        Log.e("JSON", "Execution error, download aborted");
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        Log.e("JSON", "Image download interrupted");
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer ... position) {
            // Notify that a bitmap is available
            adapter.notifyItemChanged(position[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loading = false;
        }
    }
}
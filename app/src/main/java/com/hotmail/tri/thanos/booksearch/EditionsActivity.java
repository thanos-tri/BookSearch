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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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


public class EditionsActivity extends AppCompatActivity {

    private List<Book> books;

    private ImageView coverImage;
    private TextView titleText;
    private TextView authorsText;
    private TextView editionsText;
    private RecyclerView bookEditionsList;
    private ProgressBar editionsLoadingSpinner;

    private ListManager manager;

    private void initComponents(){
        // Init book list
        books = new ArrayList<>();

        // Init ImageView component
        coverImage = (ImageView) findViewById(R.id.coverImage);

        // Init TextView components
        titleText = (TextView) findViewById(R.id.titleText);
        authorsText = (TextView) findViewById(R.id.authorsText);
        editionsText = (TextView) findViewById(R.id.editionsText);

        // Init RecyclerView components
        bookEditionsList = (RecyclerView) findViewById(R.id.editionsRecyclerView);
        assert bookEditionsList != null;
        bookEditionsList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        bookEditionsList.setLayoutManager( manager );
        bookEditionsList.setItemAnimator(new DefaultItemAnimator());

        // Init ProgressBar loading spinner component
        editionsLoadingSpinner = (ProgressBar) findViewById(R.id.editionsLoadingSpinner);
        assert editionsLoadingSpinner != null;
        editionsLoadingSpinner.setVisibility(View.GONE);

    }

    private void renderTopUI(int editions){
        String fullTitle = getIntent().getStringExtra("fullTitle");
        String authors = getIntent().getStringExtra("allAuthors");
        String coverUrl = getIntent().getStringExtra("coverUrl");
        Bitmap cover = getIntent().getParcelableExtra("cover");
        if(cover == null)
            coverImage.setImageResource(R.drawable.no_cover);
        else
            coverImage.setImageBitmap(cover);
        titleText.setText(fullTitle);
        authorsText.setText("By " + authors);
        editionsText.setText("Found " + editions + " editions for this title:");
    }

    private void addListenerToRecycleView(){
        // Add an OnTouchListener to RecyclerView to catch clicks on Views
        final GestureDetector detector = new GestureDetector(EditionsActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        bookEditionsList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if( child != null && detector.onTouchEvent(motionEvent)){
                    // Need to pass title, author, coverId (or coverOLID) and editionKeys to make the new list
                    Book current = books.get(recyclerView.getChildAdapterPosition(child));

                    Intent intent = new Intent(EditionsActivity.this, DetailsActivity.class);
                    intent.putExtra("authors", current.getAuthorsString());
                    intent.putExtra("fullTitle", current.getFullTitle());
                    intent.putExtra("publishers", current.getPublishersString());
                    intent.putExtra("url", current.getBookUrl());
                    intent.putExtra("pages", current.getPages() + "");
                    intent.putExtra("cover", current.getCover());
                    startActivity(intent);

                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
        });
    }

    private Boolean isNetworkAvailable(){
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectionManager.getActiveNetworkInfo();
        if(activeNetworkInfo == null)
            return false;
        if(activeNetworkInfo.isConnected())
            return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editions);

        initComponents();
        ArrayList<String> editionKeys = getIntent().getStringArrayListExtra("editionKeys");
        renderTopUI(editionKeys.size());
        addListenerToRecycleView();


        String [] editionKeysArray = editionKeys.toArray(new String[editionKeys.size()]);
        if(isNetworkAvailable()) {
            manager = new ListManager();
            manager.execute(editionKeysArray);
        }
        else{
            Toast.makeText(EditionsActivity.this, "Not connected to the internet", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class ListManager extends AsyncTask<String, Void, Void>{

        private String buildFetchInfoURLString(String editionKey){
            StringBuffer buffer = new StringBuffer();
            String domain = "https://openlibrary.org/api/books?bibkeys=";
            String idType = "OLID:";
            String format = "format=json";
            String details = "jscmd=data";

            buffer  .append(domain)
                    .append(idType)
                    .append(editionKey)
                    .append("&")
                    .append(format)
                    .append("&")
                    .append(details);

            return buffer.toString();
        }

        private void emptyList(){
            books.clear();
            System.gc();
        }

        @Override
        protected void onPreExecute() {
            books.clear();
            editionsLoadingSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            // Update ImageView with real book cover

            for(int i=0; i < params.length; i++) {
                String fetchDataUrl = buildFetchInfoURLString(params[i]);
                DownloadJSON downloader = new DownloadJSON(fetchDataUrl);

                JSONObject obj = downloader.downloadJSONObject();

                if(obj.has("OLID:" + params[i])){
                    JSONObject bookObj;
                    try {
                        bookObj = obj.getJSONObject("OLID:" + params[i]);
                        Book b = new Book();
                        b.addTitle(bookObj.getString("title"));
                        if(bookObj.has("subtitle"))
                            b.addSubtitle(bookObj.getString("subtitle"));
                        if(bookObj.has("cover")) {
                            b.addCoverURL(bookObj.getJSONObject("cover").getString("medium"));
                        }
                        if(bookObj.has("url")){
                            b.setBookUrl(bookObj.getString("url"));
                        }
                        if(bookObj.has("number_of_pages"))
                            b.addPages(Integer.parseInt(bookObj.getString("number_of_pages")));
                        if(bookObj.has("authors")){
                            JSONArray authorsArray = bookObj.getJSONArray("authors");
                            for(int j=0; j < authorsArray.length(); j++){
                                b.addAuthor(authorsArray.getJSONObject(j).getString("name"));
                            }
                        }
                        if(bookObj.has("publishers")){
                            JSONArray array = bookObj.getJSONArray("publishers");
                            for(int j=0; j < array.length(); j++){
                                b.addPublisher(array.getJSONObject(j).getString("name"));
                            }
                        }
                        if(b.hasCoverUrl()){
                            Bitmap bmp = Glide.with(EditionsActivity.this.getApplicationContext()).load(b.getCoverUrl()).asBitmap().into(-1, -1).get();
                            b.setCoverBitmap(bmp);
                        }
                        books.add(b);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    catch (ExecutionException e){
                        e.printStackTrace();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            editionsLoadingSpinner.setVisibility(View.GONE);

            BookEditionAdapter adapter = new BookEditionAdapter(books);
            bookEditionsList.setAdapter(adapter);
            // TODO implement items per page!! See Endless RecyclerView Scroll Listener
        }
        @Override
        protected void onCancelled(Void voidElement) {
            emptyList();
        }
    }
}



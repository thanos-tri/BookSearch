package com.hotmail.tri.thanos.booksearch;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class Book {

    private String title;
    private String subtitle;
    private String coverURL;
    private Bitmap coverBitmap;
    private ArrayList<String> authors;

    public Book(){
        title = "";
        subtitle = null;
        coverURL = null;
        coverBitmap = null;
        this.authors = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getFullTitle(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(title);
        if(this.hasSubtitle())
            buffer.append(": " + subtitle);
        return buffer.toString();
    }

    public String getCoverURL() {
        return this.coverURL;
    }

    public Bitmap getCoverBitmap(){
        return this.coverBitmap;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public String getAuthorAt(int index){
        return authors.get(index);
    }

    public String getAuthorsString(){
        StringBuffer buffer = new StringBuffer();
        if(authors.isEmpty())
            return "Unknown author";
        for(int i=0; i < authors.size(); i++){
            buffer.append(authors.get(i));
            buffer.append(", ");
        }
        buffer.deleteCharAt(buffer.length() - 1);           // Delete last space
        buffer.deleteCharAt(buffer.length() - 1);           // Delete last comma

        return buffer.toString();
    }

    public Boolean hasSubtitle(){
        if(subtitle != null)
            return true;
        return false;
    }

    public Boolean hasCoverBitmap(){
        if(this.coverBitmap != null)
            return true;
        return false;
    }

    public Boolean hasCoverURL(){
        if(this.coverURL != null)
            return true;
        return false;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setSubtitle(String subtitle){
        this.subtitle = subtitle;
    }

    public void setCoverURL(String coverURL){
        this.coverURL = coverURL;
    }

    public void setCoverBitmap(Bitmap bmp){
        this.coverBitmap = bmp;
    }

    public void setAuthors(ArrayList<String> authors){
        this.authors = authors;
    }

    public void addAuthor(String author){
        this.authors.add(author);
    }
}

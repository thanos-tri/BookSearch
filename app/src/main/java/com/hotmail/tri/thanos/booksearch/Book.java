package com.hotmail.tri.thanos.booksearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Book {

    public static int SMALL = 1;
    public static int MEDIUM = 2;
    public static int LARGE = 3;

    private ArrayList<String> editionKeys;
    private String title;
    private String subtitle;
    private String coverId;
    private String coverOLID;
    private String coverUrl;           // or cover id?
    private Bitmap cover;
    private String bookUrl;
    private ArrayList<String> authors;
    private ArrayList<String> publishers;
    private int pages;              // ebooks??
    private String description;     // Necessary?

    public Book(){
        title = "";
        subtitle = null;
        coverOLID = null;
        coverId = null;
        coverUrl = null;

        this.editionKeys = new ArrayList<>();
        this.authors = new ArrayList<>();
        this.publishers = new ArrayList<>();
    }

    public Book(String title, String subtitle, String coverUrl, ArrayList<String> authors, int pages, String description) {
        this.editionKeys = new ArrayList<>();
        this.title = title;
        this.subtitle = subtitle;
        this.coverUrl = coverUrl;
        this.authors = authors;
        this.pages = pages;
        this.description = description;
    }

    public Book(String title, String coverId){
        this.title = title;
        this.coverId = coverId;
    }

    public ArrayList<String> getEditionKeys() {
        return editionKeys;
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

    public String getCoverUrl(int size) {
        if(!this.hasCover())
            return null;
        StringBuffer buffer = new StringBuffer();
        String prefix = "http://covers.openlibrary.org/b/";
        String postfix = ".jpg";

        String sizeChar;
        if(size == SMALL){
            sizeChar = "S";
        }
        else if(size == MEDIUM) {
            sizeChar = "M";
        }
        else if(size == LARGE){
            sizeChar = "L";
        }
        else{
            return null;
        }

        String type;
        if(hasCoverId())
            type = "id/" + coverId;
        else
            type = "olid/" + coverOLID;

        buffer.append(prefix).append(type).append("-").append(sizeChar).append(postfix);
        coverUrl = buffer.toString();
        return coverUrl;
    }

    public Bitmap getCover(){
        return this.cover;
    }

    public String getCoverUrl(){
        return coverUrl;
    }

    public String getCoverId(){
        return coverId;
    }

    public String getCoverOLID(){
        return coverOLID;
    }

    public ArrayList<String> getAuthors() {
        return authors;
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

    public String getPublishersString(){
        StringBuffer buffer = new StringBuffer();
        if(publishers.isEmpty())
            return "Unknown publisher";
        for(int i=0; i < publishers.size(); i++){
            buffer.append(publishers.get(i));
            buffer.append(", ");
        }
        buffer.deleteCharAt(buffer.length() - 1);           // Delete last space
        buffer.deleteCharAt(buffer.length() - 1);           // Delete last comma

        return buffer.toString();
    }

    public int getPages() {
        return pages;
    }

    public String getBookUrl(){
        return this.bookUrl;
    }

    public Boolean hasSubtitle(){
        if(subtitle != null)
            return true;
        return false;
    }

    public Boolean hasCoverId(){
        if(coverId == null)
            return false;
        return true;
    }

    public Boolean hasCoverOLID(){
        if(coverOLID == null)
            return false;
        return true;
    }

    public Boolean hasCover(){
        if(coverUrl != null)
            return true;
        return false;
    }

    public Boolean hasCoverUrl(){
        if(coverUrl != null)
            return true;
        return false;
    }

    public void addTitle(String title){
        this.title = title;
    }

    public void addSubtitle(String subtitle){
        this.subtitle = subtitle;
    }

    public void addCoverId(String coverId){
        this.coverId = coverId;
        // make url???
    }

    public void addCoverOLID(String olid){
        this.coverOLID = olid;
    }

    public void addCoverBitmap(Bitmap bmp){
        this.cover = bmp;
    }

    public void addPages(int pages){
        this.pages = pages;
    }

    public void addAuthor(String authorName){
        this.authors.add(authorName);
    }

    public void addPublisher(String publisherName){
        this.publishers.add(publisherName);
    }

    public void addEditionKey(String ocid){
        this.editionKeys.add(ocid);
    }

    public void addCoverURL(int size){
        StringBuffer buffer = new StringBuffer();
        String prefix = "http://covers.openlibrary.org/b/";
        String postfix = ".jpg";

        String sizeChar;
        if(size == SMALL){
            sizeChar = "S";
        }
        else if(size == MEDIUM) {
            sizeChar = "M";
        }
        else if(size == LARGE){
            sizeChar = "L";
        }
        else{
            return;
        }

        String type;
        if(hasCoverId())
            type = "id/" + coverId;
        else
            type = "olid/" + coverOLID;

        buffer  .append(prefix)
                .append(type)
                .append("-")
                .append(sizeChar)
                .append(postfix);

        coverUrl = buffer.toString();
    }

    public void addCoverURL(String url){
        this.coverUrl = url;
    }

    public void setCoverBitmap(Bitmap bmp){
        this.cover = bmp;
    }

    public void setBookUrl(String url){
        this.bookUrl = url;
    }
}

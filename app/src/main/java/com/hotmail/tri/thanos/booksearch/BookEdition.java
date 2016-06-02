package com.hotmail.tri.thanos.booksearch;

import java.util.ArrayList;

public class BookEdition extends Book{
    private String referenceURL;
    private int numberOfPages;
    private ArrayList<String> publishers;

    public BookEdition(){
        super();
        publishers = new ArrayList<>();
        numberOfPages = 0;
        referenceURL = null;
    }

    public String getReferenceURL(){
        return this.referenceURL;
    }

    public int getNumberOfPages(){
        return this.numberOfPages;
    }

    public ArrayList<String> getPublishers(){
        return publishers;
    }

    public String getPublisherAt(int index){
        return publishers.get(index);
    }

    public String getPublishersString(){
        StringBuffer buffer = new StringBuffer();
        if(publishers.isEmpty())
            return "Unknown publisher";
        for(int i=0; i < publishers.size(); i++){
            buffer.append(publishers.get(i));
            buffer.append(", ");
        }
        buffer  .deleteCharAt(buffer.length() - 1)            // Delete last space
                .deleteCharAt(buffer.length() - 1);           // Delete last comma

        return buffer.toString();
    }

    public void setReferenceURL(String refURL){
        this.referenceURL = refURL;
    }

    public void setNumberOfPages(int pages){
        this.numberOfPages = pages;
    }

    public void setPublishers(ArrayList<String> publishers){
        this.publishers = publishers;
    }

    public void addPublisher(String publisher){
        this.publishers.add(publisher);
    }

}

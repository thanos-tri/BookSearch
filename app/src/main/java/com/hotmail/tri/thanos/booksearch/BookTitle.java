package com.hotmail.tri.thanos.booksearch;

import java.util.ArrayList;

public class BookTitle extends Book {
    private String coverID;
    private String coverOLID;
    private ArrayList<String> editionKeys;

    public static int SMALL = 1;
    public static int MEDIUM = 2;
    public static int LARGE = 3;

    public BookTitle(){
        super();
        editionKeys = new ArrayList<>();
        coverID = null;
        coverOLID = null;
    }

    public String getCoverID(){
        return coverID;
    }

    public String getCoverOLID(){
        return coverOLID;
    }

    public ArrayList<String> getEditionKeys(){
        return editionKeys;
    }

    public String getEditionKeyAt(int index){
        return editionKeys.get(index);
    }

    public Boolean hasCoverId(){
        if(this.coverID == null)
            return false;
        return true;
    }

    public Boolean hasCoverOLID(){
        if(this.coverOLID == null)
            return false;
        return true;
    }

    public void setCoverID(String ID){
        this.coverID = ID;
    }

    public void setCoverOLID(String OLID){
        this.coverOLID = OLID;
    }

    public void setEditionKeys(ArrayList<String> keys){
        this.editionKeys = keys;
    }

    public void addEditionKey(String key){
        this.editionKeys.add(key);
    }

    public void buildCoverURL(int size){
        StringBuffer buffer = new StringBuffer();
        String prefix = "http://covers.openlibrary.org/b/";
        String postfix = ".jpg";

        String sizeChar;
        if(size == SMALL)
            sizeChar = "S";
        else if(size == MEDIUM)
            sizeChar = "M";
        else    // size == LARGE
            sizeChar = "L";

        String type;
        if(hasCoverId())
            type = "id/" + coverID;
        else
            type = "olid/" + coverOLID;

        buffer  .append(prefix)
                .append(type)
                .append("-")
                .append(sizeChar)
                .append(postfix);

        setCoverURL(buffer.toString());
    }
}

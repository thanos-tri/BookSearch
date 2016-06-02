package com.hotmail.tri.thanos.booksearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadJSON {
    private URL url;

    public DownloadJSON(String urlString){
        setUrl(urlString);
    }

    public DownloadJSON(URL url){
        setUrl(url);
    }

    public void setUrl(String urlString){
        try{
            url = new URL(urlString);
        } catch (MalformedURLException e){
            Log.w("JSON", "Malformed URL given");
            e.printStackTrace();
        }
    }

    public void setUrl(URL url){
        this.url = url;
    }

    public JSONObject downloadJSONObject(){
        String data = "";
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();

            String line = "";
            while( (line = reader.readLine()) != null){
                buffer.append(line);
                buffer.append("\n");
            }
            data = buffer.toString();
            stream.close();
            connection.disconnect();
        } catch (IOException e){
            e.printStackTrace();
        }
        JSONObject obj = null;
        try{
            obj = new JSONObject(data);
        } catch (JSONException e){
            Log.e("JSON", "Error in creating new JSONObject");
        }
        return( obj );
    }

    public JSONArray getJSONArrayFromJSONObject(JSONObject obj, String key){
        JSONArray array = null;
        try{
            array = obj.getJSONArray(key);
        } catch (JSONException e){
            Log.w("JSON", "Could not retrieve JSONArray for key " + key);
            e.printStackTrace();
        }
        return array;
    }
}

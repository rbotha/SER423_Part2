package rbotha.bsse.asu.edu.rbothaapplication;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;


/*
 * Copyright 2018 Ruan Botha,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: Assignment for week 5 demonstrating multiple views, database
 * integration (SQLite), lists, and some maths.
 *
 * Ser423 Mobile Applications
 * see http://pooh.poly.asu.edu/Mobile
 * @author Ruan Botha rbotha@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2018
 */

public class JsonRPCRequestViaHttp extends Thread {

    private final Map<String, String> headers;
    private URL url;
    private String method;
    private String requestData;
    private Handler handler;
    private DatabaseHelper db;
    private Context context;
    //private MainActivity parent;

    public JsonRPCRequestViaHttp(URL url, Handler handler, String method, String parmsArray, DatabaseHelper db, Context context){
        this.url =url;
        this.db = db;
        this.context = context;
        //this.parent = parent;
        this.method = method;
        this.handler = handler;
        this.headers = new HashMap<String, String>();
        requestData = "{ \"jsonrpc\":\"2.0\", \"method\":\""+method+"\", \"params\":"+parmsArray+
                ",\"id\":3}";
    }

    public void run(){
        try {
            String respData = this.post(url, headers, requestData);
            android.util.Log.d(this.getClass().getSimpleName(),"Result of JsonRPC request: "+respData);
            if(method.equals("getNames")){
                JSONObject jo = new JSONObject(respData);
                JSONArray ja = jo.getJSONArray("result");
                ArrayList<String> al = new ArrayList<String>();
                ArrayList<String> dbList = new ArrayList<String>();
                String dbSelectString = "select * from pl_places where NAME NOT IN (";
                for(int i=0; i< ja.length(); i++){
                    al.add(ja.getString(i));
                    int temp = i;
                    temp +=1;
                    if(temp != ja.length()){dbSelectString += "'"+ja.getString(i)+"',";}
                    else{dbSelectString += "'"+ja.getString(i)+"');";}
                }
                //String[] arr = al.toArray(new String[0]);
                Cursor res = db.getNotIn(dbSelectString);
                if(res.getCount() > 0){
                    while(res.moveToNext()){
                        URL url = null;

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        String urlStr = sharedPrefs.getString("pref_url",String.valueOf(R.string.defaulturl));
                        url = new URL(urlStr);

                        PlaceDescription placeDesc = new PlaceDescription(res.getString(0), res.getString(1),
                                res.getString(2), res.getString(3), res.getString(4),
                                res.getDouble(5), res.getDouble(6), res.getDouble(7));
                        android.util.Log.d(this.getClass().getSimpleName(),"Place Descript: "+placeDesc.toJSonString());
                        JsonRPCRequestViaHttp request = new JsonRPCRequestViaHttp(url, handler, "add",
                                "["+placeDesc.toJSonString()+"]",db, context);
                        request.start();
                    }
                }
                res = db.getAllData();
                while (res.moveToNext()){
                    dbList.add(res.getString(0));
                }
                for(String aStr: al){
                    if(!dbList.contains(aStr)){
                        String respGetData = post(url, headers, "{ \"jsonrpc\":\"2.0\", \"method\":\"get\", \"params\":["+aStr+
                                "],\"id\":3}");
                        JSONObject retrJ = new JSONObject(respGetData);
                        JSONObject  retrA = retrJ.getJSONObject("result");

                        android.util.Log.d(this.getClass().getSimpleName(),"Place Descript: "+ retrA.toString());
                        PlaceDescription place = new PlaceDescription(retrA.toString());
                        db.insertData(aStr, place.description, place.category, place.addressTitle, place.addressStreet, place.elevation, place.latitude, place.longitute);
                    }
                }
                String save = post(url, headers, "{ \"jsonrpc\":\"2.0\", \"method\":\"saveToJsonFile\", \"params\":[ ],\"id\":3}");
            }else if(method.equals("get")){
                //meh
            }else if(method.equals("add") || method.equals("remove")){
                String save = post(url, headers, "{ \"jsonrpc\":\"2.0\", \"method\":\"saveToJsonFile\", \"params\":[ ],\"id\":3}");
            }
        }catch (Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"Exception in JsonRPC request: "+ex.toString());
        }
    }

    private String post(URL url, Map<String, String> headers, String data) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.addRequestProperty("Accept-Encoding", "gzip");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.connect();
        OutputStream out = null;
        try {
            out = connection.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception(
                        "Unexpected status from post: " + statusCode);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
        String responseEncoding = connection.getHeaderField("Content-Encoding");
        responseEncoding = (responseEncoding == null ? "" : responseEncoding.trim());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream in = connection.getInputStream();
        try {
            in = connection.getInputStream();
            if ("gzip".equalsIgnoreCase(responseEncoding)) {
                in = new GZIPInputStream(in);
            }
            in = new BufferedInputStream(in);
            byte[] buff = new byte[1024];
            int n;
            while ((n = in.read(buff)) > 0) {
                bos.write(buff, 0, n);
            }
            bos.flush();
            bos.close();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        android.util.Log.d(this.getClass().getSimpleName(),"json rpc request via http returned string "+bos.toString());
        return bos.toString();
    }
}
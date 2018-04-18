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

import android.os.Handler;
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
    //private MainActivity parent;

    public JsonRPCRequestViaHttp(URL url, Handler handler, String method, String parmsArray){
        this.url =url;
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
                for(int i=0; i< ja.length(); i++){
                    al.add(ja.getString(i));
                }
                String[] arr = al.toArray(new String[0]);
                //handler.post(new StudentSpinnerUpdater(parent,arr, handler, url));
            }else if(method.equals("get")){
                JSONObject jo = new JSONObject(respData);
                //Student aStud = new Student(jo.getJSONObject("result"));
                //handler.post(new StudentInfoUpdater(parent,aStud));
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
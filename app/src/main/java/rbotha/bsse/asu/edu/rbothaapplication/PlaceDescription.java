package rbotha.bsse.asu.edu.rbothaapplication;

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
 * Purpose: Create json formatted place descriptions.
 * This class will be incrementally expanded upon and integrated into
 * SQL Lite database
 *
 * Ser423 Mobile Applications
 * see http://pooh.poly.asu.edu/Mobile for assignment details
 * @author Ruan Botha rbotha@asu.edu
 *         Software Engineering, BSSE Program
 * @version April 2018
 */

import org.json.JSONObject;

/**
 * Created by Ruan Botha on 03/20/2018
 */

public class PlaceDescription {
    public String name;
    public String description;
    public String category;
    public String addressTitle;
    public String addressStreet;
    public double elevation;
    public double latitude;
    public double longitute;

    PlaceDescription(String name, String description, String category, String addressTitle, String addressStreet,
                     double elevation, double latitude, double longitude){
        this.name = name;
        this.description = description;
        this.category = category;
        this.addressTitle = addressTitle;
        this.addressStreet = addressStreet;
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitute = longitude;
    }

    PlaceDescription(String jsonStr){
        try{
            JSONObject jo = new JSONObject(jsonStr);
            name = jo.getString("name");
            description = jo.getString("description");
            category = jo.getString("category");
            addressTitle = jo.getString("address-title");
            addressStreet = jo.getString("address-street");
            elevation = jo.getDouble("elevation");
            latitude = jo.getDouble("latitude");
            longitute = jo.getDouble("longitude");
        }catch (Exception e){
            android.util.Log.e(this.getClass().getSimpleName(), "Error converting to  and from json");
        }
    }

    public String toJSonString(){
        String result = "";
        try{
            JSONObject jo = new JSONObject();
            jo.put("name",name);
            jo.put("description", description);
            jo.put("category", category);
            jo.put("address-title", addressTitle);
            jo.put("address-street", addressStreet);
            jo.put("elevation", elevation);
            jo.put("latitude", latitude);
            jo.put("longitude", longitute);
            result = jo.toString();
        }catch(Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(), "error converting to a jsonString");
        }

        return result;
    }
}

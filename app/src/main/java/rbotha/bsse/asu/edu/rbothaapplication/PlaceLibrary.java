package rbotha.bsse.asu.edu.rbothaapplication;


import java.util.ArrayList;


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

public class PlaceLibrary {

    private ArrayList<PlaceDescription> places;

    PlaceLibrary(){
        setPlaces(new ArrayList<PlaceDescription>());
    }

    PlaceLibrary(ArrayList<PlaceDescription> p){
        setPlaces(p);
    }

    public void addPlace(PlaceDescription p){
        getPlaces().add(p);
    }

    public ArrayList<PlaceDescription> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<PlaceDescription> places) {
        this.places = places;
    }

    public void clear(){
        places.clear();
    }
}

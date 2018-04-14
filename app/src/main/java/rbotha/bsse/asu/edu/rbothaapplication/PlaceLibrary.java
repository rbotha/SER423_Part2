package rbotha.bsse.asu.edu.rbothaapplication;


import java.util.ArrayList;

/**
 * Created by ruanbotha on 4/9/18.
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

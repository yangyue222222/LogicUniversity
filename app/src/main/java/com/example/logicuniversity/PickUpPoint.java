package com.example.logicuniversity;

import java.util.HashMap;

public class PickUpPoint extends HashMap<String, String> {

    public PickUpPoint(String id, String time, String location){
        put("Id", id);
        put("PickUpTime", time);
        put("Location", location);
    }
}
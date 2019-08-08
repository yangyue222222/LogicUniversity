package com.example.logicuniversity;

import java.util.HashMap;

public class Requisition extends HashMap<String, String> {

    public Requisition(String id, String date, String requestor){
        put("id", id);
        put("date", date);
        put("requestor", requestor);
    }
}

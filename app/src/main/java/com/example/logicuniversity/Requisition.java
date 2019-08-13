package com.example.logicuniversity;

import java.util.HashMap;

public class Requisition extends HashMap<String, String> {

    public Requisition(String id, String date, String requestor){
        put("Id", id);
        put("Date", date);
        put("Requestor", requestor);
    }
}

package com.example.logicuniversity;

import java.util.HashMap;

public class RequisitionDetail extends HashMap<String, String> {

    public RequisitionDetail(String description, String qty){
        put("Description", description);
        put("Quantity", qty);
    }
}
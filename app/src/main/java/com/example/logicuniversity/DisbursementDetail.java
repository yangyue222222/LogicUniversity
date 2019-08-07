package com.example.logicuniversity;

import java.util.HashMap;
public class DisbursementDetail extends HashMap<String, String> {

    public DisbursementDetail(String ddid, String itemid, String description, String qty){
        put("DisbursementDetailId", ddid);
        put("ItemId", itemid);
        put("Description", description);
        put("Quantity", qty);
    }
}
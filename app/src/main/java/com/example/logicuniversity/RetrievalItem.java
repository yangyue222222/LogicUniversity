package com.example.logicuniversity;

import java.util.HashMap;

public class RetrievalItem extends HashMap<String, String> {

    public RetrievalItem(String id, String description, String qty, String stockqty){
        put("ItemId", id);
        put("StockQuantity",stockqty);
        put("Description", description);
        put("AllocatedQuantity", qty);
    }
}
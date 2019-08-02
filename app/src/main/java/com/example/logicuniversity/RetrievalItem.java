package com.example.logicuniversity;

import java.util.HashMap;

public class RetrievalItem extends HashMap<String, String> {

    public RetrievalItem(String id, String description, String qty){
        put("ItemId", id);
        put("Description", description);
        put("AllocatedQuantity", qty);
    }
}
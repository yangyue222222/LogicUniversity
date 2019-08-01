package com.example.logicuniversity;
import java.util.HashMap;
public class DisbursementDetail extends HashMap<String, String> {

    public DisbursementDetail(String Itemid, String description, String qty){
        put("ItemId", Itemid);
        put("Description", description);
        put("Qty", qty);
    }
}
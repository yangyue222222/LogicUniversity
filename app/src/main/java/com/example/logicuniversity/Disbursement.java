package com.example.logicuniversity;
import java.util.HashMap;
public class Disbursement extends HashMap<String, String>{

    public Disbursement(String id, String deptName, String representative){
        put("id", id);
        put("deptName", deptName);
        put("representative", representative);
    }
}
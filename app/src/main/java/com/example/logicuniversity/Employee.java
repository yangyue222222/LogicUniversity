package com.example.logicuniversity;

import java.util.HashMap;

public class Employee extends HashMap<String,String> {
    public Employee(String id, String name, String rank){
        put("Id", id);
        put("Name", name);
        put("Rank", rank);
    }
}
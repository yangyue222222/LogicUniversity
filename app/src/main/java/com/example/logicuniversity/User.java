package com.example.logicuniversity;

import java.util.HashMap;

public class User extends HashMap<String,String> {
    public User(String username, String password){
        put("Username", username);
        put("Password", password);
    }
}

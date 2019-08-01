package com.example.logicuniversity;

import org.json.JSONArray;
import org.json.JSONObject;

public class Command {
    protected AsyncToServer.IServerResponse callback;
    protected int checksum;
    protected String endPt;
    protected JSONArray data;

    Command(AsyncToServer.IServerResponse callback,
            int checksum, String endPt, JSONArray data)
    {
        this.callback = callback;
        this.checksum = checksum;
        this.endPt = endPt;
        this.data = data;
    }
}


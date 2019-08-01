package com.example.logicuniversity;


import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncToServer extends AsyncTask<Command, Void, JSONArray> {
    IServerResponse callback;

    protected JSONArray doInBackground(Command... cmds) {
        Command cmd = cmds[0];
        this.callback = cmd.callback;

        JSONArray jsonArr = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(cmd.endPt);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // send data
            if (cmd.data != null) {
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                DataOutputStream outstream = new DataOutputStream(conn.getOutputStream());
                outstream.writeBytes(cmd.data.toString());
                outstream.flush();
                outstream.close();
            }

            // receive response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null; ) {
                response.append(line).append('\n');
            }

            try {
                jsonArr = new JSONArray(response.toString());
                jsonArr.put(cmd.checksum);
                System.out.println(jsonArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArr;
    }

    protected void onPostExecute(JSONArray jsonArr) {
        if (jsonArr != null)
            this.callback.onServerResponse(jsonArr);
    }

    public interface IServerResponse {
        void onServerResponse(JSONArray jsonArr);
    }
}

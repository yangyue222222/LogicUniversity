package com.example.logicuniversity;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class AsyncLogin extends AsyncTask<LoginCommand, Void, JSONObject> {
    AsyncLogin.IServerResponse callback;

    protected JSONObject doInBackground(LoginCommand... lcmd) {
        LoginCommand lc = lcmd[0];
        this.callback = lc.callback;
        StringBuilder response = new StringBuilder();
        JSONObject jo = null;
        CookieManager cM = LoginActivity.cM;

        try {
            URL url = new URL(lc.endPt);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Cookie", TextUtils.join(";",  cM.getCookieStore().getCookies()));
            // send data
            if (lc.data != null) {
                JSONObject login = new JSONObject(lc.data);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                DataOutputStream outstream = new DataOutputStream(conn.getOutputStream());
                outstream.writeBytes(login.toString());

                outstream.flush();
                outstream.close();
            }

            // receive response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null; ) {
                response.append(line).append('\n');
            }

            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> cookiesHeader = headerFields.get("Set-Cookie");
            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cM.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            try {
                jo = new JSONObject(response.toString());

                System.out.println(jo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jo;
    }

    protected void onPostExecute(JSONObject jo) {
        if (jo != null)
            this.callback.onServerResponse(jo);
    }

    public interface IServerResponse {
        void onServerResponse(JSONObject jo);
    }
}

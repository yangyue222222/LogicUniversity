package com.example.logicuniversity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AsyncToServer.IServerResponse, AsyncLogin.IServerResponse {

    Button btnGet;
    Button btnSRF;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGet = findViewById(R.id.btnGet);
        btnGet.setOnClickListener(this);
        btnSRF = findViewById(R.id.btnSRF);
        btnSRF.setOnClickListener(this);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Command cmd;

        int id = v.getId();
        switch (id) {
            case R.id.btnGet:
                cmd = new Command(this, 0,
                        "http://10.0.2.2:50271/deliveriesmobile", null);
                new AsyncToServer().execute(cmd);
                break;
            case R.id.btnSRF:
                cmd = new Command(this, 4,
                        "http://10.0.2.2:50271/retrievalmobile", null);
                new AsyncToServer().execute(cmd);
                break;
            case R.id.logout:
                LoginCommand lc = new LoginCommand(this, "http://10.0.2.2:50271/logoutmobile", null);
                new AsyncLogin().execute(lc);
                break;
        }
    }
    @Override
    public void onServerResponse(JSONObject jo){
        if(jo == null)
            return;
        try {
            int status = jo.getInt("status");
            if(status == 1){
                Toast.makeText(this, "Logout successful", Toast.LENGTH_LONG).show();
                Intent dl = new Intent(this, LoginActivity.class);
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onServerResponse(JSONArray jsonArr) {
        if (jsonArr == null)
            return;
        try {
            int checksum = jsonArr.getInt(jsonArr.length()-1);
            if(checksum == 0){

                Intent dl = new Intent(this, DisburseList.class);
                dl.putExtra("disbursejson", jsonArr.toString());

                startActivity(dl);
            }
            if(checksum == 4){

                Intent dl = new Intent(this, StationeryRetrievalForm.class);
                dl.putExtra("srf", jsonArr.toString());

                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
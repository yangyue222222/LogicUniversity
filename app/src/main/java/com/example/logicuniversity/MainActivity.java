package com.example.logicuniversity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AsyncToServer.IServerResponse {

    Button btnGet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGet = findViewById(R.id.btnGet);
        btnGet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Command cmd;

        int id = v.getId();
        switch (id) {
            case R.id.btnGet:
                cmd = new Command(this, 0,
                        "http://10.0.2.2:50742/Disbursement/GetDisbursements", null);
                new AsyncToServer().execute(cmd);
                break;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
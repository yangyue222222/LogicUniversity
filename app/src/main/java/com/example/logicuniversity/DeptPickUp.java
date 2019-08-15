package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeptPickUp extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncToServer.IServerResponse  {

    JSONArray jsonArr;
    String reqId;
    String currentLocation;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        List<PickUpPoint> plist = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        try {
            jsonArr = new JSONArray(extras.getString("points"));
            String id;
            String time;
            String location;

            for (int i = 0; i < jsonArr.length()-2; i++) {
                JSONObject jsonobject = jsonArr.getJSONObject(i);
                id = jsonobject.getString("PickUpPointId");
                time = jsonobject.getString("PickUpTime");
                location = jsonobject.getString("Location");

                PickUpPoint p = new PickUpPoint(id, time, location);
                plist.add(p);
            }
            JSONObject curLoc = jsonArr.getJSONObject(jsonArr.length()-2);
            currentLocation = curLoc.getString("Location") + ",  " + curLoc.getString("PickUpTime");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        ListView listView = findViewById(R.id.listView1);
        listView.setAdapter(new SimpleAdapter(this, plist,R.layout.puprow,
                new String[] {"Location","PickUpTime"},
                new int[]{R.id.rowpt1,R.id.rowpt2}));
        listView.setOnItemClickListener(this);
        TextView curLoc = findViewById(R.id.curLoc);
        curLoc.setText("Current Pick Up Point: " + currentLocation);
        Button back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id){
        PickUpPoint p = (PickUpPoint) av.getItemAtPosition(pos);
        reqId = p.get("Id");

        Command cmd = new Command(this, 9,
                "http://10.0.2.2:50271/PickUpPointMobile/"+p.get("Id"), null);
        System.out.println(cmd.endPt);
        new AsyncToServer().execute(cmd);
    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.back:
                Intent dl = new Intent(this, DeptActivity.class);
                startActivity(dl);
                break;
        }
    }
    @Override
    public void onServerResponse(JSONArray jsonArr) {
        if (jsonArr == null)
            return;
        try {
            int checksum = jsonArr.getInt(jsonArr.length()-1);
            if(checksum == 9){
                Toast.makeText(this, "Pick Up Point updated", Toast.LENGTH_LONG).show();
                Command cmd = new Command(this, 8,
                        "http://10.0.2.2:50271/pickupmobile", null);
                new AsyncToServer().execute(cmd);
            }
            if(checksum == 8){
                Intent dl = new Intent(this, DeptPickUp.class);
                dl.putExtra("points", jsonArr.toString());
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
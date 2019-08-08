package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeptApprovalsList extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncToServer.IServerResponse  {

    JSONArray jsonArr;
    String reqId;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disburselist);
        List<Requisition> rlist = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        try {
            jsonArr = new JSONArray(extras.getString("requisitions"));
            String id;
            String date;
            String requestor;

            for (int i = 0; i < jsonArr.length()-1; i++) {
                JSONObject jsonobject = jsonArr.getJSONObject(i);
                id = jsonobject.getString("RequestId");
                date = jsonobject.getString("Date");
                date = date.replace("/Date(", "").replace(")/", "");
                long time = Long.parseLong(date);
                Date d = new Date(time);

                requestor = jsonobject.getJSONObject("Requestor").getString("Name");
                Requisition r = new Requisition(id, new SimpleDateFormat("dd/MM/yyyy").format(d), requestor);
                rlist.add(r);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        ListView listView = findViewById(R.id.listView1);
        listView.setAdapter(new SimpleAdapter(this, rlist,R.layout.disburserow,
                new String[] {"id","date","requestor"},
                new int[]{R.id.rowpt1,R.id.rowpt2,R.id.rowpt3}));
        listView.setOnItemClickListener(this);
        Button back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id){
        Requisition r = (Requisition) av.getItemAtPosition(pos);
        reqId = r.get("id");
        Command cmd = new Command(this, 9,
                "http://10.0.2.2:50271/Requisitions/"+r.get("id"), null);
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
                Intent dl = new Intent(this, DeptApproveRequest.class);
                dl.putExtra("requisition", jsonArr.toString());
                dl.putExtra("id", reqId);
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
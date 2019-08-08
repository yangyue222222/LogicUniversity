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

import java.util.ArrayList;
import java.util.List;

public class DisburseList extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener  {

    JSONArray jsonArr;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disburselist);
        List<Disbursement> dl = new ArrayList<Disbursement>();
        Bundle extras = getIntent().getExtras();
        try {
            jsonArr = new JSONArray(extras.getString("disbursejson"));
            String id;
            String deptName;
            String representative;

            for (int i = 0; i < jsonArr.length()-1; i++) {
                JSONObject jsonobject = jsonArr.getJSONObject(i);
                id = jsonobject.getString("DisbursementId");
                deptName = jsonobject.getString("DepartmentName");
                representative = jsonobject.getString("Representative");
                Disbursement d = new Disbursement(id, deptName, representative);
                dl.add(d);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        ListView listView = findViewById(R.id.listView1);
        listView.setAdapter(new SimpleAdapter(this, dl,R.layout.disburserow,
                new String[] {"id","deptName","representative"},
                new int[]{R.id.rowpt1,R.id.rowpt2,R.id.rowpt3}));
        listView.setOnItemClickListener(this);
        Button back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id){
        Disbursement d = (Disbursement) av.getItemAtPosition(pos);
        Intent dl = new Intent(this, DisburseOrder.class);
        dl.putExtra("disbursejson", jsonArr.toString());
        dl.putExtra("id", d.get("id"));
        startActivity(dl);
    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.back:
                    Intent dl = new Intent(this, MainActivity.class);
                    startActivity(dl);
                    break;
        }
    }
}
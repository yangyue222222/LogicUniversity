package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class DeptDelegation extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncToServer.IServerResponse  {

    JSONArray jsonArr;
    String reqId;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disburselist);
        List<Employee> elist = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        try {
            jsonArr = new JSONArray(extras.getString("employees"));
            String id;
            String name;
            String rank;

            for (int i = 0; i < jsonArr.length()-1; i++) {
                JSONObject jsonobject = jsonArr.getJSONObject(i);
                id = jsonobject.getString("UserId");
                name = jsonobject.getString("Name");
                rank = jsonobject.getString("Rank");
                int r = parseInt(rank);
                switch(r) {
                    case 1:
                        rank = "Employee";
                        break;
                    case 5:
                        rank = "TemporaryHead";
                        break;
                }
                Employee e = new Employee(id, name, rank);
                if(rank.compareTo("0") != 0) {
                    elist.add(e);
                }
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this, "You don't have the authority to access this function", Toast.LENGTH_LONG).show();
        }
        ListView listView = findViewById(R.id.listView1);
        listView.setAdapter(new SimpleAdapter(this, elist,R.layout.disburserow,
                new String[] {"Id","Name","Rank"},
                new int[]{R.id.rowpt1,R.id.rowpt2,R.id.rowpt3}));
        listView.setOnItemClickListener(this);
        Button back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id){
        Employee e = (Employee) av.getItemAtPosition(pos);
        reqId = e.get("Id");
        String option = "2";
        if(e.get("Rank").compareTo("Employee") == 0) {
            option = "1";
        }
        Command cmd = new Command(this, 9,
                "http://10.0.2.2:50271/DelegateAuthMobile/"+e.get("Id")+"/"+option, null);
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
                Toast.makeText(this, "Assignment updated", Toast.LENGTH_LONG).show();
                Command cmd = new Command(this, 8,
                        "http://10.0.2.2:50271/Delegate/DelegationMobile", null);
                new AsyncToServer().execute(cmd);
            }
            if(checksum == 8){
                Intent dl = new Intent(this, DeptDelegation.class);
                dl.putExtra("employees", jsonArr.toString());
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeptConfirmDelivery extends AppCompatActivity implements AsyncToServer.IServerResponse, View.OnClickListener  {
    List<DisbursementDetail> dl = new ArrayList<>();
    String disburseId;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disburseorder);

        Bundle extras = getIntent().getExtras();
        Button confirmDisburse = findViewById(R.id.confirm);
        confirmDisburse.setOnClickListener(this);
        disburseId = extras.getString("id");
        try {
            JSONArray jsonArr = new JSONArray(extras.getString("confirmations"));
            String qty;
            String description;
            String itemId;
            String ddid;
            for (int i = 0; i < jsonArr.length()-1; i++) {
                JSONObject disbursement = jsonArr.getJSONObject(i);
                if(disbursement.getString("DisbursementId").compareTo(disburseId) == 0) {
                    System.out.println(disbursement);
                    JSONArray disDetails = disbursement.getJSONArray("DisbursementDetails");
                    for(int j = 0; j < disDetails.length(); j++) {
                        JSONObject singleDetail = disDetails.getJSONObject(j);
                        System.out.println(singleDetail);
                        ddid = singleDetail.getString("DisbursementDetailId");
                        qty = singleDetail.getString("Quantity");
                        description = singleDetail.getString("ItemName");
                        itemId = singleDetail.getString("ItemId");
                        DisbursementDetail d = new DisbursementDetail(ddid, itemId, description, qty);
                        dl.add(d);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        LinearLayout orderDetails = findViewById(R.id.orderDetails);
        LinearLayout.LayoutParams big = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1F);
        LinearLayout.LayoutParams small = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,5F);

        LinearLayout header = new LinearLayout(this);
        header.setLayoutParams(big);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView des = new TextView(this);
        des.setText("Description");
        des.setLayoutParams(big);
        header.addView(des);

        TextView q = new TextView(this);
        q.setText("Qty");
        q.setLayoutParams(small);
        header.addView(q);
        orderDetails.addView(header);

        for(DisbursementDetail dd : dl){
            LinearLayout order = new LinearLayout(this);
            order.setLayoutParams(small);
            order.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv1 = new TextView(this);
            tv1.setText(dd.get("Description"));
            tv1.setLayoutParams(big);
            order.addView(tv1);
            TextView tv2 = new TextView(this);
            tv2.setText(dd.get("Quantity"));
            tv2.setLayoutParams(small);
            order.addView(tv2);
            orderDetails.addView(order);
        }


    }
    @Override
    public void onServerResponse(JSONArray jsonArr) {
        if (jsonArr == null)
            return;
        try {
            int checksum = jsonArr.getInt(jsonArr.length()-1);
            if(checksum == 2){
                Toast.makeText(this,"Receipt confirmed", Toast.LENGTH_LONG).show();
                Command cmd = new Command(this, 4,"http://10.0.2.2:50271/delivereddisbursementsmobile", null);
                new AsyncToServer().execute(cmd);
            }
            if(checksum == 4){
                Intent dl = new Intent(this, DeptConfirmationList.class);
                dl.putExtra("confirmations", jsonArr.toString());
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View v){
            Command cmd = new Command(this, 2,"http://10.0.2.2:50271/approvedisbursementsmobile/"+disburseId, null);
            new AsyncToServer().execute(cmd);
        }
    }

package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class DisburseOrder extends AppCompatActivity implements AsyncToServer.IServerResponse, View.OnClickListener  {
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
            JSONArray jsonArr = new JSONArray(extras.getString("disbursejson"));
            String qty;
            String description;
            String itemId;
            for (int i = 0; i < jsonArr.length()-1; i++) {
                JSONObject disbursement = jsonArr.getJSONObject(i);
                if(disbursement.getString("DisbursementId").compareTo(disburseId) == 0) {
                    System.out.println(disbursement);
                    JSONArray disDetails = disbursement.getJSONArray("DisbursementDetails");
                    for(int j = 0; j < disDetails.length(); j++) {
                        JSONObject singleDetail = disDetails.getJSONObject(j);
                        System.out.println(singleDetail);
                        qty = singleDetail.getString("Qty");
                        JSONObject item = singleDetail.getJSONObject("ItemId");
                        description = item.getString("Description");
                        itemId = item.getString("ItemId");
                        DisbursementDetail d = new DisbursementDetail(itemId, description, qty);
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
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,3F);

        for(DisbursementDetail dd : dl){
            LinearLayout order = new LinearLayout(this);
            order.setLayoutParams(p);
            order.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv1 = new TextView(this);
            tv1.setText(dd.get("Description"));
            tv1.setLayoutParams(p);
            order.addView(tv1);
            TextView tv2 = new TextView(this);
            tv2.setText(dd.get("Qty"));
            tv2.setLayoutParams(p);
            order.addView(tv2);
            EditText input = new EditText(this);
            input.setLayoutParams(p);
            input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setId(parseInt(dd.get("ItemId")));
            input.setText(dd.get("Qty"));
            order.addView(input);
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
                Toast.makeText(this,"Disbursement recorded", Toast.LENGTH_LONG).show();
                Command cmd = new Command(this, 0,"http://10.0.2.2:50742/Disbursement/GetDisbursements", null);
                new AsyncToServer().execute(cmd);
            }
            if(checksum == 0){
                Intent dl = new Intent(this, DisburseList.class);
                dl.putExtra("disbursejson", jsonArr.toString());

                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View v){
        int overdeliver = 0;
        List<DisbursementDetail> output = new ArrayList<>();
        for(DisbursementDetail dd : dl) {
            EditText receivedQty = findViewById(parseInt(dd.get("ItemId")));
            int rQty = parseInt(receivedQty.getText().toString());
            if(rQty > parseInt(dd.get("Qty"))){
                Toast.makeText(this,"Can't deliver more than the specified for item "+dd.get("Description"), Toast.LENGTH_LONG).show();
                overdeliver = 1;
                break;
            }
            dd.put("deliveredQty",String.valueOf(rQty));
            output.add(dd);
        }
        if(overdeliver == 0) {
            JSONArray confirmation = new JSONArray(output);
            confirmation.put(disburseId);
            System.out.println(confirmation.toString());
            Command cmd = new Command(this, 2,"http://10.0.2.2:50742/Disbursement/UpdateDisbursement", confirmation);
            new AsyncToServer().execute(cmd);
        }
    }
}
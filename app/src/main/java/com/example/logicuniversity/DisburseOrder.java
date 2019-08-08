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
        LinearLayout.LayoutParams small = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,3F);

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

        TextView ac = new TextView(this);
        ac.setText("Actual");
        ac.setLayoutParams(small);
        header.addView(ac);

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
            EditText input = new EditText(this);
            input.setLayoutParams(small);
            input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setId(parseInt(dd.get("ItemId")));
            input.setText(dd.get("Quantity"));
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
                Command cmd = new Command(this, 0,"http://10.0.2.2:50271/Disbursement/DeliveriesMobile", null);
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
            if(rQty > parseInt(dd.get("Quantity"))) {
                Toast.makeText(this, "Can't deliver more than the specified for item " + dd.get("Description"), Toast.LENGTH_LONG).show();
                overdeliver = 1;
                break;
            }
            dd.put("Quantity",String.valueOf(rQty));
            output.add(dd);
        }
        if(overdeliver == 0) {
            JSONArray confirmation = new JSONArray(output);
            Command cmd = new Command(this, 2,"http://10.0.2.2:50271/Disbursement/ReceivingMobile", confirmation);
            new AsyncToServer().execute(cmd);
        }
    }
}
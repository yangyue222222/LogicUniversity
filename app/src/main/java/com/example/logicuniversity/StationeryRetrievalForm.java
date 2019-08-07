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

public class StationeryRetrievalForm extends AppCompatActivity implements AsyncToServer.IServerResponse, View.OnClickListener  {
    List<RetrievalItem> srf = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srf);

        Bundle extras = getIntent().getExtras();
        Button confirmSRF = findViewById(R.id.confirmSRF);
        confirmSRF.setOnClickListener(this);
        Button cancelSRF = findViewById(R.id.cancelSRF);
        cancelSRF.setOnClickListener(this);

        try {
            JSONArray jsonArr = new JSONArray(extras.getString("srf"));
            String id = null;
            String description = null;
            String qty = null;
            String stockqty = null;

            for (int i = 0; i < jsonArr.length()-1; i++) {
                JSONObject jsonobject = jsonArr.getJSONObject(i);
                id = jsonobject.getString("ItemId");
                description = jsonobject.getString("Description");
                qty = jsonobject.getString("AllocatedQuantity");
                stockqty = jsonobject.getString("StockQuantity");
                RetrievalItem d = new RetrievalItem(id, description, qty, stockqty);
                srf.add(d);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        LinearLayout srfList = findViewById(R.id.srfList);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,3F);

        for(RetrievalItem i : srf){
            LinearLayout order = new LinearLayout(this);
            order.setLayoutParams(p);
            order.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv1 = new TextView(this);
            tv1.setText(i.get("Description"));
            tv1.setLayoutParams(p);
            order.addView(tv1);

            TextView tv2 = new TextView(this);
            tv2.setText(i.get("StockQuantity"));
            tv2.setLayoutParams(p);
            order.addView(tv2);

            TextView tv3 = new TextView(this);
            tv3.setText(i.get("AllocatedQuantity"));
            tv3.setLayoutParams(p);
            order.addView(tv3);

            EditText input = new EditText(this);
            input.setLayoutParams(p);
            input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setId(parseInt(i.get("ItemId")));
            input.setText(i.get("AllocatedQuantity"));

            order.addView(input);
            srfList.addView(order);
        }


    }
    @Override
    public void onServerResponse(JSONArray jsonArr) {
        if (jsonArr == null)
            return;
        try {
            int checksum = jsonArr.getInt(jsonArr.length()-1);
            if(checksum == 3){
                Toast.makeText(this,"Retrieval done and disbursements updated", Toast.LENGTH_LONG).show();
                Intent dl = new Intent(this, MainActivity.class);
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.confirmSRF) {
            int overdrawn = 0;
            List<RetrievalItem> output = new ArrayList<>();
            for (RetrievalItem dd : srf) {
                EditText receivedQty = findViewById(parseInt(dd.get("ItemId")));
                int rQty = parseInt(receivedQty.getText().toString());
                if (rQty > parseInt(dd.get("AllocatedQuantity"))) {
                    Toast.makeText(this, "Overdrawing item: "+ dd.get("Description"), Toast.LENGTH_LONG).show();
                    overdrawn = 1;
                    break;
                }
                dd.put("ActualQuantity", String.valueOf(rQty));
                output.add(dd);
            }
            if (overdrawn == 0) {
                JSONArray confirmation = new JSONArray(output);
                Command cmd = new Command(this, 3, "http://10.0.2.2:50271/Disbursement/DisbursementsMobile", confirmation);
                new AsyncToServer().execute(cmd);
            }

        }
        if(id == R.id.cancelSRF)
        {
            Intent dl = new Intent(this, MainActivity.class);
            startActivity(dl);
        }
    }
}
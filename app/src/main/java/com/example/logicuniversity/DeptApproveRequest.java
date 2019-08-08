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

public class DeptApproveRequest extends AppCompatActivity implements AsyncToServer.IServerResponse, View.OnClickListener {
    List<RequisitionDetail> rl = new ArrayList<>();
    String reqList;
    String reqId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approverequest);

        Bundle extras = getIntent().getExtras();
        Button approve = findViewById(R.id.approveRequest);
        Button reject = findViewById(R.id.rejectRequest);
        approve.setOnClickListener(this);
        reject.setOnClickListener(this);
        reqList = extras.getString("requisition");
        reqId = extras.getString("id");
        try {
            JSONArray jsonArr = new JSONArray(reqList);
            String qty;
            String description;
            for (int i = 0; i < jsonArr.length() - 1; i++) {
                JSONObject singleDetail = jsonArr.getJSONObject(i);
                System.out.println(singleDetail);
                qty = singleDetail.getString("Quantity");
                description = singleDetail.getJSONObject("Item").getString("Description");
                RequisitionDetail rd = new RequisitionDetail(description, qty);
                rl.add(rd);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LinearLayout reqDetails = findViewById(R.id.reqDetails);
        LinearLayout.LayoutParams big = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1F);
        LinearLayout.LayoutParams small = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4F);

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

        reqDetails.addView(header);

        for (RequisitionDetail dd : rl) {
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
            reqDetails.addView(order);
        }


    }

    @Override
    public void onServerResponse(JSONArray jsonArr) {
        if (jsonArr == null)
            return;
        try {
            int checksum = jsonArr.getInt(jsonArr.length() - 1);
            if (checksum == 0) {
                Toast.makeText(this, "Approval recorded", Toast.LENGTH_LONG).show();
                Command cmd = new Command(this, 2, "http://10.0.2.2:50271/Requisition/PendingMobile", null);
                new AsyncToServer().execute(cmd);
            }
            if (checksum == 1) {
                Toast.makeText(this, "Rejection recorded", Toast.LENGTH_LONG).show();
                Command cmd = new Command(this, 2, "http://10.0.2.2:50271/Requisition/PendingMobile", null);
                new AsyncToServer().execute(cmd);
            }
            if(checksum == 2){

                Intent dl = new Intent(this, DeptApprovalsList.class);
                dl.putExtra("requisitions", jsonArr.toString());
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Command cmd;

        int id = v.getId();
        switch (id) {
            case R.id.approveRequest:
                cmd = new Command(this, 0,
                        "http://10.0.2.2:50271/Requisitions/"+reqId+"/ACCEPT", null);
                System.out.println(cmd.endPt);
                new AsyncToServer().execute(cmd);
                break;
            case R.id.rejectRequest:
                cmd = new Command(this, 1,
                        "http://10.0.2.2:50271/Requisitions/"+reqId+"/REJECT", null);
                System.out.println(cmd.endPt);
                new AsyncToServer().execute(cmd);
                break;
        }
    }
}

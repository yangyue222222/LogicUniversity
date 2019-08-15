package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class DeptActivity extends AppCompatActivity
        implements View.OnClickListener, AsyncToServer.IServerResponse, AsyncLogin.IServerResponse {

    Button approvals;
    Button delegate;
    Button pup;
    Button countersign;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept);

        approvals = findViewById(R.id.approvals);
        approvals.setOnClickListener(this);
        delegate = findViewById(R.id.delegate);
        delegate.setOnClickListener(this);
        pup = findViewById(R.id.pup);
        pup.setOnClickListener(this);
        countersign = findViewById(R.id.countersign);
        countersign.setOnClickListener(this);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Command cmd;

        int id = v.getId();
        switch (id) {
            case R.id.approvals:
                cmd = new Command(this, 0,
                        "http://10.0.2.2:50271/pendingreqmobile", null);
                new AsyncToServer().execute(cmd);
                break;
            case R.id.delegate:
                cmd = new Command(this, 8,
                        "http://10.0.2.2:50271/delegatemobile", null);
                new AsyncToServer().execute(cmd);
                break;
            case R.id.pup:
                cmd = new Command(this, 6,
                        "http://10.0.2.2:50271/pickupmobile", null);
                new AsyncToServer().execute(cmd);
                break;
            case R.id.countersign:
                cmd = new Command(this, 4,
                        "http://10.0.2.2:50271/delivereddisbursementsmobile", null);
                new AsyncToServer().execute(cmd);
                break;
            case R.id.logout:
                LoginCommand lc = new LoginCommand(this, "http://10.0.2.2:50271/logoutmobile", null);
                new AsyncLogin().execute(lc);
                break;
        }
    }
    @Override
    public void onServerResponse(JSONObject jo){
        if(jo == null)
            return;
        try {
            int status = jo.getInt("status");
            if(status == 1){
                Toast.makeText(this, "Logout successful", Toast.LENGTH_LONG).show();
                Intent dl = new Intent(this, LoginActivity.class);
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onServerResponse(JSONArray jsonArr) {
        if (jsonArr == null)
            return;
        try {
            int checksum = jsonArr.getInt(jsonArr.length()-1);
            if(checksum == 0){
                if(jsonArr.length() == 1)
                {
                    Toast.makeText(this, "No requisitions pending your review.", Toast.LENGTH_LONG).show();
                }else {
                Intent dl = new Intent(this, DeptApprovalsList.class);
                dl.putExtra("requisitions", jsonArr.toString());

                startActivity(dl);
                }
            }
            if(checksum == 4){

                    Intent dl = new Intent(this, DeptConfirmationList.class);
                    dl.putExtra("confirmations", jsonArr.toString());

                    startActivity(dl);
            }
            if(checksum == 6){
                if(jsonArr.length() == 1)
                {
                    Toast.makeText(this, "You don't have permission to access this.", Toast.LENGTH_LONG).show();
                }else {
                    Intent dl = new Intent(this, DeptPickUp.class);
                    dl.putExtra("points", jsonArr.toString());
                    startActivity(dl);
                }
            }
            if(checksum == 8){
                if(jsonArr.length() == 1)
                {
                    Toast.makeText(this, "You don't have permission to access this.", Toast.LENGTH_LONG).show();
                }else {
                    Intent dl = new Intent(this, DeptDelegation.class);
                    dl.putExtra("employees", jsonArr.toString());
                    startActivity(dl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
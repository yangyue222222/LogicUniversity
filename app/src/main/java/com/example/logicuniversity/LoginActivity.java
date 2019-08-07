package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AsyncLogin.IServerResponse {

    EditText username;
    EditText password;
    Button login;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.un);
        password = findViewById(R.id.pw);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        String name = username.getText().toString();
        String pass = password.getText().toString();
        User login = new User(name,pass);
        LoginCommand lc = new LoginCommand(this, "http://10.0.2.2:50271/Auth/LoginMobile", login);
        new AsyncLogin().execute(lc);

    }
    @Override
    public void onServerResponse(JSONObject jo){
        if(jo == null)
            return;
        try {
            int rank = jo.getInt("Rank");
            if(rank == 4) {

                Intent dl = new Intent(this, MainActivity.class);
                startActivity(dl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
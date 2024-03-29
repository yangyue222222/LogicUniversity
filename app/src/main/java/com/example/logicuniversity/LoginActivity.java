package com.example.logicuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.CookieManager;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AsyncLogin.IServerResponse {
    static CookieManager cM = new CookieManager();
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
        LoginCommand lc = new LoginCommand(this, "http://10.0.2.2:50271/loginmobile", login);
        new AsyncLogin().execute(lc);

    }
    @Override
    public void onServerResponse(JSONObject jo){
        if(jo == null)
            return;
        try {
            int rank = jo.getInt("Rank");
            Intent dl;
            switch(rank){
                case 4:
                dl = new Intent(this, MainActivity.class);
                startActivity(dl);
                break;
                case 0:
                case 1:
                case 5:
                dl = new Intent(this, DeptActivity.class);
                startActivity(dl);
                break;
                case 2:
                case 3:
                Toast.makeText(this, "Only store clerks can use this mobile application", Toast.LENGTH_LONG).show();
                break;
                case 99:
                default:
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.sspdim;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserRegistration extends AppCompatActivity implements View.OnClickListener {

    private TextView register;
    private EditText password;
    private EditText username;
    private String uname;
    private String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
    }


    public void postRequest(){

        //URL to POST to
        String query_url = "https://capstone.devmashru.tech/login";

        //Extract data from the Edit Text boxes
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        pass = password.getText().toString();
        uname = username.getText().toString();
        Log.d("CREATION",pass);


        //JSON Object Creation
        final JSONObject root = new JSONObject();
        try {
            root.put("username",uname);
            root.put("password",pass);
        }
        catch (Exception e){
            System.out.println(e);
        }

        String json = root.toString();
        Log.d("CREATION",json);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        // Sending the JSON object
        try {
            URL url = new URL("https://capstone.devmashru.tech/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            System.out.println("Before the output stream...");
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            System.out.println("After the output stream...");

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.d("CREATION",response.toString());
            }


        } catch (Exception e) {
            System.out.println(e);
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerButton:
                postRequest();
                break;


        }
    }
}
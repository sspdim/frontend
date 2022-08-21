package com.example.sspdim;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserRegistration extends AppCompatActivity implements View.OnClickListener {

    private Button registerUser;
    private EditText password;
    private EditText username;
    private EditText confirmPass;
    private String uname;
    private String pass;
    private String pass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        registerUser = (Button) findViewById(R.id.registerButton);
        registerUser.setOnClickListener(this);
    }

    public boolean validateUsername(){

        username = (EditText) findViewById(R.id.username);
        uname = username.getText().toString();


        if (uname.length()==0){
            username.setError("Field cant be empty");
            return false;


        }else {
            username.setError(null);
            return true;
        }
    }

    public boolean validatePassword(){
        password = (EditText) findViewById(R.id.password);
        confirmPass = (EditText) findViewById(R.id.confirmPassword);

        pass = password.getText().toString();
        pass2 = confirmPass.getText().toString();

        /*
        Password must contain at least one digit [0-9].
        Password must contain at least one lowercase Latin character [a-z].
        Password must contain at least one uppercase Latin character [A-Z].
        Password must contain at least one special character like ! @ # & ( ).
        Password must contain a length of at least 8 characters and a maximum of 35 characters.
        */

        String passRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";

        System.out.println("Before setError...");
        if (pass.length()==0){
            password.setError("Password can't be empty");
            return false;
        }
        else if (!pass.matches(passRegex)){
            password.setError("Password is too weak");
            return false;
        }
        else if (pass.length()>=35){
            password.setError("Password is too long");
            return false;
        }

        else if (!pass.equals(pass2)){
            password.setError("Passwords don't match");
            return false;
        }



        else{
            password.setError(null);
            return true;
        }

    }


/*Need to update post json request for the registration link*/
    public void postRequest() {

        //URL to POST to
        String query_url = "https://capstone.devmashru.tech/register";

        //Extract data from the Edit Text boxes
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        pass = password.getText().toString();
        uname = username.getText().toString();
        Log.d("CREATION", pass);


        //JSON Object Creation
        final JSONObject root = new JSONObject();
        try {
            root.put("username", uname);
            root.put("password", pass);
        } catch (Exception e) {
            System.out.println(e);
        }

        String json = root.toString();
        Log.d("CREATION", json);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("https://capstone.devmashru.tech/register");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);
                            System.out.println("Before the output stream...");
                            try (OutputStream os = conn.getOutputStream()) {
                                byte[] input = json.getBytes("utf-8");
                                os.write(input, 0, input.length);
                            }
                            System.out.println("After the output stream...");

                            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                                StringBuilder response = new StringBuilder();
                                String responseLine = null;
                                while ((responseLine = br.readLine()) != null) {
                                    response.append(responseLine.trim());
                                }
                                Log.d("CREATION", response.toString());
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                        }


                    }
                }
        );
        thread.start();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.registerButton:
                if (validateUsername() && validatePassword() ) {
                    postRequest();
                }

                break;


        }
    }
}
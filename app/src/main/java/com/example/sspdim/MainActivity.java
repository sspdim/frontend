package com.example.sspdim;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.w3c.dom.Text;
import javax.net.ssl.HttpsURLConnection;






public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView register;
    private Button login;
    private EditText password;
    private EditText username;
    private String uname;
    private String pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView)findViewById(R.id.registerLink);
        register.setOnClickListener(this);
        login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(this);


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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

/*
    private boolean validateLogin(){
        pass = password.getText().toString();
        uname = username.getText().toString();

        if (uname.isEmpty()){
            Context context = getApplicationContext();
            CharSequence text = "Username can't be empty";
            int duration  = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context,text,duration);
            toast.show();

        }
        return false;
    }

    private boolean validateSignup(){
        return false;
    }
*/
    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.registerLink:
                startActivity(new Intent(this, UserRegistration.class));
                break;
            case R.id.loginButton:

                postRequest();
                break;
            case R.id.registerButton:
                break;


        }
    }



}
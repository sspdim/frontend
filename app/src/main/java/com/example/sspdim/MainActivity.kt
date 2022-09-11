package com.example.sspdim

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.EditText
import android.os.Bundle
import kotlin.Throws
import org.json.JSONObject
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var register: TextView? = null
    private var login: Button? = null
    private var password: EditText? = null
    private var username: EditText? = null
    private var uname: String? = null
    private var pass: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register = findViewById<View>(R.id.registerLink) as TextView
        register!!.setOnClickListener(this)
        login = findViewById<View>(R.id.loginButton) as Button
        login!!.setOnClickListener(this)
    }

    @Throws(InterruptedException::class)
    fun postRequest(): JSONObject {

        //URL to POST to
        val query_url = "https://capstone.devmashru.tech/login"

        //Extract data from the Edit Text boxes
        password = findViewById<View>(R.id.password) as EditText
        username = findViewById<View>(R.id.username) as EditText
        pass = password!!.text.toString()
        uname = username!!.text.toString()
        Log.d("CREATION", pass!!)


        //JSON Object Creation
        val root = JSONObject()
        try {
            root.put("username", uname)
            root.put("password", pass)
        } catch (e: Exception) {
            println(e)
        }
        val json = root.toString()
        Log.d("CREATION", json)
        val res = arrayOf(JSONObject())

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        val thread = Thread {
            try {
                val url = URL("https://capstone.devmashru.tech/login")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                println("Before the output stream...")
                conn.outputStream.use { os ->
                    val input = json.toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                }
                println("After the output stream...")
                BufferedReader(InputStreamReader(conn.inputStream, "utf-8")).use { br ->
                    val response = StringBuilder()
                    var responseLine: String? = null
                    while (br.readLine().also { responseLine = it } != null) {
                        response.append(responseLine!!.trim { it <= ' ' })
                    }
                    Log.d("CREATION", response.toString())
                    res[0] = JSONObject(response.toString())
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        thread.start()
        thread.join()
        return res[0]
    }

    fun validateUsername(): Boolean {
        username = findViewById<View>(R.id.username) as EditText
        uname = username!!.text.toString()

        //TextInputLayout trial = (TextInputLayout) findViewById(R.id.trial);
        //String val = trial.getEditText().getText().toString();
        return if (uname!!.length == 0) {
            username!!.error = "Field cant be empty"
            //trial.setError("Field cant be empty");
            false
        } else {
            username!!.error = null
            //trial.setError(null);
            true
        }
    }

    fun validatePassword(): Boolean {
        password = findViewById<View>(R.id.password) as EditText
        pass = password!!.text.toString()

        //TextInputLayout trial = (TextInputLayout) findViewById(R.id.trial);
        //String val = trial.getEditText().getText().toString();
        println("Before setError...")
        return if (pass!!.isEmpty()) {
            password!!.error = "Field can't be empty."
            //trial.setError("Field cant be empty");
            false
        } else {
            password!!.error = null
            //trial.setError(null);
            true
        }
    }

    override fun onClick(view: View) {
        var response = JSONObject()
        when (view.id) {
            R.id.registerLink -> startActivity(Intent(this, UserRegistration::class.java))
            R.id.loginButton -> {
                if (validateUsername() && validatePassword()) {
                    try {
                        response = postRequest()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                try {
                    if (response.getInt("status") == 200) {
                        startActivity(Intent(this, ChatInterface::class.java))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            R.id.registerButton -> {}
        }
    }

    override fun onStart() {
        super.onStart()
        val usernameField = findViewById<EditText>(R.id.username)
        val passwordField = findViewById<EditText>(R.id.password)
        usernameField.setText("")
        passwordField.setText("")
    }
}
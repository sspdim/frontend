package com.example.sspdim

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import kotlin.Throws
import org.json.JSONObject
import android.widget.Toast
import org.json.JSONException
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class UserRegistration : AppCompatActivity(), View.OnClickListener {
    private var registerUser: Button? = null
    private var password: EditText? = null
    private var username: EditText? = null
    private var confirmPass: EditText? = null
    private var uname: String? = null
    private var pass: String? = null
    private var pass2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration)
        registerUser = findViewById<View>(R.id.registerButton) as Button
        registerUser!!.setOnClickListener(this)
    }

    fun validateUsername(): Boolean {
        username = findViewById<View>(R.id.username) as EditText
        uname = username!!.text.toString()
        return if (uname!!.isEmpty()) {
            username!!.error = "Field cant be empty"
            false
        } else {
            username!!.error = null
            true
        }
    }

    fun validatePassword(): Boolean {
        password = findViewById<View>(R.id.password) as EditText
        confirmPass = findViewById<View>(R.id.confirmPassword) as EditText
        pass = password!!.text.toString()
        pass2 = confirmPass!!.text.toString()

        /*
        Password must contain at least one digit [0-9].
        Password must contain at least one lowercase Latin character [a-z].
        Password must contain at least one uppercase Latin character [A-Z].
        Password must contain at least one special character like ! @ # & ( ).
        Password must contain a length of at least 8 characters and a maximum of 35 characters.
        */
        val passRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$")
        println("Before setError...")
        return if (pass!!.isEmpty()) {
            password!!.error = "Password can't be empty"
            false
        } else if (!pass!!.matches(passRegex)) {
            password!!.error = "Password is too weak"
            false
        } else if (pass!!.length >= 35) {
            password!!.error = "Password is too long"
            false
        } else if (pass != pass2) {
            password!!.error = "Passwords don't match"
            false
        } else {
            password!!.error = null
            true
        }
    }

    /*Need to update post json request for the registration link*/
    @Throws(InterruptedException::class)
    fun postRequest(): JSONObject {

        //URL to POST to
        val query_url = "https://capstone.devmashru.tech/register"

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
                val url = URL("https://capstone.devmashru.tech/register")
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
                    runOnUiThread {
                        try {
                            Toast.makeText(
                                applicationContext,
                                res[0].getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: JSONException) {
                            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                        }
                    }
                    Log.d("UserRegistration", res[0].getString("message"))
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        thread.start()
        thread.join()
        return res[0]
    }

    override fun onClick(view: View) {
        var response = JSONObject()
        when (view.id) {
            R.id.registerButton -> if (validateUsername() && validatePassword()) {
                try {
                    response = postRequest()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
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

    override fun onStart() {
        super.onStart()
        val usernameField = findViewById<EditText>(R.id.username)
        val passwordField = findViewById<EditText>(R.id.password)
        val confirmPasswordField = findViewById<EditText>(R.id.confirmPassword)
        usernameField.setText("")
        passwordField.setText("")
        confirmPasswordField.setText("")
    }
}
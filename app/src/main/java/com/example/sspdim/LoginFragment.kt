package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sspdim.databinding.FragmentLoginBinding
import com.google.android.material.button.MaterialButton
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class LoginFragment(): Fragment() {
    private lateinit var register: TextView
    private var login: Button? = null
    private lateinit var password: EditText
    private lateinit var username: EditText
    private lateinit var uname: String
    private lateinit var pass: String

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        register = binding.registerLink
        register.setOnClickListener {
            onClickRegister()
        }
        login = binding.loginButton
        (login as MaterialButton).setOnClickListener {
            onClickLogin()
        }
        username = binding.username
        password = binding.password
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Throws(InterruptedException::class)
    fun postRequest(): JSONObject {

        val query_url = "https://capstone.devmashru.tech/login"

        password = binding.password
        username = binding.username
        pass = password.text.toString()
        uname = username.text.toString()
        Log.d("CREATION", pass)

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
                    Thread {
                        try {
                            Toast.makeText(
                                requireContext(),
                                res[0].getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: JSONException) {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                        }
                    }
                    Log.d("MainActivity", res[0].getString("message"))
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
        username = binding.username
        uname = username.text.toString()

        return if (uname.length == 0) {
            username.error = "Field cant be empty"
            false
        } else {
            username.error = null
            true
        }
    }

    fun validatePassword(): Boolean {
        password = binding.password
        pass = password.text.toString()

        println("Before setError...")
        return if (pass.isEmpty()) {
            password.error = "Field can't be empty."
            false
        } else {
            password.error = null
            true
        }
    }

    private fun onClickRegister() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
    }

    private fun onClickLogin() {
        var response = JSONObject()
        if (validateUsername() && validatePassword()) {
            try {
                response = postRequest()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        try {
            if (response.getInt("status") == 200) {
                startActivity(Intent(requireContext(), ChatInterface::class.java))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        username.setText("")
        password.setText("")
    }
}
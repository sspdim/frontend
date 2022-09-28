package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sspdim.databinding.FragmentLoginBinding
import com.example.sspdim.databinding.FragmentRegisterBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class RegisterFragment(): Fragment() {
    private lateinit var registerUser: Button
    private lateinit var password: EditText
    private lateinit var username: EditText
    private lateinit var confirmPass: EditText
    private lateinit var uname: String
    private lateinit var pass: String
    private lateinit var pass2: String

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerUser = binding.registerButton
        registerUser.setOnClickListener {
            onSubmit()
        }
        username = binding.username
        password = binding.password
        confirmPass = binding.confirmPassword
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateUsername(): Boolean {
        username = binding.username
        uname = username!!.text.toString()
        return if (uname!!.isEmpty()) {
            username!!.error = "Field cant be empty"
            false
        } else {
            username!!.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        password = binding.password
        confirmPass = binding.confirmPassword
        pass = password!!.text.toString()
        pass2 = confirmPass!!.text.toString()

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

    @Throws(InterruptedException::class)
    fun postRequest(): JSONObject {

        val query_url = "https://capstone.devmashru.tech/register"

        password = binding.password
        username = binding.username
        pass = password!!.text.toString()
        uname = username!!.text.toString()
        Log.d("CREATION", pass!!)

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

    private fun onSubmit() {
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
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        username.setText("")
        password.setText("")
        confirmPass.setText("")
    }
}
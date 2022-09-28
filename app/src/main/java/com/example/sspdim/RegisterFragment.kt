package com.example.sspdim

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sspdim.databinding.FragmentLoginBinding
import com.example.sspdim.databinding.FragmentRegisterBinding
import com.example.sspdim.model.LoginRegisterViewModel
import com.example.sspdim.network.LoginRegisterRequest
import com.example.sspdim.network.Response
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "RegisterFragment"

class RegisterFragment(): Fragment() {
    private val viewModel: LoginRegisterViewModel by viewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.registerButton.setOnClickListener {
            onSubmit()
        }
        binding.username.doOnTextChanged { text, _, _, _ ->
            viewModel.username = text.toString()
        }
        binding.password.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
        }
        binding.confirmPassword.doOnTextChanged {text, _, _, _ ->
            viewModel.confirmPassword = text.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setError(): Boolean {
        val passRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$")

        if (viewModel.username.isEmpty()) {
            binding.username.error = "Field cant be empty"
            return true
        } else {
            binding.username.error = null
        }

        if (viewModel.password.isEmpty()) {
            binding.password.error = "Field can't be empty."
            return true
        } else {
            binding.password.error = null
        }

        if (viewModel.password.isEmpty()) {
            binding.password.error = "Password can't be empty"
            return false
        }
        else if (!viewModel.password.matches(passRegex)) {
            binding.password.error = "Password is too weak"
            return false
        } else if (viewModel.password.length >= 35) {
            binding.password.error = "Password is too long"
            return false
        } else if (viewModel.password != viewModel.confirmPassword) {
            binding.password.error = "Passwords don't match"
            return false
        } else {
            binding.password.error = null
        }
        return true
    }

    /*
    private fun validatePassword(): Boolean {
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
    private fun submitRegisterDetails(): Response? {
        pass = password!!.text.toString()
        uname = username!!.text.toString()
        val request = LoginRegisterRequest(uname, pass)
        var response: Response? = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "sending request")
                response = SspdimApi.retrofitService.submitRegister(request)
                Log.d(TAG, "received response")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }
    */


    /*
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
     */

    private fun onSubmit() {
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()
        viewModel.initData(username, password)

        if (!setError()) {
            viewModel.submitRegisterDetails()
            if (viewModel.status > 0) {
                try {
                    Toast.makeText(
                        requireContext(),
                        viewModel.message,
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: JSONException) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }
            try {
                if (viewModel.status == Response.STATUS_SUCCESS) {
                    startActivity(Intent(requireContext(), ChatInterface::class.java))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.username.setText(viewModel.username)
        binding.password.setText(viewModel.password)
        binding.confirmPassword.setText(viewModel.confirmPassword)
    }
}
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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sspdim.databinding.FragmentLoginBinding
import com.example.sspdim.model.LoginRegisterViewModel
import com.example.sspdim.network.LoginRegisterRequest
import com.example.sspdim.network.Response
import com.example.sspdim.network.Response.Companion.STATUS_SUCCESS
import com.example.sspdim.network.SspdimApi
import com.google.android.material.button.MaterialButton
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

private const val TAG = "LoginFragment"

class LoginFragment: Fragment() {
    private val viewModel: LoginRegisterViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding = _binding!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.registerLink.setOnClickListener {
            onClickRegister()
        }
        binding.loginButton.setOnClickListener {
            onClickLogin()
        }
        binding.username.doOnTextChanged { text, _, _, _ ->
            viewModel.username = text.toString()
        }
        binding.password.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun submitLoginDetails(): Response? {
        val request = LoginRegisterRequest(viewModel.username, viewModel.password)
        var response: Response? = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "sending request")
                response = SspdimApi.retrofitService.submitLogin(request)
                Log.d(TAG, "received response")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return response
    }

    /*
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
    */

    private fun setError(): Boolean {
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
        return false
    }

    private fun onClickRegister() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
    }

    private fun onClickLogin() {
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()
        viewModel.initData(username, password)

        if (!setError()) {
            viewModel.submitLoginDetails()
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
                if (viewModel.status == STATUS_SUCCESS) {
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
    }
}
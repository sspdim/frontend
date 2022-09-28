package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sspdim.databinding.FragmentLoginBinding
import com.example.sspdim.model.LoginRegisterViewModel
import com.example.sspdim.network.Response.Companion.STATUS_SUCCESS
import org.json.JSONException

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
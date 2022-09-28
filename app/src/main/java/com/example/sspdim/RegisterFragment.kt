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
import com.example.sspdim.databinding.FragmentRegisterBinding
import com.example.sspdim.model.LoginRegisterViewModel
import com.example.sspdim.network.Response
import org.json.JSONException

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
        val passRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,}$")

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
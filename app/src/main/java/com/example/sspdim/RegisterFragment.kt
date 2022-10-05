package com.example.sspdim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sspdim.databinding.FragmentRegisterBinding
import com.example.sspdim.model.RegisterViewModel

class RegisterFragment(): Fragment() {
    private val viewModel: RegisterViewModel by activityViewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.registerNextButton.setOnClickListener {
            onClickNext()
        }
        binding.registerUsername.doOnTextChanged { text, _, _, _ ->
            viewModel.username = text.toString()
        }
        binding.registerPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
        }
        binding.registerConfirmPassword.doOnTextChanged {text, _, _, _ ->
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
            binding.registerUsername.error = "Field cant be empty"
            return true
        } else {
            binding.registerUsername.error = null
        }

        if (viewModel.password.isEmpty()) {
            binding.registerPassword.error = "Field can't be empty."
            return true
        } else {
            binding.registerPassword.error = null
        }

        if (viewModel.password.isEmpty()) {
            binding.registerPassword.error = "Password can't be empty"
            return true
        }
        else if (!viewModel.password.matches(passRegex)) {
            binding.registerPassword.error = "Password is too weak"
            return true
        } else if (viewModel.password.length >= 35) {
            binding.registerPassword.error = "Password is too long"
            return true
        } else if (viewModel.password != viewModel.confirmPassword) {
            binding.registerPassword.error = "Passwords don't match"
            return true
        } else {
            binding.registerPassword.error = null
        }
        return false
    }

    private fun onClickNext()  {
        val username = binding.registerUsername.text.toString()
        val password = binding.registerPassword.text.toString()
        val confirmPassword = binding.registerConfirmPassword.text.toString()
        viewModel.initData(username, password, confirmPassword)

        if (!setError()) {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToRegisterServerListFragment())
        }
    }

    override fun onStart() {
        super.onStart()
        binding.registerUsername.setText(viewModel.username)
        binding.registerPassword.setText(viewModel.password)
        binding.registerConfirmPassword.setText(viewModel.confirmPassword)
    }
}
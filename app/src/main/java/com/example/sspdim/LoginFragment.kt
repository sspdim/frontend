package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sspdim.databinding.FragmentLoginBinding
import com.example.sspdim.model.LoginViewModel

class LoginFragment: Fragment() {
    private val viewModel: LoginViewModel by activityViewModels()

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
        binding.loginNextButton.setOnClickListener {
            onClickNext()
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
        startActivity(Intent(requireContext(), RegisterActivity::class.java))
    }

    private fun onClickNext() {
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()
        viewModel.initData(username, password)

        if (!setError()) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToLoginServerListFragment())
        }
    }

    override fun onStart() {
        super.onStart()
        binding.username.setText(viewModel.username)
        binding.password.setText(viewModel.password)
    }
}
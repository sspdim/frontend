package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentLoginBinding
import com.example.sspdim.model.KeysModel
import com.example.sspdim.model.LoginViewModel
import com.example.sspdim.network.Response
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException

private const val TAG = "LoginFragment"

class LoginFragment: Fragment() {
    private val viewModel: LoginViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null
    private lateinit var binding: FragmentLoginBinding

    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding = _binding!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsDataStore = SettingsDataStore(requireContext())
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
        val list = binding.username.text.toString().split("@")
        val username = list[0]
        val password = binding.password.text.toString()
        viewModel.initData(username, password)

        if (!setError()) {
            if (list.size < 2) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToLoginServerListFragment())
            }
            else if (list.size > 2) {
                Toast.makeText(requireContext(),"@ not allowed in username", Toast.LENGTH_SHORT).show()
            }
            else {
                if(list[1].isNotEmpty()) {
                    Log.d(TAG, "Split result: ${list[1]}")
                    loginWithServer(list[1])
                }
                else {
                    Toast.makeText(requireContext(),"Domain name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.username.setText(viewModel.username)
        binding.password.setText(viewModel.password)
    }

    private fun loginWithServer(serverDomain: String) {
        viewModel.selectedServerDomainName = serverDomain
        Log.d(TAG, "Selected server: ${viewModel.selectedServerDomainName}")
        viewModel.setBaseDomain()
        val username = viewModel.submitLoginDetails()
        val key : KeysModel = KeysModel(requireContext(), username)
        key.addKeys()
        val keyStatus = key.submitKeysDetails()
        if (viewModel.status > 0 && keyStatus == 200) {
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
                runBlocking {
                    launch {
                        settingsDataStore.saveLoggedInPreference(true, requireContext())
                        settingsDataStore.saveUsernamePreference(
                            viewModel.username,
                            requireContext()
                        )
                        settingsDataStore.saveServerPreference(
                            viewModel.selectedServerDomainName,
                            requireContext()
                        )
                    }
                }
                startActivity(Intent(requireContext(), ChatActivity::class.java))
            }
            else {
                Log.d(TAG, "failed")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
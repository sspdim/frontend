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
import com.example.sspdim.crypto.KeysModel
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentRegisterBinding
import com.example.sspdim.model.RegisterViewModel
import com.example.sspdim.network.Response
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException

private const val TAG = "RegisterFragment"

class RegisterFragment(): Fragment() {
    private val viewModel: RegisterViewModel by activityViewModels()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        settingsDataStore = SettingsDataStore(requireContext())
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
        val list = binding.registerUsername.text.toString().split("@")
        val username = list[0]
        val password = binding.registerPassword.text.toString()
        val confirmPassword = binding.registerConfirmPassword.text.toString()
        viewModel.initData(username, password, confirmPassword)

        if (!setError()) {
            if (list.size < 2) {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToRegisterServerListFragment())
            }
            else if (list.size > 2) {
                Toast.makeText(requireContext(),"@ not allowed in username", Toast.LENGTH_SHORT).show()
            }
            else {
                if(list[1].isNotEmpty()) {
                    onClickRegister(list[1])
                }
                else {
                    Toast.makeText(requireContext(),"Domain name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.registerUsername.setText(viewModel.username)
        binding.registerPassword.setText(viewModel.password)
        binding.registerConfirmPassword.setText(viewModel.confirmPassword)
    }

    private fun onClickRegister(serverDomain: String) {
        Log.d(TAG, "Selected server: ${viewModel.selectedServerDomainName}")
        viewModel.selectedServerDomainName = serverDomain
        viewModel.setBaseDomain()
        val username : String = viewModel.submitRegisterDetails()
        val key : KeysModel = KeysModel(requireContext(), username)
        key.addKeys()
        val keyStatus = key.submitKeysDetails()
        Log.d("srg", "${viewModel.status}, ${viewModel.message}; ${viewModel.response?.message}, ${viewModel.response?.status}")
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
        Log.d(TAG, "Registration response: ${viewModel.status}")
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
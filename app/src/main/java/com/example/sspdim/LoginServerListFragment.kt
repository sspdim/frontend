package com.example.sspdim

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sspdim.crypto.KeysModel
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentLoginServerListBinding
import com.example.sspdim.model.LoginViewModel
import com.example.sspdim.model.ServerListAdapter
import com.example.sspdim.network.Response
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException

private const val TAG = "LoginServerListFragment"

class LoginServerListFragment: Fragment() {

    private lateinit var settingsDataStore: SettingsDataStore

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginServerListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.serversRecyclerView.adapter = ServerListAdapter { server ->
            viewModel.selectedServerDomainName = server.domainName
            Log.d(TAG, "Selected server: $viewModel.selectedServerDomainName")
        }
        binding.loginButton.setOnClickListener {
            onClickLogin()
        }

        viewModel.getServerDetails()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDataStore = SettingsDataStore(requireContext())
    }

    @SuppressLint("LongLogTag")
    private fun onClickLogin() {
        Log.d(TAG, "Selected server: $viewModel.selectedServerDomainName")
        if (viewModel.selectedServerDomainName.isEmpty()) {
            try {
                Toast.makeText(
                    requireContext(),
                    "Select a server!",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: JSONException) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }
            return
        }
        viewModel.setBaseDomain()
        var username = viewModel.submitLoginDetails()
        var key : KeysModel = KeysModel(requireContext(), username)
        key.addKeys()
        var keyStatus = key.submitKeysDetails()
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
                findNavController().navigate(LoginServerListFragmentDirections.actionLoginServerListFragmentToLoginFragment())
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
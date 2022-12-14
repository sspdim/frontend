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
import com.example.sspdim.databinding.FragmentRegisterServerListBinding
import com.example.sspdim.model.RegisterViewModel
import com.example.sspdim.model.ServerListAdapter
import com.example.sspdim.network.Response
import com.example.sspdim.network.setBaseUrl
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException

private const val TAG = "RegisterServerListFragment"

class RegisterServerListFragment: Fragment() {

    private lateinit var settingsDataStore: SettingsDataStore

    private val viewModel: RegisterViewModel by activityViewModels()

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterServerListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.serversRecyclerView.adapter = ServerListAdapter { server ->
            viewModel.selectedServerDomainName = server.domainName
            Log.d(TAG, "Selected server: ${viewModel.selectedServerDomainName}")
        }

        viewModel.getServerDetails()

        binding.registerButton.setOnClickListener {
            onClickRegister()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDataStore = SettingsDataStore(requireContext())
    }

    @SuppressLint("LongLogTag")
    private fun onClickRegister() {
        Log.d(TAG, "Selected server: ${viewModel.selectedServerDomainName}")
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
        var username : String = viewModel.submitRegisterDetails()
        var key : KeysModel = KeysModel(requireContext(), username)
        key.addKeys()
        var keyStatus = key.submitKeysDetails()
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
                Log.d("excc", "failed")
                findNavController().navigate(RegisterServerListFragmentDirections.actionRegisterServerListFragmentToRegisterFragment())
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
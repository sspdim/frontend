package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentRegisterServerListBinding
import com.example.sspdim.model.RegisterViewModel
import com.example.sspdim.model.ServerListAdapter
import com.example.sspdim.network.Response
import com.example.sspdim.network.setBaseUrl
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException

class RegisterServerListFragment: Fragment() {

    private lateinit var settingsDataStore: SettingsDataStore

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterServerListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.serversRecyclerView.adapter = ServerListAdapter()
        binding.registerButton.setOnClickListener {
            onClickRegister()
        }

        viewModel.getServerDetails()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsDataStore = SettingsDataStore(requireContext())
    }

    private fun onClickRegister() {
        viewModel.servers.value?.get(0)?.let { setBaseUrl("https://" + it.domainName) }
//        runBlocking {
//            viewModel.submitRegisterDetails()
//        }
        viewModel.submitRegisterDetails()
        Log.d("srg", "${viewModel.status}, ${viewModel.message}; ${viewModel.response?.message}, ${viewModel.response?.status}")
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
                lifecycleScope.launch {
                settingsDataStore.saveLoggedInPreference(true, requireContext())
            }
                startActivity(Intent(requireContext(), ChatInterface::class.java))
            }
            else {
                Log.d("excc", "failed")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
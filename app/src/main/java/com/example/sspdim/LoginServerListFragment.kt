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
import com.example.sspdim.databinding.FragmentLoginServerListBinding
import com.example.sspdim.model.LoginViewModel
import com.example.sspdim.model.ServerListAdapter
import com.example.sspdim.network.Response
import com.example.sspdim.network.setBaseUrl
import org.json.JSONException

class LoginServerListFragment: Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginServerListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.serversRecyclerView.adapter = ServerListAdapter()
        binding.loginButton.setOnClickListener {
            onClickLogin()
        }

        viewModel.getServerDetails()

        return binding.root
    }

    private fun onClickLogin() {
        viewModel.servers.value?.get(0)?.let { setBaseUrl("https://" + it.domainName) }
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
            if (viewModel.status == Response.STATUS_SUCCESS) {
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
package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sspdim.databinding.FragmentMainBinding
import com.example.sspdim.model.MainViewModel
import com.example.sspdim.model.MainViewModelFactory

private const val TAG = "MainFragment"

class MainFragment: Fragment() {
    private var isLoggedIn = false

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (activity?.application as SspdimApplication).database.preferenceDao()
        )
    }

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "starting login activity")
        viewModel.getIsLoggedIn()?.observe(this.viewLifecycleOwner) { res ->
            isLoggedIn = res?.value?.toBoolean() ?: false
            chooseActivity()
        }
    }

    private fun chooseActivity() {
        if (isLoggedIn) {
            startActivity(Intent(requireContext(), ChatInterface::class.java))
        }
        else {
            Log.d(TAG, "starting login activity")
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }
}
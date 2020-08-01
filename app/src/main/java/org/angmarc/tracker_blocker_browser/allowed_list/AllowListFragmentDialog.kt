package org.angmarc.tracker_blocker_browser.allowed_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import org.angmarc.tracker_blocker_browser.databinding.FragmentAllowListBinding

class AllowListFragmentDialog : DialogFragment() {

    private lateinit var binding: FragmentAllowListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: AllowListViewModel by viewModels()
        binding = FragmentAllowListBinding
            .inflate(LayoutInflater.from(context), null, false)
            .apply {
                viewmodel = viewModel
            }
        return binding.root
    }

    companion object {
        fun instance() = AllowListFragmentDialog()
    }
}
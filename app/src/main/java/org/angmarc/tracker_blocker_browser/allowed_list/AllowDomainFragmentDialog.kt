package org.angmarc.tracker_blocker_browser.allowed_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.TrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.core.EventObserver
import org.angmarc.tracker_blocker_browser.databinding.FragmentAllowListBinding
import javax.inject.Inject

private const val DOMAIN_NAME = "domain-name"

class AllowDomainFragmentDialog : DialogFragment() {

    private lateinit var binding: FragmentAllowListBinding

    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerAllowDomainComponent
            .builder()
            .applicationComponent(
                (requireActivity().application as TrackerBlockingApplication).applicationComponent
            )
            .viewModelStoreOwner(this)
            .domainNameToAllow(arguments?.getString(DOMAIN_NAME) ?: "")
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = viewModelProvider.get(AllowDomainViewModel::class.java)
        binding = FragmentAllowListBinding
            .inflate(LayoutInflater.from(context), null, false)
            .apply {
                viewmodel = viewModel
            }
        viewModel.onAllowWebsiteAdded.observe(this,
            EventObserver {
                dismiss()
            })
        return binding.root
    }

    companion object {
        fun instance(domainNameToAllow: String) = AllowDomainFragmentDialog().also {
            it.arguments = bundleOf(DOMAIN_NAME to domainNameToAllow)
        }
    }
}
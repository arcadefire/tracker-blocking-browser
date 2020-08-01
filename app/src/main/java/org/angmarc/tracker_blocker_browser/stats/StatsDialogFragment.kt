package org.angmarc.tracker_blocker_browser.stats

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.TrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.databinding.FragmentStatsBinding
import javax.inject.Inject

private const val THREE_SECONDS = 3 * 1000L

class StatsDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentStatsBinding

    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerStatsComponent
            .builder()
            .applicationComponent(
                (requireActivity().application as TrackerBlockingApplication).applicationComponent
            )
            .viewModelStoreOwner(this)
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding
            .inflate(LayoutInflater.from(context), null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelProvider
            .get(StatsViewModel::class.java)
            .blockedTrackersAmount.observe(this, Observer {
                animateCounterIncrement(it)
            })
    }

    private fun animateCounterIncrement(amount: Int) {
        ValueAnimator().apply {
            setObjectValues(0, amount)
            addUpdateListener { animation ->
                binding.blockedTrackersNumber.text = (animation.animatedValue.toString())
            }
            interpolator = AccelerateDecelerateInterpolator()
            duration = THREE_SECONDS
            start()
        }
    }

    companion object {
        fun instance() = StatsDialogFragment()
    }
}
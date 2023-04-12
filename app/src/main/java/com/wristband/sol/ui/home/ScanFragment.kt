package com.wristband.sol.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.wristband.sol.MainActivity
import com.wristband.sol.databinding.FragmentScanBinding
import com.wristband.sol.ui.login.afterTextChanged
import com.wristband.sol.ui.vm.AttendanceViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [ScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class ScanFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentScanBinding

    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScanFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener {
            mainActivity.supportFragmentManager.popBackStack()
        }

        binding.code.afterTextChanged {
            if (it.length >= 12) {
                binding.caption.text = ""
                viewModel.verifyAndMark(it)
                binding.verify.isEnabled = false
            }

            if(it.length >= 6) binding.verify.isEnabled = true
        }

        //
        viewModel.attendance.observe(requireActivity()) {
            val result = it ?: return@observe
            binding.verify.isEnabled = true
            binding.caption.text = result.message
            if(result.status) {
                binding.code.text?.clear()
                binding.caption.setTextColor(Color.WHITE)
            }
            else {
                binding.caption.setTextColor(Color.RED)
            }
        }
    }

}
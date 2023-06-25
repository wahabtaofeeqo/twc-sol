package com.wristband.sol.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.wristband.sol.R
import com.wristband.sol.databinding.FragmentConfirmTicketBinding
import com.wristband.sol.ui.login.afterTextChanged
import com.wristband.sol.ui.vm.AttendanceViewModel
import com.wristband.sol.ui.vm.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmTicketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class ConfirmTicketFragment : Fragment() {

    private val viewModel: TicketViewModel by viewModels()
    private lateinit var binding: FragmentConfirmTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ConfirmTicketFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentConfirmTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0)
                requireActivity().supportFragmentManager.popBackStack()
            else requireActivity().finish()
        }

        binding.code.afterTextChanged {
            if (it.trim().isNotEmpty()) {
                binding.caption.text = ""
                viewModel.confirmCode(it)
                binding.verify.isEnabled = true
            }
            else binding.verify.isEnabled = true
        }

        //
        viewModel.ticket.observe(requireActivity()) {
            val result = it ?: return@observe
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
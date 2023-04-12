package com.wristband.sol.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wristband.sol.MainActivity
import com.wristband.sol.R
import com.wristband.sol.data.adapters.AttendanceAdapter
import com.wristband.sol.data.adapters.MemberAdapter
import com.wristband.sol.databinding.FragmentClockInBinding
import com.wristband.sol.databinding.FragmentMemberBinding
import com.wristband.sol.ui.vm.AttendanceViewModel
import com.wristband.sol.ui.vm.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [MemberFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class MemberFragment : Fragment() {

    @Inject
    lateinit var adapter: MemberAdapter

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentMemberBinding
    private val viewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    companion object {
        @JvmStatic
        fun newInstance() = MemberFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        mainActivity.setSupportActionBar(binding.toolbar)

        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadMembers(20).collectLatest { adapter.submitData(it) }
        }

        adapter.addLoadStateListener {
            if(adapter.itemCount > 0) binding.noRecord.visibility = View.GONE
        }

        observeAPIResult()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)

        //
        menu.findItem(R.id.sync).isVisible = true
        menu.findItem(R.id.scan).isVisible = false
        menu.findItem(R.id.export).isVisible = false
        menu.findItem(R.id.members).isVisible = false
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.scan) {
            mainActivity.changeFragment(ScanFragment.newInstance())
        }

        if(item.itemId == R.id.sync) this.loadRemoteData()

        return super.onOptionsItemSelected(item)
    }

    private fun observeAPIResult() {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            viewModel.apiResult.observe(requireActivity(), Observer {
                val response = it?: return@Observer
                if(!response.status)
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                else {
                    viewModel.updateAll(response.data!!)
                }
                binding.progress.visibility = View.GONE
            })
        }
    }

    private fun loadRemoteData() {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.setMessage(R.string.confirm_message)?.setTitle(R.string.confirm_title)
        builder?.apply {
            setPositiveButton("Okay") { _, _ ->
                binding.progress.visibility = View.VISIBLE
                viewModel.loadMembersAPI()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

}
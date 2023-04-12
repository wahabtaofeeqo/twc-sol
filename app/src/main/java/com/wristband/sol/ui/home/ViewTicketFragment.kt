package com.wristband.sol.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wristband.sol.MainActivity
import com.wristband.sol.R
import com.wristband.sol.data.adapters.MemberAdapter
import com.wristband.sol.data.adapters.TicketAdapter
import com.wristband.sol.databinding.FragmentMemberBinding
import com.wristband.sol.databinding.FragmentViewTicketBinding
import com.wristband.sol.ui.vm.MemberViewModel
import com.wristband.sol.ui.vm.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [ViewTicketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class ViewTicketFragment : Fragment() {

    @Inject
    lateinit var adapter: TicketAdapter

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentViewTicketBinding
    private val viewModel: TicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTicketFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewTicketBinding.inflate(inflater, container, false)
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
            viewModel.loadTickets(20).collectLatest { adapter.submitData(it) }
        }

        adapter.addLoadStateListener {
            if(adapter.itemCount > 0) binding.noRecord.visibility = View.GONE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)

        //
        menu.findItem(R.id.sync).isVisible = false
        menu.findItem(R.id.scan).isVisible = false
        menu.findItem(R.id.upload).isVisible = true
        menu.findItem(R.id.export).isVisible = false
        menu.findItem(R.id.members).isVisible = false
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.upload) {
            this.postData()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun postData() {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.setMessage(R.string.upload_message)?.setTitle(R.string.upload_title)
        builder?.apply {
            setPositiveButton("Okay") { _, _ ->
                binding.progress.visibility = View.VISIBLE
                viewModel.sendTicketsAPI()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }
}
package com.wristband.sol.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wristband.sol.MainActivity
import com.wristband.sol.R
import com.wristband.sol.data.adapters.AttendanceAdapter
import com.wristband.sol.databinding.FragmentClockInBinding
import com.wristband.sol.ui.vm.AttendanceViewModel
import com.wristband.sol.ui.vm.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [ClockInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class ClockInFragment : Fragment() {

    @Inject
    lateinit var adapter: AttendanceAdapter

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentClockInBinding

    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = activity as MainActivity
    }

    companion object {
        @JvmStatic
        fun newInstance() = ClockInFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentClockInBinding.inflate(inflater, container, false)
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
            viewModel.loadAttendance(20).collectLatest { adapter.submitData(it) }
        }

        adapter.addLoadStateListener {
            if(adapter.itemCount > 0) binding.noRecord.visibility = View.GONE
        }

        //
        viewModel.exportResult.observe(requireActivity()) {
            val result = it ?: return@observe
            Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scan -> {
                mainActivity.changeFragment(ScanFragment.newInstance(), true)
            }

            R.id.members -> {
                mainActivity.changeFragment(MemberFragment.newInstance(), true)
            }

            R.id.export -> createFile()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/excel"
            putExtra(Intent.EXTRA_TITLE, "attendance.csv")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOCUMENTS)
            }
        }

        fileLauncher.launch(intent)
    }

    private val fileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result?.data?.also {
            it.data?.let { uri ->
                val stream = requireContext().contentResolver.openOutputStream(uri)
                viewModel.exportAttendanceToCSV(stream!!)
            }
        }
    }
}
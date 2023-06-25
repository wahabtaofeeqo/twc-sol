package com.wristband.sol.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wristband.sol.MainActivity
import com.wristband.sol.R
import com.wristband.sol.data.Constants
import com.wristband.sol.data.SessionManager
import com.wristband.sol.data.adapters.TicketAdapter
import com.wristband.sol.databinding.ActivityWelcomeBinding
import com.wristband.sol.ui.login.LoginActivity
import com.wristband.sol.ui.vm.MemberViewModel
import com.wristband.sol.ui.vm.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    @Inject
    lateinit var adapter: TicketAdapter
    lateinit var sessionManager: SessionManager

    private lateinit var binding: ActivityWelcomeBinding
    private val viewModel: TicketViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val intent = Intent(this, MainActivity::class.java)

        binding.ticket.setOnClickListener {
            intent.putExtra(Constants.PAGE, Constants.TICKET)
            startActivity(intent)
        }

        binding.clockIn.setOnClickListener {
            intent.putExtra(Constants.PAGE, Constants.CLOCK_IN)
            startActivity(intent)
        }

        binding.clockIns.setOnClickListener {
            intent.putExtra(Constants.PAGE, Constants.CLOCK_IN)
            startActivity(intent)
        }

        binding.tickets.setOnClickListener {
            intent.putExtra(Constants.PAGE, Constants.TICKETS)
            startActivity(intent)
        }

        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            viewModel.loadTickets(10).collectLatest { adapter.submitData(it) }
        }

        adapter.addLoadStateListener {
            if(adapter.itemCount > 0) binding.noRecord.visibility = View.GONE
        }

        //
        viewModel.loadCount()
        memberViewModel.loadCount()

        this.updateStats()
    }

    private fun updateStats() {
        viewModel.bookings.observe(this) {
            binding.bookingsLabel.text = resources.getString(R.string.label_total_tickets, it)
        }

        memberViewModel.clockIns.observe(this) {
            binding.clockInLabel.text = resources.getString(R.string.total_clock_ins, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_welcome, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        if (item.itemId == R.id.scan) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.PAGE, Constants.SCAN)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}
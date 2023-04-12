package com.wristband.sol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.wristband.sol.data.Constants
import com.wristband.sol.ui.home.ClockInFragment
import com.wristband.sol.ui.home.TicketFragment
import com.wristband.sol.ui.home.ViewTicketFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null) {
            when(intent.getStringExtra(Constants.PAGE)) {
                Constants.TICKET -> changeFragment(TicketFragment.newInstance())
                Constants.CLOCK_IN ->  changeFragment(ClockInFragment.newInstance())
                Constants.TICKETS ->  changeFragment(ViewTicketFragment.newInstance())
            }
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.container, fragment).commit()
    }
}
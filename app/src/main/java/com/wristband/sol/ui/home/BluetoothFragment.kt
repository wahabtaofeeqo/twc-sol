package com.wristband.sol.ui.home

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.wristband.sol.MainActivity
import com.wristband.sol.data.adapters.BluetoothDeviceAdapter
import com.wristband.sol.data.model.BluetoothDetails
import com.wristband.sol.databinding.FragmentBluetoothBinding
import com.wristband.sol.ui.vm.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class BluetoothFragment : Fragment() {

    private lateinit var adapter: BluetoothDeviceAdapter
    private lateinit var binding: FragmentBluetoothBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val viewModel: TicketViewModel by activityViewModels()

    private val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, Boolean> ->
            var allGranted = true
        }

    private val singlePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

    }

    private val bluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) searchBluetooth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BluetoothFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBluetoothBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.initBluetooth()
        this.initBroadcast()
        adapter =  BluetoothDeviceAdapter()
        adapter.listener = object : BluetoothDeviceAdapter.ItemClickListener {
            override fun onItemClick(details: BluetoothDetails) {
                viewModel.bluetoothSelected(details)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        // Set the adapter
        binding.list.adapter = adapter
        binding.list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.scan.setOnClickListener { searchBluetooth() }
    }

    private fun initBroadcast() {
        try {
            val filter = IntentFilter()
            filter.addAction(BluetoothDevice.ACTION_FOUND)
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

            context?.registerReceiver(receiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun searchBluetooth() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                if(!bluetoothAdapter.isDiscovering)
                    bluetoothAdapter.startDiscovery()
            }
            else Toast.makeText(context, "Permission is needed", Toast.LENGTH_LONG).show()
        }
        else {
            if(hasPermission(Manifest.permission.BLUETOOTH)) {
                if(!bluetoothAdapter.isDiscovering) bluetoothAdapter.startDiscovery()
            }
            else Toast.makeText(context, "Permission is needed", Toast.LENGTH_LONG).show()
        }
    }

    private fun initBluetooth() {
        val bluetoothManager = requireContext()
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // Check permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            multiplePermissionsLauncher.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else {
            multiplePermissionsLauncher.launch(arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }

        // location
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            singlePermissionLauncher.launch(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                singlePermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }

        // enable bluetooth (if not)
        if (!bluetoothAdapter.isEnabled) {
            bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    private fun hasPermission(vararg permissions: String): Boolean {
        var isGranted = false
        for (permission in permissions) {
            isGranted = (ActivityCompat.checkSelfPermission(requireContext(), permission)
                    == PackageManager.PERMISSION_GRANTED)
        }
        return isGranted
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0)

                    if(device.name != null) {
                        val details = BluetoothDetails(
                            name = device.name,
                            mac = device.address,
                            length = rssi.toString())

                        adapter.addItem(details)
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    //Toast.makeText(context, "Started", Toast.LENGTH_LONG).show()
                    binding.scan.text = "Scanning"
                    binding.scan.isEnabled = false
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    binding.scan.text = "Scan"
                    binding.scan.isEnabled = true
                    // Toast.makeText(context, "Finished", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(hasPermission(Manifest.permission.BLUETOOTH_SCAN) ||
                hasPermission(Manifest.permission.BLUETOOTH)) {
            bluetoothAdapter.cancelDiscovery()
        }
    }
}
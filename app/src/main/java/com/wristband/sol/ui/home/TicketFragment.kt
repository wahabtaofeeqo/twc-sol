package com.wristband.sol.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.gprinter.bean.PrinterDevices
import com.gprinter.utils.CallbackListener
import com.gprinter.utils.Command
import com.gprinter.utils.ConnMethod
import com.gprinter.utils.LogUtils
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.Validator.ValidationListener
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.Min
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.wristband.sol.MainActivity
import com.wristband.sol.PrintContent
import com.wristband.sol.Printer
import com.wristband.sol.R
import com.wristband.sol.data.SessionManager
import com.wristband.sol.data.model.BluetoothDetails
import com.wristband.sol.data.model.Ticket
import com.wristband.sol.databinding.FragmentTicketBinding
import com.wristband.sol.ui.login.afterTextChanged
import com.wristband.sol.ui.vm.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [TicketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class TicketFragment : ValidationListener, CallbackListener, Fragment() {

    private lateinit var validator: Validator
    private val viewModel: TicketViewModel by activityViewModels()

//    @NotEmpty
//    private lateinit var name: TextInputEditText

//    @NotEmpty
//    @Length(trim = true, min = 11, max = 12)
//    private lateinit var phone: TextInputEditText

    @NotEmpty
    private lateinit var accessType: MaterialAutoCompleteTextView

    @NotEmpty
    @Min(value = 1)
    private lateinit var accessQuantity: TextInputEditText

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var binding: FragmentTicketBinding

    private val accessTypes = listOf("General", "Adults", "7 - 17 Years", "6 Years and below")
    private val packages = listOf("Umbrella", "Beach-side Lounger", "Poolside Lounger",
        "Beach-side Picnic Pallet (6 ppl)", "Beach-side Micro Bench (3 ppl)", "Beach-side Deluxe Lounger (2 ppl)")

    private val canabas = listOf("Beach-side Bed (2 ppl)", "Poolside Bed (2 ppl)",
        "Beach-side Standard Cabana (6 ppl)", "Beach-side large Cabana (10 ppl)")

    private var connected = false

    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        validator = Validator(this)
        validator.setValidationListener(this)
        mainActivity = activity as MainActivity
    }

    companion object {
        @JvmStatic
        fun newInstance() = TicketFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.updateCost()
        this.setInputAdapters()
        this.initValidatorViews()

        binding.back.setOnClickListener { activity?.finish() }

        //
        binding.accessType.afterTextChanged {
            this.updateCost()
        }

        binding.accessQuantity.afterTextChanged {
            this.updateCost()
        }

        binding.packageType.afterTextChanged {
            this.updateCost()
        }

        binding.packageQuantity.afterTextChanged {
            this.updateCost()
        }

        binding.cabanaType.afterTextChanged {
            this.updateCost()
        }
        binding.cabanaQuantity.afterTextChanged {
            this.updateCost()
        }

        // Save booking
        binding.book.setOnClickListener {
            validator.validate()
        }

        // connect bluetooth
        binding.connect.setOnClickListener {
            mainActivity.changeFragment(BluetoothFragment.newInstance(), true)
        }

        //
        viewModel.createResponse.observe(requireActivity()) {
            val result = it?:return@observe
            if(result.status)  {
                printLabel()
                this.resetForm()
                viewModel._createResponse.value = null
            }

            binding.loading.isVisible = false
            Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
        }

        viewModel.bluetooth.observe(requireActivity()) {
            if(it != null) {
                setupPrinter(it)
                binding.connect.isEnabled = false
            }
        }
    }

    private fun resetForm() {
        // name.text?.clear()
        // phone.text?.clear()
        accessType.text.clear()
        accessQuantity.text?.clear()
        binding.packageType.text.clear()
        binding.packageQuantity.text?.clear()
        binding.cabanaType.text.clear()
        binding.cabanaQuantity.text?.clear()

        //
        updateCost()
    }


    private fun setInputAdapters() {

        val accessAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, accessTypes)
        binding.accessType.setAdapter(accessAdapter)

        val packageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, packages)
        binding.packageType.setAdapter(packageAdapter)

        val canabaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, canabas)
        binding.cabanaType.setAdapter(canabaAdapter)
    }

    private fun calculateCost(): Int {
        val accessCost = this.currentAccessTypeCost()
        val cabanaCost = this.currentCabanaTypeCost()
        val packageConst = this.currentPackageTypeCost()

        return (accessCost + cabanaCost + packageConst)
    }

    private fun updateCost() {
        binding.labelCost.text = resources.getString(R.string.summary, this.calculateCost())
    }

    private fun currentAccessTypeCost(): Int {
        val cost = when(accessTypes.indexOf(binding.accessType.text.toString())) {
            0 -> 5000
            1 -> 5000
            2 -> 3000
            else -> 0
        }

        var quantity = 0
        if(binding.accessQuantity.text.toString().isNotEmpty())
            quantity = binding.accessQuantity.text.toString().toInt()

        return cost * quantity
    }

    private fun currentPackageTypeCost(): Int {
        val cost = when(packages.indexOf(binding.packageType.text.toString())) {
            0 -> 1500
            1 -> 3000
            2 -> 5000
            3 -> 8000
            4 -> 12000
            5 -> 12000
            else -> 0
        }

        var quantity = 0
        if(binding.packageQuantity.text.toString().isNotEmpty())
            quantity = binding.packageQuantity.text.toString().toInt()

        return cost * quantity
    }

    private fun currentCabanaTypeCost(): Int {
        val cost = when(canabas.indexOf(binding.cabanaType.text.toString())) {
            0 -> 12000
            1 -> 20000
            2 -> 40000
            3 -> 75000
            else -> 0
        }

        var quantity = 0
        if(binding.cabanaQuantity.text.toString().isNotEmpty())
            quantity = binding.cabanaQuantity.text.toString().toInt()

        return cost * quantity
    }

    private fun initValidatorViews() {
        // name = binding.name
        // phone = binding.phone
        accessType = binding.accessType
        accessQuantity = binding.accessQuantity
    }

    override fun onValidationSucceeded() {
        binding.loading.isVisible = true
        val model = Ticket(name = "", phone = "",
            cost = calculateCost(), accessType = accessType.text.toString(),
            accessQuantity = accessQuantity.text.toString().toInt(), date = Date())

        viewModel.createTicket(model)
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.view
            val message = error.getCollatedErrorMessage(requireContext())

            if (view is TextInputEditText) { view.error = message }
            if (view is MaterialAutoCompleteTextView) { view.error = message }
        }
    }

    private fun printLabel() {
        if (!connected) return
        try {
            val code = generateCode()
            val counter = sessionManager.increaseCounter()

            val result: Boolean = Printer.portManager!!
                .writeDataImmediately(PrintContent.getLabel(requireContext(), code, counter))
            if (result) {
                //tipsDialog(getString(R.string.send_success))
            } else {
                //tipsDialog(getString(R.string.send_fail))
            }
        }
        catch (_ : Exception) {}
        finally {
            if (Printer.portManager == null) {
                Printer.close()
            }
        }
    }

    private fun setupPrinter(details: BluetoothDetails) {
        val blueTooth = PrinterDevices.Build()
            .setContext(context)
            .setConnMethod(ConnMethod.BLUETOOTH)
            .setMacAddress(details.mac)
            .setCommand(Command.TSC)
            .setCallbackListener(this).build()

        Printer.connect(blueTooth)
    }

    override fun onConnecting() {
        // Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show()
    }

    override fun onCheckCommand() {
        // Toast.makeText(context, "Command", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(p0: PrinterDevices?) {
        connected = true
        binding.connect.isVisible = false
        // Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
    }

    override fun onReceive(p0: ByteArray?) {
        // Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure() {
        binding.connect.isEnabled = true
        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
    }

    override fun onDisconnect() {
        // Toast.makeText(context, "Disconnect", Toast.LENGTH_SHORT).show()
    }

    fun generateCode(): String {
        val randomPin = (Math.random() * 10000).toInt() + 1000
        return randomPin.toString()
    }
}
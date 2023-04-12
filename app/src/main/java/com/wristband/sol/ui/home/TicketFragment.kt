package com.wristband.sol.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.Validator.ValidationListener
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.Min
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.wristband.sol.R
import com.wristband.sol.data.model.Ticket
import com.wristband.sol.databinding.FragmentTicketBinding
import com.wristband.sol.ui.login.afterTextChanged
import com.wristband.sol.ui.vm.LoginViewModel
import com.wristband.sol.ui.vm.TicketViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [TicketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class TicketFragment : ValidationListener, Fragment() {

    private lateinit var validator: Validator
    private val viewModel: TicketViewModel by viewModels()

    @NotEmpty
    private lateinit var name: TextInputEditText

    @NotEmpty
    @Length(trim = true, min = 11, max = 12)
    private lateinit var phone: TextInputEditText

    @NotEmpty
    private lateinit var accessType: MaterialAutoCompleteTextView

    @NotEmpty
    @Min(value = 1)
    private lateinit var accessQuantity: TextInputEditText

    private lateinit var binding: FragmentTicketBinding

    private val accessTypes = listOf("Adults", "7 - 17 Years", "6 Years and below")
    private val packages = listOf("Umbrella", "Beach-side Lounger", "Poolside Lounger",
        "Beach-side Picnic Pallet (6 ppl)", "Beach-side Micro Bench (3 ppl)", "Beach-side Deluxe Lounger (2 ppl)")

    private val canabas = listOf("Beach-side Bed (2 ppl)", "Poolside Bed (2 ppl)",
        "Beach-side Standard Cabana (6 ppl)", "Beach-side large Cabana (10 ppl)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        validator = Validator(this)
        validator.setValidationListener(this)
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

        binding.back.setOnClickListener {
            Log.i("@@@@@@@TAG","Clicked")
            parentFragmentManager.popBackStack()
        }

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

        //
        viewModel.createResponse.observe(requireActivity()) {
            val result = it?:return@observe

            if(result.status) this.resetForm()
            binding.loading.isVisible = false
            Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun resetForm() {
        name.text?.clear()
        phone.text?.clear()
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
            1 -> 3000
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
        name = binding.name
        phone = binding.phone
        accessType = binding.accessType
        accessQuantity = binding.accessQuantity
    }

    override fun onValidationSucceeded() {
        binding.loading.isVisible = true
        val model = Ticket(name = name.text.toString(), phone = phone.text.toString(), cost = calculateCost(),
            accessType = accessType.text.toString(), accessQuantity = accessQuantity.text.toString().toInt(), date = Date())

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
}
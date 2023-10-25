package com.phntechnolab.sales.fragment

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.gson.Gson
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.databinding.FragmentCostingMoaDocumentBinding
import com.phntechnolab.sales.databinding.LogoutDialogBinding
import com.phntechnolab.sales.databinding.VisitedSuccessDialogBinding
import com.phntechnolab.sales.di.FileDownloader
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.textChange
import com.phntechnolab.sales.util.toastMsg
import com.phntechnolab.sales.viewmodel.CostingMoaDocumentViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class CostingMOADocumentFragment : Fragment(), MenuProvider {

    private var _binding: FragmentCostingMoaDocumentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CostingMoaDocumentViewModel by viewModels()

    private val args: CostingMOADocumentFragmentArgs by navArgs()

    private var image: Uri? = null

    @Inject
    lateinit var download: FileDownloader

    private val backPressHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
//            setButtonName(viewModel.oldSchoolData.value)

            when (position) {
                0 -> {
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.proposeCostingStage.root.visibility = View.VISIBLE
                    binding.moaDocument.root.visibility = View.GONE
                    binding.topAppBar.title =
                        requireActivity().getString(com.phntechnolab.sales.R.string.propose_costing_stage)
                    position = 0
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                else -> {
                    position = 1
                    binding.stepView.done(true)
                    binding.stepView.go(1, true)
                }
            }
        }
    }

    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCostingMoaDocumentBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)

        viewModel._proposeCostingData.postValue(args.proposeCostingDetails)

        viewModel._moaDocumentData.postValue(args.moaDocumentDetails)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observers()

        checkedChangeListener()

        nextMeetingDateTimeClickListener()

        quotationValidity()

//        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.topAppBar.setNavigationOnClickListener {
            when (position) {
                0 -> {
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.proposeCostingStage.root.visibility = View.VISIBLE
                    binding.moaDocument.root.visibility = View.GONE
                    binding.topAppBar.title =
                        requireActivity().getString(com.phntechnolab.sales.R.string.propose_costing_stage)
                    position = 0
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                else -> {
                    position = 1
                    binding.stepView.done(true)
                    binding.stepView.go(1, true)
                }
            }
        }

        binding.proposeCostingStage.updateBtn.setOnClickListener {
            Timber.e("DATA POST")
            Timber.e(Gson().toJson(viewModel._proposeCostingData.value))
            if (isProposeCostingFieldsValid()) {
                viewModel.updateProposeCostingDetails()
            }
        }

        binding.moaDocument.updateBtn.setOnClickListener {
            Timber.e("MOA DATA POST")
            Timber.e(Gson().toJson(viewModel._moaDocumentData.value))
            if (isMOADocumentFieldsValid()) {
                binding.progressIndicator.visibility = View.VISIBLE
                viewModel.updateMoaDocumentDetails()
            } else {
                Timber.e("fill all man")
            }
        }

        binding.proposeCostingStage.autoAgreementDuration.setOnItemClickListener { parent, view, position, id ->
            val agreementDuration = parent.adapter.getItem(position) as String
            viewModel._proposeCostingData.value?.agreementDuration = agreementDuration
        }

        binding.proposeCostingStage.autoMeetingWithWhom.setOnItemClickListener { parent, view, position, id ->
            val meetingWithWhoom = parent.adapter.getItem(position) as String
            viewModel._proposeCostingData.value?.meetingWithWhoom = meetingWithWhoom
            if (meetingWithWhoom == "Other") {
                binding.proposeCostingStage.meetingWithWhoomOthers.visibility = View.VISIBLE
            } else {
                binding.proposeCostingStage.meetingWithWhoomOthers.visibility = View.GONE
            }
        }

        binding.proposeCostingStage.autoConversionRatio.setOnItemClickListener { parent, view, position, id ->
            val conversationRatio = parent.adapter.getItem(position) as String
            viewModel._proposeCostingData.value?.conversationRatio = conversationRatio
        }

        binding.moaDocument.autoAgreementDuration.setOnItemClickListener { parent, view, position, id ->
            val moaAgreementDuration = parent.adapter.getItem(position) as String
            viewModel._moaDocumentData.value?.agreementDuration = moaAgreementDuration

        }

//        binding.moaDocument.edtDiscussedWithWhom.setOnItemClickListener { parent, view, position, id ->
//            val moaMeetingWithWhoom = parent.adapter.getItem(position) as String
//            viewModel._moaDocumentData.value?.disscussedWithWhom = moaMeetingWithWhoom
//        }

        binding.moaDocument.autoSelectDesignation.setOnItemClickListener { parent, view, position, id ->
            val designation = parent.adapter.getItem(position) as String
            viewModel._moaDocumentData.value?.designation = designation
            if (designation == "Others") {
                binding.moaDocument.tilSelectDesignationOther.visibility = View.VISIBLE
            } else {
                binding.moaDocument.tilSelectDesignationOther.visibility = View.GONE
            }

        }
        binding.moaDocument.edtSelectDesignationOther.textChange { designation ->
            viewModel._moaDocumentData.value?.designation = designation
        }


        binding.moaDocument.documentConstraint.setOnClickListener {
            moaDocument.launch("application/pdf")
        }
    }

    private fun isProposeCostingFieldsValid(): Boolean {
        val isPriceDiscussedPending = viewModel._proposeCostingData.value?.priceDiscussed != "yes"

        val isPricepPerStudentPending =
            viewModel._proposeCostingData.value?.pricePerStudent.isNullOrBlank()

        val isQuotationSharedPending = viewModel._proposeCostingData.value?.quotationShared != "yes"

        val isPaymentScheduledPending = viewModel._proposeCostingData.value?.paymentShedule != "yes"

        val isAgreementDurationNotSelected =
            viewModel._proposeCostingData.value?.agreementDuration.isNullOrBlank()

        val isMeetingWithWhoomNotSelected =
            viewModel._proposeCostingData.value?.meetingWithWhoom.isNullOrBlank()

        val isConversationRatioNotSelected =
            viewModel._proposeCostingData.value?.conversationRatio.isNullOrBlank()

        val isNextMeetingDateNotSelected =
            viewModel._proposeCostingData.value?.nextMeet.isNullOrBlank()

//        isPriceDiscussedPending ||
//        isQuotationSharedPending ||

//        isPaymentScheduledPending ||
        return if (
            isPricepPerStudentPending ||
            isAgreementDurationNotSelected ||
            isMeetingWithWhoomNotSelected ||
            isConversationRatioNotSelected ||
            isNextMeetingDateNotSelected
        ) {
            Toast.makeText(
                requireContext(),
                requireActivity().getString(com.phntechnolab.sales.R.string.please_fill_all_the_mendate_and_mark_yes_details),
                Toast.LENGTH_LONG
            ).show()
            false
        } else {
            true
        }
    }

    private fun isMOADocumentFieldsValid(): Boolean {

        val isTotalInterestedIntakeNotFilled =
            viewModel._moaDocumentData.value?.interestedIntake.isNullOrBlank()

        val isCostingPerStudentNotFilled =
            viewModel._moaDocumentData.value?.finalCosting.isNullOrBlank()

        val isAgreementDurationNotSelected =
            viewModel._moaDocumentData.value?.agreementDuration.isNullOrBlank()

        val isDiscussedWithWhoomNotSelected =
            viewModel._moaDocumentData.value?.disscussedWithWhom.isNullOrBlank()

        val isDesignationNotSelected =
            viewModel._moaDocumentData.value?.designation.isNullOrBlank()

        val isMoaDocumentNotUploaded = viewModel._requestFile == null


        return if (isTotalInterestedIntakeNotFilled ||
            isCostingPerStudentNotFilled ||
            isDiscussedWithWhoomNotSelected ||
            isDesignationNotSelected ||
            isAgreementDurationNotSelected || isMoaDocumentNotUploaded
        ) {
            if (isMoaDocumentNotUploaded) {
                Toast.makeText(
                    requireContext(),
                    requireActivity().getString(com.phntechnolab.sales.R.string.please_upload_moa_document),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    requireActivity().getString(com.phntechnolab.sales.R.string.please_fill_all_the_mendate_details),
                    Toast.LENGTH_LONG
                ).show()
            }
            false
        } else {
            true
        }
    }

    private fun quotationValidity() {
        binding.proposeCostingStage.edtQuotationValidity.setOnClickListener {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if (!viewModel.proposeCostingData.value?.quotationValidity.isNullOrEmpty()) {
                viewModel.proposeCostingData.value?.quotationValidity?.split(" ")
                    ?.let { _dateAndTime ->
                        binding.proposeCostingStage.edtQuotationValidity.setText(_dateAndTime[0])
                        if (!_dateAndTime[0].trim().isNullOrBlank()) {
                            _dateAndTime[0].split("/").let { _dateArray ->
                                day = _dateArray[0].toInt()
                                month = _dateArray[1].toInt()
                                year = _dateArray[2].toInt()
                            }
                        }
                    }
            }

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val updatedDateAndTime =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    binding.proposeCostingStage.edtQuotationValidity.setText(updatedDateAndTime)
                    viewModel.proposeCostingData.value?.quotationValidity.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = (_nextFollowUpDate ?: "").split(" ")
                            viewModel.proposeCostingData.value?.quotationValidity =
                                "$updatedDateAndTime ${dateAndTime[1]}"
                        } else {
                            viewModel.proposeCostingData.value?.quotationValidity =
                                updatedDateAndTime
                        }

                        Timber.e("Date")
                        Timber.e(viewModel.proposeCostingData.value?.quotationValidity)

                        val c = Calendar.getInstance()

                        var hour = c.get(Calendar.HOUR)
                        var minute = c.get(Calendar.MINUTE)
                        if (!viewModel.proposeCostingData.value?.quotationValidity.isNullOrEmpty()) {
                            viewModel.proposeCostingData.value?.quotationValidity?.split(" ")
                                ?.let { _dateAndTime ->
                                    if (_dateAndTime.size > 1) {
                                        binding.proposeCostingStage.edtQuotationValidity.setText(
                                            _dateAndTime[0] + " " + _dateAndTime[1]
                                        )
                                        _dateAndTime[1].split(":").let { _timeArray ->
                                            hour = _timeArray[0].toInt()
                                            minute = _timeArray[1].toInt()
                                        }
                                    }
                                }
                        }

                        val timePickerDialog = TimePickerDialog(
                            requireContext(),
                            { view, hourOfDay, minute ->
                                val updatedTime = "$hourOfDay:$minute"
                                viewModel.proposeCostingData.value?.quotationValidity.let { _nextFollowUpDate ->
                                    if ((_nextFollowUpDate ?: "").contains(" ")) {
                                        val dateAndTime = _nextFollowUpDate?.split(" ")
                                        viewModel.proposeCostingData.value?.quotationValidity =
                                            "${dateAndTime?.get(0)} $updatedTime"
                                    } else {
                                        viewModel.proposeCostingData.value?.quotationValidity =
                                            "$_nextFollowUpDate $updatedTime"
                                    }

                                    Timber.e("Time")
                                    Timber.e(viewModel.proposeCostingData.value?.quotationValidity)

                                }

                                binding.proposeCostingStage.edtQuotationValidity.setText(viewModel.proposeCostingData.value?.quotationValidity)
                            },
                            hour,
                            minute,
                            false
                        )
                        timePickerDialog.show()

                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
    }

    private fun observers() {

        viewModel.proposeCostingDetails.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    setPositionView()
                }

                is NetworkResult.Error -> {
                    Timber.e(it.toString())
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.moaDocumentDetails.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    showDialog()
//                    setPositionView()
                }

                is NetworkResult.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Timber.e(it.toString())
                }

                else -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.proposeCostingData.observe(viewLifecycleOwner) {
            setProposeCostingDropdowns(it)
            initializeProposeCostingData(it)
        }

        viewModel.moaDocumentData.observe(viewLifecycleOwner) {
            setMoaDocumentDropdowns(it)
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        val dialogBinding = VisitedSuccessDialogBinding.inflate(layoutInflater)
        dialogBinding.title.text =
            requireActivity().resources.getString(com.phntechnolab.sales.R.string.great_work_title)
        dialogBinding.details.text =
            requireActivity().resources.getString(com.phntechnolab.sales.R.string.moa_success_dialog_message)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().popBackStack()
            dialog.dismiss()
        }, 3000)
    }

    private fun initializeProposeCostingData(proposeCostingData: ProposeCostingData?) {
        if (proposeCostingData?.priceDiscussed == "yes") binding.proposeCostingStage.priceDiscussedGroup.check(
            com.phntechnolab.sales.R.id.priceDiscussedYes
        )
        else binding.proposeCostingStage.priceDiscussedGroup.check(com.phntechnolab.sales.R.id.priceDiscussedNo)

        if (proposeCostingData?.quotationShared == "yes") binding.proposeCostingStage.quotationSharedGroup.check(
            com.phntechnolab.sales.R.id.quotationSharedYes
        )
        else binding.proposeCostingStage.quotationSharedGroup.check(com.phntechnolab.sales.R.id.quotationSharedNo)

        if (proposeCostingData?.paymentShedule == "yes") binding.proposeCostingStage.paymentScheduledLockedGroup.check(
            com.phntechnolab.sales.R.id.paymentScheduledLockedYes
        )
        else binding.proposeCostingStage.paymentScheduledLockedGroup.check(com.phntechnolab.sales.R.id.paymentScheduledLockedNo)

        binding.proposeCostingStage.edtQuotationValidity.setText(
            viewModel._proposeCostingData.value?.quotationValidity ?: ""
        )
//        viewModel._proposeCostingData.value?.quotationValidity.let {
//            val dateAndTime = it?.split(" ")
//            binding.proposeCostingStage.edtQuotationValidity.setText(
//                dateAndTime?.get(0))
//            if((dateAndTime?.size ?: 0) > 1)
//                binding.proposeCostingStage.edtQuotationValidity.setText(dateAndTime?.get(1) ?: "")
//        }

        viewModel._proposeCostingData.value?.nextMeet.let {
            val dateAndTime = it?.split(" ")
            binding.proposeCostingStage.edtDate.setText(
                dateAndTime?.get(0)
            )
            if ((dateAndTime?.size ?: 0) > 1)
                binding.proposeCostingStage.edtTime.setText(dateAndTime?.get(1) ?: "")
        }

    }

    private fun setPositionView() {
//        setButtonName(viewModel.oldSchoolData.value)

        when (position) {
            0 -> {
                binding.proposeCostingStage.root.visibility = View.GONE
                binding.moaDocument.root.visibility = View.VISIBLE
                position = 1
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            1 -> {
                binding.proposeCostingStage.root.visibility = View.GONE
                binding.moaDocument.root.visibility = View.VISIBLE
                position = 2
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            else -> {
                position = 0
                binding.stepView.done(true)
                binding.stepView.go(0, true)
            }
        }
    }

    private fun setProposeCostingDropdowns(proposeCostingData: ProposeCostingData?) {
        //Set agreement duration data

        val dropdown: AutoCompleteTextView = binding.proposeCostingStage.autoAgreementDuration
        val items = ArrayList<String>()
        if (proposeCostingData?.agreementDuration != null && !proposeCostingData.agreementDuration.isNullOrBlank()) {
            items.add(proposeCostingData.agreementDuration!!)
        }

        arrayOf("1 year", "3 year", "5 year").forEach {
            if (!items.any { itemName -> itemName.contains(it) }) {
                items.add(it)
            } else {
                binding.proposeCostingStage.autoAgreementDuration.setText(it)
            }
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                items
            )
        dropdown.setAdapter(adapter)

        //Meeting with whoom dropdown set

        val meetingWithWhoomDropdown: AutoCompleteTextView =
            binding.proposeCostingStage.autoMeetingWithWhom
        val meetingWithWhoomItem = ArrayList<String>()

        if (proposeCostingData?.meetingWithWhoom != null && !proposeCostingData.meetingWithWhoom.isNullOrBlank()) {
            meetingWithWhoomItem.add(proposeCostingData.meetingWithWhoom!!)
            if (proposeCostingData.meetingWithWhoom == "Other") {
                binding.proposeCostingStage.meetingWithWhoomOthers.visibility = View.VISIBLE
            } else {
                binding.proposeCostingStage.meetingWithWhoomOthers.visibility = View.GONE
            }
        }

        arrayOf(
            "Principal Level",
            "Director Level",
            "Other"
        ).forEach {
            if (!meetingWithWhoomItem.any { itemName -> itemName.contains(it) }) {
                meetingWithWhoomItem.add(it)
            } else {
                binding.proposeCostingStage.autoMeetingWithWhom.setText(it)
            }
        }
        val labsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                meetingWithWhoomItem
            )
        meetingWithWhoomDropdown.setAdapter(labsAdapter)

        //set conversation ratio dropdown

        val conversationRationDropdown: AutoCompleteTextView =
            binding.proposeCostingStage.autoConversionRatio
        val conversationRationItems = ArrayList<String>()

        if (proposeCostingData?.conversationRatio != null && !proposeCostingData.conversationRatio.isNullOrBlank()) {
            conversationRationItems.add(proposeCostingData.conversationRatio!!)
        }

        arrayOf(
            "High",
            "Medium",
            "Low"
        ).forEach {
            if (!conversationRationItems.any { itemName -> itemName.contains(it) }) {
                conversationRationItems.add(it)
            } else {
                binding.proposeCostingStage.autoConversionRatio.setText(it)
            }
        }
        val leadsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                conversationRationItems
            )
        conversationRationDropdown.setAdapter(leadsAdapter)
    }

    private fun setMoaDocumentDropdowns(moaDocumentData: MOADocumentData?) {

        //set moa file name
        if (moaDocumentData?.moaFile != null && !moaDocumentData.moaFile!!.startsWith("/tmp/")) {
//            download.downloadFile(moaDocumentData.moaFile!!, "moaDoc.pdf")
            try {
                val fileName = moaDocumentData.moaFile!!.substring(
                    moaDocumentData.moaFile!!.lastIndexOf('/') + 1
                )
                binding.moaDocument.documentFileName.text = fileName
            } catch (e: Exception) {
                e.printStackTrace()
                binding.moaDocument.documentFileName.text =
                    requireContext().resources.getString(com.phntechnolab.sales.R.string.select_file)
            }
            binding.moaDocument.moaDownload.visibility = View.VISIBLE
            binding.moaDocument.moaDownload.setOnClickListener {
                val sdf = SimpleDateFormat("ddMyyyyhhmmss")
                toastMsg(requireContext().resources.getString(com.phntechnolab.sales.R.string.downloading_start))
                download.downloadFile(moaDocumentData.moaFile!!, "MOAFile${sdf.format(Date())}.pdf")
            }
        } else {
            binding.moaDocument.documentFileName.text =
                requireContext().resources.getString(com.phntechnolab.sales.R.string.select_file)
            binding.moaDocument.moaDownload.visibility = View.GONE
        }

        //Set agreement duration data
        val dropdown: MaterialAutoCompleteTextView = binding.moaDocument.autoAgreementDuration
        val items = ArrayList<String>()
        if (moaDocumentData?.agreementDuration != null && !moaDocumentData.agreementDuration.isNullOrBlank()) {
            items.add(moaDocumentData.agreementDuration!!)
        }

        arrayOf("1 year", "3 year", "5 year").forEach {
            if (!items.any { itemName -> itemName.contains(it) }) {
                items.add(it)
            } else {
                binding.moaDocument.autoAgreementDuration.setText(it)
            }
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                items
            )
        dropdown.setAdapter(adapter)

        //Meeting with whoom dropdown set

//        val meetingWithWhoomDropdown: MaterialAutoCompleteTextView =
//            binding.moaDocument.edtDiscussedWithWhom
//        val meetingWithWhoomItem = ArrayList<String>()
//
//        if (moaDocumentData?.disscussedWithWhom != null && !moaDocumentData.disscussedWithWhom.isNullOrBlank()) {
//            meetingWithWhoomItem.add(moaDocumentData.disscussedWithWhom!!)
//            if (moaDocumentData.disscussedWithWhom == "Other") {
//                binding.moaDocument.meetingWithWhoomOthers.visibility = View.VISIBLE
//            } else {
//                binding.moaDocument.meetingWithWhoomOthers.visibility = View.GONE
//            }
//        }
//
//        arrayOf(
//            "Principal Level",
//            "Director Level",
//            "Other"
//        ).forEach {
//            if (!meetingWithWhoomItem.any { itemName -> itemName.contains(it) }) {
//                meetingWithWhoomItem.add(it)
//            } else {
//                binding.moaDocument.edtDiscussedWithWhom.setText(it)
//            }
//        }
//        val discussedWithWhoomAdapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(
//                requireContext(),
//                R.layout.simple_spinner_dropdown_item,
//                meetingWithWhoomItem
//            )
//        meetingWithWhoomDropdown.setAdapter(discussedWithWhoomAdapter)

        //select designation dropdown

        val designationDropdown: MaterialAutoCompleteTextView =
            binding.moaDocument.autoSelectDesignation
        val designationItems = ArrayList<String>()
        val array = arrayOf(
            "Principal", "Teacher", "Director", "Owner", "HOD", "Others"
        )
        if (moaDocumentData?.designation != null && !moaDocumentData.designation.isNullOrBlank()) {
            if (array.contains(moaDocumentData.designation) && moaDocumentData.designation != "Others") {
                designationItems.add(moaDocumentData.designation!!)
                binding.moaDocument.tilSelectDesignationOther.visibility = View.GONE
            } else {
                designationItems.add(moaDocumentData.designation!!)
                binding.moaDocument.autoSelectDesignation.setText(getString(com.phntechnolab.sales.R.string._designation))
                binding.moaDocument.edtSelectDesignationOther.setText(moaDocumentData.designation)
                binding.moaDocument.tilSelectDesignationOther.visibility = View.VISIBLE
            }
        }

        array.forEach {
            if (!designationItems.any { itemName -> itemName.contains(it) }) {
                designationItems.add(it)
            } else {
                binding.moaDocument.autoSelectDesignation.setText(it)
            }
        }
        val designationAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.simple_spinner_dropdown_item,
                designationItems
            )
        designationDropdown.setAdapter(designationAdapter)
    }

    private fun checkedChangeListener() {
        //price discussed checked box listener
        binding.proposeCostingStage.priceDiscussedGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._proposeCostingData.value?.priceDiscussed =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }

        //quotation shared checked box listener
        binding.proposeCostingStage.quotationSharedGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._proposeCostingData.value?.quotationShared =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }

        //price payment schedule checked box listener
        binding.proposeCostingStage.paymentScheduledLockedGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._proposeCostingData.value?.paymentShedule =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }
    }

    private fun nextMeetingDateTimeClickListener() {
        binding.proposeCostingStage.edtDate.setOnClickListener {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if (!viewModel.proposeCostingData.value?.nextMeet.isNullOrEmpty()) {
                viewModel.proposeCostingData.value?.nextMeet?.split(" ")?.let { _dateAndTime ->
                    binding.proposeCostingStage.edtDate.setText(_dateAndTime[0])
                    if (!_dateAndTime[0].trim().isNullOrBlank()) {
                        _dateAndTime[0].split("/").let { _dateArray ->
                            day = _dateArray[0].toInt()
                            month = _dateArray[1].toInt()
                            year = _dateArray[2].toInt()
                        }
                    }
                }
            }

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val updatedDateAndTime =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    binding.proposeCostingStage.edtDate.setText(updatedDateAndTime)
                    viewModel.proposeCostingData.value?.nextMeet.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = (_nextFollowUpDate ?: "").split(" ")
                            viewModel.proposeCostingData.value?.nextMeet =
                                "$updatedDateAndTime ${dateAndTime[1]}"
                        } else {
                            viewModel.proposeCostingData.value?.nextMeet = updatedDateAndTime
                        }

                        Timber.e("Date")
                        Timber.e(viewModel.proposeCostingData.value?.nextMeet)
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }

        binding.proposeCostingStage.edtTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR)
            var minute = c.get(Calendar.MINUTE)
            if (!viewModel.proposeCostingData.value?.nextMeet.isNullOrEmpty()) {
                viewModel.proposeCostingData.value?.nextMeet?.split(" ")?.let { _dateAndTime ->
                    if (_dateAndTime.size > 1) {
                        binding.proposeCostingStage.edtTime.setText(_dateAndTime[1])
                        _dateAndTime[1].split(":").let { _timeArray ->
                            hour = _timeArray[0].toInt()
                            minute = _timeArray[1].toInt()
                        }
                    }
                }
            }

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    val updatedTime = "$hourOfDay:$minute"
                    binding.proposeCostingStage.edtTime.setText(updatedTime)
                    viewModel.proposeCostingData.value?.nextMeet.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = _nextFollowUpDate?.split(" ")
                            viewModel.proposeCostingData.value?.nextMeet =
                                "${dateAndTime?.get(0)} $updatedTime"
                        } else {
                            viewModel.proposeCostingData.value?.nextMeet =
                                "$_nextFollowUpDate $updatedTime"
                        }

                        Timber.e("Time")
                        Timber.e(viewModel.proposeCostingData.value?.nextMeet)

                    }
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private var moaDocument = registerForActivityResult(ActivityResultContracts.GetContent()) {
        Timber.e("BACK")
        if (it != null) {
            image = it
            Timber.e(image.toString())
            viewModel.uploadDocument(it, requireContext())
            binding.moaDocument.documentFileName.text = "${viewModel.imageName}.pdf"
        }
    }

    override fun onStart() {
        super.onStart()
        setActionBar()

        setOnClickListeners()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).removeMenuProvider(this)
        activity?.removeMenuProvider(this)
    }

    private fun setActionBar() {
        (requireActivity() as MainActivity).setSupportActionBar(binding.topAppBar)
        activity?.addMenuProvider(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(com.phntechnolab.sales.R.menu.meeting_topbar_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            com.phntechnolab.sales.R.id.menu_home_n -> {
                findNavController().navigate(com.phntechnolab.sales.R.id.homeFragment)
                true
            }

            else -> {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
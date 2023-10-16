package com.phntechnolab.sales.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentCoordinatorDmMeetingBinding
import com.phntechnolab.sales.databinding.VisitedSuccessDialogBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.CoordinatorDmMeetingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import timber.log.Timber
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class CoordinatorDmMeetingFragment : Fragment() {

    private var _binding: FragmentCoordinatorDmMeetingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoordinatorDmMeetingViewModel by viewModels()

    private val args: CoordinatorDmMeetingFragmentArgs by navArgs()

    var position = 0

    private val backPressHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            when (position) {
                0 -> {
                    binding.topAppBar.title = getString(R.string.teacher_principal_meeting)
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.coordinatorMeeting.root.visibility = View.VISIBLE
                    binding.dmMeeting.root.visibility = View.GONE
                    position = 0
                    binding.topAppBar.title = getString(R.string.teacher_principal_meeting)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoordinatorDmMeetingBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)

        viewModel._coordinatorMeetData.postValue(args.coordinatorMeetingDetails)

        viewModel._dmMeetData.postValue(args.dmMeetingDetails)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observers()

        checkedChangeListener()

        rescheduleDateTimeClickListener()

        rescheduleDMDateTimeClickListener()

        nextMeetingDateTimeClickListener()

        nextDMMeetingDateTimeClickListener()

        initializeListener()


    }

    private fun initializeListener() {
        binding.topAppBar.setNavigationOnClickListener {
            when (position) {
                0 -> {
                    binding.topAppBar.title = getString(R.string.teacher_principal_meeting)
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.coordinatorMeeting.root.visibility = View.VISIBLE
                    binding.dmMeeting.root.visibility = View.GONE
                    position = 0
                    binding.topAppBar.title = getString(R.string.teacher_principal_meeting)
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

        binding.coordinatorMeeting.button.setOnClickListener {
            if (checkCoordinatorRequiredFieldsData()) {
                viewModel.updateCoordinatorDetails()
            }
        }

        binding.dmMeeting.updateBtn.setOnClickListener {
            if (checkDmRequiredFieldsData()) {
                binding.progressIndicator.visibility = View.VISIBLE
                viewModel.updatedMDetails(requireContext())
            }
        }

        binding.dmMeeting.autoMeetingAgenda.setOnItemClickListener { parent, view, position, id ->
            val meetingStatus = parent.adapter.getItem(position) as String
            if (meetingStatus == "Price discussion")
                viewModel._dmMeetData.value?.meetingStatus = "Propose Costing"
            else if (meetingStatus == "Demo/Discussion")
                viewModel._dmMeetData.value?.meetingStatus = "Visited"
        }
    }

    private fun checkCoordinatorRequiredFieldsData(): Boolean {
        val isCoordinatorAttendedMeet = viewModel._coordinatorMeetData.value?.coAttendMeet
        if (isCoordinatorAttendedMeet != "yes") {
            Toast.makeText(
                requireContext(),
                requireActivity().getString(R.string.please_attend_the_meeting_with_coordinator),
                Toast.LENGTH_LONG
            ).show()
        }

        val isInterested = viewModel._coordinatorMeetData.value?.interested == "yes"
        val isRescheduledMeeting = viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator

        if(isInterested) {
            if (isRescheduledMeeting == "yes") {
                if (viewModel._coordinatorMeetData.value?.meetDateCoordinator.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().getString(R.string.please_fill_rescheduled_date_for_proceed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                if (viewModel._coordinatorMeetData.value?.nextMeetDateDm.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().getString(R.string.please_next_rescheduled_date_for_proceed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
        val isRescheduledMeetingDateAvailableWithDate =
            !viewModel._coordinatorMeetData.value?.meetDateCoordinator.isNullOrBlank() && isRescheduledMeeting == "yes"
        val isNextMeetingDateAvailableWithDate =
            !viewModel._coordinatorMeetData.value?.nextMeetDateDm.isNullOrBlank() && isRescheduledMeeting != "yes"
        return if(!isInterested) {
            (isCoordinatorAttendedMeet == "yes") && !isInterested
        }else{
            (isCoordinatorAttendedMeet == "yes") && (isRescheduledMeetingDateAvailableWithDate || isNextMeetingDateAvailableWithDate)
        }
    }

    private fun checkDmRequiredFieldsData(): Boolean {
        val isDmAttendedMeet = viewModel._dmMeetData.value?.coAttendMeet
        if (isDmAttendedMeet != "yes") {
            Toast.makeText(
                requireContext(),
                requireActivity().getString(R.string.please_attend_the_meeting_with_director),
                Toast.LENGTH_LONG
            ).show()
        }

        val isMeetingAgenda = viewModel._dmMeetData.value?.meetingStatus
        if (isMeetingAgenda.isNullOrBlank()) {
            binding.dmMeeting.tilMeetingAgenda.error =
                requireActivity().getString(R.string.select_meeting_agenda)
        } else {
            binding.dmMeeting.tilMeetingAgenda.error = null
        }

        val isRescheduledMeeting = viewModel._dmMeetData.value?.rescheduleWithDirector
        val isInterested = viewModel._dmMeetData.value?.interested == "yes"

        if(isInterested) {
            if (isRescheduledMeeting == "yes") {
                if (viewModel._dmMeetData.value?.nextMeetDateDm.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().getString(R.string.please_fill_rescheduled_date_for_proceed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                if (viewModel._dmMeetData.value?.nextMeetDate.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().getString(R.string.please_next_rescheduled_date_for_proceed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val isRescheduledMeetingDateAvailableWithDate =
            !viewModel._dmMeetData.value?.rescheduleWithDirector.isNullOrBlank() && isRescheduledMeeting == "yes"
        val isNextMeetingDateAvailableWithDate =
            !viewModel._dmMeetData.value?.nextMeetDate.isNullOrBlank() && isRescheduledMeeting != "yes"

        return if(!isInterested){
            return (isDmAttendedMeet == "yes") && !isInterested
        }else {
            return (isDmAttendedMeet == "yes") && (!isMeetingAgenda.isNullOrBlank()) && (isRescheduledMeetingDateAvailableWithDate || isNextMeetingDateAvailableWithDate || isInterested)
        }
    }

    private fun checkedChangeListener() {

        //coordinator check box listener
        binding.coordinatorMeeting.attendedMeetGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._coordinatorMeetData.value?.coAttendMeet =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }

        binding.coordinatorMeeting.demoHappenedGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._coordinatorMeetData.value?.productDemoHappen =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }

        binding.coordinatorMeeting.rescheduleMeetingGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            if (checkedRadioButtonText == "Yes") {
                viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator = "yes"
                showRescheduleAndHideNextMeetingDate()
            } else {
                viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator = "no"
                hideRescheduleAndShowNextMeetingDate()
            }
        }

        binding.dmMeeting.rescheduleMeetingGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            if (checkedRadioButtonText == "Yes") {
                viewModel._dmMeetData.value?.rescheduleWithDirector = "yes"
                showDMRescheduleAndHideNextMeetingDate()
            } else {
                viewModel._dmMeetData.value?.rescheduleWithDirector = "no"
                hideDMRescheduleAndShowNextMeetingDate()
            }
        }

        binding.coordinatorMeeting.labSetupGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._coordinatorMeetData.value?.interested =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }

        //director check box listener
        binding.dmMeeting.attendedMeetGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._dmMeetData.value?.coAttendMeet =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }

        binding.dmMeeting.demoHappenedGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._dmMeetData.value?.productDemoHappen =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }

        binding.dmMeeting.nextMeetingGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            if (checkedRadioButtonText == "Yes") {
                viewModel._dmMeetData.value?.nextFollowup = "yes"
                binding.dmMeeting.nextMeetingDateAndTimeHeading.visibility = View.VISIBLE
                binding.dmMeeting.tilNextMeetingDate.visibility = View.VISIBLE
                binding.dmMeeting.tilNextMeetingTime.visibility = View.VISIBLE
            } else {
                viewModel._dmMeetData.value?.nextFollowup = "no"
                binding.dmMeeting.nextMeetingDateAndTimeHeading.visibility = View.GONE
                binding.dmMeeting.tilNextMeetingDate.visibility = View.GONE
                binding.dmMeeting.tilNextMeetingTime.visibility = View.GONE
            }
        }

        binding.dmMeeting.labSetupGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._dmMeetData.value?.interested =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
        }
    }

    private fun initializeCoordinatorData(_coordinatorData: CoordinatorData?) {
        if (_coordinatorData?.coAttendMeet == "yes") binding.coordinatorMeeting.attendedMeetGroup.check(
            R.id.attendedMeetYes
        )
        else binding.coordinatorMeeting.attendedMeetGroup.check(R.id.attendedMeetNo)

        if (_coordinatorData?.productDemoHappen == "yes") binding.coordinatorMeeting.demoHappenedGroup.check(
            R.id.demoHappenedYes
        )
        else binding.coordinatorMeeting.demoHappenedGroup.check(R.id.demoHappenedNo)

        if (_coordinatorData?.rescheduleWithCoordinator == "yes") {
            binding.coordinatorMeeting.rescheduleMeetingGroup.check(R.id.rescheduleMeetingYes)
            showRescheduleAndHideNextMeetingDate()
        } else {
            binding.coordinatorMeeting.rescheduleMeetingGroup.check(R.id.rescheduleMeetingNo)
            hideRescheduleAndShowNextMeetingDate()
        }

        if (_coordinatorData?.interested == "yes") binding.coordinatorMeeting.labSetupGroup.check(R.id.labSetupYes)
        else binding.coordinatorMeeting.labSetupGroup.check(R.id.labSetupNo)

        viewModel._coordinatorMeetData.value?.meetDateCoordinator.let {
            val dateAndTime = it?.split(" ")
            binding.coordinatorMeeting.edtRescheduleMeetingDate.setText(
                dateAndTime?.get(0)
            )
            if ((dateAndTime?.size ?: 0) > 1)
                binding.coordinatorMeeting.edtRescheduleMeetingTime.setText(
                    dateAndTime?.get(1) ?: ""
                )
        }

        viewModel._coordinatorMeetData.value?.nextMeetDateDm.let {
            val dateAndTime = it?.split(" ")
            binding.coordinatorMeeting.edtNextMeetingDate.setText(
                dateAndTime?.get(0)
            )
            if ((dateAndTime?.size ?: 0) > 1)
                binding.coordinatorMeeting.edtNextMeetingTime.setText(dateAndTime?.get(1) ?: "")
        }
    }

    private fun initializeDmData(_dmData: DMData?) {
        if (_dmData?.coAttendMeet == "yes") binding.dmMeeting.attendedMeetGroup.check(R.id.attendedMeetYes)
        else binding.dmMeeting.attendedMeetGroup.check(R.id.attendedMeetNo)

        if (_dmData?.productDemoHappen == "yes") binding.dmMeeting.demoHappenedGroup.check(R.id.demoHappenedYes)
        else binding.dmMeeting.demoHappenedGroup.check(R.id.demoHappenedNo)

        if (_dmData?.nextFollowup == "yes") {
            binding.dmMeeting.nextMeetingGroup.check(R.id.nextMeetingYes)
            binding.dmMeeting.nextMeetingDateAndTimeHeading.visibility = View.VISIBLE
            binding.dmMeeting.tilNextMeetingDate.visibility = View.VISIBLE
            binding.dmMeeting.tilNextMeetingTime.visibility = View.VISIBLE
        } else {
            binding.dmMeeting.nextMeetingGroup.check(R.id.nextMeetingNo)
            binding.dmMeeting.nextMeetingDateAndTimeHeading.visibility = View.GONE
            binding.dmMeeting.tilNextMeetingDate.visibility = View.GONE
            binding.dmMeeting.tilNextMeetingTime.visibility = View.GONE
        }

        if (_dmData?.interested == "yes") binding.dmMeeting.labSetupGroup.check(R.id.labSetupYes)
        else binding.dmMeeting.labSetupGroup.check(R.id.labSetupNo)


        if (_dmData?.rescheduleWithDirector == "yes") {
            binding.dmMeeting.rescheduleMeetingGroup.check(R.id.rescheduleMeetingYes)
            showDMRescheduleAndHideNextMeetingDate()
        } else {
            binding.dmMeeting.rescheduleMeetingGroup.check(R.id.rescheduleMeetingNo)
            hideDMRescheduleAndShowNextMeetingDate()
        }

        viewModel._dmMeetData.value?.nextMeetDateDm.let {
            val dateAndTime = it?.split(" ")
            binding.dmMeeting.edtRescheduleMeetingDate.setText(
                dateAndTime?.get(0)
            )
            if ((dateAndTime?.size ?: 0) > 1)
                binding.dmMeeting.edtRescheduleMeetingTime.setText(
                    dateAndTime?.get(1) ?: ""
                )
        }

        viewModel._dmMeetData.value?.nextMeetDate.let {
            val dateAndTime = it?.split(" ")
            binding.dmMeeting.edtNextMeetingDate.setText(
                dateAndTime?.get(0)
            )
            if ((dateAndTime?.size ?: 0) > 1)
                binding.dmMeeting.edtNextMeetingTime.setText(dateAndTime?.get(1) ?: "")
        }

        //Set meeting agenda

        val dropdown: MaterialAutoCompleteTextView = binding.dmMeeting.autoMeetingAgenda
        val items = ArrayList<String>().apply {
            add("Demo/Discussion")
            add("Price discussion")
        }

        if (viewModel.dmMeetData.value?.meetingStatus == "Propose Costing") {
            binding.dmMeeting.autoMeetingAgenda.setText("Price discussion")
        } else if (viewModel.dmMeetData.value?.meetingStatus == "Visited") {
            binding.dmMeeting.autoMeetingAgenda.setText("Demo/Discussion")
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        dropdown.setAdapter(adapter)
    }

    private fun observers() {
        viewModel.updateCoordinatorLevelMeetDetails.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    if (viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator != "yes" && viewModel._coordinatorMeetData.value?.interested == "yes") {
                        printToast(getString(R.string.coordinator_meeting_details_updated_successfully))
                        setPositionView()
                    } else if (viewModel._coordinatorMeetData.value?.interested == "yes" && viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator == "yes") {
                        printToast(getString(R.string.meeting_has_been_rescheduled_with_coordinator))
                        findNavController().popBackStack()
                    } else if (viewModel._coordinatorMeetData.value?.interested != "yes" && viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator != "yes") {
                        printToast(getString(R.string.meeting_has_been_moved_to_not_interested_section))
                        findNavController().popBackStack()
                    } else if (viewModel._coordinatorMeetData.value?.interested != "yes" && viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator == "yes") {
                        printToast(getString(R.string.meeting_has_been_moved_to_not_interested_section))
                        findNavController().popBackStack()
                    } else {
                        printToast(getString(R.string.coordinator_meeting_details_updated_successfully))
                        setPositionView()
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
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

        viewModel.updateDMLevelMeetDetails.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    if (viewModel._dmMeetData.value?.rescheduleWithDirector != "yes" && viewModel._dmMeetData.value?.interested == "yes") {
//                        printToast(getString(R.string.director_meeting_details_updated_successfully))
                        showDialog()
                    } else if (viewModel._dmMeetData.value?.interested == "yes" && viewModel._dmMeetData.value?.rescheduleWithDirector == "yes") {
                        printToast(getString(R.string.director_meeting_details_updated_successfully))
                        findNavController().popBackStack()
                    } else if (viewModel._dmMeetData.value?.interested != "yes" && viewModel._dmMeetData.value?.rescheduleWithDirector != "yes") {
                        printToast(getString(R.string.meeting_has_been_moved_to_not_interested_section))
                        findNavController().popBackStack()
                    } else if (viewModel._dmMeetData.value?.interested != "yes" && viewModel._dmMeetData.value?.rescheduleWithDirector == "yes") {
                        printToast(getString(R.string.meeting_has_been_moved_to_not_interested_section))
                        findNavController().popBackStack()
                    } else {
                        printToast(getString(R.string.director_meeting_details_updated_successfully))
                        findNavController().popBackStack()
                    }
                    viewModel.updateCoordinatorLevelMeetDetails.removeObservers(viewLifecycleOwner)
                    viewModel.updateDMLevelMeetDetails.removeObservers(viewLifecycleOwner)
                }

                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_LONG
                    ).show()
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

        viewModel.coordinatorMeetData.observe(viewLifecycleOwner) { _coordinatorData ->
            initializeCoordinatorData(_coordinatorData)
        }

        viewModel.dmMeetData.observe(viewLifecycleOwner) { _dmData ->
            initializeDmData(_dmData)

        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        val dialogBinding = VisitedSuccessDialogBinding.inflate(layoutInflater)
        dialogBinding.title.text = getString(R.string.well_done)
        dialogBinding.details.text = getString(R.string.dm_meeting_dialog_details)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            findNavController().popBackStack()
        }, 3000)

    }

    private fun setPositionView() {
//        setButtonName(viewModel.oldSchoolData.value)

        when (position) {
            0 -> {
                binding.topAppBar.title = getString(R.string.dm_level_meeting)
                Timber.e("0")
                binding.coordinatorMeeting.root.visibility = View.GONE
                binding.dmMeeting.root.visibility = View.VISIBLE
                position = 1
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            1 -> {
                Timber.e("1")
                binding.topAppBar.title = getString(R.string.dm_level_meeting)
                binding.coordinatorMeeting.root.visibility = View.GONE
                binding.dmMeeting.root.visibility = View.VISIBLE
                position = 2
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            else -> {
                binding.topAppBar.title = getString(R.string.teacher_principal_meeting)
                Timber.e("else")
                position = 0
                binding.stepView.done(true)
                binding.stepView.go(0, true)
            }
        }
    }

    private fun rescheduleDateTimeClickListener() {
        binding.coordinatorMeeting.edtRescheduleMeetingDate.setOnClickListener {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if (!viewModel._coordinatorMeetData.value?.meetDateCoordinator.isNullOrEmpty()) {
                viewModel._coordinatorMeetData.value?.meetDateCoordinator?.split(" ")
                    ?.let { _dateAndTime ->
                        binding.coordinatorMeeting.edtRescheduleMeetingDate.setText(_dateAndTime[0])
                        if(!_dateAndTime[0].trim().isNullOrBlank()) {
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
                    binding.coordinatorMeeting.edtRescheduleMeetingDate.setText(updatedDateAndTime)
                    viewModel._coordinatorMeetData.value?.meetDateCoordinator.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = (_nextFollowUpDate ?: "").split(" ")
                            viewModel._coordinatorMeetData.value?.meetDateCoordinator =
                                "$updatedDateAndTime ${dateAndTime[1]}"
                        } else {
                            viewModel._coordinatorMeetData.value?.meetDateCoordinator =
                                updatedDateAndTime
                        }

                        Timber.e("Date")
                        Timber.e(viewModel._coordinatorMeetData.value?.meetDateCoordinator)
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }

        binding.coordinatorMeeting.edtRescheduleMeetingTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR)
            var minute = c.get(Calendar.MINUTE)
            if (!viewModel._coordinatorMeetData.value?.meetDateCoordinator.isNullOrEmpty()) {
                viewModel._coordinatorMeetData.value?.meetDateCoordinator?.split(" ")
                    ?.let { _dateAndTime ->
                        if (_dateAndTime.size > 1) {
                            binding.coordinatorMeeting.edtRescheduleMeetingTime.setText(_dateAndTime[1])
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
                    binding.coordinatorMeeting.edtRescheduleMeetingTime.setText(updatedTime)
                    viewModel._coordinatorMeetData.value?.meetDateCoordinator?.let { _nextFollowUpDate ->
                        if (_nextFollowUpDate.contains(" ")) {
                            val dateAndTime = _nextFollowUpDate.split(" ")
                            viewModel._coordinatorMeetData.value?.meetDateCoordinator =
                                "${dateAndTime[0]} $updatedTime"
                        } else {
                            viewModel._coordinatorMeetData.value?.meetDateCoordinator =
                                "$_nextFollowUpDate $updatedTime"
                        }

                        Timber.e("Time")
                        Timber.e(viewModel._coordinatorMeetData.value?.meetDateCoordinator)

                    }
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun rescheduleDMDateTimeClickListener() {
        binding.dmMeeting.edtRescheduleMeetingDate.setOnClickListener {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if (!viewModel._dmMeetData.value?.nextMeetDateDm.isNullOrEmpty()) {
                viewModel._dmMeetData.value?.nextMeetDateDm?.split(" ")
                    ?.let { _dateAndTime ->
                        binding.dmMeeting.edtRescheduleMeetingDate.setText(_dateAndTime[0])
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
                    binding.dmMeeting.edtRescheduleMeetingDate.setText(updatedDateAndTime)
                    viewModel._dmMeetData.value?.nextMeetDateDm.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = (_nextFollowUpDate ?: "").split(" ")
                            viewModel._dmMeetData.value?.nextMeetDateDm =
                                "$updatedDateAndTime ${dateAndTime[1]}"
                        } else {
                            viewModel._dmMeetData.value?.nextMeetDateDm =
                                updatedDateAndTime
                        }

                        Timber.e("Date")
                        Timber.e(viewModel._dmMeetData.value?.nextMeetDateDm)
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }

        binding.dmMeeting.edtRescheduleMeetingTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR)
            var minute = c.get(Calendar.MINUTE)
            if (!viewModel._dmMeetData.value?.nextMeetDateDm.isNullOrEmpty()) {
                viewModel._dmMeetData.value?.nextMeetDateDm?.split(" ")
                    ?.let { _dateAndTime ->
                        if (_dateAndTime.size > 1) {
                            binding.dmMeeting.edtRescheduleMeetingTime.setText(_dateAndTime[1])
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
                    binding.dmMeeting.edtRescheduleMeetingTime.setText(updatedTime)
                    viewModel._dmMeetData.value?.nextMeetDateDm?.let { _nextFollowUpDate ->
                        if (_nextFollowUpDate.contains(" ")) {
                            val dateAndTime = _nextFollowUpDate.split(" ")
                            viewModel._dmMeetData.value?.nextMeetDateDm =
                                "${dateAndTime[0]} $updatedTime"
                        } else {
                            viewModel._dmMeetData.value?.nextMeetDateDm =
                                "$_nextFollowUpDate $updatedTime"
                        }

                        Timber.e("Time")
                        Timber.e(viewModel._dmMeetData.value?.nextMeetDateDm)

                    }
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun nextMeetingDateTimeClickListener() {
        binding.coordinatorMeeting.edtNextMeetingDate.setOnClickListener {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if (!viewModel._coordinatorMeetData.value?.nextMeetDateDm.isNullOrEmpty()) {
                viewModel._coordinatorMeetData.value?.nextMeetDateDm?.split(" ")
                    ?.let { _dateAndTime ->
                        binding.coordinatorMeeting.edtNextMeetingDate.setText(_dateAndTime[0])
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
                    binding.coordinatorMeeting.edtNextMeetingDate.setText(updatedDateAndTime)
                    viewModel._coordinatorMeetData.value?.nextMeetDateDm.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = (_nextFollowUpDate ?: "").split(" ")
                            viewModel._coordinatorMeetData.value?.nextMeetDateDm =
                                "$updatedDateAndTime ${dateAndTime[1]}"
                        } else {
                            viewModel._coordinatorMeetData.value?.nextMeetDateDm =
                                updatedDateAndTime
                        }

                        Timber.e("Date")
                        Timber.e(viewModel._coordinatorMeetData.value?.nextMeetDateDm)
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }

        binding.coordinatorMeeting.edtNextMeetingTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR)
            var minute = c.get(Calendar.MINUTE)
            if (!viewModel._coordinatorMeetData.value?.nextMeetDateDm.isNullOrEmpty()) {
                viewModel._coordinatorMeetData.value?.nextMeetDateDm?.split(" ")
                    ?.let { _dateAndTime ->
                        if (_dateAndTime.size > 1) {
                            binding.coordinatorMeeting.edtNextMeetingTime.setText(_dateAndTime[1])
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
                    binding.coordinatorMeeting.edtNextMeetingTime.setText(updatedTime)
                    viewModel._coordinatorMeetData.value?.nextMeetDateDm?.let { _nextFollowUpDate ->
                        if (_nextFollowUpDate.contains(" ")) {
                            val dateAndTime = _nextFollowUpDate.split(" ")
                            viewModel._coordinatorMeetData.value?.nextMeetDateDm =
                                "${dateAndTime[0]} $updatedTime"
                        } else {
                            viewModel._coordinatorMeetData.value?.nextMeetDateDm =
                                "$_nextFollowUpDate $updatedTime"
                        }

                        Timber.e("Time")
                        Timber.e(viewModel._coordinatorMeetData.value?.nextMeetDateDm)

                    }
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun nextDMMeetingDateTimeClickListener() {
        binding.dmMeeting.edtNextMeetingDate.setOnClickListener {
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if (!viewModel._dmMeetData.value?.nextMeetDate.isNullOrEmpty()) {
                viewModel._dmMeetData.value?.nextMeetDate?.split(" ")?.let { _dateAndTime ->
                    binding.dmMeeting.edtNextMeetingDate.setText(_dateAndTime[0])
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
                    binding.dmMeeting.edtNextMeetingDate.setText(updatedDateAndTime)
                    viewModel._dmMeetData.value?.nextMeetDate.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = (_nextFollowUpDate ?: "").split(" ")
                            viewModel._dmMeetData.value?.nextMeetDate =
                                "$updatedDateAndTime ${dateAndTime[1]}"
                        } else {
                            viewModel._dmMeetData.value?.nextMeetDate = updatedDateAndTime
                        }

                        Timber.e("Date")
                        Timber.e(viewModel._dmMeetData.value?.nextMeetDate)
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }

        binding.dmMeeting.edtNextMeetingTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR)
            var minute = c.get(Calendar.MINUTE)
            if (!viewModel._dmMeetData.value?.nextMeetDate.isNullOrEmpty()) {
                viewModel._dmMeetData.value?.nextMeetDate?.split(" ")?.let { _dateAndTime ->
                    if (_dateAndTime.size > 1) {
                        binding.dmMeeting.edtNextMeetingTime.setText(_dateAndTime[1])
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
                    binding.dmMeeting.edtNextMeetingTime.setText(updatedTime)
                    viewModel._dmMeetData.value?.nextMeetDate?.let { _nextFollowUpDate ->
                        if (_nextFollowUpDate.contains(" ")) {
                            val dateAndTime = _nextFollowUpDate.split(" ")
                            viewModel._dmMeetData.value?.nextMeetDate =
                                "${dateAndTime[0]} $updatedTime"
                        } else {
                            viewModel._dmMeetData.value?.nextMeetDate =
                                "$_nextFollowUpDate $updatedTime"
                        }

                        Timber.e("Time")
                        Timber.e(viewModel._dmMeetData.value?.nextMeetDate)

                    }
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun showRescheduleAndHideNextMeetingDate() {
        binding.coordinatorMeeting.meetingDateAndTimeHeading.visibility = View.VISIBLE
        binding.coordinatorMeeting.tilRescheduleMeetingDate.visibility = View.VISIBLE
        binding.coordinatorMeeting.tilRescheduleMeetingTime.visibility = View.VISIBLE
        binding.coordinatorMeeting.nextMeetingDateAndTimeHeading.visibility = View.GONE
        binding.coordinatorMeeting.tilNextMeetingDate.visibility = View.GONE
        binding.coordinatorMeeting.tilNextMeetingTime.visibility = View.GONE
    }

    private fun hideRescheduleAndShowNextMeetingDate() {
        binding.coordinatorMeeting.meetingDateAndTimeHeading.visibility = View.GONE
        binding.coordinatorMeeting.tilRescheduleMeetingDate.visibility = View.GONE
        binding.coordinatorMeeting.tilRescheduleMeetingTime.visibility = View.GONE
        binding.coordinatorMeeting.nextMeetingDateAndTimeHeading.visibility = View.VISIBLE
        binding.coordinatorMeeting.tilNextMeetingDate.visibility = View.VISIBLE
        binding.coordinatorMeeting.tilNextMeetingTime.visibility = View.VISIBLE
    }

    private fun showDMRescheduleAndHideNextMeetingDate() {
        binding.dmMeeting.meetingDateAndTimeHeading.visibility = View.VISIBLE
        binding.dmMeeting.tilRescheduleMeetingDate.visibility = View.VISIBLE
        binding.dmMeeting.tilRescheduleMeetingTime.visibility = View.VISIBLE
        binding.dmMeeting.nextMeetingDateAndTimeHeading.visibility = View.GONE
        binding.dmMeeting.tilNextMeetingDate.visibility = View.GONE
        binding.dmMeeting.tilNextMeetingTime.visibility = View.GONE
    }

    private fun hideDMRescheduleAndShowNextMeetingDate() {
        binding.dmMeeting.meetingDateAndTimeHeading.visibility = View.GONE
        binding.dmMeeting.tilRescheduleMeetingDate.visibility = View.GONE
        binding.dmMeeting.tilRescheduleMeetingTime.visibility = View.GONE
        binding.dmMeeting.nextMeetingDateAndTimeHeading.visibility = View.VISIBLE
        binding.dmMeeting.tilNextMeetingDate.visibility = View.VISIBLE
        binding.dmMeeting.tilNextMeetingTime.visibility = View.VISIBLE
    }

    fun printToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    override fun onDestroyView() {
        _binding = null
        position = 0
        viewModel.defaultScope.cancel()
//        viewModel.job.cancel()
        super.onDestroyView()
    }
}
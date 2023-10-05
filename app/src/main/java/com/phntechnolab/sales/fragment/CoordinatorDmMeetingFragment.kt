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
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentCoordinatorDmMeetingBinding
import com.phntechnolab.sales.databinding.VisitedSuccessDialogBinding
import com.phntechnolab.sales.model.CoordinatorData
import com.phntechnolab.sales.model.DMData
import com.phntechnolab.sales.model.MOADocumentData
import com.phntechnolab.sales.model.ProposeCostingData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.CoordinatorDmMeetingViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Calendar

@AndroidEntryPoint
class CoordinatorDmMeetingFragment : Fragment() {

    private var _binding: FragmentCoordinatorDmMeetingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoordinatorDmMeetingViewModel by viewModels()

    private val args: CoordinatorDmMeetingFragmentArgs by navArgs()

    var position = 0

    private val backPressHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
//            setButtonName(viewModel.oldSchoolData.value)

            when (position) {
                0 -> {
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.coordinatorMeeting.root.visibility = View.VISIBLE
                    binding.dmMeeting.root.visibility = View.GONE
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

        nextMeetingDateTimeClickListener()

        nextDMMeetingDateTimeClickListener()

        binding.coordinatorMeeting.button.setOnClickListener {
            viewModel.updateCoordinatorDetails()
        }

        binding.dmMeeting.button.setOnClickListener {
            viewModel.updatedMDetails()
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
                binding.coordinatorMeeting.meetingDateAndTimeHeading.visibility = View.VISIBLE
                binding.coordinatorMeeting.tilRescheduleMeetingDate.visibility = View.VISIBLE
                binding.coordinatorMeeting.tilRescheduleMeetingTime.visibility = View.VISIBLE
            } else {
                viewModel._coordinatorMeetData.value?.rescheduleWithCoordinator = "no"
                binding.coordinatorMeeting.meetingDateAndTimeHeading.visibility = View.GONE
                binding.coordinatorMeeting.tilRescheduleMeetingDate.visibility = View.GONE
                binding.coordinatorMeeting.tilRescheduleMeetingTime.visibility = View.GONE
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
            viewModel._dmMeetData.value?.nextFollowupDm =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
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

        if (_coordinatorData?.rescheduleWithCoordinator == "yes") binding.coordinatorMeeting.rescheduleMeetingGroup.check(
            R.id.rescheduleMeetingYes
        )
        else binding.coordinatorMeeting.rescheduleMeetingGroup.check(R.id.rescheduleMeetingNo)

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

        if (_dmData?.nextFollowupDm == "yes") binding.dmMeeting.nextMeetingGroup.check(R.id.nextMeetingYes)
        else binding.dmMeeting.nextMeetingGroup.check(R.id.nextMeetingNo)

        if (_dmData?.interested == "yes") binding.dmMeeting.labSetupGroup.check(R.id.labSetupYes)
        else binding.dmMeeting.labSetupGroup.check(R.id.labSetupNo)

        viewModel._dmMeetData.value?.nextMeetDateDm.let {
            val dateAndTime = it?.split(" ")
            binding.dmMeeting.edtNextMeetingDate.setText(
                dateAndTime?.get(0)
            )
            if ((dateAndTime?.size ?: 0) > 1)
                binding.dmMeeting.edtNextMeetingTime.setText(dateAndTime?.get(1) ?: "")
        }
    }

    private fun observers() {
        viewModel.updateCoordinatorLevelMeetDetails.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(requireContext(), getString(R.string.coordinator_meeting_details_updated_successfully), Toast.LENGTH_LONG).show()
                    setPositionView()
                }

                is NetworkResult.Error -> {
                    Timber.e(it.toString())
                    Toast.makeText(requireContext(), requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                }

                else -> {
                    Toast.makeText(requireContext(), requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.updateDMLevelMeetDetails.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    showDialog()
                }

                is NetworkResult.Error -> {
                    Timber.e(it.toString())
                    Toast.makeText(requireContext(), requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
                }

                else -> {
                    Toast.makeText(requireContext(), requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please), Toast.LENGTH_LONG).show()
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

            requireView().findNavController()
                .navigate(
                    CoordinatorDmMeetingFragmentDirections.actionCoordinatordmmeetingToCostingMoaDocumentFragment (
                        ProposeCostingData(schoolId = viewModel._coordinatorMeetData.value?.schoolId),
                        MOADocumentData(schoolId = viewModel._coordinatorMeetData.value?.schoolId)
                    )
                )
        }, 3000)

    }

    private fun setPositionView() {
//        setButtonName(viewModel.oldSchoolData.value)

        when (position) {
            0 -> {
                binding.topAppBar.title = getString(R.string.teacher_principal_meeting)
                Timber.e("0")
                binding.coordinatorMeeting.root.visibility = View.GONE
                binding.dmMeeting.root.visibility = View.VISIBLE
                position = 1
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            1 -> {
                binding.topAppBar.title = getString(R.string.dm_level_meeting)
                Timber.e("1")
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
                        _dateAndTime[0].split("/").let { _dateArray ->
                            day = _dateArray[0].toInt()
                            month = _dateArray[1].toInt()
                            year = _dateArray[2].toInt()
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

            datePickerDialog.datePicker.minDate = Calendar.getInstance().apply {
                this.set(Calendar.DAY_OF_MONTH, day)
                this.set(Calendar.MONTH, month)
                this.set(Calendar.YEAR, year)
            }.timeInMillis - 1000
            datePickerDialog.show()
        }

        binding.coordinatorMeeting.edtRescheduleMeetingTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR_OF_DAY)
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
                        _dateAndTime[0].split("/").let { _dateArray ->
                            day = _dateArray[0].toInt()
                            month = _dateArray[1].toInt()
                            year = _dateArray[2].toInt()
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

            datePickerDialog.datePicker.minDate = Calendar.getInstance().apply {
                this.set(Calendar.DAY_OF_MONTH, day)
                this.set(Calendar.MONTH, month)
                this.set(Calendar.YEAR, year)
            }.timeInMillis - 1000
            datePickerDialog.show()
        }

        binding.coordinatorMeeting.edtNextMeetingTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR_OF_DAY)
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
            if (!viewModel._dmMeetData.value?.nextMeetDateDm.isNullOrEmpty()) {
                viewModel._dmMeetData.value?.nextMeetDateDm?.split(" ")?.let { _dateAndTime ->
                    binding.dmMeeting.edtNextMeetingDate.setText(_dateAndTime[0])
                    _dateAndTime[0].split("/").let { _dateArray ->
                        day = _dateArray[0].toInt()
                        month = _dateArray[1].toInt()
                        year = _dateArray[2].toInt()
                    }
                }
            }

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val updatedDateAndTime =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    binding.dmMeeting.edtNextMeetingDate.setText(updatedDateAndTime)
                    viewModel._dmMeetData.value?.nextMeetDateDm.let { _nextFollowUpDate ->
                        if ((_nextFollowUpDate ?: "").contains(" ")) {
                            val dateAndTime = (_nextFollowUpDate ?: "").split(" ")
                            viewModel._dmMeetData.value?.nextMeetDateDm =
                                "$updatedDateAndTime ${dateAndTime[1]}"
                        } else {
                            viewModel._dmMeetData.value?.nextMeetDateDm = updatedDateAndTime
                        }

                        Timber.e("Date")
                        Timber.e(viewModel._dmMeetData.value?.nextMeetDateDm)
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().apply {
                this.set(Calendar.DAY_OF_MONTH, day)
                this.set(Calendar.MONTH, month)
                this.set(Calendar.YEAR, year)
            }.timeInMillis - 1000
            datePickerDialog.show()
        }

        binding.dmMeeting.edtNextMeetingTime.setOnClickListener {
            val c = Calendar.getInstance()

            var hour = c.get(Calendar.HOUR_OF_DAY)
            var minute = c.get(Calendar.MINUTE)
            if (!viewModel._dmMeetData.value?.nextMeetDateDm.isNullOrEmpty()) {
                viewModel._dmMeetData.value?.nextMeetDateDm?.split(" ")?.let { _dateAndTime ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
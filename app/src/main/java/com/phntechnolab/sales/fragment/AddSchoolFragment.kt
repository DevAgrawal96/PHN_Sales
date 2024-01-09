package com.phntechnolab.sales.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.databinding.FragmentAssignedSchoolsStepperBinding
import com.phntechnolab.sales.databinding.VisitedSuccessDialogBinding
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.TakePictureFromCamera
import com.phntechnolab.sales.util.TakePictureFromCameraOrGalley
import com.phntechnolab.sales.util.getFileSize
import com.phntechnolab.sales.util.hideKeyboard
import com.phntechnolab.sales.util.hideSoftKeyboard
import com.phntechnolab.sales.util.isNotNullOrZero
import com.phntechnolab.sales.util.isValidEmail
import com.phntechnolab.sales.util.isValidMobileNumber
import com.phntechnolab.sales.util.isValidName
import com.phntechnolab.sales.util.isValidNumber
import com.phntechnolab.sales.util.pickDate
import com.phntechnolab.sales.util.pickTime
import com.phntechnolab.sales.util.setupUI
import com.phntechnolab.sales.util.textChange
import com.phntechnolab.sales.util.toastMsg
import com.phntechnolab.sales.viewmodel.AddSchoolViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.regex.Pattern


@AndroidEntryPoint
class AddSchoolFragment : Fragment(), MenuProvider {

    private var _binding: FragmentAssignedSchoolsStepperBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddSchoolViewModel by viewModels()

    private val args: AddSchoolFragmentArgs by navArgs()

    var position = 0
    var _imageUri: Uri? = null

    private val backPressHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setButtonName(viewModel.oldSchoolData.value)

            when (position) {
                0 -> {
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.basicDetails.root.visibility = View.VISIBLE
                    binding.schoolDetails.root.visibility = View.GONE
                    position = 0
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                2 -> {
                    binding.schoolDetails.root.visibility = View.VISIBLE
                    binding.followupDetails.root.visibility = View.GONE
                    position = 1
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                else -> {
                    position = 3
                    binding.stepView.done(true)
                    binding.stepView.go(0, true)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_assigned_schools_stepper,
            container,
            false
        )

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)

        viewModel.setOldSchoolData(args.schoolData)

        setTopBarTitle()

        setButtonName(args.schoolData)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)

        onCheckedChangedListener()

        observers()

    }


    private fun onCheckedChangedListener() {
        binding.followupDetails.labSetupGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButtonText = group.findViewById<RadioButton>(checkedId).text
            viewModel._newSchoolData.value?.interested =
                if (checkedRadioButtonText == "Yes") "yes" else "no"
            viewModel._newSchoolData.value?.interested?.let { Log.e("CHecked box", it) }
        }

        binding.basicDetails.edtSchoolTotalIntake.textChange { intake ->
            try {
                viewModel._newSchoolData.value?.intake = if(intake == "") 0 else intake.toInt()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        binding.basicDetails.edtTotalNoOfClassroom.textChange { classRoom ->
            try {
                viewModel._newSchoolData.value?.totalClassRoom = if(classRoom == "") 0 else classRoom.toInt()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    private fun addValidationWatchers() {
        binding.basicDetails.edtSchoolName.textChange { name ->
            binding.basicDetails.tilSchoolName.error = if (isValidName(
                    name,
                    resources.getString(R.string.please_enter_valid_school_name)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidName(
                    name,
                    resources.getString(R.string.please_enter_valid_school_name)
                ).toString()
            }

        }
        binding.basicDetails.edtSchoolAddress.textChange { address ->
            binding.basicDetails.tilSchoolAddress.error = if (isValidName(
                    address,
                    resources.getString(R.string.please_enter_valid_school_address)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidName(
                    address,
                    resources.getString(R.string.please_enter_valid_school_address)
                ).toString()
            }
        }
        binding.basicDetails.edtSchoolName.textChange { name ->
            binding.basicDetails.tilSchoolAddress.error = if (isValidName(
                    name,
                    resources.getString(R.string.please_enter_valid_school_address)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidName(
                    name,
                    resources.getString(R.string.please_enter_valid_school_address)
                ).toString()
            }
        }
        binding.basicDetails.boardSpinner.textChange { board ->
            binding.basicDetails.tilBoard.error = if (isValidName(
                    board,
                    resources.getString(R.string.please_select_board)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidName(
                    board,
                    resources.getString(R.string.please_select_board)
                ).toString()
            }
        }
        binding.basicDetails.edtSchoolTotalIntake.textChange { intake ->
            binding.basicDetails.tilSchoolTotalIntake.error = if (isNotNullOrZero(
                    intake,
                    resources.getString(R.string.please_enter_total_school_intake)
                ).toString() == "null"
            ) {
                ""
            } else {
                isNotNullOrZero(
                    intake,
                    resources.getString(R.string.please_enter_total_school_intake)
                ).toString()
            }
        }
        binding.basicDetails.edtTotalNoOfClassroom.textChange { classroom ->
            binding.basicDetails.tilSchoolTotalNoOfClassroom.error = if (isNotNullOrZero(
                    classroom,
                    resources.getString(R.string.please_enter_number_of_classrooms)
                ).toString() == "null"
            ) {
                ""
            } else {
                isNotNullOrZero(
                    classroom,
                    resources.getString(R.string.please_enter_number_of_classrooms)
                ).toString()
            }
        }
        binding.basicDetails.edtCoordinatorName.textChange { name ->
            binding.basicDetails.tilCoordinatorName.error = if (isValidName(
                    name,
                    resources.getString(R.string.please_enter_valid_coordinator_name)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidName(
                    name,
                    resources.getString(R.string.please_enter_valid_coordinator_name)
                ).toString()
            }
        }
        binding.basicDetails.edtEmailId.textChange { email ->
            binding.basicDetails.tilEmailId.error = if (isValidEmail(
                    email,
                    resources.getString(R.string.please_enter_valid_email_address)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidEmail(
                    email,
                    resources.getString(R.string.please_enter_valid_email_address)
                ).toString()
            }
        }
        binding.basicDetails.edtCoordinatorMono.textChange { coordinator ->
            binding.basicDetails.tilCoordinatorMono.error = if (isValidMobileNumber(
                    coordinator,
                    resources.getString(R.string.please_enter_valid_phone_no)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidMobileNumber(
                    coordinator,
                    resources.getString(R.string.please_enter_valid_phone_no)
                ).toString()
            }
        }
        binding.schoolDetails.edtDirectorDmPhoneNo.textChange { number ->
            binding.schoolDetails.tilDirectorDmPhoneNo.error = if (isValidNumber(
                    number,
                    resources.getString(R.string.please_enter_valid_phone_no)
                ).toString() == "null"
            ) {
                ""
            } else {
                isValidNumber(
                    number,
                    resources.getString(R.string.please_enter_valid_phone_no)
                ).toString()
            }
        }
    }

    private fun setDropdowns(_schoolData: SchoolData?) {
        //Set board spinner data

        val dropdown: AutoCompleteTextView = binding.basicDetails.boardSpinner
        val items = ArrayList<String>()
        if (_schoolData?.board != null && !_schoolData.board.isNullOrBlank()) {
            items.add(_schoolData.board)
        }

        arrayOf("State Board", "CBSE", "ICSE", "NIOS", "IB", "CIE").forEach {
            if (!items.any { itemName -> itemName.contains(it) }) {
                items.add(it)
            } else {
                binding.basicDetails.boardSpinner.setText(it)
            }
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        dropdown.setAdapter(adapter)

        //Labs dropdown set

        setLabsDialog()

        //set lead type dropdown

        val leadTypeDropdown: AutoCompleteTextView = binding.followupDetails.edtLeadType
        val leadTypes = ArrayList<String>()

        if (_schoolData?.leadType != null && !_schoolData.leadType.isNullOrBlank()) {
            leadTypes.add(_schoolData.leadType)
        }

        arrayOf(
            "Hot",
            "Cool",
            "Warm",
            "Dead",
        ).forEach {
            if (!leadTypes.any { itemName -> itemName.contains(it) }) {
                leadTypes.add(it)
            } else {
                binding.followupDetails.edtLeadType.setText(it)
            }
        }
        val leadsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                leadTypes
            )
        leadTypeDropdown.setAdapter(leadsAdapter)
    }

    private fun setLabsDialog() {
        binding.schoolDetails.existingLabs.setOnClickListener {
            openMultiSelectionDialog()
        }
    }

    private fun openMultiSelectionDialog() {
        val labsData = arrayOf(
            "Science Lab",
            "Computer Lab",
            "Engineering and Robotics Lab",
            "Art and Creativity Lab",
            "Environmental Science Lab",
            "Music and Audio Lab",
            "Physics and Electronics Lab",
            "Chemistry Lab",
            "Biology Lab"
        )
        val selectedLabs = BooleanArray(labsData.size)

        val labList = ArrayList<Int>()

        labsData.forEachIndexed { index, s ->
            if (viewModel.newSchoolData.value?.existingLab?.contains(s) == true) {
                selectedLabs[index] = true
                labList.add(index)

            } else {
                selectedLabs[index] = false
            }
        }
        // Initialize alert dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        // set title
        builder.setTitle("Select existing labs in school")

        // set dialog non cancelable
        builder.setCancelable(false)

        builder.setMultiChoiceItems(
            labsData, selectedLabs
        ) { dialogInterface, i, b ->
            // check condition
            if (b) {
                // Add position  in lang list
                labList.add(i)
                // Sort array list
                labList.sort()
            } else {
                // when checkbox unselected
                // Remove position from langList
                labList.remove(Integer.valueOf(i))
            }
        }

        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i -> // Initialize string builder
            val stringBuilder = StringBuilder()
            // use for loop
            for (j in 0 until labList.size) {
                // concat array value
                stringBuilder.append(labsData[labList[j]])
                // check condition
                if (j != labList.size - 1) {
                    // When j value  not equal
                    // to lang list size - 1
                    // add comma
                    stringBuilder.append(", ")
                }
            }
            // set text on textView
            binding.schoolDetails.existingLabs.setText(stringBuilder.toString())
            viewModel._newSchoolData.value?.existingLab = stringBuilder.toString()
        }

        builder.setNegativeButton(
            "Cancel"
        ) { dialogInterface, i -> // dismiss dialog
            dialogInterface.dismiss()
        }

        builder.setNeutralButton(
            "Clear All"
        ) { dialogInterface, i ->
            // use for loop
            for (j in selectedLabs.indices) {
                // remove all selection
                selectedLabs[j] = false
                // clear language list
                labList.clear()
                // clear text view value
                binding.schoolDetails.existingLabs.setText("")
            }
        }
        // show dialog
        builder.show()
        hideKeyboard()
    }

    private fun observers() {

        viewModel.uploadImgResponse.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {

                    if (viewModel.newSchoolData.value?.interested == "yes")
                        Toast.makeText(
                            requireContext(),
                            "School details updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.meeting_has_been_moved_to_not_interested_section),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    findNavController().popBackStack()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                    Timber.e(it.toString())
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.newSchoolData.observe(viewLifecycleOwner) { _schoolData ->
            setSchoolDetails()
            if (!_schoolData?.schoolImage.isNullOrEmpty()) {
                binding.schoolDetails.schoolImage.visibility = View.VISIBLE
                binding.schoolDetails.imgIcon.visibility = View.GONE
                binding.schoolDetails.imgName.visibility = View.GONE
                Glide.with(requireContext()).load(_schoolData?.schoolImage).override(140, 140)
                    .error(R.drawable.demo_img).into(binding.schoolDetails.schoolImage)
            } else {
                binding.schoolDetails.schoolImage.visibility = View.GONE
                binding.schoolDetails.imgIcon.visibility = View.VISIBLE
                binding.schoolDetails.imgName.visibility = View.VISIBLE
            }

            setDropdowns(_schoolData)

        }

        viewModel.addSchoolResponse.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            binding.progressBar.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    if (viewModel.newSchoolData.value?.interested == "yes")
                        showDialog()
                    else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.meeting_has_been_moved_to_not_interested_section),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }

                is NetworkResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                    Timber.e(it.toString())
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        viewModel.updateSchoolResponse.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            binding.progressBar.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    if (viewModel._requestFile != null)
                        viewModel.uploadImage()
                    else {
                        if (viewModel.newSchoolData.value?.interested != "yes")
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.meeting_has_been_moved_to_not_interested_section),
                                Toast.LENGTH_SHORT
                            ).show()
                        toastMsg(requireContext().resources.getString(R.string.update_school))
                        findNavController().popBackStack()
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                    Timber.e(it.toString())
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().resources.getString(com.phntechnolab.sales.R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        val dialogBinding = VisitedSuccessDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            findNavController().popBackStack()
        }, 3000)
    }

    private var cameraResult =
        registerForActivityResult(TakePictureFromCamera) { imageUri ->
            if (imageUri != null && getFileSize(imageUri, requireContext()) > 0) {
                Timber.e("$imageUri")
                viewModel.uploadImage(imageUri, requireContext())
                binding.schoolDetails.imgName.visibility = View.VISIBLE
                binding.schoolDetails.imgIcon.visibility = View.VISIBLE
                binding.schoolDetails.imgName.text = "${viewModel.imageName}.jpg"
                binding.schoolDetails.schoolImage.visibility = View.GONE
            }
        }

    private fun initializeListener() {
        binding.topBar.setNavigationOnClickListener {
            when (position) {
                0 -> {
                    findNavController().popBackStack()
                }

                1 -> {
                    binding.basicDetails.root.visibility = View.VISIBLE
                    binding.schoolDetails.root.visibility = View.GONE
                    position = 0
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                2 -> {
                    binding.schoolDetails.root.visibility = View.VISIBLE
                    binding.followupDetails.root.visibility = View.GONE
                    position = 1
                    binding.stepView.done(false)
                    binding.stepView.go(position, true)
                }

                else -> {
                    position = 3
                    binding.stepView.done(true)
                    binding.stepView.go(0, true)
                }
            }
        }

        binding.schoolDetails.selectFileContainer.setOnClickListener {
            cameraResult.launch(Unit)
        }


        binding.basicDetails.btnSave.setOnClickListener {
            Timber.d("data binding data")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            addValidationWatchers()
            checkValidationsAndApiCall(1)
            hideKeyboard()
        }

        binding.schoolDetails.btnNext.setOnClickListener {
            Timber.d("data binding data 2")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            checkValidationsAndApiCall(2)
            hideKeyboard()
        }

        binding.followupDetails.btnSave.setOnClickListener {
            Timber.d("data binding data 3")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            checkValidationsAndApiCall(3)
            hideKeyboard()
        }

        binding.basicDetails.boardSpinner.setOnItemClickListener { parent, view, position, id ->
            val updatedBoardName = parent.adapter.getItem(position) as String
            viewModel._newSchoolData.value?.board = updatedBoardName
        }

        binding.schoolDetails.existingLabs.setOnItemClickListener { parent, view, position, id ->
            val updatedLabName = parent.adapter.getItem(position) as String
            viewModel._newSchoolData.value?.existingLab = updatedLabName
        }

        binding.followupDetails.edtLeadType.setOnItemClickListener { parent, view, position, id ->
            val updatedLeadType = parent.adapter.getItem(position) as String
            viewModel._newSchoolData.value?.leadType = updatedLeadType
        }

        binding.followupDetails.edtFollowType.setOnItemClickListener { parent, view, position, id ->
            val updatedFollowUpLead = parent.adapter.getItem(position) as String
            viewModel._newSchoolData.value?.nextFollowup = updatedFollowUpLead
        }

        binding.followupDetails.edtSchoolDate.setOnClickListener {
            hideKeyboard()
            val c = Calendar.getInstance()
            var year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
            var day = c.get(Calendar.DAY_OF_MONTH)
            if (!viewModel.newSchoolData.value?.nextFollowup.isNullOrEmpty()) {
                viewModel.newSchoolData.value?.nextFollowup?.split(" ")?.let { _dateAndTime ->
                    if (!_dateAndTime[0].trim().isNullOrBlank()) {
                        binding.followupDetails.edtSchoolDate.setText(_dateAndTime[0])
                        _dateAndTime[0].split("/").let { _dateArray ->
                            day = _dateArray[0].toInt()
                            month = _dateArray[1].toInt() - 1
                            year = _dateArray[2].toInt()
                        }
                    }
                }
            }
            pickDate(day, month, year) { _year, monthOfYear, dayOfMonth ->
                val updatedDateAndTime = if (dayOfMonth < 10){
                    "0$dayOfMonth" + "/" + (monthOfYear + 1) + "/" + _year
                }else{
                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + _year
                }
                binding.followupDetails.edtSchoolDate.setText(updatedDateAndTime)
                viewModel.newSchoolData.value?.nextFollowup =
                    if (viewModel.newSchoolData.value?.nextFollowup.isNullOrEmpty()) "null" else viewModel.newSchoolData.value?.nextFollowup.toString()
                viewModel.newSchoolData.value?.nextFollowup?.let { _nextFollowUpDate ->
                    if (_nextFollowUpDate.contains(" ")) {
                        val dateAndTime = _nextFollowUpDate.split(" ")
                        viewModel.newSchoolData.value?.nextFollowup =
                            "$updatedDateAndTime ${dateAndTime[1]}"
                    } else {
                        viewModel.newSchoolData.value?.nextFollowup = updatedDateAndTime
                    }

                    Timber.e("Date")
                    Timber.e(viewModel.newSchoolData.value?.nextFollowup)
                }
            }
        }

        binding.followupDetails.edtSchoolTime.setOnClickListener {
            hideKeyboard()
            val c = Calendar.getInstance()
            var minute = c.get(Calendar.MINUTE)
            var hour = c.get(Calendar.HOUR)
            Timber.e(c.get(Calendar.HOUR).toString())
            if (!viewModel.newSchoolData.value?.nextFollowup.isNullOrEmpty()) {
                viewModel.newSchoolData.value?.nextFollowup?.split(" ")?.let { _dateAndTime ->
                    if (_dateAndTime.size > 1) {
                        binding.followupDetails.edtSchoolTime.setText(_dateAndTime[1])
                        _dateAndTime[1].split(":").let { _timeArray ->
                            hour = _timeArray[0].toInt()
                            minute = _timeArray[1].toInt()
                        }
                    }
                }
            }

            pickTime(hour, minute, false) { hourOfDay, _minute ->
                val updatedTime = "$hourOfDay:$_minute"
                binding.followupDetails.edtSchoolTime.setText(updatedTime)
                viewModel.newSchoolData.value?.nextFollowup?.let { _nextFollowUpDate ->
                    if (_nextFollowUpDate.contains(" ")) {
                        val dateAndTime = _nextFollowUpDate.split(" ")
                        viewModel.newSchoolData.value?.nextFollowup =
                            "${dateAndTime[0]} $updatedTime"
                    } else {
                        viewModel.newSchoolData.value?.nextFollowup =
                            "$_nextFollowUpDate $updatedTime"
                    }

                    Timber.e("Time")
                    Timber.e(viewModel.newSchoolData.value?.nextFollowup)
                }
            }
        }
    }

    private fun openCamera(): Intent {
        val sdf = SimpleDateFormat("ddMyyyyhhmmss")
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "SchoolImage${sdf.format(Date())}")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        _imageUri =
            requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, _imageUri)
        }
        return cameraIntent
    }

    private fun checkValidationsAndApiCall(stepCount: Int) {
        if (viewModel.newSchoolData.value?.schoolId.isNullOrBlank()) {
            Timber.e("In if condition")
            Timber.e(viewModel.newSchoolData.value.toString())

            if (stepCount == 1) {

                if (checkBasicDetailsValidations()) {
                    setPositionView()
                    binding.basicDetails.tilSchoolTotalNoOfClassroom.error = null
                    binding.basicDetails.tilSchoolTotalIntake.error = null
                } else {
                    Snackbar.make(
                        requireView(),
                        resources.getString(R.string.some_fields_are_empty_or_not_valid),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else if (stepCount == 2) {
                if (checkSchoolDetailsValidation()) {
                    setPositionView()
                } else {
                    Snackbar.make(
                        requireView(),
                        resources.getString(R.string.some_fields_are_empty_or_not_valid),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {

                val isLeadTypeEmpty =
                    binding.followupDetails.edtLeadType.text.toString().isNullOrEmpty()
                if (isLeadTypeEmpty)
                    binding.followupDetails.tilLeadType.error =
                        resources.getString(R.string.please_select_the_lead_type)
                else {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.addNewSchool()
                }
            }
        } else {
            if (stepCount == 1) {

                if (checkBasicDetailsValidations()) {
                    setPositionView()
                    binding.basicDetails.tilSchoolTotalNoOfClassroom.error = null
                    binding.basicDetails.tilSchoolTotalIntake.error = null
                } else {
                    Snackbar.make(
                        requireView(),
                        resources.getString(R.string.some_fields_are_empty_or_not_valid),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else if (stepCount == 2) {
                if (checkSchoolDetailsValidation()) {
                    setPositionView()
                } else {
                    Snackbar.make(
                        requireView(),
                        resources.getString(R.string.some_fields_are_empty_or_not_valid),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                val isLeadTypeEmpty =
                    binding.followupDetails.edtLeadType.text.toString().isNullOrEmpty()
                if (isLeadTypeEmpty)
                    binding.followupDetails.tilLeadType.error =
                        resources.getString(R.string.please_select_the_lead_type)
                else {
                    binding.followupDetails.tilLeadType.error = null
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.updateSchoolDetails()
                }
            }
        }
    }

    private fun checkSchoolDetailsValidation(): Boolean {
        val mPhonePattern = Pattern.compile("[0123456789]{10}")
        val isDirectorDMPhoneValid =
            mPhonePattern.matcher(binding.schoolDetails.edtDirectorDmPhoneNo.text.toString())
                .matches()
        if (binding.schoolDetails.edtDirectorDmPhoneNo.text.toString().trim().isEmpty())
            binding.schoolDetails.tilDirectorDmPhoneNo.error = null
        else if (!isDirectorDMPhoneValid)
            binding.schoolDetails.tilDirectorDmPhoneNo.error =
                resources.getString(R.string.please_enter_valid_phone_no)
        else
            binding.schoolDetails.tilDirectorDmPhoneNo.error = null

        return if (
            binding.schoolDetails.edtDirectorDmPhoneNo.text.toString().trim().isEmpty()
        ) {
            true
        } else isDirectorDMPhoneValid
    }

    private fun checkBasicDetailsValidations(): Boolean {

        val isSchoolNameEmpty: Boolean =
            binding.basicDetails.edtSchoolName.text.toString().isNullOrEmpty();
        if (isSchoolNameEmpty)
            binding.basicDetails.tilSchoolName.error =
                resources.getString(R.string.please_enter_valid_school_name)
        else
            binding.basicDetails.tilSchoolName.error = null

        val isSchoolAddressEmpty =
            binding.basicDetails.edtSchoolAddress.text.toString().isNullOrEmpty()
        if (isSchoolAddressEmpty)
            binding.basicDetails.tilSchoolAddress.error =
                resources.getString(R.string.please_enter_valid_school_address)
        else
            binding.basicDetails.tilSchoolAddress.error = null

        val isBoardEmpty = binding.basicDetails.boardSpinner.text.toString().isNullOrEmpty()
        if (isBoardEmpty)
            binding.basicDetails.tilBoard.error = resources.getString(R.string.please_select_board)
        else
            binding.basicDetails.tilBoard.error = null

        val isCoordinatorNameEmpty =
            binding.basicDetails.edtCoordinatorName.text.toString().isNullOrEmpty()
        if (isCoordinatorNameEmpty)
            binding.basicDetails.tilCoordinatorName.error =
                resources.getString(R.string.please_enter_valid_coordinator_name)
        else
            binding.basicDetails.tilCoordinatorName.error = null

        val mPhonePattern = Pattern.compile("[0123456789]{10}")
        val isCoordinatorPhoneValid =
            !mPhonePattern.matcher(binding.basicDetails.edtCoordinatorMono.text.toString())
                .matches()
        if (binding.basicDetails.edtCoordinatorMono.text.toString().trim().isEmpty())
            binding.basicDetails.tilCoordinatorMono.error = null
        else if (isCoordinatorPhoneValid)
            binding.basicDetails.tilCoordinatorMono.error =
                resources.getString(R.string.please_enter_valid_phone_no)
        else
            binding.basicDetails.tilCoordinatorMono.error = null

        val isEmailValid =
            !Pattern.compile("[a-zA-Z0-9+_.-]+@[a-zA-Z0-9]+[.-][a-zA-Z][a-z.A-Z]+")
                .matcher(binding.basicDetails.edtEmailId.text.toString())
                .matches()
        if (binding.basicDetails.edtEmailId.text.toString().trim().isEmpty())
            binding.basicDetails.tilEmailId.error = null
        else if (isEmailValid)
            binding.basicDetails.tilEmailId.error =
                resources.getString(R.string.please_enter_valid_email_address)
        else
            binding.basicDetails.tilEmailId.error = null
        return if (binding.basicDetails.edtEmailId.text.toString().trim().isNotEmpty()
        ) {
            if (binding.basicDetails.edtCoordinatorMono.text.toString().trim().isNotEmpty()) {
                !(isSchoolNameEmpty || isSchoolAddressEmpty || isBoardEmpty || isCoordinatorNameEmpty || isCoordinatorPhoneValid || isEmailValid)
            } else !(isSchoolNameEmpty || isSchoolAddressEmpty || isBoardEmpty || isCoordinatorNameEmpty || isEmailValid)

        } else if (binding.basicDetails.edtCoordinatorMono.text.toString().trim().isNotEmpty()) {
            if (binding.basicDetails.edtEmailId.text.toString().trim().isNotEmpty()
            ) {
                !(isSchoolNameEmpty || isSchoolAddressEmpty || isBoardEmpty || isCoordinatorNameEmpty || isCoordinatorPhoneValid || isEmailValid)
            } else !(isSchoolNameEmpty || isSchoolAddressEmpty || isBoardEmpty || isCoordinatorNameEmpty || isCoordinatorPhoneValid)
        } else !(isSchoolNameEmpty || isSchoolAddressEmpty || isBoardEmpty || isCoordinatorNameEmpty)
    }

    private fun setPositionView() {
        setButtonName(viewModel.oldSchoolData.value)

        when (position) {
            0 -> {
                binding.basicDetails.root.visibility = View.GONE
                binding.schoolDetails.root.visibility = View.VISIBLE
                position = 1
                binding.stepView.done(false)
                binding.stepView.go(position, true)
            }

            1 -> {
                binding.schoolDetails.root.visibility = View.GONE
                binding.followupDetails.root.visibility = View.VISIBLE
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

    private fun setSchoolDetails() {
        binding.basicDetails.edtSchoolName.setText(viewModel.newSchoolData.value?.schoolName)
        binding.basicDetails.edtSchoolAddress.setText(viewModel.newSchoolData.value?.schoolAddress)
        if (viewModel.newSchoolData.value?.intake.toString().trim() != "0")
            binding.basicDetails.edtSchoolTotalIntake.setText("${viewModel.newSchoolData.value?.intake}")
        if (viewModel.newSchoolData.value?.totalClassRoom.toString().trim() != "0")
            binding.basicDetails.edtTotalNoOfClassroom.setText("${viewModel.newSchoolData.value?.totalClassRoom}")
        binding.basicDetails.edtEmailId.setText(viewModel.newSchoolData.value?.email)
        binding.basicDetails.edtCoordinatorName.setText(viewModel.newSchoolData.value?.coName)
        binding.basicDetails.edtCoordinatorMono.setText(viewModel.newSchoolData.value?.coMobileNo)
        binding.schoolDetails.edtDirectorDmName.setText(viewModel.newSchoolData.value?.directorName)
        binding.schoolDetails.edtDirectorDmPhoneNo.setText(viewModel.newSchoolData.value?.directorMobNo)
        binding.schoolDetails.edtAvgSchoolFee.setText(viewModel.newSchoolData.value?.avgSchoolFees)
        binding.schoolDetails.existingLabs.setText(viewModel.newSchoolData.value?.existingLab)
        binding.schoolDetails.edtValuePerStudent.setText(viewModel.newSchoolData.value?.expQuatedValue)

        if (viewModel.newSchoolData.value?.interested == "yes") binding.followupDetails.labSetupGroup.check(
            R.id.labSetupYes
        )
        else binding.followupDetails.labSetupGroup.check(R.id.labSetupNo)

        viewModel.newSchoolData.value?.nextFollowup.let {
            val dateAndTime = it?.split(" ")
            binding.followupDetails.edtSchoolDate.setText(
                dateAndTime?.get(0)
            )
            if ((dateAndTime?.size ?: 0) > 1)
                binding.followupDetails.edtSchoolTime.setText(dateAndTime?.get(1) ?: "")
        }
    }

    fun setButtonName(schoolData: SchoolData?) {
        if (schoolData == null) {
            binding.basicDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.next)
            binding.schoolDetails.btnNext.text =
                resources.getString(com.phntechnolab.sales.R.string.next)
            binding.followupDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
        } else {
            binding.basicDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
            binding.schoolDetails.btnNext.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
            binding.followupDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
        }
    }

    private fun setTopBarTitle() {
        if (args.schoolData == null) {
            binding.topBar.title = "Add School"
            viewModel.setNewSchoolData(SchoolData())
        } else {
            binding.topBar.title = args.schoolData?.schoolName
            viewModel.setNewSchoolData(args.schoolData)
        }
    }


    override fun onStart() {
        super.onStart()

        setActionBar()

        initializeListener()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).removeMenuProvider(this)
        activity?.removeMenuProvider(this)
    }

    private fun setActionBar() {
        (requireActivity() as MainActivity).setSupportActionBar(binding.topBar)
        activity?.addMenuProvider(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.meeting_topbar_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_home_n -> {
                findNavController().navigate(R.id.homeFragment)
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
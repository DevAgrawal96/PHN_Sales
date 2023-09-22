package com.phntechnolab.sales.fragment

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentAssignedSchoolsStepperBinding
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.TextValidator
import com.phntechnolab.sales.viewmodel.AddSchoolViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.regex.Pattern

@AndroidEntryPoint
class AddSchoolFragment : Fragment() {

    private var _binding: FragmentAssignedSchoolsStepperBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddSchoolViewModel by viewModels()

    private val args: AddSchoolFragmentArgs by navArgs()

    var position = 0

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
            com.phntechnolab.sales.R.layout.fragment_assigned_schools_stepper,
            container,
            false
        )

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressHandler)

        viewModel.setOldSchoolData(args.schoolData)

        if (args.schoolData == null) {
            viewModel.setNewSchoolData(SchoolData())
        } else {
            viewModel.setNewSchoolData(args.schoolData)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        Timber.d("Argument data")
        Timber.d(Gson().toJson(viewModel.oldSchoolData))

        setButtonName(args.schoolData)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.newSchoolData.value != null) {
            setSchoolDetails()
        }

        setDropdowns()

        oncClickListener()

        observers()

        addValidationWatchers()
    }

    private fun addValidationWatchers() {

        validationTextWatcherSchema(
            binding.basicDetails.edtSchoolName,
            "checkStringNullOrEmpty",
            binding.basicDetails.tilSchoolName,
            resources.getString(R.string.please_enter_valid_school_name)
        )

        validationTextWatcherSchema(
            binding.basicDetails.edtSchoolAddress,
            "checkStringNullOrEmpty",
            binding.basicDetails.tilSchoolAddress,
            resources.getString(R.string.please_enter_valid_school_address)
        )

        validationTextWatcherSchema(
            binding.basicDetails.edtSchoolName,
            "spinner",
            binding.basicDetails.tilBoard,
            resources.getString(R.string.please_select_board),
            binding.basicDetails.boardSpinner
        )

        validationTextWatcherSchema(
            binding.basicDetails.edtSchoolTotalIntake,
            "checkIntegerNullOrZero",
            binding.basicDetails.tilSchoolTotalIntake,
            resources.getString(R.string.please_enter_total_school_intake)
        )

        validationTextWatcherSchema(
            binding.basicDetails.edtCoordinatorName,
            "checkStringNullOrEmpty",
            binding.basicDetails.tilCoordinatorName,
            resources.getString(R.string.please_enter_valid_coordinator_name)
        )

        validationTextWatcherSchema(
            binding.basicDetails.edtEmailId,
            "email",
            binding.basicDetails.tilEmailId,
            resources.getString(R.string.please_enter_valid_email_address)
        )

        validationTextWatcherSchema(
            binding.basicDetails.edtCoordinatorMono,
            "phone",
            binding.basicDetails.tilCoordinatorMono,
            resources.getString(R.string.please_enter_valid_phone_no)
        )

        validationTextWatcherSchema(
            binding.schoolDetails.edtDirectorDmPhoneNo,
            "phone",
            binding.schoolDetails.tilDirectorDmPhoneNo,
            resources.getString(R.string.please_enter_valid_phone_no)
        )
    }

    private fun validationTextWatcherSchema(
        editText: TextInputEditText,
        type: String,
        til: TextInputLayout,
        errorMessage: String,
        spinner: AutoCompleteTextView? = null
    ) {
        editText.addTextChangedListener(object :
            TextValidator(editText) {

            override fun validate(textView: TextInputEditText?, text: String?) {

                if (type == "checkStringNullOrEmpty" || type == "spinner") {
                    val isNameEmpty: Boolean = if (type == "spinner")
                        editText.text.toString().isNullOrEmpty() else spinner?.text.toString()
                        .isNullOrEmpty()
                    if (isNameEmpty)
                        til.error = errorMessage
                    else
                        til.error = null

                } else if (type == "checkIntegerNullOrZero") {
                    val isNameEmpty: Boolean =
                        editText.text.toString().isNullOrEmpty() || editText.text.toString() == "0";
                    if (isNameEmpty)
                        til.error = errorMessage
                    else
                        til.error = null
                } else if (type == "phone" || type == "email") {

                    var pattern: Pattern = Pattern.compile("")

                    when (type) {
                        "phone" -> {
                            pattern = Pattern.compile("^[0123456789]{15}\$")
                        }
                        "email" -> {
                            pattern = Pattern.compile("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9]+[.-][a-zA-Z][a-z.A-Z]+\$")
                        }
                        else -> {

                        }
                    }

                    if (pattern.matcher(text).matches())
                        til.error = errorMessage
                    else
                        til.error = null
                }
            }
        })
    }

    private fun setDropdowns() {
        val dropdown: AutoCompleteTextView = binding.basicDetails.boardSpinner
        val items = arrayOf("State Board", "CBSE", "ICSE", "NIOS", "IB", "CIE")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        dropdown.setAdapter(adapter)

        val labsDropdown: AutoCompleteTextView = binding.schoolDetails.existingLabs
        val labsItems = arrayOf(
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
        val labsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                labsItems
            )
        labsDropdown.setAdapter(labsAdapter)
    }

    private fun observers() {
        viewModel.addSchoolResponse.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }

                is NetworkResult.Error -> {
                    Timber.e(it.toString())
                }

                else -> {

                }
            }
        }

        viewModel.updateSchoolResponse.observe(viewLifecycleOwner) {
            Timber.e("Response dd")
            Timber.e(Gson().toJson(it))
            when (it) {
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }

                is NetworkResult.Error -> {
                    Timber.e(it.toString())
                }

                else -> {

                }
            }
        }
    }

    private fun oncClickListener() {
        binding.basicDetails.btnSave.setOnClickListener {
            Timber.d("data binding data")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            checkValidationsAndApiCall(1)
        }

        binding.schoolDetails.btnSave.setOnClickListener {
            Timber.d("data binding data 2")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            checkValidationsAndApiCall(2)
        }

        binding.followupDetails.btnSave.setOnClickListener {
            Timber.d("data binding data 3")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            checkValidationsAndApiCall(3)

        }

        binding.followupDetails
    }

    private fun checkValidationsAndApiCall(stepCount: Int) {
        if (viewModel.newSchoolData.value?.schoolId.isNullOrBlank()) {
            Timber.e("In if condition")
            Timber.e(viewModel.newSchoolData.value.toString())

            if (stepCount == 1) {

                if (checkBasicDetailsValidations()) {
                    setPositionView()
                } else {
                    Snackbar.make(
                        requireView(),
                        resources.getString(R.string.some_fields_are_empty_or_not_valid),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else if( stepCount == 2) {
                setPositionView()
            } else {
                viewModel.addNewSchool()
            }
        } else {
            if (stepCount == 1) {

                if (checkBasicDetailsValidations()) {
                    setPositionView()
                } else {
                    Snackbar.make(
                        requireView(),
                        resources.getString(R.string.some_fields_are_empty_or_not_valid),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else if( stepCount == 2) {
                setPositionView()
            } else {
                viewModel.updateSchoolDetails()
            }
        }
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
                resources.getString(R.string.please_enter_valid_email_address)
        else
            binding.basicDetails.tilSchoolAddress.error = null

        val isBoardEmpty = binding.basicDetails.boardSpinner.text.toString().isNullOrEmpty()
        if (isBoardEmpty)
            binding.basicDetails.tilBoard.error = resources.getString(R.string.please_select_board)
        else
            binding.basicDetails.tilBoard.error = null

        val isSchoolIntakeEmpty = binding.basicDetails.edtSchoolTotalIntake.text.toString()
            .isNullOrEmpty() || binding.basicDetails.edtSchoolTotalIntake.text.toString()
            .trim() == "0"
        if (isSchoolIntakeEmpty)
            binding.basicDetails.tilSchoolTotalIntake.error =
                resources.getString(R.string.please_enter_total_school_intake)
        else
            binding.basicDetails.tilSchoolTotalIntake.error = null

        val isCoordinatorNameEmpty =
            binding.basicDetails.edtCoordinatorName.text.toString().isNullOrEmpty()
        if (isCoordinatorNameEmpty)
            binding.basicDetails.tilCoordinatorName.error =
                resources.getString(R.string.please_enter_valid_coordinator_name)
        else
            binding.basicDetails.tilCoordinatorName.error = null

        val mPhonePattern = Pattern.compile("^[0123456789]{15}\$")
        val isCoordinatorPhoneValid =
            mPhonePattern.matcher(binding.basicDetails.edtCoordinatorMono.text.toString()).matches()
        if (isCoordinatorPhoneValid)
            binding.basicDetails.tilCoordinatorMono.error =
                resources.getString(R.string.please_enter_valid_phone_no)
        else
            binding.basicDetails.tilCoordinatorMono.error = null

        val isEmailValid =
            Pattern.compile("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9]+[.-][a-zA-Z][a-z.A-Z]+\$").matcher(binding.basicDetails.edtEmailId.text.toString())
                .matches()
        if (isEmailValid)
            binding.basicDetails.tilEmailId.error =
                resources.getString(R.string.please_enter_valid_email_address)
        else
            binding.basicDetails.tilEmailId.error = null

        return !(isSchoolNameEmpty || isSchoolAddressEmpty || isBoardEmpty || isSchoolIntakeEmpty || isCoordinatorNameEmpty || isCoordinatorNameEmpty || isCoordinatorPhoneValid || isEmailValid)
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
        binding.basicDetails.edtSchoolTotalIntake.setText("${viewModel.newSchoolData.value?.intake ?: 0}")
        binding.basicDetails.edtTotalNoOfClassroom.setText("${viewModel.newSchoolData.value?.totalClassRoom ?: 0}")
        binding.basicDetails.edtEmailId.setText(viewModel.newSchoolData.value?.email)
        binding.basicDetails.edtCoordinatorName.setText(viewModel.newSchoolData.value?.coName)
        binding.basicDetails.edtCoordinatorMono.setText(viewModel.newSchoolData.value?.coMobileNo)
        binding.schoolDetails.edtDirectorDmName.setText(viewModel.newSchoolData.value?.directorName)
        binding.schoolDetails.edtDirectorDmPhoneNo.setText(viewModel.newSchoolData.value?.directorMobNo)
        binding.schoolDetails.edtAvgSchoolFee.setText(viewModel.newSchoolData.value?.avgSchoolFees)
        binding.schoolDetails.existingLabs.setText(viewModel.newSchoolData.value?.existingLab)
        binding.schoolDetails.edtValuePerStudent.setText(viewModel.newSchoolData.value?.expQuatedValue)
    }

    fun setButtonName(schoolData: SchoolData?) {
        if (schoolData == null) {
            binding.basicDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.next)
            binding.schoolDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.next)
            binding.followupDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
        } else {
            binding.basicDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
            binding.schoolDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
            binding.followupDetails.btnSave.text =
                resources.getString(com.phntechnolab.sales.R.string.save)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
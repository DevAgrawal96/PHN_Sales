package com.phntechnolab.sales.fragment

import android.os.Bundle
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
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentAssignedSchoolsStepperBinding
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.AddSchoolViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


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
    }

    private fun setDropdowns() {
        val dropdown: AutoCompleteTextView = binding.basicDetails.boardSpinner
        val items = arrayOf("State Board", "CBSE", "ICSE", "NIOS", "IB", "CIE")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.setAdapter(adapter)

        val labsDropdown: AutoCompleteTextView = binding.schoolDetails.existingLabs
        val labsItems = arrayOf("Science Lab", "Computer Lab", "Engineering and Robotics Lab", "Art and Creativity Lab", "Environmental Science Lab", "Music and Audio Lab", "Physics and Electronics Lab", "Chemistry Lab", "Biology Lab")
        val labsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, labsItems)
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
    }

    private fun oncClickListener() {
        binding.basicDetails.btnSave.setOnClickListener {
            Timber.d("data binding data")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            if (viewModel.newSchoolData.value?.schoolId.isNullOrBlank()) {
                viewModel.newSchoolData.value?.let { it1 -> viewModel.addNewSchool(it1) }
            } else {

            }
            setPositionView()
        }

        binding.schoolDetails.btnSave.setOnClickListener {
            Timber.d("data binding data 2")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))
            setPositionView()
        }

        binding.followupDetails.btnSave.setOnClickListener {
            Timber.d("data binding data 3")
            Timber.d(Gson().toJson(viewModel.newSchoolData.value))

        }
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

        //follow up meetings
    }

    fun setButtonName(schoolData: SchoolData?) {
        if (schoolData == null) {
            binding.basicDetails.btnSave.text = resources.getString(com.phntechnolab.sales.R.string.next)
            binding.schoolDetails.btnSave.text = resources.getString(com.phntechnolab.sales.R.string.next)
            binding.followupDetails.btnSave.text = resources.getString(com.phntechnolab.sales.R.string.save)
        } else {
            binding.basicDetails.btnSave.text = resources.getString(com.phntechnolab.sales.R.string.save)
            binding.schoolDetails.btnSave.text = resources.getString(com.phntechnolab.sales.R.string.save)
            binding.followupDetails.btnSave.text = resources.getString(com.phntechnolab.sales.R.string.save)
        }
    }
//
//    inner class DynamicTextWatcher(var editText: EditText,var fieldName: String): TextWatcher{
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//        }
//
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        }
//
//        override fun afterTextChanged(s: Editable?) {
//            editText.text = s
//            schoolDetails.({fieldName })
//        }
//
//    }
}
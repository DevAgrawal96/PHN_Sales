package com.phntechnolab.sales.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentInstalmentBinding
import com.phntechnolab.sales.di.FileDownloader
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.InstallmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class InstallmentFragment : Fragment() {
    private var _binding: FragmentInstalmentBinding? = null
    private val binding get() = _binding!!
    private val args: InstallmentFragmentArgs by navArgs()
    private val viewModel: InstallmentViewModel by viewModels()


    private var pdfOrImg: Uri? = null

    private var isFirstReceipt: Boolean = false
    private var isSecondReceipt: Boolean = false
    private var isThirdReceipt: Boolean = false

    private var receipt1: String? = null
    private var receipt2: String? = null
    private var receipt3: String? = null
    private var id: String? = null
    private var schoolId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInstalmentBinding.inflate(inflater, container, false)
        setOnBackPressed()
        viewModel.setInstallmentData(args.schoolData)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        initializeAddInstallmentCard()
        initializeListener()
    }

    private fun initializeSchoolDetails(data: SchoolData) {
        val imageUrl = if (!data.schoolImage.isNullOrEmpty()) data.schoolImage else "sjfdsdfjhudsf"
        val image = GlideUrl(
            imageUrl, LazyHeaders.Builder()
                .addHeader("User-Agent", "5")
                .build()
        )
        Glide.with(requireContext()).load(image).override(300, 200)
            .error(R.drawable.demo_img).into(binding.schoolDetails.schoolImg)
        binding.schoolDetails.editIcon.visibility = View.GONE
        binding.schoolDetails.schoolName.text = data.schoolName
        binding.schoolDetails.txtEmail.text = data.email
        binding.schoolDetails.txtMono.text = data.coMobileNo
        binding.schoolDetails.locationTxt.text = data.schoolAddress
        binding.topAppBar.title = data.schoolName
        try {
            val fileName = data.moaDocumentData.moaFile!!.substring(
                data.moaDocumentData.moaFile!!.lastIndexOf('/') + 1
            )
            binding.fileName.text = fileName
            val fileDownloader = FileDownloader(requireContext())
            binding.download.setOnClickListener {
                fileDownloader.downloadFile(data.moaDocumentData.moaFile!!, fileName)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.start_downloading),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private var receiptPdf = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        Timber.e("BACK")
        if (uri != null) {
            pdfOrImg = uri
            val sdf = SimpleDateFormat("dd/M/yyyy")
            when (viewModel.getPosition()) {
                0 -> {
                    viewModel.uploadInstallmentDocument(
                        pdfOrImg!!,
                        requireContext(),
                        pdfOrImg.toString().split(".").last(),
                        viewModel.getPosition()
                    )
//                    isFirstReceipt = true

                    binding.addInstallment1.fileInstallmentName.text =
                        "${viewModel.imageName1}.${pdfOrImg.toString().split(".").last()}"

                    binding.addInstallment1.fileInstallmentInfo.text =
                        getString(
                            R.string.file_size_and_today_date_,
                            viewModel.imagesize1.toString(),
                            sdf.format(Date())
                        )


                    if (pdfOrImg.toString().split(".").last() == "jpg") {
                        binding.addInstallment1.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (pdfOrImg.toString().split(".").last() == "png") {
                        binding.addInstallment1.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (pdfOrImg.toString().split(".").last() == "pdf") {
                        binding.addInstallment1.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                        )
                    }
                    binding.addInstallment1.uploadReceiptContainer.visibility = View.VISIBLE
                }

                1 -> {
                    viewModel.uploadInstallmentDocument(
                        pdfOrImg!!,
                        requireContext(),
                        pdfOrImg.toString().split(".").last(),
                        viewModel.getPosition()
                    )
//                    isSecondReceipt = true

                    binding.addInstallment2.fileInstallmentName.text =
                        "${viewModel.imageName2}.${pdfOrImg.toString().split(".").last()}"
                    binding.addInstallment2.fileInstallmentInfo.text =
                        getString(
                            R.string.file_size_and_today_date_,
                            viewModel.imagesize2.toString(),
                            sdf.format(Date())
                        )


                    if (pdfOrImg.toString().split(".").last() == "jpg") {
                        binding.addInstallment2.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (pdfOrImg.toString().split(".").last() == "png") {
                        binding.addInstallment2.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (pdfOrImg.toString().split(".").last() == "pdf") {
                        binding.addInstallment2.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                        )
                    }
                    binding.addInstallment2.uploadReceiptContainer.visibility = View.VISIBLE
                }

                2 -> {
                    viewModel.uploadInstallmentDocument(
                        pdfOrImg!!,
                        requireContext(),
                        pdfOrImg.toString().split(".").last(),
                        viewModel.getPosition()
                    )
//                    isThirdReceipt = true

                    binding.addInstallment3.fileInstallmentName.text =
                        "${viewModel.imageName3}.${pdfOrImg.toString().split(".").last()}"

                    binding.addInstallment3.fileInstallmentInfo.text =
                        getString(
                            R.string.file_size_and_today_date_,
                            viewModel.imagesize3.toString(),
                            sdf.format(Date())
                        )


                    if (pdfOrImg.toString().split(".").last() == "jpg") {
                        binding.addInstallment3.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (pdfOrImg.toString().split(".").last() == "png") {
                        binding.addInstallment3.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (pdfOrImg.toString().split(".").last() == "pdf") {
                        binding.addInstallment3.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                        )
                    }
                    binding.addInstallment3.uploadReceiptContainer.visibility = View.VISIBLE
                }
            }
            Timber.e(pdfOrImg.toString())
        }

    }

    private fun initializeAddInstallmentCard() {

        binding.addInstallment1.uploadReceipt.setOnClickListener {
//            Toast.makeText(requireContext(), "Coming soon!", Toast.LENGTH_SHORT).show()
            receiptPdf.launch(
                arrayOf(
                    "image/*",
                    "application/pdf"
                )
            )
            viewModel.setPosition(0)
        }
        binding.addInstallment2.uploadReceipt.setOnClickListener {
//            Toast.makeText(requireContext(), "Coming soon!", Toast.LENGTH_SHORT).show()
            receiptPdf.launch(
                arrayOf(
                    "image/*",
                    "application/pdf"
                )
            )
            viewModel.setPosition(1)
        }
        binding.addInstallment3.uploadReceipt.setOnClickListener {
//            Toast.makeText(requireContext(), "Coming soon!", Toast.LENGTH_SHORT).show()
            receiptPdf.launch(
                arrayOf(
                    "image/*",
                    "application/pdf"
                )
            )
            viewModel.setPosition(2)
        }

        binding.addInstallment1.edtInstallmentTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    val updatedTime = "$hourOfDay:$minute"
                    binding.addInstallment1.edtInstallmentTime.setText(updatedTime)
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
        binding.addInstallment2.edtInstallmentTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    val updatedTime = "$hourOfDay:$minute"
                    binding.addInstallment2.edtInstallmentTime.setText(updatedTime)
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
        binding.addInstallment3.edtInstallmentTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    val updatedTime = "$hourOfDay:$minute"
                    binding.addInstallment3.edtInstallmentTime.setText(updatedTime)
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }

        binding.addInstallment1.edtInstallmentDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val updatedDateAndTime =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    binding.addInstallment1.edtInstallmentDate.setText(updatedDateAndTime)
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
        binding.addInstallment2.edtInstallmentDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val updatedDateAndTime =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    binding.addInstallment2.edtInstallmentDate.setText(updatedDateAndTime)
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }
        binding.addInstallment3.edtInstallmentDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val updatedDateAndTime =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    binding.addInstallment3.edtInstallmentDate.setText(updatedDateAndTime)
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
        viewModel.addInstallmentResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Timber.e("addInstallment success")
                    when (viewModel.getPosition()) {
                        0 -> {
                            if (viewModel._requestFile1 != null)
                                viewModel.uploadInstallmentImages()
                            else
                                Timber.e("_requestFile1 null")
                        }

                        1 -> {
                            if (viewModel._requestFile2 != null)
                                viewModel.uploadInstallmentImages()
                            else
                                Timber.e("_requestFile1 _requestFile2 null")
                        }

                        2 -> {
                            if (viewModel._requestFile3 != null)
                                viewModel.uploadInstallmentImages()
                            else
                                Timber.e("_requestFile1 _requestFile2 _requestFile3 null")
                        }
                    }


                }

                is NetworkResult.Error -> {
                    Timber.e("Add installment error add installment ")
                }

                else -> {

                }
            }
        }

        viewModel.addInstallmentImageResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "Added successfully!!", Toast.LENGTH_SHORT)
                        .show()
                }

                is NetworkResult.Error -> {
                    Timber.e("Add installment error addInstallment upload image")
                }

                else -> {

                }
            }
        }

        viewModel.installmentData.observe(viewLifecycleOwner) {
            initializeSchoolDetails(it ?: SchoolData())
            id = it?.installmentData?.id ?: ""
            schoolId = it?.schoolId
            if (!it?.installmentData?.firstInstallmentAmount.isNullOrEmpty()) {
                binding.installment1.installmentDetailsTxt.text =
                    getString(R.string._1st_installment_details, "1st")
                binding.installment1.amount.text = it?.installmentData?.firstInstallmentAmount
                binding.addInstallment1.edtInstallmentAmount.setText(it?.installmentData?.firstInstallmentAmount)
                try {
                    val date: String =
                        it?.installmentData?.firstInstallmentDateTime?.split(",")?.get(0) ?: ""
                    val time: String =
                        it?.installmentData?.firstInstallmentDateTime?.split(",")?.get(1) ?: ""
                    Timber.e(date + "," + time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

//                binding.addInstallment1.edtInstallmentDate.setText(date)
//                binding.addInstallment1.edtInstallmentTime.setText(time)
                binding.addInstallment1.root.visibility = View.GONE
                binding.installment1.dateAndTime.text =
                    it?.installmentData?.firstInstallmentDateTime
                binding.installment1.root.visibility = View.VISIBLE
                viewModel.setCount(0)
                if (!it?.installmentData?.firstInstallmentReciept.isNullOrEmpty()) {
                    val fileName = it?.installmentData?.firstInstallmentReciept!!.substring(
                        it.installmentData?.firstInstallmentReciept!!.lastIndexOf('/') + 1
                    )
                    if (fileName.split(".").last() == "jpg") {
                        binding.installment1.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (fileName.split(".").last() == "png") {
                        binding.installment1.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (fileName.split(".").last() == "pdf") {
                        binding.installment1.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                        )
                    }
                    receipt1 = it.installmentData?.firstInstallmentReciept
                    binding.installment1.fileName.text = fileName

                    Timber.e("$fileName")
                }

            } else {
                binding.addInstallment1.root.visibility = View.VISIBLE
                binding.installment1.root.visibility = View.GONE

            }
            if (!it?.installmentData?.secondInstallmentAmount.isNullOrEmpty()) {
                binding.addInstallment2.edtInstallmentAmount.setText(it?.installmentData?.secondInstallmentAmount)
                try {
                    val date: String =
                        it?.installmentData?.secondInstallmentDateTime?.split(",")?.get(0) ?: ""
                    val time: String =
                        it?.installmentData?.secondInstallmentDateTime?.split(",")?.get(1) ?: ""
                    Timber.e(date + "," + time)
                    binding.addInstallment2.edtInstallmentDate.setText(date)
                    binding.addInstallment2.edtInstallmentTime.setText(time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.installment2.installmentDetailsTxt.text =
                    getString(R.string._1st_installment_details, "2nd")
                binding.installment2.amount.text = it?.installmentData?.secondInstallmentAmount
                binding.installment2.dateAndTime.text =
                    it?.installmentData?.secondInstallmentDateTime
                binding.addInstallment1.root.visibility = View.GONE
                binding.addInstallment2.root.visibility = View.GONE
                binding.installment2.root.visibility = View.VISIBLE
                viewModel.setCount(1)
                if (!it?.installmentData?.secondInstallmentReciept.isNullOrEmpty()) {
                    val fileName = it?.installmentData?.secondInstallmentReciept!!.substring(
                        it.installmentData?.secondInstallmentReciept!!.lastIndexOf('/') + 1
                    )
                    if (fileName.split(".").last() == "jpg") {
                        binding.installment2.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (fileName.split(".").last() == "png") {
                        binding.installment2.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (fileName.split(".").last() == "pdf") {
                        binding.installment2.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                        )
                    }

                    receipt2 = it.installmentData?.secondInstallmentReciept
                    binding.installment2.fileName.text = fileName

                    Timber.e("$fileName")

                }
            } else {
                binding.installment2.root.visibility = View.GONE
            }
            if (!it?.installmentData?.thirdInstallmentAmount.isNullOrEmpty()) {
                binding.addInstallment3.edtInstallmentAmount.setText(it?.installmentData?.thirdInstallmentAmount)
                try {
                    val date: String =
                        it?.installmentData?.thirdInstallmentDateTime?.split(",")?.get(0) ?: ""
                    val time: String =
                        it?.installmentData?.thirdInstallmentDateTime?.split(",")?.get(1) ?: ""
                    Timber.e(date + "," + time)
                    binding.addInstallment3.edtInstallmentDate.setText(date)
                    binding.addInstallment3.edtInstallmentTime.setText(time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.installment3.installmentDetailsTxt.text =
                    getString(R.string._1st_installment_details, "3rd")
                binding.installment3.amount.text = it?.installmentData?.secondInstallmentAmount
                binding.installment3.dateAndTime.text =
                    it?.installmentData?.secondInstallmentDateTime
                binding.installment3.root.visibility = View.VISIBLE
                binding.addInstallment1.root.visibility = View.GONE
                binding.addInstallment2.root.visibility = View.GONE
                binding.addInstallment3.root.visibility = View.GONE
                binding.addInstallmentDetails.visibility = View.GONE
                viewModel.setCount(2)
                if (!it?.installmentData?.thirdInstallmentReciept.isNullOrEmpty()) {
                    val fileName = it?.installmentData?.thirdInstallmentReciept!!.substring(
                        it.installmentData?.thirdInstallmentReciept!!.lastIndexOf('/') + 1
                    )
                    if (fileName.split(".").last() == "jpg") {
                        binding.installment3.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (fileName.split(".").last() == "png") {
                        binding.installment3.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (fileName.split(".").last() == "pdf") {
                        binding.installment3.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                        )
                    }
                    receipt3 = it.installmentData?.thirdInstallmentReciept
                    binding.installment3.fileName.text = fileName

                    Timber.e("$fileName")

                }
            } else {
                binding.installment3.root.visibility = View.GONE
            }
        }
    }

    private fun uploadInstallmentData() {
        if (viewModel.getCount() == 2) {
            binding.updateBtn.isEnabled = false
        } else {
            binding.progressIndicator.visibility = View.VISIBLE
            val data = InstallmentData(
                firstInstallmentReciept = args.schoolData?.installmentData?.firstInstallmentReciept,
                secondInstallmentReciept = args.schoolData?.installmentData?.secondInstallmentReciept,
                thirdInstallmentReciept = args.schoolData?.installmentData?.thirdInstallmentReciept,
                schoolId = args.schoolData?.schoolId,
                totalInstallment = viewModel.getCount().toString(),
                firstInstallment = binding.addInstallment1.installmentTxt.text.toString(),
                firstInstallmentAmount = binding.addInstallment1.edtInstallmentAmount.text.toString(),
                firstInstallmentDateTime = binding.addInstallment1.edtInstallmentDate.text.toString() + ", " + binding.addInstallment1.edtInstallmentTime.text.toString(),
                secondInstallment = binding.addInstallment2.installmentTxt.text.toString(),
                secondInstallmentAmount = binding.addInstallment2.edtInstallmentAmount.text.toString(),
                secondInstallmentDateTime = binding.addInstallment2.edtInstallmentDate.text.toString() + ", " + binding.addInstallment2.edtInstallmentTime.text.toString(),
                thirdInstallment = binding.addInstallment3.installmentTxt.text.toString(),
                thirdInstallmentAmount = binding.addInstallment3.edtInstallmentAmount.text.toString(),
                thirdInstallmentDateTime = binding.addInstallment3.edtInstallmentDate.text.toString() + ", " + binding.addInstallment3.edtInstallmentTime.text.toString()
            )
            viewModel.setInstallmentsData(data)
            viewModel.addNewInstallment(data)
        }
    }

    private fun initializeListener() {
        binding.updateBtn.setOnClickListener {
            Timber.e(viewModel.getPosition().toString())
            if (isFirstReceipt) {
//                if (viewModel.is_requestFile1) {
                Timber.e("isFirstReceipt")
                uploadInstallmentData()
//                } else {
//                    toastMsg("Please upload receipt and amount")
//                }
            } else {
                toastMsg("Please upload receipt and amount")
            }
            if (isSecondReceipt) {
//                if (viewModel.is_requestFile2) {
                Timber.e("isSecondReceipt")
                uploadInstallmentData()
//                } else {
//                    toastMsg("Please upload receipt and amount")
//                }
            } else {
                toastMsg("Please upload receipt and amount")
            }
            if (isThirdReceipt) {
//                if (viewModel.is_requestFile3) {
                Timber.e("isThirdReceipt")
                uploadInstallmentData()
//                } else {
//                    toastMsg("Please upload receipt and amount")
//                }
            } else {
                toastMsg("Please upload receipt and amount")
            }


        }


        val fileDownloader = FileDownloader(requireContext())
        binding.installment1.downloadImg.setOnClickListener {
            if (!receipt1.isNullOrBlank()) {
                fileDownloader.downloadFile(
                    receipt1!!, receipt1!!.substring(
                        receipt1!!.lastIndexOf('/') + 1
                    )
                )
                Toast.makeText(
                    requireContext(),
                    getString(R.string.start_downloading),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Timber.e("receipt1 null or blank")
            }
        }
        binding.installment2.downloadImg.setOnClickListener {
            if (!receipt2.isNullOrBlank()) {
                fileDownloader.downloadFile(
                    receipt2!!, receipt2!!.substring(
                        receipt2!!.lastIndexOf('/') + 1
                    )
                )
                Toast.makeText(
                    requireContext(),
                    getString(R.string.start_downloading),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Timber.e("receipt2 null or blank")
            }
        }
        binding.installment3.downloadImg.setOnClickListener {
            if (!receipt3.isNullOrBlank()) {
                fileDownloader.downloadFile(
                    receipt3!!, receipt3!!.substring(
                        receipt3!!.lastIndexOf('/') + 1
                    )
                )
                Toast.makeText(
                    requireContext(),
                    getString(R.string.start_downloading),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Timber.e("receipt3 null or blank")
            }
        }




        binding.addInstallmentDetails.setOnClickListener {
            binding.updateBtn.visibility = View.VISIBLE
            when (viewModel.getCount()) {
                0 -> {
                    binding.addInstallment2.root.visibility = View.VISIBLE
                    viewModel.setCount(1)
                    isFirstReceipt = true
                    binding.addInstallment2.installmentTxt.text =
                        getString(R.string.nd_installment, "2nd")
                }

                1 -> {
                    binding.addInstallment3.root.visibility = View.VISIBLE
                    binding.addInstallmentDetails.visibility = View.GONE
                    viewModel.setCount(1)
                    isSecondReceipt = true
                    binding.addInstallment3.installmentTxt.text =
                        getString(R.string.nd_installment, "3rd")
                }

                else -> {
                    binding.addInstallment1.root.visibility = View.VISIBLE
                    viewModel.setCount(1)
                    isThirdReceipt = true
                    binding.addInstallment1.installmentTxt.text =
                        getString(R.string.nd_installment, "1st")

                }
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun toastMsg(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
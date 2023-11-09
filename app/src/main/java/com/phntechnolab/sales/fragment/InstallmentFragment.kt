package com.phntechnolab.sales.fragment

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentInstalmentBinding
import com.phntechnolab.sales.di.FileDownloader
import com.phntechnolab.sales.model.InstallmentData
import com.phntechnolab.sales.model.SchoolData
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.util.TakePictureFromCameraOrGalley
import com.phntechnolab.sales.util.getFileSize
import com.phntechnolab.sales.util.pickDate
import com.phntechnolab.sales.util.pickTime
import com.phntechnolab.sales.util.setupUI
import com.phntechnolab.sales.viewmodel.InstallmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class InstallmentFragment : Fragment() {
    private var _binding: FragmentInstalmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var fileDownloader: FileDownloader

    private val args: InstallmentFragmentArgs by navArgs()
    private val viewModel: InstallmentViewModel by viewModels()

    private var pdfOrImg: Uri? = null

    private var isFirstReceipt: Boolean = false
    private var isSecondReceipt: Boolean = false
    private var isThirdReceipt: Boolean = false
    private var isFourthReceipt: Boolean = false

    private var receipt1: String? = null
    private var receipt2: String? = null
    private var receipt3: String? = null
    private var receipt4: String? = null
    private var id: String? = null
    private var schoolId: String? = null


    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

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
        setupUI(view)
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
        binding.schoolDetails.chipStatus.text = data.status
        binding.schoolDetails.chipLeadStatus.text = data.leadType
        binding.download.setOnClickListener {
            try {
                val fileName = data.moaDocumentData.moaFile.substring(
                    data.moaDocumentData.moaFile.lastIndexOf('/') + 1
                )
//            binding.fileName.text = fileName
                try {
                    data.moaDocumentData.moaFile.let {
                        if (!data.moaDocumentData.moaFile.startsWith("/tmp/")) {
                            fileDownloader.downloadFile(data.moaDocumentData.moaFile, fileName)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.start_downloading),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.file_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestPermission() {
        val hasReadExternalStorage = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest = mutableListOf<String>()
        if (!hasReadExternalStorage) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private var advancePaymentReceiptPdf = registerForActivityResult(
        TakePictureFromCameraOrGalley
    ) { uri ->
        if (uri != null && getFileSize(uri, requireContext()) > 0) {
            pdfOrImg = uri
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val type = requireContext().contentResolver.getType(uri);
            val fileExtention = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
            viewModel.uploadInstallmentDocument(
                pdfOrImg!!,
                requireContext(),
                3
            )
            isFourthReceipt = true

            binding.addAdvancePayment.fileAdvancePaymentName.text =
                "${viewModel.imageName4}.${fileExtention}"

            binding.addAdvancePayment.fileInstallmentInfo.text =
                getString(
                    R.string.file_size_and_today_date_,
                    viewModel.imagesize4.toString(),
                    sdf.format(Date())
                )


            if (fileExtention == "jpg") {
                binding.addAdvancePayment.pdfAdvancePaymentImage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                )
            } else if (fileExtention == "png") {
                binding.addAdvancePayment.pdfAdvancePaymentImage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                )
            } else if (fileExtention == "pdf") {
                binding.addAdvancePayment.pdfAdvancePaymentImage.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                )
            }
            binding.addAdvancePayment.uploadReceiptContainer.visibility = View.VISIBLE
        }
    }
    private var receiptPdf = registerForActivityResult(
        TakePictureFromCameraOrGalley
    ) { uri ->
        if (uri != null && getFileSize(uri, requireContext()) > 0) {
            pdfOrImg = uri
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val type = requireContext().contentResolver.getType(uri);
            val fileExtention = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
            when (viewModel.getPosition()) {
                0 -> {
                    viewModel.uploadInstallmentDocument(
                        pdfOrImg!!,
                        requireContext(),
                        viewModel.getPosition()
                    )
                    isFirstReceipt = true

                    binding.addInstallment1.fileInstallmentName.text =
                        "${viewModel.imageName1}.${fileExtention}"

                    binding.addInstallment1.fileInstallmentInfo.text =
                        getString(
                            R.string.file_size_and_today_date_,
                            viewModel.imagesize1.toString(),
                            sdf.format(Date())
                        )


                    if (fileExtention == "jpg") {
                        binding.addInstallment1.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (fileExtention == "png") {
                        binding.addInstallment1.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (fileExtention == "pdf") {
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
                        viewModel.getPosition()
                    )
                    isSecondReceipt = true

                    binding.addInstallment2.fileInstallmentName.text =
                        "${viewModel.imageName2}.${fileExtention}"
                    binding.addInstallment2.fileInstallmentInfo.text =
                        getString(
                            R.string.file_size_and_today_date_,
                            viewModel.imagesize2.toString(),
                            sdf.format(Date())
                        )


                    if (fileExtention == "jpg") {
                        binding.addInstallment2.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (fileExtention == "png") {
                        binding.addInstallment2.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (fileExtention == "pdf") {
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
                        viewModel.getPosition()
                    )
                    isThirdReceipt = true

                    binding.addInstallment3.fileInstallmentName.text =
                        "${viewModel.imageName3}.${fileExtention}"

                    binding.addInstallment3.fileInstallmentInfo.text =
                        getString(
                            R.string.file_size_and_today_date_,
                            viewModel.imagesize3.toString(),
                            sdf.format(Date())
                        )


                    if (fileExtention == "jpg") {
                        binding.addInstallment3.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (fileExtention == "png") {
                        binding.addInstallment3.pdfInstallmentImage.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (fileExtention == "pdf") {
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

        if (viewModel.getCount() == 0 && viewModel.getPosition() == 0) {
            binding.updateBtn.visibility = View.VISIBLE
        }

        binding.addAdvancePayment.uploadReceipt.setOnClickListener {
            advancePaymentReceiptPdf.launch(Unit)
        }

        binding.addInstallment1.uploadReceipt.setOnClickListener {
            receiptPdf.launch(Unit)
            viewModel.setPosition(0)
        }
        binding.addInstallment2.uploadReceipt.setOnClickListener {
            receiptPdf.launch(Unit)
            viewModel.setPosition(1)
        }
        binding.addInstallment3.uploadReceipt.setOnClickListener {
            receiptPdf.launch(Unit)
            viewModel.setPosition(2)
        }

        binding.addInstallment1.edtInstallmentTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            pickTime(hour, minute, false) { hourOfDay, _minute ->
                val updatedTime = "$hourOfDay:$_minute"
                binding.addInstallment1.edtInstallmentTime.setText(updatedTime)
            }
        }
        binding.addAdvancePayment.edtAdvancePaymentTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            pickTime(hour, minute, false) { hourOfDay, _minute ->
                val updatedTime = "$hourOfDay:$_minute"
                binding.addAdvancePayment.edtAdvancePaymentTime.setText(updatedTime)
            }
        }

        binding.addInstallment2.edtInstallmentTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            pickTime(hour, minute, false) { hourOfDay, _minute ->
                val updatedTime = "$hourOfDay:$_minute"
                binding.addInstallment2.edtInstallmentTime.setText(updatedTime)
            }
        }
        binding.addInstallment3.edtInstallmentTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR)
            val minute = c.get(Calendar.MINUTE)
            pickTime(hour, minute, false) { hourOfDay, _minute ->
                val updatedTime = "$hourOfDay:$_minute"
                binding.addInstallment3.edtInstallmentTime.setText(updatedTime)
            }
        }

        binding.addInstallment1.edtInstallmentDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            pickDate(day, month, year) { _year, monthOfYear, dayOfMonth ->
                val updatedDateAndTime =
                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + _year
                binding.addInstallment1.edtInstallmentDate.setText(updatedDateAndTime)
            }
        }
        binding.addAdvancePayment.edtAdvancePaymentDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            pickDate(day, month, year) { _year, monthOfYear, dayOfMonth ->
                val updatedDateAndTime =
                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + _year
                binding.addAdvancePayment.edtAdvancePaymentDate.setText(updatedDateAndTime)
            }
        }
        binding.addInstallment2.edtInstallmentDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            pickDate(day, month, year) { _year, monthOfYear, dayOfMonth ->
                val updatedDateAndTime =
                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + _year
                binding.addInstallment2.edtInstallmentDate.setText(updatedDateAndTime)
            }
        }
        binding.addInstallment3.edtInstallmentDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            pickDate(day, month, year) { _year, monthOfYear, dayOfMonth ->
                val updatedDateAndTime =
                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + _year
                binding.addInstallment3.edtInstallmentDate.setText(updatedDateAndTime)
            }
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
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
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
                    binding.addInstallment1.edtInstallmentDate.setText(date)
                    binding.addInstallment1.edtInstallmentTime.setText(time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                binding.addInstallment1.root.visibility = View.GONE
                binding.installment1.dateAndTime.text =
                    it?.installmentData?.firstInstallmentDateTime
                binding.installment1.root.visibility = View.VISIBLE
                viewModel.setCount(0)
                if (!it?.installmentData?.firstInstallmentReciept.isNullOrEmpty()) {
                    binding.installment1.receiptContainer.visibility = View.VISIBLE
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
                } else {
                    binding.installment1.receiptContainer.visibility = View.GONE
                }

            } else {
                binding.addInstallment1.root.visibility = View.VISIBLE
                binding.installment1.root.visibility = View.GONE
            }


            if (!it?.installmentData?.advancePaymentAmount.isNullOrEmpty()) {
//                binding.advancePayment.advancePaymentDetailsTxt.text =
//                    getString(R.string._1st_installment_details, "1st")
                binding.advancePayment.amount.text = it?.installmentData?.advancePaymentAmount
                binding.addAdvancePayment.edtAdvancePaymentAmount.setText(it?.installmentData?.advancePaymentAmount)
                try {
                    val date: String =
                        it?.installmentData?.advancePaymentDateTime?.split(",")?.get(0) ?: ""
                    val time: String =
                        it?.installmentData?.advancePaymentDateTime?.split(",")?.get(1) ?: ""
                    Timber.e(date + "," + time)
                    binding.addAdvancePayment.edtAdvancePaymentDate.setText(date)
                    binding.addAdvancePayment.edtAdvancePaymentTime.setText(time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                binding.addAdvancePayment.root.visibility = View.GONE
                binding.advancePayment.root.visibility = View.VISIBLE
                binding.advancePayment.dateAndTime.text =
                    it?.installmentData?.advancePaymentDateTime
                if (!it?.installmentData?.advancePaymentReceipt.isNullOrEmpty()) {
                    binding.advancePayment.receiptContainer.visibility = View.VISIBLE
                    val fileName = it?.installmentData?.advancePaymentReceipt!!.substring(
                        it.installmentData?.advancePaymentReceipt!!.lastIndexOf('/') + 1
                    )
                    if (fileName.split(".").last() == "jpg") {
                        binding.advancePayment.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_jpg)
                        )
                    } else if (fileName.split(".").last() == "png") {
                        binding.advancePayment.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_png)
                        )
                    } else if (fileName.split(".").last() == "pdf") {
                        binding.advancePayment.fileTypeImg.setImageDrawable(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf)
                        )
                    }
                    receipt4 = it.installmentData.advancePaymentReceipt
                    binding.advancePayment.fileName.text = fileName

                    Timber.e("$fileName")
                } else {
                    binding.advancePayment.receiptContainer.visibility = View.GONE
                }
            } else {
                binding.addAdvancePayment.root.visibility = View.VISIBLE
                binding.advancePayment.root.visibility = View.GONE

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
                    binding.installment2.receiptContainer.visibility = View.VISIBLE
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

                } else {
                    binding.installment2.receiptContainer.visibility = View.GONE
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
                    binding.installment3.receiptContainer.visibility = View.VISIBLE
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

                } else {
                    binding.installment3.receiptContainer.visibility = View.GONE
                }
            } else {
                binding.installment3.root.visibility = View.GONE
            }

            if (viewModel.getCount() == 2) {
                Timber.e("2")
                binding.updateBtn.visibility = View.GONE
            }
        }
    }

    private fun uploadInstallmentData() {
        binding.progressIndicator.visibility = View.VISIBLE
        val data = InstallmentData(
            advancePaymentReceipt = args.schoolData?.installmentData?.advancePaymentReceipt,
            advancePayment = binding.addAdvancePayment.advancePaymentOptionalTxt.text.toString(),
            advancePaymentAmount = binding.addAdvancePayment.edtAdvancePaymentAmount.text.toString(),
            advancePaymentDateTime = binding.addAdvancePayment.edtAdvancePaymentDate.text.toString() + ", " + binding.addAdvancePayment.edtAdvancePaymentTime.text.toString(),

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

    private fun initializeListener() {
        binding.addInstallment1.deleteReceipt.setOnClickListener {
            viewModel._requestFile1 = null
            isFirstReceipt = false
            binding.addInstallment1.uploadReceiptContainer.visibility = View.GONE
        }
        binding.addAdvancePayment.deleteReceipt.setOnClickListener {
            viewModel._requestFile4 = null
//            isFirstReceipt = false
            binding.addAdvancePayment.uploadReceiptContainer.visibility = View.GONE
        }
        binding.addInstallment2.deleteReceipt.setOnClickListener {
            viewModel._requestFile2 = null
            isSecondReceipt = false
            binding.addInstallment2.uploadReceiptContainer.visibility = View.GONE
        }
        binding.addInstallment3.deleteReceipt.setOnClickListener {
            viewModel._requestFile3 = null
            isThirdReceipt = false
            binding.addInstallment3.uploadReceiptContainer.visibility = View.GONE
        }


        binding.updateBtn.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            Timber.e(viewModel.getPosition().toString())
            if (isFirstReceipt || isSecondReceipt || isThirdReceipt) {
                uploadInstallmentData()
            } else {
                binding.progressIndicator.visibility = View.GONE
                toastMsg("Please upload receipt and amount")
            }
        }


        binding.advancePayment.downloadImg.setOnClickListener {
            if (!receipt4.isNullOrBlank()) {
                fileDownloader.downloadFile(
                    receipt4!!, receipt4!!.substring(
                        receipt4!!.lastIndexOf('/') + 1
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
                    binding.addInstallment2.installmentTxt.text =
                        getString(R.string.nd_installment, "2nd")
                }

                1 -> {
                    binding.addInstallment3.root.visibility = View.VISIBLE
                    binding.addInstallmentDetails.visibility = View.GONE
                    viewModel.setCount(2)
                    binding.addInstallment3.installmentTxt.text =
                        getString(R.string.nd_installment, "3rd")
                }

                else -> {
                    binding.addInstallment1.root.visibility = View.VISIBLE
                    viewModel.setCount(0)
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
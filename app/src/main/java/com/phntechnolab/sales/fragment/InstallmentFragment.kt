package com.phntechnolab.sales.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
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
import com.google.gson.Gson
import com.phntechnolab.sales.R
import com.phntechnolab.sales.databinding.FragmentInstalmentBinding
import com.phntechnolab.sales.di.FileDownloader
import com.phntechnolab.sales.model.InstallmentData
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

    private var count: Int = 0

    private var position: Int = 0

    private var pdfOrImg: Uri? = null

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
        viewModel.setInstallmentData(args.moaSchoolData)
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

    private var receiptPdf = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        pdfOrImg = uri!!
        val sdf = SimpleDateFormat("dd/M/yyyy")
        when (position) {
            0 -> {
                viewModel.uploadInstallmentDocument(
                    pdfOrImg!!,
                    requireContext(),
                    pdfOrImg.toString().split(".").last(),
                    position
                )


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
                    position
                )


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
                    position
                )


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

    private fun initializeAddInstallmentCard() {

        binding.addInstallment1.uploadReceipt.setOnClickListener {
            receiptPdf.launch(
                arrayOf(
                    "image/*",
                    "application/pdf"
                )
            )
            position = 0
        }
        binding.addInstallment2.uploadReceipt.setOnClickListener {
            receiptPdf.launch(
                arrayOf(
                    "image/*",
                    "application/pdf"
                )
            )
            position = 1
        }
        binding.addInstallment3.uploadReceipt.setOnClickListener {
            receiptPdf.launch(
                arrayOf(
                    "image/*",
                    "application/pdf"
                )
            )
            position = 2
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
                    when (position) {
                        0 -> {
                            if (viewModel._requestFile1 != null)
                                viewModel.uploadInstallmentImages()
                            else
                                Timber.e("_requestFile1 null")
//                            Toast.makeText(
//                                requireContext(),
//                                "please upload reciept",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            findNavController().popBackStack()
                        }

                        1 -> {
                            if (viewModel._requestFile1 != null && viewModel._requestFile2 != null)
                                viewModel.uploadInstallmentImages()
                            else
                                Timber.e("_requestFile1 _requestFile2 null")
//                            Toast.makeText(
//                                requireContext(),
//                                "please upload reciept",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            findNavController().popBackStack()
                        }

                        2 -> {
                            if (viewModel._requestFile1 != null && viewModel._requestFile2 != null && viewModel._requestFile3 != null)
                                viewModel.uploadInstallmentImages()
                            else
                                Timber.e("_requestFile1 _requestFile2 _requestFile3 null")
//                            Toast.makeText(
//                                requireContext(),
//                                "please upload reciept",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            findNavController().popBackStack()
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
            id = it?.id
            schoolId = it?.schoolId
            if (!it?.firstInstallmentAmount.isNullOrEmpty()) {
                binding.installment1.installmentDetailsTxt.text =
                    getString(R.string._1st_installment_details, "1st")
                binding.installment1.amount.text = it?.firstInstallmentAmount
                binding.installment1.dateAndTime.text = it?.firstInstallmentDateTime
                binding.installment1.root.visibility = View.VISIBLE
                binding.addInstallment1.root.visibility = View.GONE
                count = 0
                if (!it?.firstInstallmentReciept.isNullOrEmpty()) {
                    val fileName = it?.firstInstallmentReciept!!.substring(
                        it.firstInstallmentReciept!!.lastIndexOf('/') + 1
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
                    receipt1 = it.firstInstallmentReciept
                    binding.installment1.fileName.text = fileName

                    Timber.e("$fileName")
                }

            } else {
                binding.addInstallment1.root.visibility = View.VISIBLE
                binding.installment1.root.visibility = View.GONE

            }
            if (!it?.secondInstallmentAmount.isNullOrEmpty()) {

                binding.installment2.installmentDetailsTxt.text =
                    getString(R.string._1st_installment_details, "2nd")
                binding.installment2.amount.text = it?.secondInstallmentAmount
                binding.installment2.dateAndTime.text = it?.firstInstallmentDateTime
                binding.addInstallment1.root.visibility = View.GONE
                binding.addInstallment2.root.visibility = View.GONE
                binding.installment2.root.visibility = View.VISIBLE
                count = 1
                if (!it?.secondInstallmentReciept.isNullOrEmpty()) {
                    val fileName = it?.secondInstallmentReciept!!.substring(
                        it.secondInstallmentReciept!!.lastIndexOf('/') + 1
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

                    receipt2 = it.secondInstallmentReciept
                    binding.installment2.fileName.text = fileName

                    Timber.e("$fileName")

                }
            } else {
                binding.installment2.root.visibility = View.GONE
            }
            if (!it?.thirdInstallmentAmount.isNullOrEmpty()) {

                binding.installment3.installmentDetailsTxt.text =
                    getString(R.string._1st_installment_details, "3rd")
                binding.installment3.amount.text = it?.secondInstallmentAmount
                binding.installment3.dateAndTime.text = it?.secondInstallmentDateTime
                binding.installment3.root.visibility = View.VISIBLE
                binding.addInstallment1.root.visibility = View.GONE
                binding.addInstallment2.root.visibility = View.GONE
                binding.addInstallment3.root.visibility = View.GONE
                binding.addInstallmentDetails.visibility = View.GONE
                count = 2
                if (!it?.thirdInstallmentReciept.isNullOrEmpty()) {
                    val fileName = it?.thirdInstallmentReciept!!.substring(
                        it.thirdInstallmentReciept!!.lastIndexOf('/') + 1
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
                    receipt3 = it.thirdInstallmentReciept
                    binding.installment3.fileName.text = fileName

                    Timber.e("$fileName")

                }
            } else {
                binding.installment3.root.visibility = View.GONE
            }
        }
    }

    private fun initializeListener() {
        binding.updateBtn.setOnClickListener {
            val data = InstallmentData(
                schoolId = args.moaSchoolData?.schoolId,
                totalInstallment = count.toString(),
                firstInstallment = binding.addInstallment1.installmentTxt.text.toString(),
                firstInstallmentAmount = binding.addInstallment1.edtInstallmentAmount.text.toString(),
                firstInstallmentDateTime = binding.addInstallment1.edtInstallmentDate.text.toString(),
                secondInstallment = binding.addInstallment2.installmentTxt.text.toString(),
                secondInstallmentAmount = binding.addInstallment2.edtInstallmentAmount.text.toString(),
                secondInstallmentDateTime = binding.addInstallment2.edtInstallmentDate.text.toString(),
                thirdInstallment = binding.addInstallment3.installmentTxt.text.toString(),
                thirdInstallmentAmount = binding.addInstallment3.edtInstallmentAmount.text.toString(),
                thirdInstallmentDateTime = binding.addInstallment3.edtInstallmentDate.text.toString()
            )
            viewModel.setInstallmentData(data)
            viewModel.addNewInstallment(data)
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
            when (count) {
                0 -> {
                    binding.addInstallment2.root.visibility = View.VISIBLE
                    count = 1
                    binding.addInstallment2.installmentTxt.text =
                        getString(R.string.nd_installment, "2nd")
                }

                1 -> {
                    binding.addInstallment3.root.visibility = View.VISIBLE
                    binding.addInstallmentDetails.visibility = View.GONE
                    count = 2
                    binding.addInstallment3.installmentTxt.text =
                        getString(R.string.nd_installment, "3rd")
                }

                else -> {
                    binding.addInstallment1.root.visibility = View.VISIBLE
                    count = 0
                    binding.addInstallment1.installmentTxt.text =
                        getString(R.string.nd_installment, "1st")

                }
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
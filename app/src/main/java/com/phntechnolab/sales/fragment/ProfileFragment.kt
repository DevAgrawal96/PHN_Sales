package com.phntechnolab.sales.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import com.phntechnolab.sales.activity.MainActivity
import com.phntechnolab.sales.adapter.ProfileSettingAdapter
import com.phntechnolab.sales.databinding.FragmentProfileBinding
import com.phntechnolab.sales.databinding.LogoutDialogBinding
import com.phntechnolab.sales.databinding.VisitedSuccessDialogBinding
import com.phntechnolab.sales.model.SettingModel
import com.phntechnolab.sales.model.UserDataModel
import com.phntechnolab.sales.util.DataStoreManager.clearDataStore
import com.phntechnolab.sales.util.NetworkResult
import com.phntechnolab.sales.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), MenuProvider {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var _adapter: ProfileSettingAdapter? = null
    private val adapter get() = _adapter!!

    private val viewModel: ProfileViewModel by viewModels()

    private var _userProfileData: UserDataModel? = null
    private val userProfileData get() = _userProfileData!!

    @Inject
    lateinit var dataStoreProvider: DataStoreProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        fetchUserProfileData()
        userProfileDataObservable()
        initializeAdapter()
        setOnBackPressed()
        return binding.root
    }

    private fun fetchUserProfileData() {
        viewModel.userProfileData()
    }

    private fun initializeAdapter() {
        val callback = object : ProfileSettingAdapter.Callback {
            override fun openSetting(position: Int) {
                when (position) {
                    0 -> {
                        findNavController().navigate(
                            ProfileFragmentDirections.actionProfileFragmentToMyAccountFragment(
                                UserDataModel(
                                    userProfileData.userId,
                                    userProfileData.id,
                                    userProfileData.name,
                                    userProfileData.email,
                                    userProfileData.mobile_no,
                                    userProfileData.password ?: "null",
                                    userProfileData.role
                                )
                            )
                        )
                    }

                    1 -> {
                        Toast.makeText(requireContext(), "coming soon!", Toast.LENGTH_SHORT).show()
                    }

                    2 -> {
                        Toast.makeText(requireContext(), "coming soon!", Toast.LENGTH_SHORT).show()
                    }

                    3 -> {
                        findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
                    }

                    4 -> {
                        Toast.makeText(requireContext(), "coming soon!", Toast.LENGTH_SHORT).show()
                    }

                    5 -> {
                        showDialog()
                    }

                    else -> {

                    }
                }
            }
        }
        _adapter = ProfileSettingAdapter(callback)
        binding.settingRv.adapter = adapter
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        val dialogBinding = LogoutDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.logoutNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.logoutYes.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            dialog.dismiss()
            viewModel.logout(requireContext())
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun setOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataToAdapter()
        observable()
    }

    private fun observable() {
        viewModel.logoutLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.progressIndicator.visibility = View.GONE
                            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                        }
                        clearDataStore(requireContext(), dataStoreProvider)
                    }

                }

                is NetworkResult.Error -> {
                    when (it.message) {
                        requireContext().resources.getString(R.string.no_internet_connection) -> {
                            Toast.makeText(
                                requireContext(),
                                getText(R.string.no_internet_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        requireContext().resources.getString(R.string.something_went_wrong) -> {
                            Toast.makeText(
                                requireContext(),
                                getText(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().resources.getString(R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun userProfileDataObservable() {
        viewModel.userProfile.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    _userProfileData = it.data
                    binding.executiveName.text = userProfileData.name
                    binding.executiveRole.text = userProfileData.role
                }

                is NetworkResult.Error -> {
                    when (it.message) {
                        getString(R.string.no_internet_connection) -> {
                            Toast.makeText(
                                requireContext(),
                                getText(R.string.no_internet_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        getString(R.string.something_went_wrong) -> {
                            Toast.makeText(
                                requireContext(),
                                getText(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        requireActivity().resources.getString(R.string.something_went_wrong_please),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setDataToAdapter() {
        adapter.setData(ArrayList<SettingModel>().apply {
            add(
                SettingModel(
                    R.drawable.my_account,
                    resources.getString(R.string.my_account),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.activity,
                    resources.getString(R.string.activity),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.revenue,
                    resources.getString(R.string.revenue),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.change_password,
                    resources.getString(R.string.change_password),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.chart_analysis,
                    resources.getString(R.string.chart_analysis),
                    resources.getString(R.string.demo_text)
                )
            )
            add(
                SettingModel(
                    R.drawable.log_out,
                    resources.getString(R.string.log_out),
                    resources.getString(R.string.demo_text)
                )
            )
        })
    }

    override fun onStart() {
        super.onStart()
        setActionBar()
    }
    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).removeMenuProvider(this)
        activity?.removeMenuProvider(this)
    }

    private fun setActionBar() {
        (requireActivity() as MainActivity).setSupportActionBar(binding.homeTopBar)
        activity?.addMenuProvider(this)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_top_bar_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_search -> {
                findNavController().navigate(R.id.action_profileFragment_to_searchFragment)
                true
            }

            R.id.menu_notification -> {
                findNavController().navigate(R.id.action_profileFragment_to_notificationFragment)
                true
            }

            R.id.menu_home -> {
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
        _adapter = null
    }
}
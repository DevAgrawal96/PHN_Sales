package com.phntechnolab.sales.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import kotlinx.coroutines.flow.first
import java.io.File
import java.net.URI
import java.util.Calendar
import java.util.regex.Pattern

suspend fun saveData(
    context: Context,
    dataStoreProvider: DataStoreProvider,
    key: String,
    value: Boolean
) {
    val dataStore = dataStoreProvider.getDataStoreInstance(context.applicationContext)
    val dataStoreKey = booleanPreferencesKey(key)
    dataStore.edit { onBoarding ->
        onBoarding[dataStoreKey] = value
    }
}

fun Fragment.hideSoftKeyboard() {
    requireActivity().currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun Fragment.setupUI(view: View) {

    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { v, event ->
            hideSoftKeyboard()
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            setupUI(innerView)
        }
    }
}

suspend fun readData(
    context: Context,
    dataStoreProvider: DataStoreProvider,
    key: String
): Boolean? {
    val dataStore = dataStoreProvider.getDataStoreInstance(context.applicationContext)
    val dataStoreKey = booleanPreferencesKey(key)
    val preferences = dataStore.data.first()
    return preferences[dataStoreKey]
}

fun getChipColor(context: Context, chipName: String?, chipColor: (Int, Int) -> Unit) {
    when (chipName) {
        "Hot" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.hot_chip_text_color),
                ContextCompat.getColor(context, R.color.hot_chip_color)
            )
        }

        "Cool" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.cool_chip_text_color),
                ContextCompat.getColor(context, R.color.cool_chip_color)
            )
        }

        "Warm" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.Warm_costing_chip_text_color),
                ContextCompat.getColor(context, R.color.Warm_costing_chip_color)
            )
        }

        "Dead" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.dead_chip_text_color),
                ContextCompat.getColor(context, R.color.dead_chip_color)
            )
        }

        else -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.hot_chip_text_color),
                ContextCompat.getColor(context, R.color.hot_chip_color)
            )
        }
    }
}

fun getStatusChipColor(context: Context, chipName: String?, chipColor: (Int, Int) -> Unit) {
    when (chipName) {
        "Not Interested" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.hot_chip_text_color),
                ContextCompat.getColor(context, R.color.hot_chip_color)
            )
        }

        "MOA Signed" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.moa_signed_chip_text_color),
                ContextCompat.getColor(context, R.color.moa_signed_chip_color)
            )
        }

        "Assigned" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.cool_chip_text_color),
                ContextCompat.getColor(context, R.color.cool_chip_color)
            )
        }

        "Visited" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.Warm_costing_chip_text_color),
                ContextCompat.getColor(context, R.color.Warm_costing_chip_color)
            )
        }

        "Propose Costing" -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.dead_chip_text_color),
                ContextCompat.getColor(context, R.color.dead_chip_color)
            )
        }

        else -> {
            chipColor.invoke(
                ContextCompat.getColor(context, R.color.cool_chip_text_color),
                ContextCompat.getColor(context, R.color.cool_chip_color)
            )
        }
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getFileSize(uri: Uri?, context: Context): Long {
    if (uri != null) {
        return context.contentResolver.openAssetFileDescriptor(uri, "r")!!.length
    }
    return 0
}

fun Fragment.orientationPortrait() {
    requireActivity()
        .requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Fragment.orientationLandscape() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Fragment.showToolbarAndClearFullScreen() {
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
}

fun Fragment.hideToolbarAndClearFullScreen() {
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
}


fun TextInputEditText.textChange(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(text: Editable?) {
            afterTextChanged.invoke(text.toString())
        }
    })
}

fun AutoCompleteTextView.textChange(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(text: Editable?) {
            afterTextChanged.invoke(text.toString())
        }
    })
}

fun Fragment.pickDate(
    _day: Int,
    _month: Int,
    _year: Int,
    _date: (Int, Int, Int) -> Unit
) {
    val datePickerDialog = DatePickerDialog(
        requireContext(),
        { view, year, monthOfYear, dayOfMonth ->
            _date.invoke(year, monthOfYear, dayOfMonth)
        },
        _year,
        _month,
        _day
    )
    datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
    datePickerDialog.show()
}

fun Fragment.pickTime(
    _hour: Int,
    _minute: Int,
    is24Hour: Boolean,
    _time: (hourOfDay: Int, minute: Int) -> Unit
) {
    val timePickerDialog = TimePickerDialog(
        requireContext(),
        { view, hourOfDay, minute ->
            _time.invoke(hourOfDay, minute)
        },
        _hour,
        _minute,
        is24Hour
    )
    timePickerDialog.show()
}


abstract class TextValidator(private val textView: TextInputEditText) : TextWatcher {
    abstract fun validate(textView: TextInputEditText?, text: String?)
    override fun afterTextChanged(s: Editable) {
        val text = textView.text.toString()
        validate(textView, text)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) { /* Don't care */
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) { /* Don't care */
    }
}

fun isNotNullOrZero(number: String, errorMessage: String): String? {
    if (number.trim().isNullOrEmpty() || number.trim() == "0") {
        return errorMessage
    }
    return null
}

fun isValidName(name: String, errorMessage: String = ""): String? {
    if (name.trim().isNullOrEmpty()) {
        return errorMessage
    }
    return null
}

fun isValidEmail(email: String, errorMessage: String = ""): String? {
    if (email.trim().isNullOrEmpty()) {
        return errorMessage
    } else if (!Pattern.compile("[a-zA-Z0-9+_.-]+@[a-zA-Z0-9]+[.-][a-zA-Z][a-z.A-Z]+")
            .matcher(email)
            .matches()
    ) {
        return errorMessage
    }
    return null
}

fun isValidMobileNumber(number: String, errorMessage: String): String? {
    if (number.trim().isNullOrEmpty()) {
        return errorMessage
    } else if (number.trim().length != 10) {
        return errorMessage
    } else if (!number.trim().matches("^[6-9][0-9]{9}$".toRegex())) {
        return errorMessage
    }
    return null
}

fun isValidNumber(number: String, errorMessage: String): String? {
    if (number.trim().isNullOrEmpty()) {
        return errorMessage
    } else if (number.trim().length != 10) {
        return errorMessage
    }
    return null
}

fun Fragment.disableScreen() {
    activity!!.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun View.disable() {
    background.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
    isClickable = false
}

fun View.enabled() {
    background.colorFilter = null
    isClickable = true
}

fun Fragment.toastMsg(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}


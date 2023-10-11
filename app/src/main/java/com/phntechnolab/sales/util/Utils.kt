package com.phntechnolab.sales.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.phntechnolab.sales.Modules.DataStoreProvider
import com.phntechnolab.sales.R
import kotlinx.coroutines.flow.first


//fun Fragment.hideSoftKeyboard() {
//    val inputMethodManager = activity?.getSystemService(
//        INPUT_METHOD_SERVICE
//    ) as InputMethodManager
//    if (inputMethodManager.isAcceptingText) {
//        inputMethodManager.hideSoftInputFromWindow(
//            activity?.currentFocus!!.windowToken,
//            0
//        )
//    }
//}


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

fun Fragment.oriantaionPortrait() {
    requireActivity()
        .requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Fragment.oriantaionLandscape() {
    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Fragment.showToolbarAndClearFullScreen() {
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
}

fun Fragment.hideToolbarAndClearFullScreen() {
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
}


fun TextInputEditText.validation(afterTextChanged: (String) -> Unit) {
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

fun isValidName(name: String, context: Context): String? {
    if (name.isBlank()) {
        return context.getString(R.string.empty_, "name")
    }
    if (name.length <= 1) {
        return context.getString(R.string.name_length)
    }
    return null
}

fun isValidEmail(email: String, context: Context): String? {
    if (email.isBlank()) {
        return context.getString(R.string.empty_, "Email")
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return context.getString(R.string.enter_valid_email)
    }
    return null
}

fun isValidMobileNumber(number: String, context: Context): String? {
    if (number.isBlank()) {
        return context.getString(R.string.empty_, "Mobile Number")
    }
    if (number.length != 10) {
        return context.getString(R.string.mobile_number_length)
    }
    if (!number.matches("^[6-9][0-9]{9}$".toRegex())) {
        return context.getString(R.string.enter_valid_number)
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
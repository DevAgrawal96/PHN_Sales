package com.phntechnolab.sales.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult


object ImagePicker {

    fun onChooseImageButtonClick(): Intent {

        // Create a new intent and set its type to image
        val pickIntent = Intent()
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT

        // Intent for camera activity to capture a new picture
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Title of the popup
        val pickTitle = "Choose a Picture"
        val chooserIntent = Intent.createChooser(pickIntent, pickTitle)
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent, takePhotoIntent)
        )

        return chooserIntent
    }
}
package com.phntechnolab.sales.util

import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//object ImagePicker  {
//    var _imageUri: Uri? = null
//    fun onChooseImageButtonClick(context: Context): Intent {
//
//        // Create a new intent and set its type to image
//        val pickIntent = Intent()
//        pickIntent.type = "image/*"
//        pickIntent.action = Intent.ACTION_GET_CONTENT
//
//        // Intent for camera activity to capture a new picture
//        val sdf = SimpleDateFormat("ddMyyyyhhmmss")
//
//        val values = ContentValues().apply {
//            put(MediaStore.Images.Media.TITLE, "SchoolImage${sdf.format(Date())}")
//            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
//        }
//
//        _imageUri =
//            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
//            putExtra(MediaStore.EXTRA_OUTPUT, _imageUri)
//        }
//
//
//        // Title of the popup
//        val pickTitle = "Choose a Picture"
//        val chooserIntent = Intent.createChooser(pickIntent, pickTitle)
//        chooserIntent.putExtra(
//            Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent, cameraIntent)
//        )
//
//        return chooserIntent
//    }
//}

object TakePictureFromCameraOrGalley: ActivityResultContract<Unit, Uri?>() {

    private var _photoUri: Uri? = null
    private val photoUri: Uri get() = _photoUri!!

    override fun createIntent(context: Context, input: Unit): Intent {
        return openImageIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data ?: photoUri
    }

    private fun openImageIntent(context: Context): Intent {
        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        _photoUri = createPhotoTakenUri(context)

        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        val gallIntent = Intent(Intent.ACTION_GET_CONTENT)
        gallIntent.type = "image/*"

        val yourIntentsList = ArrayList<Intent>()
        val packageManager = context.packageManager

        packageManager.queryIntentActivities(camIntent, 0).forEach{
            val finalIntent = Intent(camIntent)
            finalIntent.component = ComponentName(it.activityInfo.packageName, it.activityInfo.name)
            yourIntentsList.add(finalIntent)
        }

        packageManager.queryIntentActivities(gallIntent, 0).forEach {
            val finalIntent = Intent(gallIntent)
            finalIntent.component = ComponentName(it.activityInfo.packageName, it.activityInfo.name)
            yourIntentsList.add(finalIntent)
        }

        val pickTitle = "Choose a Picture"
        val chooser = Intent.createChooser(gallIntent, pickTitle)
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, yourIntentsList.toTypedArray())

        return chooser

    }

    private fun createFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw IllegalStateException("Dir not found")
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun createPhotoTakenUri(context: Context): Uri {
        val file = createFile(context)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, context.applicationContext.packageName.toString() + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
    }
}
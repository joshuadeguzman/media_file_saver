package com.freelancer.media_file_saver

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.app.ActivityCompat
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Joshua de Guzman on 2020-01-06.
 */
class MediaFileSaverDelegate(
    private val activity: Activity
) : PluginRegistry.RequestPermissionsResultListener,
    PluginRegistry.ActivityResultListener {
    private val bitmap: Bitmap? = null
    private val filePath: String? = null
    private var result: MethodChannel.Result? = null

    companion object {
        private const val IMAGE_QUALITY = 100
        private const val WRITE_EXTERNAL_STORAGE_IMAGE = 1001
        private const val WRITE_EXTERNAL_STORAGE_FILE = 1002
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == WRITE_EXTERNAL_STORAGE_IMAGE || requestCode == WRITE_EXTERNAL_STORAGE_FILE) {
            result?.success(if (resultCode == Activity.RESULT_OK) 0 else 1)
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        // TODO: Add check whether the permission has been denied permanently
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_IMAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    bitmap?.let {
                        saveImage(it)
                    }
                } else {
                    // TODO: Expose error in the channel
                }
            }

            WRITE_EXTERNAL_STORAGE_FILE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    filePath?.let {
                        saveFile(it)
                    }
                } else {
                    // TODO: Expose error in the channel
                }
            }
        }
        return true
    }

    fun checkPermissionSaveImage(image: ByteArray, result: MethodChannel.Result): String {
        this.result = result
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_IMAGE)
        } else {
            saveImage(bitmap)
        }
        return "Cannot continue saving the image."
    }

    fun checkPermissionSaveFile(path: String, result: MethodChannel.Result): String {
        this.result = result
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_IMAGE)
        } else {
            return saveFile(path)
        }
        return "Cannot continue saving the image."
    }

    private fun saveImage(bitmap: Bitmap): String {
        return try {
            val file = createFile("png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, fileOutputStream)
            fileOutputStream.close()

            val uri = Uri.fromFile(file)
            val context = activity.applicationContext
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            uri.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            "Failed to save image to the gallery."
        }
    }


    private fun saveFile(filePath: String): String {
        return try {
            val tempFile = File(filePath)
            val file = createFile(tempFile.extension)
            tempFile.copyTo(file)

            val uri = Uri.fromFile(file)
            val context = activity.applicationContext
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            uri.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            "Failed to save file to the gallery."
        }
    }

    private fun createFile(extension: String = ""): File {
        // TODO: Expose MethodChannel to allow custom path
        // TODO: Expose MethodChannel to allow custom extensions
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        val directory = File(path)
        if (!directory.exists()) {
            directory.mkdir()
        }
        var fileName = System.currentTimeMillis().toString()
        if (extension.isNotEmpty()) {
            fileName += ("." + extension)
        }
        return File(directory, fileName)
    }
}
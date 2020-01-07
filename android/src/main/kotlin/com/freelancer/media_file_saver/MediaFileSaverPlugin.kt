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
import androidx.core.content.ContextCompat
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

interface IMediaFileSaverPlugin {
    fun onGrantExternalStorageImage(): String
    fun onGrantExternalStorageFile(): String
}

class MediaFileSaverPlugin(
    private val activity: Activity,
    private val registrar: Registrar
) : MethodCallHandler, IMediaFileSaverPlugin {

    private val IMAGE_QUALITY = 100

    private val bitmap: Bitmap? = null
    private val filePath: String? = null

    companion object {
        val WRITE_EXTERNAL_STORAGE_IMAGE = 1001
        val WRITE_EXTERNAL_STORAGE_FILE = 1002

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "com.freelancer.flutter.plugins/media_file_saver")
            val plugin = MediaFileSaverPlugin(registrar.activity(), registrar)
            channel.setMethodCallHandler(plugin)
            registrar.addRequestPermissionsResultListener(MediaFileSaverDelegate(plugin))

        }
    }

    override fun onMethodCall(call: MethodCall, result: Result): Unit {
        when {
            call.method == "saveImage" -> {
                val image = call.arguments as ByteArray
                result.success(checkPermissionSaveImage(BitmapFactory.decodeByteArray(image, 0, image.size)))
            }
            call.method == "saveFile" -> {
                val path = call.arguments as String
                result.success(checkPermissionSaveFile(path))
            }
            else -> result.notImplemented()
        }
    }

    override fun onGrantExternalStorageImage(): String {
        return if (bitmap != null) {
            saveImage(bitmap)
        } else {
            "Bitmap cannot be null"
        }
    }

    override fun onGrantExternalStorageFile(): String {
        return if (filePath != null) {
            saveFile(filePath)
        } else {
            "File path cannot be null"
        }
    }

    private fun checkPermissionSaveImage(bitmap: Bitmap): String {
        // TODO: Add check whether the permission has been denied permanently
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_IMAGE)
        } else {
            return saveImage(bitmap)
        }
        return "Cannot continue saving the image."
    }

    private fun saveImage(bitmap: Bitmap): String {
        return try {
            val file = createFile("png")
            val fileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, fileOutputStream)
            fileOutputStream.close()

            val uri = Uri.fromFile(file)
            val context = registrar.activeContext().applicationContext
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            uri.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            "Failed to save image to the gallery."
        }
    }

    private fun checkPermissionSaveFile(filePath: String): String {
        // TODO: Add check whether the permission has been denied permanently
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_IMAGE)
        } else {
            return saveFile(filePath)
        }
        return "Cannot continue saving the image."
    }

    private fun saveFile(filePath: String): String {
        return try {
            val tempFile = File(filePath)
            val file = createFile(tempFile.extension)
            tempFile.copyTo(file)

            val uri = Uri.fromFile(file)
            val context = registrar.activeContext().applicationContext
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
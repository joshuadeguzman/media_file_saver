package com.freelancer.media_file_saver

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MediaFileSaverPlugin(
  private val registrar: Registrar
): MethodCallHandler {

  private val IMAGE_QUALITY = 100

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "com.freelancer.flutter.plugins/media_file_saver")
      channel.setMethodCallHandler(MediaFileSaverPlugin(registrar))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result): Unit {
    when {
        call.method == "saveImage" -> {
          val image = call.arguments as ByteArray
          result.success(saveImage(BitmapFactory.decodeByteArray(image, 0, image.size)))
        }
        call.method == "saveFile" -> {
          val path = call.arguments as String
          result.success(saveFile(path))
        }
        else -> result.notImplemented()
    }

  }

  private fun saveImage(bitmap: Bitmap): String {
    try {
      val file = createFile("png")
      val fileOutputStream = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, fileOutputStream)
      fileOutputStream.close()
    
      val uri = Uri.fromFile(file)
      val context = registrar.activeContext().applicationContext
      context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
      return uri.toString()
    } catch (e: IOException) {
      e.printStackTrace()
      return "Failed to save image to the gallery."
    }
  }

  private fun saveFile(filePath: String): String {
    try {
      val tempFile = File(filePath)
      val file = createFile(tempFile.extension)
      tempFile.copyTo(file)

      val uri = Uri.fromFile(file)
      val context = registrar.activeContext().applicationContext
      context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
      return uri.toString()
    } catch (e: IOException) {
      e.printStackTrace()
      return "Failed to save file to the gallery."
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
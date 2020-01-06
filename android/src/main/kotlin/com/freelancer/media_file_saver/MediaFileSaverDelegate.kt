package com.freelancer.media_file_saver

import android.content.pm.PackageManager
import com.freelancer.media_file_saver.MediaFileSaverPlugin.Companion.WRITE_EXTERNAL_STORAGE_FILE
import com.freelancer.media_file_saver.MediaFileSaverPlugin.Companion.WRITE_EXTERNAL_STORAGE_IMAGE
import io.flutter.plugin.common.PluginRegistry

/**
 * Created by Joshua de Guzman on 2020-01-06.
 */
class MediaFileSaverDelegate(
    private val iMediaFileSaverPlugin: IMediaFileSaverPlugin
) : PluginRegistry.RequestPermissionsResultListener {
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_IMAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    iMediaFileSaverPlugin.onGrantExternalStorageImage()
                } else {
                    // TODO: Expose error in the channel
                }
            }

            WRITE_EXTERNAL_STORAGE_FILE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    iMediaFileSaverPlugin.onGrantExternalStorageFile()
                } else {
                    // TODO: Expose error in the channel
                }
            }
        }
        return true
    }
}
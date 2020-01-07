package com.freelancer.media_file_saver

/**
 * Created by Joshua de Guzman on 2020-01-07.
 */

enum class MediaFileSaverPluginMethodChannel constructor(private val str: String) {
    SAVE_IMAGE("saveImage"),
    SAVE_FILE("saveFile");

    override fun toString(): String {
        return this.str
    }
}
package com.freelancer.media_file_saver

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar

class MediaFileSaverPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {

    private var context: Context? = null
    private var binaryMessenger: BinaryMessenger? = null
    private var methodChannel: MethodChannel? = null
    private var registrar: Registrar? = null
    private var mediaFileSaverPlugin: MediaFileSaverDelegate? = null

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = MediaFileSaverPlugin()
            plugin.onAttachedToEngine(registrar.context(), registrar.messenger(), registrar)
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when {
            call.method == MediaFileSaverPluginMethodChannel.SAVE_IMAGE.toString() -> {
                val image = call.arguments as ByteArray
                this.mediaFileSaverPlugin?.checkPermissionSaveImage(image, result)
            }
            call.method == MediaFileSaverPluginMethodChannel.SAVE_FILE.toString() -> {
                val path = call.arguments as String
                this.mediaFileSaverPlugin?.checkPermissionSaveFile(path, result)
            }
            else -> result.notImplemented()
        }
    }

    private fun onAttachedToEngine(context: Context, binaryMessenger: BinaryMessenger, registrar: Registrar? = null) {
        this.context = context
        this.binaryMessenger = binaryMessenger
        this.methodChannel = MethodChannel(binaryMessenger, "com.freelancer.flutter.plugins/media_file_saver")
        this.methodChannel?.setMethodCallHandler(this)
        registrar?.let {
            this.registrar = it
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        onAttachedToEngine(binding.applicationContext, binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        // TODO
    }

    override fun onDetachedFromActivity() {
        this.mediaFileSaverPlugin = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.mediaFileSaverPlugin = null
        this.mediaFileSaverPlugin = MediaFileSaverDelegate(binding.activity)
        this.mediaFileSaverPlugin?.let {
            registrar?.addRequestPermissionsResultListener(it)
            registrar?.addActivityResultListener(it)
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.mediaFileSaverPlugin = MediaFileSaverDelegate(binding.activity)
        this.mediaFileSaverPlugin?.let {
            registrar?.addRequestPermissionsResultListener(it)
            registrar?.addActivityResultListener(it)
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.mediaFileSaverPlugin = null
    }
}
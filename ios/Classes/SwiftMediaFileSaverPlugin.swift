import Flutter
import UIKit

public class SwiftMediaFileSaverPlugin: NSObject, FlutterPlugin {
    var result: FlutterResult?;

    public static func register(with registrar: FlutterPluginRegistrar) {
      let channel = FlutterMethodChannel(name: "com.freelancer.flutter.plugins/media_file_saver", binaryMessenger: registrar.messenger())
      let instance = SwiftMediaFileSaverPlugin()
      registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      self.result = result
      if call.method == "saveImage" {
        guard let imageData = (call.arguments as? FlutterStandardTypedData)?.data, let image = UIImage(data: imageData) else { return }
        // TODO: Add MethodChannel to handle callbacks and errors and return show it to users (if deemed necessary).
        UIImageWriteToSavedPhotosAlbum(image, self, nil, nil)

      } else if (call.method == "saveFile") {
        guard let path = call.arguments as? String else { return }
        if (isImageFile(filename: path)) {
            if let image = UIImage(contentsOfFile: path) {
                // TODO: Add MethodChannel to handle callbacks and errors and return show it to users (if deemed necessary).
                 UIImageWriteToSavedPhotosAlbum(image, self, nil, nil)
            }
        } else {
            if (UIVideoAtPathIsCompatibleWithSavedPhotosAlbum(path)) {
                // TODO: Add MethodChannel to handle callbacks and errors and return show it to users (if deemed necessary).
                UISaveVideoAtPathToSavedPhotosAlbum(path, self, nil, nil)
            }
        }
      } else {
        result(FlutterMethodNotImplemented)
      }
    }
    
    // TODO: Check the the file type programmatically via Data <> FlutterStandardTypedData from the byte stream.
    func isImageFile(filename: String) -> Bool {
        return filename.hasSuffix(".jpg")
            || filename.hasSuffix(".JPG")
            || filename.hasSuffix(".jpeg")
            || filename.hasSuffix(".JPEG")
            || filename.hasSuffix(".png")
            || filename.hasSuffix(".PNG")
            || filename.hasSuffix(".gif")
            || filename.hasSuffix(".GIF")
    }
}
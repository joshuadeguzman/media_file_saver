import Flutter
import UIKit
import Photos

public class SwiftMediaFileSaverPlugin: NSObject, FlutterPlugin {
    
    private var result: FlutterResult?
        
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "com.freelancer.flutter.plugins/media_file_saver", binaryMessenger: registrar.messenger())
        registrar.addMethodCallDelegate(SwiftMediaFileSaverPlugin(), channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        
        self.result = result
        
        checkAuthorization { [weak self] isAuthorized in
            guard let self = self, isAuthorized else {
                result(false)
                return
            }
            
            switch call.method {
            case "saveImage":
                guard let imageData = (call.arguments as? FlutterStandardTypedData)?.data, let image = UIImage(data:imageData) else {
                    result(false)
                    return
                }
                
                self.saveImage(image)
            
            case "saveFile":
                guard let path = call.arguments as? String else {
                    result(false)
                    return
                }
                
                self.saveFile(path)
                
            default:
                result(FlutterMethodNotImplemented)
            }
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
    
    func checkAuthorization(_ completed: @escaping (Bool) -> Void) {
        
        switch PHPhotoLibrary.authorizationStatus() {
        case .notDetermined:
            PHPhotoLibrary.requestAuthorization({ status in
                completed(status == .authorized)
            })
        case .denied, .restricted:
            completed(false)
        default:
            completed(true)
        }
    }
    
    private func saveImage(_ image: UIImage) {
        UIImageWriteToSavedPhotosAlbum(image, self, #selector(image(_:didFinishSavingWithError:contextInfo:)), nil)
    }
    
    private func saveFile(_ path: String) {
        if isImageFile(filename: path), let image = UIImage(contentsOfFile: path) {
            UIImageWriteToSavedPhotosAlbum(image, self, #selector(image(_:didFinishSavingWithError:contextInfo:)), nil)
            
        } else if UIVideoAtPathIsCompatibleWithSavedPhotosAlbum(path) {
            UISaveVideoAtPathToSavedPhotosAlbum(path, self, #selector(image(_:didFinishSavingWithError:contextInfo:)), nil)
        }
    }
    
    @objc func image(_ image: UIImage, didFinishSavingWithError error: NSError?, contextInfo: UnsafeRawPointer) {
        if let error = error {
            print("Failed to save image - \(error.localizedDescription)")
            result?(false)
        } else {
           result?(true)
        }
    }
}

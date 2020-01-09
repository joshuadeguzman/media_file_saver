## media_file_saver

🖼️ A plugin for saving images and other media files in the device's pictures directory (Android) or photo library (iOS).

## Features

- Simplified image and media file download
- Supports PNG, JPG, JPEG, GIF, MP3 and more
- Implements native permission (Android) and authorization requests (iOS)

## Installation

Add it to your pubspec.yaml file
```
dependencies:
   media_file_saver: <PLUGIN_VERSION>
```

Install it

```
pub get
```

Import it

```
import 'package:media_file_saver/media_file_saver.dart;
```

## Usage

Eg. Saving image from cache

```dart
// Getting the cached image
final file = await DefaultCacheManager().getSingleFile("https//example.com/profile-picture.png");

// Downloading the image file to your photo library
await MediaFileSaver.saveImage(file.readAsBytes());
```

## License

BSD [@joshuadeguzman](https://github.com/joshuadeguzman/media_file_saver)

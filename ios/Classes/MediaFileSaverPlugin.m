#import "MediaFileSaverPlugin.h"
#if __has_include(<media_file_saver/media_file_saver-Swift.h>)
#import <media_file_saver/media_file_saver-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "media_file_saver-Swift.h"
#endif

@implementation MediaFileSaverPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMediaFileSaverPlugin registerWithRegistrar:registrar];
}
@end

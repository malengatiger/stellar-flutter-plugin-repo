#import "StellarpluginPlugin.h"
#if __has_include(<stellarplugin/stellarplugin-Swift.h>)
#import <stellarplugin/stellarplugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "stellarplugin-Swift.h"
#endif

@implementation StellarpluginPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftStellarpluginPlugin registerWithRegistrar:registrar];
}
@end

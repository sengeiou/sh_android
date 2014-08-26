#import "_Device.h"

@interface Device : _Device {}

//+(Device *)insertWithDictionary:(NSDictionary *)dict;
+(Device *)updateWithDictionary:(NSDictionary *)dict;
+(Device *)createTemporaryDevice;
+(Device *)createTemporaryDeviceWithDevice:(Device *)device;
-(void)resetToken;

@end

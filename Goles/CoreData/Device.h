#import "_Device.h"

@interface Device : _Device {}

//+(Device *)insertWithDictionary:(NSDictionary *)dict;
+(Device *)updateWithDictionary:(NSDictionary *)dict;

-(void)resetToken;

@end

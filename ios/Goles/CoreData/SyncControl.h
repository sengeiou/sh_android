#import "_SyncControl.h"

@interface SyncControl : _SyncControl {}

+(instancetype)insertWithDictionary:(NSDictionary *)dict;
+(instancetype)updateWithDictionary:(NSDictionary *)dict;

@end

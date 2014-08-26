#import "_Provider.h"

@interface Provider : _Provider {}

+(Provider *)insertWithDictionary:(NSDictionary *)dict;
+(Provider *)updateWithDictionary:(NSDictionary *)dict;
+(Provider *)createTemporaryProvider;

@end

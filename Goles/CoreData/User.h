#import "_User.h"

@interface User : _User {}

+ (User *)getUser;

+(instancetype)insertWithDictionary:(NSDictionary *)dict;
+(instancetype)updateWithDictionary:(NSDictionary *)dict;

@end

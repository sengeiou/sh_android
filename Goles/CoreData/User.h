#import "_User.h"

@interface User : _User {}

+(instancetype)insertWithDictionary:(NSDictionary *)dict;
+(instancetype)updateWithDictionary:(NSDictionary *)dict;
+(User *)createUserWithUser:(User *)user;

@end

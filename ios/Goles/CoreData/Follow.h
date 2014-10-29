#import "_Follow.h"

@interface Follow : _Follow {}

+(instancetype)insertWithDictionary:(NSDictionary *)dict;
+(instancetype)updateWithDictionary:(NSDictionary *)dict;

+ (NSDictionary *)createDictFromEntity:(Follow *)follow;

@end

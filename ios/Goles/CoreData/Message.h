#import "_Message.h"

@interface Message : _Message {}

+(instancetype)insertWithDictionary:(NSDictionary *)dict;
+(instancetype)updateWithDictionary:(NSDictionary *)dict;

@end

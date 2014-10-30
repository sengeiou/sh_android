#import "_AppAdvice.h"

@interface AppAdvice : _AppAdvice {}

+(instancetype)insertWithDictionary:(NSDictionary *)dict;
+(instancetype)updateWithDictionary:(NSDictionary *)dict;

+(NSArray *)getAppAdviceForPath:(NSString *)path;

@end

#import "_PlayerProvider.h"

@interface PlayerProvider : _PlayerProvider {}

+(PlayerProvider *)insertWithDictionary:(NSDictionary *)dict;
+(PlayerProvider *)updateWithDictionary:(NSDictionary *)dict;

@end

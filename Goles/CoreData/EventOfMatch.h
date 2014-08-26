#import "_EventOfMatch.h"

@interface EventOfMatch : _EventOfMatch {}

+(EventOfMatch *)insertWithDictionary:(NSDictionary *)dict;
+(EventOfMatch *)updateWithDictionary:(NSDictionary *)dict;
-(void)twittDone;

@end

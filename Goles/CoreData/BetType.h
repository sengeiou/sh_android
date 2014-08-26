#import "_BetType.h"

@interface BetType : _BetType {}

+(BetType *)insertWithDictionary:(NSDictionary *)dict;
+(BetType *)updateWithDictionary:(NSDictionary *)dict;
+(BetType *)createTemporaryBetType;

@end

#import "_MatchBetType.h"

@interface MatchBetType : _MatchBetType {}

+(MatchBetType *)insertWithDictionary:(NSDictionary *)dict;
+(MatchBetType *)updateWithDictionary:(NSDictionary *)dict;
+(MatchBetType *)createTemporaryMatchBetType;

@end
